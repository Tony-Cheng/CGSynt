package test.termination;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.junit.jupiter.api.Test;

import cgsynt.interpol.IStatement;
import cgsynt.interpol.ScriptAssignmentStatement;
import cgsynt.interpol.ScriptAssumptionStatement;
import cgsynt.interpol.TraceGlobalVariables;
import cgsynt.interpol.TraceToInterpolants;
import cgsynt.interpol.VariableFactory;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.INestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedRun;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWord;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.buchi.NestedLassoRun;
import de.uni_freiburg.informatik.ultimate.core.model.models.Payload;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger.LogLevel;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.boogie.BoogieNonOldVar;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.BasicIcfg;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.CfgSmtToolkit;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IcfgEdge;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IcfgEdgeFactory;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IcfgInternalTransition;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IcfgLocation;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.debugidentifiers.DebugIdentifier;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.debugidentifiers.StringDebugIdentifier;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.SmtUtils.SimplificationTechnique;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.SmtUtils.XnfConversionTechnique;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.managedscript.ManagedScript;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.taskidentifier.SubtaskFileIdentifier;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.taskidentifier.TaskIdentifier;
import de.uni_freiburg.informatik.ultimate.plugins.generator.buchiautomizer.BinaryStatePredicateManager;
import de.uni_freiburg.informatik.ultimate.plugins.generator.buchiautomizer.BuchiCegarLoopBenchmarkGenerator;
import de.uni_freiburg.informatik.ultimate.plugins.generator.buchiautomizer.LassoCheck;
import de.uni_freiburg.informatik.ultimate.plugins.generator.buchiautomizer.RankVarConstructor;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.CFG2NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.CegarAbsIntRunner;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.PathProgramCache;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.PredicateFactoryForInterpolantAutomata;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.interpolantautomata.builders.InterpolantAutomatonBuilderFactory;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.predicates.PredicateFactory;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.preferences.TAPreferences;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.preferences.TraceAbstractionPreferenceInitializer.InterpolationTechnique;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.tracehandling.RefinementStrategyFactory;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.tracehandling.TaCheckAndRefinementPreferences;
import de.uni_freiburg.informatik.ultimate.test.mocks.ConsoleLogger;
import de.uni_freiburg.informatik.ultimate.util.datastructures.SerialProvider;

public class TestLassoCheck {
	
	@Test
	public void test() throws Exception {
		TraceGlobalVariables globalVars = new TraceGlobalVariables();
		
		ManagedScript mScript = globalVars.getManagedScript();
		IUltimateServiceProvider serviceProvider = globalVars.getService();
		VariableFactory vf = globalVars.getVariableFactory();
		
		TraceToInterpolants tti = new TraceToInterpolants(mScript, serviceProvider, vf.getSymbolTable());
		
		RankVarConstructor rankVarConstructor = new RankVarConstructor(tti.getCfgSmtToolkit());
		
		PredicateFactory predicateFactory = new PredicateFactory(serviceProvider, mScript, 
				rankVarConstructor.getCsToolkitWithRankVariables().getSymbolTable(),
				SimplificationTechnique.NONE, XnfConversionTechnique.BDD_BASED);
		
		CfgSmtToolkit csToolkitWithRankVars = rankVarConstructor.getCsToolkitWithRankVariables();
		
		BinaryStatePredicateManager bspm = new BinaryStatePredicateManager(csToolkitWithRankVars,
				predicateFactory, rankVarConstructor.getUnseededVariable(), 
				rankVarConstructor.getOldRankVariables(), serviceProvider,
				SimplificationTechnique.NONE, XnfConversionTechnique.BDD_BASED);
		
		TAPreferences taPrefs = new TAPreferences(serviceProvider);
		ILogger logger = new ConsoleLogger(LogLevel.ERROR);
		
		PathProgramCache<IcfgInternalTransition> pathProgramCache = new PathProgramCache<>(logger);
		BuchiCegarLoopBenchmarkGenerator benchmarker = new BuchiCegarLoopBenchmarkGenerator();
		
		/////////////////////////////////////////////////////////////////////////////////////
		// Build the ICFG
		/////////////////////////////////////////////////////////////////////////////////////
		Script script = globalVars.getManagedScript().getScript();

		BoogieNonOldVar i = vf.constructVariable("i", VariableFactory.INT);

		IStatement ipp = new ScriptAssignmentStatement(i, script.term("+", i.getTerm(), script.numeral("1")),
				globalVars.getManagedScript(), vf.getSymbolTable());
		
		IcfgEdgeFactory edgeFactory = new IcfgEdgeFactory(new SerialProvider());
		
		BasicIcfg<IcfgLocation> icfg = new BasicIcfg<>("test", tti.getCfgSmtToolkit(), IcfgLocation.class);
		IcfgLocation l1 = new IcfgLocation(new StringDebugIdentifier("l1"), "l1");
		IcfgLocation l2 = new IcfgLocation(new StringDebugIdentifier("l2"), "l2");
		
		icfg.addLocation(l1, true, false, true, false, false);
		icfg.addLocation(l2, false, false, false, false, true);
		
		IcfgInternalTransition e1 = edgeFactory.createInternalTransition(l1, l2, new Payload(), ipp.getTransFormula());
		IcfgInternalTransition e2 = edgeFactory.createInternalTransition(l2, l2, new Payload(), ipp.getTransFormula());
		
		IPredicate node1 = predicateFactory.newDebugPredicate("l1");
		IPredicate node2 = predicateFactory.newDebugPredicate("l2");
			
		NestedWord<IcfgInternalTransition> stemWord = new NestedWord<>(e1, NestedWord.INTERNAL_POSITION);
		ArrayList<IPredicate> stemStates = new ArrayList<>();
		stemStates.add(node1);
		stemStates.add(node2);
		
		NestedWord<IcfgInternalTransition> loopWord = new NestedWord<>(e2, NestedWord.INTERNAL_POSITION);
		ArrayList<IPredicate> loopStates = new ArrayList<>();
		stemStates.add(node2);
		stemStates.add(node2);
		
		NestedRun<IcfgInternalTransition, IPredicate> stem = new NestedRun<>(stemWord, stemStates);
		
		NestedRun<IcfgInternalTransition, IPredicate> loop = new NestedRun<>(loopWord, loopStates);
		
		NestedLassoRun<IcfgInternalTransition, IPredicate> counterexample = new NestedLassoRun<>(stem, loop);
		
		/////////////////////////////////////////////////////////////////////////////////////
		
		CegarAbsIntRunner<IcfgInternalTransition> absIntRunner =
				new CegarAbsIntRunner<>(serviceProvider, benchmarker, icfg, SimplificationTechnique.NONE,
						XnfConversionTechnique.BDD_BASED, tti.getCfgSmtToolkit(), pathProgramCache, taPrefs);
		
		PredicateFactoryForInterpolantAutomata stateFactory = new PredicateFactoryForInterpolantAutomata(csToolkitWithRankVars.getManagedScript(),
				predicateFactory, taPrefs.computeHoareAnnotation());
		
		InterpolantAutomatonBuilderFactory<IcfgInternalTransition> interpolantAutomatonBuilderFactory =
				new InterpolantAutomatonBuilderFactory<>(serviceProvider, tti.getCfgSmtToolkit(), stateFactory,
						icfg, absIntRunner, taPrefs, InterpolationTechnique.Craig_NestedInterpolation, taPrefs.interpolantAutomaton(),
						benchmarker);
		
		final TaCheckAndRefinementPreferences<IcfgInternalTransition> taCheckAndRefinementPrefs =
				new TaCheckAndRefinementPreferences<>(serviceProvider, taPrefs, InterpolationTechnique.Craig_NestedInterpolation,
						SimplificationTechnique.NONE, XnfConversionTechnique.BDD_BASED,
						tti.getCfgSmtToolkit(), predicateFactory, icfg,
						interpolantAutomatonBuilderFactory);
		
		RefinementStrategyFactory<IcfgInternalTransition> refinementFactory = new RefinementStrategyFactory<>(
				logger, serviceProvider, taPrefs, taCheckAndRefinementPrefs, absIntRunner, icfg, predicateFactory,
				pathProgramCache);
		
		Collection<IcfgLocation> allNodes = new HashSet<>();
		for (final Map<DebugIdentifier, ? extends IcfgLocation> prog2pp : icfg.getProgramPoints().values())
			allNodes.addAll(prog2pp.values());
		
		INestedWordAutomaton<IcfgInternalTransition, IPredicate> omega = CFG2NestedWordAutomaton.constructAutomatonWithSPredicates(
				serviceProvider, icfg, stateFactory, allNodes,
				taPrefs.interprocedural(), predicateFactory);
		
		TaskIdentifier taskIdentifier = new SubtaskFileIdentifier(null, icfg.getIdentifier());
		
		LassoCheck<IcfgInternalTransition> check = new LassoCheck<>(
				InterpolationTechnique.Craig_NestedInterpolation,
				tti.getCfgSmtToolkit(), 
				predicateFactory,
				csToolkitWithRankVars.getSymbolTable(),
				tti.getCfgSmtToolkit().getModifiableGlobalsTable(),
				tti.getCfgSmtToolkit().getSmtSymbols(),
				bspm, counterexample, 
				"Test", serviceProvider,
				SimplificationTechnique.NONE, XnfConversionTechnique.BDD_BASED,
				refinementFactory,
				omega,
				taskIdentifier,
				benchmarker);
		
		check.getNonTerminationArgument();
	}
}
