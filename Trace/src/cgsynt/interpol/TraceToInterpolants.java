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
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.singletracecheck.TraceCheck;
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
	private PredicateUnifier pUnifier;
	private IPredicate preconditions;
	private IPredicate postconditions;
	private long totaltime;
	private long numSamples;

	public TraceToInterpolants(ManagedScript managedScript, IUltimateServiceProvider service,
			DefaultIcfgSymbolTable symbolTable) throws Exception {
		ILogger logger = new ConsoleLogger();
		logger.setLevel(LogLevel.OFF);
		this.managedScript = managedScript;
		this.service = service;
		this.symbolTable = symbolTable;
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
		pUnifier = new PredicateUnifier(logger, service, managedScript, predicateFactory, symbolTable,
				SimplificationTechnique.NONE, XnfConversionTechnique.BDD_BASED);
		postconditions = pUnifier.getTruePredicate();
		postconditions = pUnifier.getTruePredicate();
		this.totaltime = 0;
		this.numSamples = 0;
	}

	private NestedWord<IAction> buildTrace(List<IStatement> statements) {
		int len = statements.size();
		IAction[] word = new IAction[len];
		int[] nestingRelation = new int[len];

		for (int i = 0; i < len; i++) {
			word[i] = statements.get(i).getFormula();
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
		long time = System.nanoTime();
		InterpolatingTraceCheckCraig<IAction> interpolate = new InterpolatingTraceCheckCraig<>(preconditions,
				postconditions, pendingContexts, trace, controlLocationSequence, service, toolkit, managedScript, null,
				pUnifier, AssertCodeBlockOrder.NOT_INCREMENTALLY, false, true,
				InterpolationTechnique.Craig_NestedInterpolation, false, XnfConversionTechnique.BDD_BASED,
				SimplificationTechnique.NONE, false);
		totaltime += System.nanoTime() - time;
		numSamples++;
		if (interpolate.isCorrect() == LBool.UNKNOWN) {
			System.err.println("Is the trace correct? Unknown.");
			return null;
		}
		if (interpolate.isCorrect() == LBool.SAT) {
			return null;
		}
		return interpolate.getInterpolants();
	}

	public void printAverageTime() {
		System.out.println(totaltime / numSamples / 1000000);
	}

	@Override
	public IPredicate getTruePredicate() {
		return pUnifier.getTruePredicate();
	}

	public PredicateUnifier getPUnifier() {
		return this.pUnifier;
	}

	@Override
	public IPredicate getFalsePredicate() {
		return pUnifier.getFalsePredicate();
	}

	public IPredicate getPreconditions() {
		return preconditions;
	}

	public void setPreconditions(IPredicate preconditions) throws Exception {
		if (preconditions == null)
			this.preconditions = pUnifier.getTruePredicate();
		else
			this.preconditions = pUnifier.getOrConstructPredicate(preconditions);
	}

	public IPredicate getPostconditions() {
		return postconditions;
	}

	public CfgSmtToolkit getCfgSmtToolkit() {
		return this.toolkit;
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
		TraceCheck<IAction> interpolate = new TraceCheck<>(pre, post, pendingContexts, trace, service, toolkit,
				AssertCodeBlockOrder.NOT_INCREMENTALLY, false, false);

		assert interpolate.isCorrect() != LBool.UNKNOWN;
		return interpolate.isCorrect() == LBool.UNSAT;
	}

	public void setPostconditions(IPredicate postconditions) throws Exception {
		if (postconditions == null)
			this.postconditions = pUnifier.getTruePredicate();
		else
			this.postconditions = pUnifier.getOrConstructPredicate(postconditions);
	}

	public BasicPredicateFactory getPredicateFactory() {
		return predicateFactory;
	}

}
