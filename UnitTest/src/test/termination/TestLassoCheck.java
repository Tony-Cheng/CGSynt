package test.termination;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.junit.jupiter.api.Test;

import cgsynt.interpol.TraceGlobalVariables;
import cgsynt.interpol.TraceToInterpolants;
import cgsynt.interpol.VariableFactory;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.INestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedRun;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.buchi.NestedLassoRun;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger.LogLevel;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.BasicIcfg;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.CfgSmtToolkit;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IcfgInternalTransition;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IcfgLocation;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.debugidentifiers.DebugIdentifier;
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

public class TestLassoCheck {
	
	@Test
	public void test() {
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
		
		BasicIcfg<IcfgLocation> icfg = new BasicIcfg<>("test", tti.getCfgSmtToolkit(), IcfgLocation.class);
		
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
		
		IcfgEdgeBuider edgeBuilder = new IcfgEdgeBuilder();
		
//		LassoCheck<IcfgInternalTransition> check = new LassoCheck<>(
//				InterpolationTechnique.Craig_NestedInterpolation,
//				tti.getCfgSmtToolkit(), 
//				predicateFactory,
//				csToolkitWithRankVars.getSymbolTable(),
//				tti.getCfgSmtToolkit().getModifiableGlobalsTable(),
//				tti.getCfgSmtToolkit().getSmtSymbols(),
//				bspm, /*COUNTEREXAMPLE*/, 
//				"Test", serviceProvider,
//				SimplificationTechnique.NONE, XnfConversionTechnique.BDD_BASED,
//				refinementFactory,
//				omega,
//				taskIdentifier,
//				benchmarker);
	}
}
