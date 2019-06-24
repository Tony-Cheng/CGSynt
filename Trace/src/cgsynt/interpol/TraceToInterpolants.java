package cgsynt.interpol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWord;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger.LogLevel;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Script.LBool;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.boogie.BoogieNonOldVar;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.CfgSmtToolkit;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.ConcurrencyInformation;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.DefaultIcfgSymbolTable;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.ModifiableGlobalsTable;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.SmtSymbols;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.ThreadInstance;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IAction;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IIcfgForkTransitionThreadCurrent;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IIcfgJoinTransitionThreadCurrent;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IcfgEdgeFactory;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IcfgLocation;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.variables.ILocalProgramVar;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.variables.IProgramNonOldVar;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.SmtUtils.SimplificationTechnique;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.SmtUtils.XnfConversionTechnique;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.managedscript.ManagedScript;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.BasicPredicateFactory;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.PredicateUnifier;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.tracecheck.ITraceCheckPreferences.AssertCodeBlockOrder;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.preferences.TraceAbstractionPreferenceInitializer.InterpolationTechnique;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.singletracecheck.InterpolatingTraceCheckCraig;
import de.uni_freiburg.informatik.ultimate.test.mocks.ConsoleLogger;
import de.uni_freiburg.informatik.ultimate.util.datastructures.SerialProvider;
import de.uni_freiburg.informatik.ultimate.util.datastructures.relation.HashRelation;

public class TraceToInterpolants implements IInterpol {
	private IUltimateServiceProvider service;
	private ManagedScript managedScript;
	private DefaultIcfgSymbolTable symbolTable;
	private Set<String> procedures;
	private ModifiableGlobalsTable modifiableGlobalsTable;
	private Map<String, List<ILocalProgramVar>> inParams;
	private Map<String, List<ILocalProgramVar>> outParams;
	private IcfgEdgeFactory icfgEdgeFactory;
	private ConcurrencyInformation concurInfo;
	private SmtSymbols smtSymbols;
	private CfgSmtToolkit toolkit;
	private SortedMap<Integer, IPredicate> pendingContexts;
	private BasicPredicateFactory predicateFactory;
	private PredicateUnifier pUnifer;
	private List<IAssumption> preconditions;
	private List<IAssumption> negatedPostconditions;

	private static TraceToInterpolants traceToInterpolants;

	public TraceToInterpolants() throws Exception {
		ILogger logger = new ConsoleLogger();
		logger.setLevel(LogLevel.OFF);
		managedScript = TraceGlobalVariables.getGlobalVariables().getManagedScript();
		service = TraceGlobalVariables.getGlobalVariables().getService();
		symbolTable = TraceGlobalVariables.getGlobalVariables().getVariableFactory().getSymbolTable();
		procedures = new HashSet<>();
		HashRelation<String, IProgramNonOldVar> mProc2Globals = new HashRelation<>();
		modifiableGlobalsTable = new ModifiableGlobalsTable(mProc2Globals);
		inParams = new HashMap<>();
		outParams = new HashMap<>();
		icfgEdgeFactory = new IcfgEdgeFactory(new SerialProvider());
		Map<IIcfgForkTransitionThreadCurrent<IcfgLocation>, ThreadInstance> threadInstanceMap = new HashMap<>();
		Collection<IIcfgJoinTransitionThreadCurrent<IcfgLocation>> joinTransitions = new ArrayList<>();
		concurInfo = new ConcurrencyInformation(threadInstanceMap, joinTransitions);
		smtSymbols = new SmtSymbols(managedScript.getScript());

		toolkit = new CfgSmtToolkit(modifiableGlobalsTable, managedScript, symbolTable, procedures, inParams, outParams,
				icfgEdgeFactory, concurInfo, smtSymbols);
		pendingContexts = new TreeMap<>();
		predicateFactory = new BasicPredicateFactory(service, managedScript, symbolTable, SimplificationTechnique.NONE,
				XnfConversionTechnique.BDD_BASED);
		pUnifer = new PredicateUnifier(logger, service, managedScript, predicateFactory, symbolTable,
				SimplificationTechnique.NONE, XnfConversionTechnique.BDD_BASED);
		preconditions = new ArrayList<>();
		negatedPostconditions = createInitialNegatedPostconditions();
		preconditions = createInitialPreconditions();
	}

	private List<IAssumption> negatePostconditions(List<IAssumption> postconditions) {
		for (IAssumption precondition : postconditions)
			precondition.negate();
		return postconditions;

	}

	private List<IAssumption> createInitialPreconditions() throws Exception {
		ArrayList<IAssumption> preconditions = new ArrayList<>();
		VariableFactory variableFactory = TraceGlobalVariables.getGlobalVariables().getVariableFactory();
		Script script = TraceGlobalVariables.getGlobalVariables().getManagedScript().getScript();
		BoogieNonOldVar var = variableFactory.constructVariable(VariableFactory.INT);
		IAssumption statement1 = new ScriptAssumptionStatement(var, script.numeral("0"), "=");
		negatedPostconditions.add(statement1);
		return preconditions;
	}

	private List<IAssumption> createInitialNegatedPostconditions() throws Exception {
		ArrayList<IAssumption> negatedPostconditions = new ArrayList<>();
		VariableFactory variableFactory = TraceGlobalVariables.getGlobalVariables().getVariableFactory();
		Script script = TraceGlobalVariables.getGlobalVariables().getManagedScript().getScript();
		BoogieNonOldVar var = variableFactory.constructVariable(VariableFactory.INT);
		IAssumption statement1 = new ScriptAssumptionStatement(var, script.numeral("0"), "=");
		IAssumption statement2 = new ScriptAssumptionStatement(var, script.numeral("1"), "=");
		negatedPostconditions.add(statement1);
		negatedPostconditions.add(statement2);
		return negatedPostconditions;
	}

	public static void reset() throws Exception {
		traceToInterpolants = new TraceToInterpolants();
	}

	public static TraceToInterpolants getTraceToInterpolants() {
		return traceToInterpolants;
	}

	private NestedWord<IAction> buildTrace(List<IStatement> statements) {
		int len = statements.size();
		IAction[] word = new IAction[len + preconditions.size() + negatedPostconditions.size()];
		int[] nestingRelation = new int[len + preconditions.size() + negatedPostconditions.size()];

		for (int i = 0; i < preconditions.size(); i++) {
			word[i] = preconditions.get(i).getFormula();
			nestingRelation[i] = NestedWord.INTERNAL_POSITION;
		}

		for (int i = preconditions.size(); i < preconditions.size() + len; i++) {
			word[i] = statements.get(i - preconditions.size()).getFormula();
			nestingRelation[i] = NestedWord.INTERNAL_POSITION;
		}
		for (int i = preconditions.size() + len; i < preconditions.size() + len + negatedPostconditions.size(); i++) {
			word[i] = negatedPostconditions.get(i - preconditions.size() - len).getFormula();
			nestingRelation[i] = NestedWord.INTERNAL_POSITION;
		}
		return new NestedWord<>(word, nestingRelation);
	}

	private List<Object> generateControlLocationSequence(int n) {
		List<Object> controlLocationSequence = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			controlLocationSequence.add(new Object());
		}
		return controlLocationSequence;

	}

	@Override
	public IPredicate[] computeInterpolants(List<IStatement> statements) throws Exception {
		NestedWord<IAction> trace = buildTrace(statements);
		List<Object> controlLocationSequence = generateControlLocationSequence(trace.length() + 1);
		InterpolatingTraceCheckCraig<IAction> interpolate = new InterpolatingTraceCheckCraig<>(
				pUnifer.getTruePredicate(), pUnifer.getFalsePredicate(), pendingContexts, trace,
				controlLocationSequence, service, toolkit, managedScript, null, pUnifer,
				AssertCodeBlockOrder.NOT_INCREMENTALLY, false, true, InterpolationTechnique.Craig_NestedInterpolation,
				false, XnfConversionTechnique.BDD_BASED, SimplificationTechnique.NONE, false);
		if (interpolate.isCorrect() == LBool.UNKNOWN) {
			throw new Exception("Is trace correct? Unknown.");
		}
		if (interpolate.isCorrect() == LBool.SAT) {
			return null;
		}
		return interpolate.getInterpolants();
	}

	@Override
	public IPredicate getTruePredicate() {
		return pUnifer.getTruePredicate();
	}

	@Override
	public IPredicate getFalsePredicate() {
		return pUnifer.getFalsePredicate();
	}

	public List<IAssumption> getPreconditions() {
		return preconditions;
	}

	public void setPreconditions(List<IAssumption> preconditions) throws Exception {
		if (preconditions.isEmpty())
			this.preconditions = createInitialPreconditions();
		else
			this.preconditions = preconditions;
	}

	public List<IAssumption> getNegatedPostconditions() {
		return negatedPostconditions;
	}

	private List<IStatement> buildStatementList(IStatement statement) {
		List<IStatement> statements = new ArrayList<>();
		statements.add(statement);
		return statements;
	}

	@Override
	public boolean isCorrect(IPredicate pre, IStatement statement, IPredicate post) {
		List<IStatement> statements = buildStatementList(statement);
		NestedWord<IAction> trace = buildTrace(statements);
		List<Object> controlLocationSequence = generateControlLocationSequence(trace.length() + 1);
		InterpolatingTraceCheckCraig<IAction> interpolate = new InterpolatingTraceCheckCraig<>(pre, post,
				pendingContexts, trace, controlLocationSequence, service, toolkit, managedScript, null, pUnifer,
				AssertCodeBlockOrder.NOT_INCREMENTALLY, false, true, InterpolationTechnique.Craig_NestedInterpolation,
				false, XnfConversionTechnique.BDD_BASED, SimplificationTechnique.NONE, false);

		assert interpolate.isCorrect() != LBool.UNKNOWN;
		return interpolate.isCorrect() == LBool.UNSAT;
	}

	public int getPreconditionsSize() {
		return preconditions.size();
	}

	public int getNegatedPostconditionsSize() {
		return negatedPostconditions.size();
	}

	public void setNegatedPostconditions(List<IAssumption> postconditions) throws Exception {
		if (postconditions.isEmpty())
			this.negatedPostconditions = createInitialNegatedPostconditions();
		else
			this.negatedPostconditions = negatePostconditions(postconditions);
	}

	public BasicPredicateFactory getPredicateFactory() {
		return predicateFactory;
	}

}
