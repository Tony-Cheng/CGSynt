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
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
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
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.predicates.PredicateFactory;
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

	private static TraceToInterpolants traceToInterpolants = new TraceToInterpolants();

	public TraceToInterpolants() {
		ILogger logger = new ConsoleLogger();
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
	}

	public static TraceToInterpolants getTraceToInterpolants() {
		return traceToInterpolants;
	}

	public NestedWord<IAction> buildTrace(List<IStatement> statements) {
		int len = statements.size();
		IAction[] word = new IAction[len];
		int[] nestingRelation = new int[len];

		for (int i = 0; i < len; i++) {
			word[i] = statements.get(i).getFormula();
			nestingRelation[i] = NestedWord.INTERNAL_POSITION;
		}
		return new NestedWord<>(word, nestingRelation);
	}

	public List<Object> generateControlLocationSequence(int n) {
		List<Object> controlLocationSequence = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			controlLocationSequence.add(new Object());
		}
		return controlLocationSequence;

	}

	@Override
	public boolean checkSat() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IPredicate[] computeInterpolants(List<IStatement> statements) {
		List<Object> controlLocationSequence = generateControlLocationSequence(statements.size() + 2);
		NestedWord<IAction> trace = buildTrace(statements);
		InterpolatingTraceCheckCraig<IAction> interpolate = new InterpolatingTraceCheckCraig<>(
				pUnifer.getTruePredicate(), pUnifer.getFalsePredicate(), pendingContexts, trace,
				controlLocationSequence, service, toolkit, managedScript, null, pUnifer,
				AssertCodeBlockOrder.NOT_INCREMENTALLY, false, true, InterpolationTechnique.Craig_NestedInterpolation,
				false, XnfConversionTechnique.BDD_BASED, SimplificationTechnique.NONE, false);
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

}
