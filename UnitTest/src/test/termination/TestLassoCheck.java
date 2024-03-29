package test.termination;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import cgsynt.automaton.factory.PDeterminizeStateFactory;
import cgsynt.interpol.IStatement;
import cgsynt.interpol.ScriptAssignmentStatement;
import cgsynt.interpol.ScriptAssumptionStatement;
import cgsynt.interpol.TraceGlobalVariables;
import cgsynt.interpol.TraceToInterpolants;
import cgsynt.interpol.VariableFactory;
import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.AutomatonDefinitionPrinter.Format;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.INestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedRun;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWord;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.VpAlphabet;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.buchi.NestedLassoRun;
import de.uni_freiburg.informatik.ultimate.core.coreplugin.services.PreferenceLayer;
import de.uni_freiburg.informatik.ultimate.core.model.models.Payload;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger.LogLevel;
import de.uni_freiburg.informatik.ultimate.core.preferences.RcpPreferenceProvider;
import de.uni_freiburg.informatik.ultimate.lassoranker.AnalysisType;
import de.uni_freiburg.informatik.ultimate.lassoranker.termination.TerminationArgument;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.boogie.BoogieNonOldVar;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.BasicIcfg;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.CfgSmtToolkit;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IcfgEdgeFactory;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IcfgInternalTransition;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IcfgLocation;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.debugidentifiers.DebugIdentifier;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.debugidentifiers.StringDebugIdentifier;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.SmtUtils.SimplificationTechnique;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.SmtUtils.XnfConversionTechnique;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.SolverBuilder.SolverMode;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.managedscript.ManagedScript;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.tracecheck.ITraceCheckPreferences.AssertCodeBlockOrder;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.tracecheck.ITraceCheckPreferences.UnsatCores;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.tracecheck.TraceCheckReasonUnknown.RefinementStrategyExceptionBlacklist;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.taskidentifier.SubtaskFileIdentifier;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.taskidentifier.TaskIdentifier;
import de.uni_freiburg.informatik.ultimate.plugins.generator.buchiautomizer.BinaryStatePredicateManager;
import de.uni_freiburg.informatik.ultimate.plugins.generator.buchiautomizer.BuchiAutomizer;
import de.uni_freiburg.informatik.ultimate.plugins.generator.buchiautomizer.BuchiCegarLoopBenchmarkGenerator;
import de.uni_freiburg.informatik.ultimate.plugins.generator.buchiautomizer.LassoCheck;
import de.uni_freiburg.informatik.ultimate.plugins.generator.buchiautomizer.RankVarConstructor;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.preferences.RcfgPreferenceInitializer;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.CFG2NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.CegarAbsIntRunner;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.HoareAnnotation;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.PathProgramCache;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.PredicateFactoryForInterpolantAutomata;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.TraceAbstraction;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.interpolantautomata.builders.InterpolantAutomatonBuilderFactory;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.predicates.PredicateFactory;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.preferences.TAPreferences;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.preferences.TAPreferences.Artifact;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.preferences.TAPreferences.Concurrency;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.preferences.TAPreferences.InterpolantAutomatonEnhancement;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.preferences.TraceAbstractionPreferenceInitializer;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.preferences.TraceAbstractionPreferenceInitializer.AbstractInterpretationMode;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.preferences.TraceAbstractionPreferenceInitializer.HoareAnnotationPositions;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.preferences.TraceAbstractionPreferenceInitializer.HoareTripleChecks;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.preferences.TraceAbstractionPreferenceInitializer.InterpolantAutomaton;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.preferences.TraceAbstractionPreferenceInitializer.InterpolationTechnique;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.preferences.TraceAbstractionPreferenceInitializer.Minimization;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.preferences.TraceAbstractionPreferenceInitializer.RefinementStrategy;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.tracehandling.RefinementStrategyFactory;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.tracehandling.TaCheckAndRefinementPreferences;
import de.uni_freiburg.informatik.ultimate.test.mocks.ConsoleLogger;
import de.uni_freiburg.informatik.ultimate.util.datastructures.SerialProvider;

public class TestLassoCheck {
	
	//@Test
	public void testTerm() throws Exception {
		CustomServiceProvider serviceProvider = new CustomServiceProvider(LogLevel.OFF);
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Setting up the TAPreferences
		RcpPreferenceProvider taPref = new RcpPreferenceProvider(TraceAbstraction.class.getPackage().getName());
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_INTERPROCEDUTAL, false);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_USERLIMIT_ITERATIONS, 100);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_WATCHITERATION, 100);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_ARTIFACT, Artifact.INTERPOLANT_AUTOMATON);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_HOARE, true);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_HOARE_POSITIONS, HoareAnnotationPositions.All);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_INTERPOLATED_LOCS, InterpolationTechnique.Craig_NestedInterpolation);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_INTERPOLANT_AUTOMATON, InterpolantAutomaton.CANONICAL);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_DUMPAUTOMATA, false);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_AUTOMATAFORMAT, Format.ATS_NUMERATE);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_DUMPPATH, "label");
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_DUMP_ONLY_REUSE, false);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_INTERPOLANT_AUTOMATON_ENHANCEMENT, InterpolantAutomatonEnhancement.NONE);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_HOARE_TRIPLE_CHECKS, HoareTripleChecks.MONOLITHIC);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_MINIMIZE, Minimization.DELAYED_SIMULATION);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_CONCURRENCY, Concurrency.FINITE_AUTOMATA);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_USERLIMIT_TRACE_HISTOGRAM, 100);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_USERLIMIT_TIME, 0);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_USERLIMIT_PATH_PROGRAM, 0);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_COMPUTE_INTERPOLANT_SEQUENCE_STATISTICS, false);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_ABSINT_MODE, AbstractInterpretationMode.USE_PREDICATES);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_SIMPLIFICATION_TECHNIQUE, SimplificationTechnique.SIMPLIFY_BDD_FIRST_ORDER);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_XNF_CONVERSION_TECHNIQUE, XnfConversionTechnique.BDD_BASED);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_REFINEMENT_STRATEGY, RefinementStrategy.CAMEL);
		taPref.put(RcfgPreferenceInitializer.LABEL_SOLVER, SolverMode.Internal_SMTInterpol);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_REFINEMENT_STRATEGY_EXCEPTION_BLACKLIST, 
				RefinementStrategyExceptionBlacklist.ALL);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_ASSERT_CODEBLOCKS_INCREMENTALLY, 
				AssertCodeBlockOrder.NOT_INCREMENTALLY);
		
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_UNSAT_CORES, UnsatCores.IGNORE);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_LIVE_VARIABLES, true);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_ABSTRACT_INTERPRETATION_FOR_PATH_INVARIANTS, true);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_INTERPOLANTS_CONSOLIDATION, true);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_NONLINEAR_CONSTRAINTS_IN_PATHINVARIANTS, true);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_UNSAT_CORES_IN_PATHINVARIANTS, true);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_WEAKEST_PRECONDITION_IN_PATHINVARIANTS, true);
	    taPref.put(TraceAbstractionPreferenceInitializer.LABEL_COMPUTE_COUNTEREXAMPLE, false);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_USE_PREDICATE_TRIE_BASED_PREDICATE_UNIFIER, true);
		
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Setting up the BuchiAutomizer Preferences
		RcpPreferenceProvider buchiPref = new RcpPreferenceProvider(BuchiAutomizer.class.getPackage().getName());
		
		buchiPref.put("Rank analysis", AnalysisType.LINEAR);
		buchiPref.put("GNTA analysis", AnalysisType.LINEAR);
		buchiPref.put("Number of GNTA directions", 10);
		buchiPref.put("Template benchmark mode", false);
		buchiPref.put("Try to simplify termination arguments", false);
		buchiPref.put("Try twofold refinement", false);
		
		PreferenceLayer taLayer = new PreferenceLayer(taPref, this.getClass());
		PreferenceLayer buchiLayer = new PreferenceLayer(buchiPref, this.getClass());
		
		serviceProvider.addPreferenceProvider(taLayer, TraceAbstraction.class.getPackage().getName());
		serviceProvider.addPreferenceProvider(buchiLayer, BuchiAutomizer.class.getPackage().getName());

		TraceGlobalVariables globalVars = new TraceGlobalVariables(serviceProvider);
		
		ManagedScript mScript = globalVars.getManagedScript();
		VariableFactory vf = globalVars.getVariableFactory();
		
		Set<String> procs = new HashSet<>();
		procs.add("l1");
		procs.add("l2");
		procs.add("l3");
		procs.add("l4");
		
		TraceToInterpolants tti = new TraceToInterpolants(mScript, serviceProvider, vf.getSymbolTable(), procs);
		
		PredicateFactory oldPredicateFactory = globalVars.getPredicateFactory();
		
		/////////////////////////////////////////////////////////////////////////////////////
		// Build the ICFG
		/////////////////////////////////////////////////////////////////////////////////////
		Script script = globalVars.getManagedScript().getScript();

		BoogieNonOldVar i = vf.constructVariable("i", VariableFactory.INT);

		IStatement imm = new ScriptAssignmentStatement(i, script.term("-", i.getTerm(), script.numeral("1")),
				mScript, vf.getSymbolTable());
		IStatement iet = new ScriptAssignmentStatement(i, script.numeral("2"), mScript,
				vf.getSymbolTable());
		ScriptAssumptionStatement ilez = new ScriptAssumptionStatement(i, script.numeral("0"), "<=", mScript, vf.getSymbolTable());
		ScriptAssumptionStatement igz = new ScriptAssumptionStatement(i, script.numeral("0"), ">", mScript, vf.getSymbolTable());
		
		IcfgEdgeFactory edgeFactory = new IcfgEdgeFactory(new SerialProvider());
		
		BasicIcfg<IcfgLocation> icfg = new BasicIcfg<>("test", tti.getCfgSmtToolkit(), IcfgLocation.class);
		IcfgLocation l1 = new IcfgLocation(new StringDebugIdentifier("l1"), "l1");
		IcfgLocation l2 = new IcfgLocation(new StringDebugIdentifier("l2"), "l2");
		IcfgLocation l3 = new IcfgLocation(new StringDebugIdentifier("l3"), "l3");
		IcfgLocation l4 = new IcfgLocation(new StringDebugIdentifier("l4"), "l4");
		
		icfg.addLocation(l1, true, false, true, false, false);
		icfg.addLocation(l2, false, false, false, false, true);
		icfg.addLocation(l3, false, false, false, false, false);
		icfg.addLocation(l4, false, false, false, true, false);
		
		IcfgInternalTransition e1 = edgeFactory.createInternalTransition(l1, l2, new Payload(), iet.getTransFormula());
		IcfgInternalTransition e2 = edgeFactory.createInternalTransition(l2, l3, new Payload(), igz.getTransFormula(false));
		IcfgInternalTransition e3 = edgeFactory.createInternalTransition(l3, l2, new Payload(), imm.getTransFormula());	
		IcfgInternalTransition e4 = edgeFactory.createInternalTransition(l2, l4, new Payload(), ilez.getTransFormula(false));
		
		HoareAnnotation node1 = oldPredicateFactory.getNewHoareAnnotation(l1, tti.getModifiableGlobalsTable());
		HoareAnnotation node2 = oldPredicateFactory.getNewHoareAnnotation(l2, tti.getModifiableGlobalsTable());
		HoareAnnotation node3 = oldPredicateFactory.getNewHoareAnnotation(l3, tti.getModifiableGlobalsTable());
		HoareAnnotation node4 = oldPredicateFactory.getNewHoareAnnotation(l4, tti.getModifiableGlobalsTable());
			
		
		NestedWord<IcfgInternalTransition> stemWord = new NestedWord<>(e1, NestedWord.INTERNAL_POSITION);
		ArrayList<IPredicate> stemStates = new ArrayList<>();
		stemStates.add(node1);
		stemStates.add(node2);
		
		IcfgInternalTransition[] loopLetters = new IcfgInternalTransition[2];
		int[] nestingRelation = new int[2];
		loopLetters[0] = e2;
		loopLetters[1] = e3;
		nestingRelation[0] = NestedWord.INTERNAL_POSITION;
		nestingRelation[1] = NestedWord.INTERNAL_POSITION;
		
		NestedWord<IcfgInternalTransition> loopWord = new NestedWord<>(loopLetters, nestingRelation);
		ArrayList<IPredicate> loopStates = new ArrayList<>();
		loopStates.add(node2);
		loopStates.add(node3);
		loopStates.add(node2);
		
		NestedRun<IcfgInternalTransition, IPredicate> stem = new NestedRun<>(stemWord, stemStates);
		
		NestedRun<IcfgInternalTransition, IPredicate> loop = new NestedRun<>(loopWord, loopStates);
		
		NestedLassoRun<IcfgInternalTransition, IPredicate> counterexample = new NestedLassoRun<>(stem, loop);
		
		/////////////////////////////////////////////////////////////////////////////////////
		
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
		
		System.out.println("Before:\n" + omega);
		
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
		
		System.out.println("After:\n" + omega);
		System.out.println(check.getNonTerminationArgument());
	}
	
	//@Test
	public void testNonTerm() throws Exception {
		CustomServiceProvider serviceProvider = new CustomServiceProvider(LogLevel.OFF);
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Setting up the TAPreferences
		RcpPreferenceProvider taPref = new RcpPreferenceProvider(TraceAbstraction.class.getPackage().getName());
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_INTERPROCEDUTAL, false);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_USERLIMIT_ITERATIONS, 100);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_WATCHITERATION, 100);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_ARTIFACT, Artifact.INTERPOLANT_AUTOMATON);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_HOARE, false);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_HOARE_POSITIONS, HoareAnnotationPositions.All);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_INTERPOLATED_LOCS, InterpolationTechnique.Craig_NestedInterpolation);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_INTERPOLANT_AUTOMATON, InterpolantAutomaton.CANONICAL);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_DUMPAUTOMATA, false);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_AUTOMATAFORMAT, Format.ATS);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_DUMPPATH, "label");
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_DUMP_ONLY_REUSE, false);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_INTERPOLANT_AUTOMATON_ENHANCEMENT, InterpolantAutomatonEnhancement.NONE);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_HOARE_TRIPLE_CHECKS, HoareTripleChecks.INCREMENTAL);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_MINIMIZE, Minimization.DELAYED_SIMULATION);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_CONCURRENCY, Concurrency.FINITE_AUTOMATA);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_USERLIMIT_TRACE_HISTOGRAM, 100);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_USERLIMIT_TIME, 1000);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_USERLIMIT_PATH_PROGRAM, 1000);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_COMPUTE_INTERPOLANT_SEQUENCE_STATISTICS, false);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_ABSINT_MODE, AbstractInterpretationMode.USE_CANONICAL);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_SIMPLIFICATION_TECHNIQUE, SimplificationTechnique.SIMPLIFY_BDD_FIRST_ORDER);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_XNF_CONVERSION_TECHNIQUE, XnfConversionTechnique.BDD_BASED);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_REFINEMENT_STRATEGY, RefinementStrategy.CAMEL);
		taPref.put(RcfgPreferenceInitializer.LABEL_SOLVER, SolverMode.Internal_SMTInterpol);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_REFINEMENT_STRATEGY_EXCEPTION_BLACKLIST, 
				RefinementStrategyExceptionBlacklist.NONE);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_ASSERT_CODEBLOCKS_INCREMENTALLY, 
				AssertCodeBlockOrder.INSIDE_LOOP_FIRST1);
		
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_UNSAT_CORES, UnsatCores.IGNORE);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_LIVE_VARIABLES, false);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_ABSTRACT_INTERPRETATION_FOR_PATH_INVARIANTS, false);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_INTERPOLANTS_CONSOLIDATION, false);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_NONLINEAR_CONSTRAINTS_IN_PATHINVARIANTS, false);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_UNSAT_CORES_IN_PATHINVARIANTS, false);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_WEAKEST_PRECONDITION_IN_PATHINVARIANTS, false);
	    taPref.put(TraceAbstractionPreferenceInitializer.LABEL_COMPUTE_COUNTEREXAMPLE, false);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_USE_PREDICATE_TRIE_BASED_PREDICATE_UNIFIER, false);
		
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Setting up the BuchiAutomizer Preferences
		RcpPreferenceProvider buchiPref = new RcpPreferenceProvider(BuchiAutomizer.class.getPackage().getName());
		
		buchiPref.put("Rank analysis", AnalysisType.LINEAR);
		buchiPref.put("GNTA analysis", AnalysisType.LINEAR);
		buchiPref.put("Number of GNTA directions", 10);
		buchiPref.put("Template benchmark mode", false);
		buchiPref.put("Try to simplify termination arguments", false);
		buchiPref.put("Try twofold refinement", false);
		
		PreferenceLayer taLayer = new PreferenceLayer(taPref, this.getClass());
		PreferenceLayer buchiLayer = new PreferenceLayer(buchiPref, this.getClass());
		
		serviceProvider.addPreferenceProvider(taLayer, TraceAbstraction.class.getPackage().getName());
		serviceProvider.addPreferenceProvider(buchiLayer, BuchiAutomizer.class.getPackage().getName());

		TraceGlobalVariables globalVars = new TraceGlobalVariables(serviceProvider);
		
		ManagedScript mScript = globalVars.getManagedScript();
		VariableFactory vf = globalVars.getVariableFactory();
		
		Set<String> procs = new HashSet<>();
		procs.add("l1");
		procs.add("l2");
		
		TraceToInterpolants tti = new TraceToInterpolants(mScript, serviceProvider, vf.getSymbolTable(), procs);
		
		PredicateFactory oldPredicateFactory = globalVars.getPredicateFactory();
		
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
		
		HoareAnnotation node1 = oldPredicateFactory.getNewHoareAnnotation(l1, tti.getModifiableGlobalsTable());
		HoareAnnotation node2 = oldPredicateFactory.getNewHoareAnnotation(l2, tti.getModifiableGlobalsTable());
			
		NestedWord<IcfgInternalTransition> stemWord = new NestedWord<>(e1, NestedWord.INTERNAL_POSITION);
		ArrayList<IPredicate> stemStates = new ArrayList<>();
		stemStates.add(node1);
		stemStates.add(node2);
		
		NestedWord<IcfgInternalTransition> loopWord = new NestedWord<>(e2, NestedWord.INTERNAL_POSITION);
		ArrayList<IPredicate> loopStates = new ArrayList<>();
		loopStates.add(node2);
		loopStates.add(node2);
		
		NestedRun<IcfgInternalTransition, IPredicate> stem = new NestedRun<>(stemWord, stemStates);
		
		NestedRun<IcfgInternalTransition, IPredicate> loop = new NestedRun<>(loopWord, loopStates);
		
		NestedLassoRun<IcfgInternalTransition, IPredicate> counterexample = new NestedLassoRun<>(stem, loop);
		
		/////////////////////////////////////////////////////////////////////////////////////
		
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
		
		System.out.println(check.getNonTerminationArgument());
	}

	//@Test
	public void testPaperExample() throws Exception {
		CustomServiceProvider serviceProvider = new CustomServiceProvider(LogLevel.OFF);
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Setting up the TAPreferences
		RcpPreferenceProvider taPref = new RcpPreferenceProvider(TraceAbstraction.class.getPackage().getName());
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_INTERPROCEDUTAL, false);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_USERLIMIT_ITERATIONS, 100);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_WATCHITERATION, 100);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_ARTIFACT, Artifact.INTERPOLANT_AUTOMATON);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_HOARE, false);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_HOARE_POSITIONS, HoareAnnotationPositions.All);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_INTERPOLATED_LOCS, InterpolationTechnique.Craig_NestedInterpolation);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_INTERPOLANT_AUTOMATON, InterpolantAutomaton.CANONICAL);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_DUMPAUTOMATA, false);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_AUTOMATAFORMAT, Format.ATS);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_DUMPPATH, "label");
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_DUMP_ONLY_REUSE, false);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_INTERPOLANT_AUTOMATON_ENHANCEMENT, InterpolantAutomatonEnhancement.NONE);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_HOARE_TRIPLE_CHECKS, HoareTripleChecks.INCREMENTAL);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_MINIMIZE, Minimization.DELAYED_SIMULATION);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_CONCURRENCY, Concurrency.FINITE_AUTOMATA);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_USERLIMIT_TRACE_HISTOGRAM, 100);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_USERLIMIT_TIME, 1000);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_USERLIMIT_PATH_PROGRAM, 1000);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_COMPUTE_INTERPOLANT_SEQUENCE_STATISTICS, false);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_ABSINT_MODE, AbstractInterpretationMode.USE_CANONICAL);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_SIMPLIFICATION_TECHNIQUE, SimplificationTechnique.SIMPLIFY_BDD_FIRST_ORDER);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_XNF_CONVERSION_TECHNIQUE, XnfConversionTechnique.BDD_BASED);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_REFINEMENT_STRATEGY, RefinementStrategy.CAMEL);
		taPref.put(RcfgPreferenceInitializer.LABEL_SOLVER, SolverMode.Internal_SMTInterpol);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_REFINEMENT_STRATEGY_EXCEPTION_BLACKLIST, 
				RefinementStrategyExceptionBlacklist.NONE);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_ASSERT_CODEBLOCKS_INCREMENTALLY, 
				AssertCodeBlockOrder.INSIDE_LOOP_FIRST1);
		
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_UNSAT_CORES, UnsatCores.IGNORE);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_LIVE_VARIABLES, false);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_ABSTRACT_INTERPRETATION_FOR_PATH_INVARIANTS, false);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_INTERPOLANTS_CONSOLIDATION, false);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_NONLINEAR_CONSTRAINTS_IN_PATHINVARIANTS, false);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_UNSAT_CORES_IN_PATHINVARIANTS, false);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_WEAKEST_PRECONDITION_IN_PATHINVARIANTS, false);
	    taPref.put(TraceAbstractionPreferenceInitializer.LABEL_COMPUTE_COUNTEREXAMPLE, false);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_USE_PREDICATE_TRIE_BASED_PREDICATE_UNIFIER, false);
		
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Setting up the BuchiAutomizer Preferences
		RcpPreferenceProvider buchiPref = new RcpPreferenceProvider(BuchiAutomizer.class.getPackage().getName());
		
		buchiPref.put("Rank analysis", AnalysisType.LINEAR);
		buchiPref.put("GNTA analysis", AnalysisType.LINEAR);
		buchiPref.put("Number of GNTA directions", 10);
		buchiPref.put("Template benchmark mode", false);
		buchiPref.put("Try to simplify termination arguments", false);
		buchiPref.put("Try twofold refinement", false);
		
		PreferenceLayer taLayer = new PreferenceLayer(taPref, this.getClass());
		PreferenceLayer buchiLayer = new PreferenceLayer(buchiPref, this.getClass());
		
		serviceProvider.addPreferenceProvider(taLayer, TraceAbstraction.class.getPackage().getName());
		serviceProvider.addPreferenceProvider(buchiLayer, BuchiAutomizer.class.getPackage().getName());

		TraceGlobalVariables globalVars = new TraceGlobalVariables(serviceProvider);
		
		ILogger logger = new ConsoleLogger(LogLevel.ERROR);
		
		ManagedScript mScript = globalVars.getManagedScript();
		VariableFactory vf = globalVars.getVariableFactory();
		
		Set<String> procs = new HashSet<>();
		procs.add("p1");
	
		TraceToInterpolants tti = new TraceToInterpolants(mScript, serviceProvider, vf.getSymbolTable(), procs);
		
		PredicateFactory oldPredicateFactory = globalVars.getPredicateFactory();
		
		/////////////////////////////////////////////////////////////////////////////////////
		// Build the ICFG
		/////////////////////////////////////////////////////////////////////////////////////
		Script script = globalVars.getManagedScript().getScript();

		BoogieNonOldVar i = vf.constructVariable("i", VariableFactory.INT);
		BoogieNonOldVar j = vf.constructVariable("j", VariableFactory.INT);

		ScriptAssumptionStatement igz = new ScriptAssumptionStatement(i, script.numeral("0"), ">", mScript, vf.getSymbolTable());
		IStatement jeo = new ScriptAssignmentStatement(j, script.numeral("1"),
				mScript, vf.getSymbolTable());
		ScriptAssumptionStatement jli = new ScriptAssumptionStatement(j, i.getTerm(), "<", mScript, vf.getSymbolTable());
		IStatement jpp = new ScriptAssignmentStatement(j, script.term("+", j.getTerm(), script.numeral("1")), mScript, vf.getSymbolTable());
		
		IcfgEdgeFactory edgeFactory = new IcfgEdgeFactory(new SerialProvider());
		
		BasicIcfg<IcfgLocation> icfg = new BasicIcfg<>("test", tti.getCfgSmtToolkit(), IcfgLocation.class);
		IcfgLocation l1 = new IcfgLocation(new StringDebugIdentifier("p1l1"), "p1");
		IcfgLocation l2 = new IcfgLocation(new StringDebugIdentifier("p1l2"), "p1");
		IcfgLocation l3 = new IcfgLocation(new StringDebugIdentifier("p1l3"), "p1");
		IcfgLocation l4 = new IcfgLocation(new StringDebugIdentifier("p1l4"), "p1");
		
		icfg.addLocation(l1, true, false, true, false, false);
		icfg.addLocation(l2, false, false, false, false, false);
		icfg.addLocation(l3, false, false, false, false, true);
		icfg.addLocation(l4, false, false, false, false, false);
		
		IcfgInternalTransition e1 = edgeFactory.createInternalTransition(l1, l2, new Payload(), igz.getTransFormula(false));
		IcfgInternalTransition e2 = edgeFactory.createInternalTransition(l2, l3, new Payload(), jeo.getTransFormula());
		IcfgInternalTransition e3 = edgeFactory.createInternalTransition(l3, l4, new Payload(), jli.getTransFormula(false));
		IcfgInternalTransition e4 = edgeFactory.createInternalTransition(l4, l3, new Payload(), jpp.getTransFormula());
		
		HoareAnnotation node1 = oldPredicateFactory.getNewHoareAnnotation(l1, tti.getModifiableGlobalsTable());
		HoareAnnotation node2 = oldPredicateFactory.getNewHoareAnnotation(l2, tti.getModifiableGlobalsTable());
		HoareAnnotation node3 = oldPredicateFactory.getNewHoareAnnotation(l3, tti.getModifiableGlobalsTable());
		HoareAnnotation node4 = oldPredicateFactory.getNewHoareAnnotation(l4, tti.getModifiableGlobalsTable());
		
		IcfgInternalTransition[] stemLetters = new IcfgInternalTransition[2];
		int[] stemNestingRelation = new int[2];
		stemLetters[0] = e1;
		stemLetters[1] = e2;
		stemNestingRelation[0] = NestedWord.INTERNAL_POSITION;
		stemNestingRelation[1] = NestedWord.INTERNAL_POSITION;
		
		NestedWord<IcfgInternalTransition> stemWord = new NestedWord<>(stemLetters, stemNestingRelation);
		ArrayList<IPredicate> stemStates = new ArrayList<>();
		stemStates.add(node1);
		stemStates.add(node2);
		stemStates.add(node3);
		
		IcfgInternalTransition[] loopLetters = new IcfgInternalTransition[2];
		int[] loopNestingRelation = new int[2];
		loopLetters[0] = e3;
		loopLetters[1] = e4;
		loopNestingRelation[0] = NestedWord.INTERNAL_POSITION;
		loopNestingRelation[1] = NestedWord.INTERNAL_POSITION;
		
		NestedWord<IcfgInternalTransition> loopWord = new NestedWord<>(loopLetters, loopNestingRelation);
		ArrayList<IPredicate> loopStates = new ArrayList<>();
		loopStates.add(node3);
		loopStates.add(node4);
		loopStates.add(node3);
		
		NestedRun<IcfgInternalTransition, IPredicate> stem = new NestedRun<>(stemWord, stemStates);
		
		NestedRun<IcfgInternalTransition, IPredicate> loop = new NestedRun<>(loopWord, loopStates);
		
		NestedLassoRun<IcfgInternalTransition, IPredicate> counterexample = new NestedLassoRun<>(stem, loop);
		
		/////////////////////////////////////////////////////////////////////////////////////
		
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
		
		PathProgramCache<IcfgInternalTransition> pathProgramCache = new PathProgramCache<>(logger);
		BuchiCegarLoopBenchmarkGenerator benchmarker = new BuchiCegarLoopBenchmarkGenerator();
		
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
		
		System.out.println(omega);
	
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
		
		System.out.println(omega);
	}

	//@Test
	public void testNoInequalities() throws Exception{
		CustomServiceProvider serviceProvider = new CustomServiceProvider(LogLevel.OFF);
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Setting up the TAPreferences
		RcpPreferenceProvider taPref = new RcpPreferenceProvider(TraceAbstraction.class.getPackage().getName());
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_INTERPROCEDUTAL, false);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_USERLIMIT_ITERATIONS, 100);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_WATCHITERATION, 100);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_ARTIFACT, Artifact.INTERPOLANT_AUTOMATON);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_HOARE, false);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_HOARE_POSITIONS, HoareAnnotationPositions.All);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_INTERPOLATED_LOCS, InterpolationTechnique.Craig_NestedInterpolation);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_INTERPOLANT_AUTOMATON, InterpolantAutomaton.CANONICAL);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_DUMPAUTOMATA, false);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_AUTOMATAFORMAT, Format.ATS);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_DUMPPATH, "label");
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_DUMP_ONLY_REUSE, false);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_INTERPOLANT_AUTOMATON_ENHANCEMENT, InterpolantAutomatonEnhancement.NONE);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_HOARE_TRIPLE_CHECKS, HoareTripleChecks.INCREMENTAL);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_MINIMIZE, Minimization.DELAYED_SIMULATION);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_CONCURRENCY, Concurrency.FINITE_AUTOMATA);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_USERLIMIT_TRACE_HISTOGRAM, 100);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_USERLIMIT_TIME, 1000);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_USERLIMIT_PATH_PROGRAM, 1000);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_COMPUTE_INTERPOLANT_SEQUENCE_STATISTICS, false);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_ABSINT_MODE, AbstractInterpretationMode.USE_CANONICAL);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_SIMPLIFICATION_TECHNIQUE, SimplificationTechnique.SIMPLIFY_BDD_FIRST_ORDER);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_XNF_CONVERSION_TECHNIQUE, XnfConversionTechnique.BDD_BASED);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_REFINEMENT_STRATEGY, RefinementStrategy.CAMEL);
		taPref.put(RcfgPreferenceInitializer.LABEL_SOLVER, SolverMode.Internal_SMTInterpol);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_REFINEMENT_STRATEGY_EXCEPTION_BLACKLIST, 
				RefinementStrategyExceptionBlacklist.NONE);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_ASSERT_CODEBLOCKS_INCREMENTALLY, 
				AssertCodeBlockOrder.INSIDE_LOOP_FIRST1);
		
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_UNSAT_CORES, UnsatCores.IGNORE);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_LIVE_VARIABLES, false);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_ABSTRACT_INTERPRETATION_FOR_PATH_INVARIANTS, false);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_INTERPOLANTS_CONSOLIDATION, false);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_NONLINEAR_CONSTRAINTS_IN_PATHINVARIANTS, false);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_UNSAT_CORES_IN_PATHINVARIANTS, false);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_WEAKEST_PRECONDITION_IN_PATHINVARIANTS, false);
	    taPref.put(TraceAbstractionPreferenceInitializer.LABEL_COMPUTE_COUNTEREXAMPLE, false);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_USE_PREDICATE_TRIE_BASED_PREDICATE_UNIFIER, false);
		
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Setting up the BuchiAutomizer Preferences
		RcpPreferenceProvider buchiPref = new RcpPreferenceProvider(BuchiAutomizer.class.getPackage().getName());
		
		buchiPref.put("Rank analysis", AnalysisType.LINEAR);
		buchiPref.put("GNTA analysis", AnalysisType.LINEAR);
		buchiPref.put("Number of GNTA directions", 10);
		buchiPref.put("Template benchmark mode", false);
		buchiPref.put("Try to simplify termination arguments", false);
		buchiPref.put("Try twofold refinement", false);
		
		PreferenceLayer taLayer = new PreferenceLayer(taPref, this.getClass());
		PreferenceLayer buchiLayer = new PreferenceLayer(buchiPref, this.getClass());
		
		serviceProvider.addPreferenceProvider(taLayer, TraceAbstraction.class.getPackage().getName());
		serviceProvider.addPreferenceProvider(buchiLayer, BuchiAutomizer.class.getPackage().getName());

		TraceGlobalVariables globalVars = new TraceGlobalVariables(serviceProvider);
		
		ManagedScript mScript = globalVars.getManagedScript();
		VariableFactory vf = globalVars.getVariableFactory();
		
		Set<String> procs = new HashSet<>();
		procs.add("p1");
	
		TraceToInterpolants tti = new TraceToInterpolants(mScript, serviceProvider, vf.getSymbolTable(), procs);
		
		PredicateFactory oldPredicateFactory = globalVars.getPredicateFactory();
		
		/////////////////////////////////////////////////////////////////////////////////////
		// Build the ICFG
		/////////////////////////////////////////////////////////////////////////////////////
		Script script = globalVars.getManagedScript().getScript();

		BoogieNonOldVar i = vf.constructVariable("i", VariableFactory.INT);
		
		IStatement iez = new ScriptAssignmentStatement(i, script.numeral("0"), mScript, vf.getSymbolTable());
		ScriptAssumptionStatement ieez = new ScriptAssumptionStatement(i, script.numeral("0"), "=", mScript, vf.getSymbolTable());
		IStatement ipp = new ScriptAssignmentStatement(i, script.term("+", i.getTerm(), script.numeral("1")), mScript, vf.getSymbolTable());
		
		IcfgEdgeFactory edgeFactory = new IcfgEdgeFactory(new SerialProvider());
		
		BasicIcfg<IcfgLocation> icfg = new BasicIcfg<>("test", tti.getCfgSmtToolkit(), IcfgLocation.class);
		IcfgLocation l1 = new IcfgLocation(new StringDebugIdentifier("p1l1"), "p1");
		IcfgLocation l2 = new IcfgLocation(new StringDebugIdentifier("p1l2"), "p1");
		IcfgLocation l3 = new IcfgLocation(new StringDebugIdentifier("p1l3"), "p1");
		
		icfg.addLocation(l1, true, false, true, false, false);
		icfg.addLocation(l2, false, false, false, false, true);
		icfg.addLocation(l3, false, false, false, false, false);
		
		IcfgInternalTransition e1 = edgeFactory.createInternalTransition(l1, l2, new Payload(), iez.getTransFormula());
		IcfgInternalTransition e2 = edgeFactory.createInternalTransition(l2, l3, new Payload(), ieez.getTransFormula(false));
		IcfgInternalTransition e3 = edgeFactory.createInternalTransition(l3, l2, new Payload(), ipp.getTransFormula());
		
		HoareAnnotation node1 = oldPredicateFactory.getNewHoareAnnotation(l1, tti.getModifiableGlobalsTable());
		HoareAnnotation node2 = oldPredicateFactory.getNewHoareAnnotation(l2, tti.getModifiableGlobalsTable());
		HoareAnnotation node3 = oldPredicateFactory.getNewHoareAnnotation(l2, tti.getModifiableGlobalsTable());
		
		NestedWord<IcfgInternalTransition> stemWord = new NestedWord<>(e1, NestedWord.INTERNAL_POSITION);
		ArrayList<IPredicate> stemStates = new ArrayList<>();
		stemStates.add(node1);
		stemStates.add(node2);
		
		IcfgInternalTransition[] loopLetters = new IcfgInternalTransition[2];
		int[] loopNestingRelation = new int[2];
		loopLetters[0] = e2;
		loopLetters[1] = e3;
		loopNestingRelation[0] = NestedWord.INTERNAL_POSITION;
		loopNestingRelation[1] = NestedWord.INTERNAL_POSITION;
		
		NestedWord<IcfgInternalTransition> loopWord = new NestedWord<>(loopLetters, loopNestingRelation);
		ArrayList<IPredicate> loopStates = new ArrayList<>();
		loopStates.add(node2);
		loopStates.add(node3);
		loopStates.add(node2);
		
		NestedRun<IcfgInternalTransition, IPredicate> stem = new NestedRun<>(stemWord, stemStates);
		
		NestedRun<IcfgInternalTransition, IPredicate> loop = new NestedRun<>(loopWord, loopStates);
		
		NestedLassoRun<IcfgInternalTransition, IPredicate> counterexample = new NestedLassoRun<>(stem, loop);
		
		/////////////////////////////////////////////////////////////////////////////////////
		
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
		
		System.out.println(omega);
	}

	//@Test
	public void testManualOmegaConstruction() throws Exception {
		CustomServiceProvider serviceProvider = new CustomServiceProvider(LogLevel.OFF);
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Setting up the TAPreferences
		RcpPreferenceProvider taPref = new RcpPreferenceProvider(TraceAbstraction.class.getPackage().getName());
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_INTERPROCEDUTAL, false);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_USERLIMIT_ITERATIONS, 100);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_WATCHITERATION, 100);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_ARTIFACT, Artifact.INTERPOLANT_AUTOMATON);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_HOARE, false);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_HOARE_POSITIONS, HoareAnnotationPositions.All);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_INTERPOLATED_LOCS, InterpolationTechnique.Craig_NestedInterpolation);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_INTERPOLANT_AUTOMATON, InterpolantAutomaton.CANONICAL);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_DUMPAUTOMATA, false);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_AUTOMATAFORMAT, Format.ATS);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_DUMPPATH, "label");
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_DUMP_ONLY_REUSE, false);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_INTERPOLANT_AUTOMATON_ENHANCEMENT, InterpolantAutomatonEnhancement.NONE);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_HOARE_TRIPLE_CHECKS, HoareTripleChecks.INCREMENTAL);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_MINIMIZE, Minimization.DELAYED_SIMULATION);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_CONCURRENCY, Concurrency.FINITE_AUTOMATA);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_USERLIMIT_TRACE_HISTOGRAM, 100);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_USERLIMIT_TIME, 1000);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_USERLIMIT_PATH_PROGRAM, 1000);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_COMPUTE_INTERPOLANT_SEQUENCE_STATISTICS, false);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_ABSINT_MODE, AbstractInterpretationMode.USE_CANONICAL);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_SIMPLIFICATION_TECHNIQUE, SimplificationTechnique.SIMPLIFY_BDD_FIRST_ORDER);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_XNF_CONVERSION_TECHNIQUE, XnfConversionTechnique.BDD_BASED);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_REFINEMENT_STRATEGY, RefinementStrategy.TAIPAN);
		taPref.put(RcfgPreferenceInitializer.LABEL_SOLVER, SolverMode.Internal_SMTInterpol);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_REFINEMENT_STRATEGY_EXCEPTION_BLACKLIST, 
				RefinementStrategyExceptionBlacklist.NONE);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_ASSERT_CODEBLOCKS_INCREMENTALLY, 
				AssertCodeBlockOrder.INSIDE_LOOP_FIRST1);
		
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_UNSAT_CORES, UnsatCores.IGNORE);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_LIVE_VARIABLES, false);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_ABSTRACT_INTERPRETATION_FOR_PATH_INVARIANTS, false);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_INTERPOLANTS_CONSOLIDATION, false);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_NONLINEAR_CONSTRAINTS_IN_PATHINVARIANTS, false);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_UNSAT_CORES_IN_PATHINVARIANTS, false);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_WEAKEST_PRECONDITION_IN_PATHINVARIANTS, false);
	    taPref.put(TraceAbstractionPreferenceInitializer.LABEL_COMPUTE_COUNTEREXAMPLE, false);
		taPref.put(TraceAbstractionPreferenceInitializer.LABEL_USE_PREDICATE_TRIE_BASED_PREDICATE_UNIFIER, false);
		
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Setting up the BuchiAutomizer Preferences
		RcpPreferenceProvider buchiPref = new RcpPreferenceProvider(BuchiAutomizer.class.getPackage().getName());
		
		buchiPref.put("Rank analysis", AnalysisType.LINEAR);
		buchiPref.put("GNTA analysis", AnalysisType.LINEAR);
		buchiPref.put("Number of GNTA directions", 10);
		buchiPref.put("Template benchmark mode", false);
		buchiPref.put("Try to simplify termination arguments", false);
		buchiPref.put("Try twofold refinement", false);
		
		PreferenceLayer taLayer = new PreferenceLayer(taPref, this.getClass());
		PreferenceLayer buchiLayer = new PreferenceLayer(buchiPref, this.getClass());
		
		serviceProvider.addPreferenceProvider(taLayer, TraceAbstraction.class.getPackage().getName());
		serviceProvider.addPreferenceProvider(buchiLayer, BuchiAutomizer.class.getPackage().getName());

		TraceGlobalVariables globalVars = new TraceGlobalVariables(serviceProvider);
		
		ILogger logger = new ConsoleLogger(LogLevel.ERROR);
		
		ManagedScript mScript = globalVars.getManagedScript();
		VariableFactory vf = globalVars.getVariableFactory();
		
		Set<String> procs = new HashSet<>();
		procs.add("p1");
	
		TraceToInterpolants tti = new TraceToInterpolants(mScript, serviceProvider, vf.getSymbolTable(), procs);
		
		PredicateFactory oldPredicateFactory = globalVars.getPredicateFactory();
		
		/////////////////////////////////////////////////////////////////////////////////////
		// Build the ICFG
		/////////////////////////////////////////////////////////////////////////////////////
		Script script = globalVars.getManagedScript().getScript();

		BoogieNonOldVar i = vf.constructVariable("i", VariableFactory.INT);
		BoogieNonOldVar j = vf.constructVariable("j", VariableFactory.INT);

		ScriptAssumptionStatement igz = new ScriptAssumptionStatement(i, script.numeral("0"), ">", mScript, vf.getSymbolTable());
		IStatement jeo = new ScriptAssignmentStatement(j, script.numeral("1"),
				mScript, vf.getSymbolTable());
		ScriptAssumptionStatement jli = new ScriptAssumptionStatement(j, i.getTerm(), "<", mScript, vf.getSymbolTable());
		IStatement jpp = new ScriptAssignmentStatement(j, script.term("+", j.getTerm(), script.numeral("1")), mScript, vf.getSymbolTable());
		
		IcfgEdgeFactory edgeFactory = new IcfgEdgeFactory(new SerialProvider());
		
		BasicIcfg<IcfgLocation> icfg = new BasicIcfg<>("test", tti.getCfgSmtToolkit(), IcfgLocation.class);
		IcfgLocation l1 = new IcfgLocation(new StringDebugIdentifier("p1l1"), "p1");
		IcfgLocation l2 = new IcfgLocation(new StringDebugIdentifier("p1l2"), "p1");
		IcfgLocation l3 = new IcfgLocation(new StringDebugIdentifier("p1l3"), "p1");
		IcfgLocation l4 = new IcfgLocation(new StringDebugIdentifier("p1l4"), "p1");
		
		icfg.addLocation(l1, true, false, true, false, false);
		icfg.addLocation(l2, false, false, false, false, false);
		icfg.addLocation(l3, false, false, false, false, true);
		icfg.addLocation(l4, false, false, false, false, false);
		
		IcfgInternalTransition e1 = edgeFactory.createInternalTransition(l1, l2, new Payload(), igz.getTransFormula(false));
		IcfgInternalTransition e2 = edgeFactory.createInternalTransition(l2, l3, new Payload(), jeo.getTransFormula());
		IcfgInternalTransition e3 = edgeFactory.createInternalTransition(l3, l4, new Payload(), jli.getTransFormula(false));
		IcfgInternalTransition e4 = edgeFactory.createInternalTransition(l4, l3, new Payload(), jpp.getTransFormula());
		
		HoareAnnotation node1 = oldPredicateFactory.getNewHoareAnnotation(l1, tti.getModifiableGlobalsTable());
		HoareAnnotation node2 = oldPredicateFactory.getNewHoareAnnotation(l2, tti.getModifiableGlobalsTable());
		HoareAnnotation node3 = oldPredicateFactory.getNewHoareAnnotation(l3, tti.getModifiableGlobalsTable());
		HoareAnnotation node4 = oldPredicateFactory.getNewHoareAnnotation(l4, tti.getModifiableGlobalsTable());
		
		IcfgInternalTransition[] stemLetters = new IcfgInternalTransition[2];
		int[] stemNestingRelation = new int[2];
		stemLetters[0] = e1;
		stemLetters[1] = e2;
		stemNestingRelation[0] = NestedWord.INTERNAL_POSITION;
		stemNestingRelation[1] = NestedWord.INTERNAL_POSITION;
		
		NestedWord<IcfgInternalTransition> stemWord = new NestedWord<>(stemLetters, stemNestingRelation);
		ArrayList<IPredicate> stemStates = new ArrayList<>();
		stemStates.add(node1);
		stemStates.add(node2);
		stemStates.add(node3);
		
		IcfgInternalTransition[] loopLetters = new IcfgInternalTransition[2];
		int[] loopNestingRelation = new int[2];
		loopLetters[0] = e3;
		loopLetters[1] = e4;
		loopNestingRelation[0] = NestedWord.INTERNAL_POSITION;
		loopNestingRelation[1] = NestedWord.INTERNAL_POSITION;
		
		NestedWord<IcfgInternalTransition> loopWord = new NestedWord<>(loopLetters, loopNestingRelation);
		ArrayList<IPredicate> loopStates = new ArrayList<>();
		loopStates.add(node3);
		loopStates.add(node4);
		loopStates.add(node3);
		
		NestedRun<IcfgInternalTransition, IPredicate> stem = new NestedRun<>(stemWord, stemStates);
		
		NestedRun<IcfgInternalTransition, IPredicate> loop = new NestedRun<>(loopWord, loopStates);
		
		NestedLassoRun<IcfgInternalTransition, IPredicate> counterexample = new NestedLassoRun<>(stem, loop);
		
		/////////////////////////////////////////////////////////////////////////////////////
		
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
		
		PathProgramCache<IcfgInternalTransition> pathProgramCache = new PathProgramCache<>(logger);
		BuchiCegarLoopBenchmarkGenerator benchmarker = new BuchiCegarLoopBenchmarkGenerator();
		
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
		
		/*INestedWordAutomaton<IcfgInternalTransition, IPredicate> omega = CFG2NestedWordAutomaton.constructAutomatonWithSPredicates(
				serviceProvider, icfg, stateFactory, allNodes,
				taPrefs.interprocedural(), predicateFactory);
		*/
		
		//////////////////////////////////////////////////////////////////////////////////////////////////////
		// Omega Construction
		AutomataLibraryServices autLib = new AutomataLibraryServices(serviceProvider);
		
		Set<IcfgInternalTransition> letters = new HashSet<>();
		letters.add(e1);
		letters.add(e2);
		letters.add(e3);
		letters.add(e4);
		VpAlphabet<IcfgInternalTransition> alphabet = new VpAlphabet<>(letters);
		
		NestedWordAutomaton<IcfgInternalTransition, IPredicate> omega = new NestedWordAutomaton<>(autLib, alphabet, 
				new PDeterminizeStateFactory(predicateFactory));
		omega.addState(true, false, node1);
		omega.addState(false, false, node2);
		omega.addState(false, true, node3);
		omega.addState(false, false, node4);
		omega.addInternalTransition(node1, e1, node2);
		omega.addInternalTransition(node2, e2, node3);
		omega.addInternalTransition(node3, e3, node4);
		omega.addInternalTransition(node4, e4, node3);
		
		//////////////////////////////////////////////////////////////////////////////////////////////////////
		
		System.out.println(omega);
	
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
		
		TerminationArgument termArg = bspm.getTerminationArgument();
		System.out.println(termArg.getRankingFunction());
	}
	
	@SuppressWarnings("rawtypes")
	@Test
	public void test5() throws Exception{
		CustomServiceProvider serviceProvider = new CustomServiceProvider(LogLevel.OFF);
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Setting up the TAPreferences
		
		RcpPreferenceProvider taPref = new RcpPreferenceProvider(TraceAbstraction.class.getPackage().getName());
		taPref.put(InitWrapper.LABEL_INTERPROCEDUTAL, InitWrapper.DEF_INTERPROCEDUTAL);
		taPref.put(InitWrapper.LABEL_USERLIMIT_ITERATIONS, 1_000_000);
		taPref.put(InitWrapper.LABEL_WATCHITERATION, 100);
		taPref.put(InitWrapper.LABEL_ARTIFACT, Artifact.INTERPOLANT_AUTOMATON);
		taPref.put(InitWrapper.LABEL_HOARE, false);
		taPref.put(InitWrapper.LABEL_HOARE_POSITIONS, HoareAnnotationPositions.All);
		taPref.put(InitWrapper.LABEL_INTERPOLATED_LOCS, InterpolationTechnique.Craig_NestedInterpolation);
		taPref.put(InitWrapper.LABEL_INTERPOLANT_AUTOMATON, InterpolantAutomaton.CANONICAL);
		taPref.put(InitWrapper.LABEL_DUMPAUTOMATA, false);
		taPref.put(InitWrapper.LABEL_AUTOMATAFORMAT, Format.ATS);
		taPref.put(InitWrapper.LABEL_DUMPPATH, "label");
		taPref.put(InitWrapper.LABEL_DUMP_ONLY_REUSE, false);
		taPref.put(InitWrapper.LABEL_INTERPOLANT_AUTOMATON_ENHANCEMENT, InterpolantAutomatonEnhancement.PREDICATE_ABSTRACTION);
		taPref.put(InitWrapper.LABEL_HOARE_TRIPLE_CHECKS, HoareTripleChecks.MONOLITHIC);
		taPref.put(InitWrapper.LABEL_MINIMIZE, Minimization.FAIR_DIRECT_SIMULATION);
		taPref.put(InitWrapper.LABEL_CONCURRENCY, Concurrency.FINITE_AUTOMATA);
		taPref.put(InitWrapper.LABEL_USERLIMIT_TRACE_HISTOGRAM, 100);
		taPref.put(InitWrapper.LABEL_USERLIMIT_TIME, 1000);
		taPref.put(InitWrapper.LABEL_USERLIMIT_PATH_PROGRAM, 1000);
		taPref.put(InitWrapper.LABEL_COMPUTE_INTERPOLANT_SEQUENCE_STATISTICS, false);
		taPref.put(InitWrapper.LABEL_ABSINT_MODE, AbstractInterpretationMode.USE_CANONICAL);
		taPref.put(InitWrapper.LABEL_SIMPLIFICATION_TECHNIQUE, SimplificationTechnique.SIMPLIFY_BDD_FIRST_ORDER);
		taPref.put(InitWrapper.LABEL_XNF_CONVERSION_TECHNIQUE, XnfConversionTechnique.BDD_BASED);
		taPref.put(InitWrapper.LABEL_REFINEMENT_STRATEGY, RefinementStrategy.MAMMOTH);
		taPref.put(RcfgPreferenceInitializer.LABEL_SOLVER, SolverMode.Internal_SMTInterpol);
		taPref.put(InitWrapper.LABEL_REFINEMENT_STRATEGY_EXCEPTION_BLACKLIST, 
				RefinementStrategyExceptionBlacklist.NONE);
		taPref.put(InitWrapper.LABEL_ASSERT_CODEBLOCKS_INCREMENTALLY, 
				AssertCodeBlockOrder.MIX_INSIDE_OUTSIDE);
		
		taPref.put(InitWrapper.LABEL_UNSAT_CORES, UnsatCores.IGNORE);
		taPref.put(InitWrapper.LABEL_LIVE_VARIABLES, false);
		taPref.put(InitWrapper.LABEL_ABSTRACT_INTERPRETATION_FOR_PATH_INVARIANTS, false);
		taPref.put(InitWrapper.LABEL_INTERPOLANTS_CONSOLIDATION, false);
		taPref.put(InitWrapper.LABEL_NONLINEAR_CONSTRAINTS_IN_PATHINVARIANTS, false);
		taPref.put(InitWrapper.LABEL_UNSAT_CORES_IN_PATHINVARIANTS, false);
		taPref.put(InitWrapper.LABEL_WEAKEST_PRECONDITION_IN_PATHINVARIANTS, false);
	    taPref.put(InitWrapper.LABEL_COMPUTE_COUNTEREXAMPLE, false);
		taPref.put(InitWrapper.LABEL_USE_PREDICATE_TRIE_BASED_PREDICATE_UNIFIER, false);
		
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Setting up the BuchiAutomizer Preferences
		RcpPreferenceProvider buchiPref = new RcpPreferenceProvider(BuchiAutomizer.class.getPackage().getName());
		
		buchiPref.put("Rank analysis", AnalysisType.LINEAR);
		buchiPref.put("GNTA analysis", AnalysisType.LINEAR);
		buchiPref.put("Number of GNTA directions", 10);
		buchiPref.put("Template benchmark mode", false);
		buchiPref.put("Try to simplify termination arguments", false);
		buchiPref.put("Try twofold refinement", true);
		
		PreferenceLayer taLayer = new PreferenceLayer(taPref, this.getClass());
		PreferenceLayer buchiLayer = new PreferenceLayer(buchiPref, this.getClass());
		
		serviceProvider.addPreferenceProvider(taLayer, TraceAbstraction.class.getPackage().getName());
		serviceProvider.addPreferenceProvider(buchiLayer, BuchiAutomizer.class.getPackage().getName());

		TraceGlobalVariables globalVars = new TraceGlobalVariables(serviceProvider);
		
		ILogger logger = new ConsoleLogger(LogLevel.ERROR);
		
		ManagedScript mScript = globalVars.getManagedScript();
		VariableFactory vf = globalVars.getVariableFactory();
		
		Set<String> procs = new HashSet<>();
		procs.add("p0");
	
		TraceToInterpolants tti = new TraceToInterpolants(mScript, serviceProvider, vf.getSymbolTable(), procs);
		
		PredicateFactory oldPredicateFactory = globalVars.getPredicateFactory();
		
		/////////////////////////////////////////////////////////////////////////////////////
		// Build the ICFG
		/////////////////////////////////////////////////////////////////////////////////////
		Script script = globalVars.getManagedScript().getScript();

		BoogieNonOldVar i = vf.constructVariable("i", VariableFactory.INT);
		BoogieNonOldVar j = vf.constructVariable("j", VariableFactory.INT);

		ScriptAssumptionStatement igz = new ScriptAssumptionStatement(i, script.numeral("0"), ">", mScript, vf.getSymbolTable());
		IStatement jeo = new ScriptAssignmentStatement(j, script.numeral("1"),
				mScript, vf.getSymbolTable());
		ScriptAssumptionStatement jli = new ScriptAssumptionStatement(j, i.getTerm(), "<", mScript, vf.getSymbolTable());
		IStatement jpp = new ScriptAssignmentStatement(j, script.term("+", j.getTerm(), script.numeral("1")), mScript, vf.getSymbolTable());
		
		IcfgEdgeFactory edgeFactory = new IcfgEdgeFactory(new SerialProvider());
		
		BasicIcfg<IcfgLocation> icfg = new BasicIcfg<>("test", tti.getCfgSmtToolkit(), IcfgLocation.class);
		IcfgLocation l1 = new IcfgLocation(new StringDebugIdentifier("0"), "p0");
		IcfgLocation l2 = new IcfgLocation(new StringDebugIdentifier("0"), "p0");
		IcfgLocation l3 = new IcfgLocation(new StringDebugIdentifier("0"), "p0");
		IcfgLocation l4 = new IcfgLocation(new StringDebugIdentifier("0"), "p0");
		
		icfg.addLocation(l1, true, false, true, false, false);
		icfg.addLocation(l2, false, false, false, false, false);
		icfg.addLocation(l3, false, false, false, false, true);
		icfg.addLocation(l4, false, false, false, false, false);
		
		IcfgInternalTransition e1 = edgeFactory.createInternalTransition(l1, l2, new Payload(), igz.getTransFormula(false));
		IcfgInternalTransition e2 = edgeFactory.createInternalTransition(l2, l3, new Payload(), jeo.getTransFormula());
		IcfgInternalTransition e3 = edgeFactory.createInternalTransition(l3, l4, new Payload(), jli.getTransFormula(false));
		IcfgInternalTransition e4 = edgeFactory.createInternalTransition(l4, l3, new Payload(), jpp.getTransFormula());
		
		HoareAnnotation node1 = oldPredicateFactory.getNewHoareAnnotation(l1, tti.getModifiableGlobalsTable());
		HoareAnnotation node2 = oldPredicateFactory.getNewHoareAnnotation(l2, tti.getModifiableGlobalsTable());
		HoareAnnotation node3 = oldPredicateFactory.getNewHoareAnnotation(l3, tti.getModifiableGlobalsTable());
		HoareAnnotation node4 = oldPredicateFactory.getNewHoareAnnotation(l4, tti.getModifiableGlobalsTable());
		
		IcfgInternalTransition[] stemLetters = new IcfgInternalTransition[2];
		int[] stemNestingRelation = new int[2];
		stemLetters[0] = e1;
		stemLetters[1] = e2;
		stemNestingRelation[0] = NestedWord.INTERNAL_POSITION;
		stemNestingRelation[1] = NestedWord.INTERNAL_POSITION;
		
		NestedWord<IcfgInternalTransition> stemWord = new NestedWord<>(stemLetters, stemNestingRelation);
		ArrayList<IPredicate> stemStates = new ArrayList<>();
		stemStates.add(node1);
		stemStates.add(node2);
		stemStates.add(node3);
		
		IcfgInternalTransition[] loopLetters = new IcfgInternalTransition[2];
		int[] loopNestingRelation = new int[2];
		loopLetters[0] = e3;
		loopLetters[1] = e4;
		loopNestingRelation[0] = NestedWord.INTERNAL_POSITION;
		loopNestingRelation[1] = NestedWord.INTERNAL_POSITION;
		
		NestedWord<IcfgInternalTransition> loopWord = new NestedWord<>(loopLetters, loopNestingRelation);
		ArrayList<IPredicate> loopStates = new ArrayList<>();
		loopStates.add(node3);
		loopStates.add(node4);
		loopStates.add(node3);
		
		NestedRun<IcfgInternalTransition, IPredicate> stem = new NestedRun<>(stemWord, stemStates);
		
		NestedRun<IcfgInternalTransition, IPredicate> loop = new NestedRun<>(loopWord, loopStates);
		
		NestedLassoRun<IcfgInternalTransition, IPredicate> counterexample = new NestedLassoRun<>(stem, loop);
		
		/////////////////////////////////////////////////////////////////////////////////////
		
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
		
		PathProgramCache<IcfgInternalTransition> pathProgramCache = new PathProgramCache<>(logger);
		BuchiCegarLoopBenchmarkGenerator benchmarker = new BuchiCegarLoopBenchmarkGenerator();
		
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
		
		/*INestedWordAutomaton<IcfgInternalTransition, IPredicate> omega = CFG2NestedWordAutomaton.constructAutomatonWithSPredicates(
				serviceProvider, icfg, stateFactory, allNodes,
				taPrefs.interprocedural(), predicateFactory);
		*/
		
		//////////////////////////////////////////////////////////////////////////////////////////////////////
		// Omega Construction
		AutomataLibraryServices autLib = new AutomataLibraryServices(serviceProvider);
		
		Set<IcfgInternalTransition> letters = new HashSet<>();
		letters.add(e1);
		letters.add(e2);
		letters.add(e3);
		letters.add(e4);
		VpAlphabet<IcfgInternalTransition> alphabet = new VpAlphabet<>(letters);
		
		NestedWordAutomaton<IcfgInternalTransition, IPredicate> omega = new NestedWordAutomaton<>(autLib, alphabet, 
				new PDeterminizeStateFactory(predicateFactory));
		omega.addState(true, false, node1);
		omega.addState(false, false, node2);
		omega.addState(false, true, node3);
		omega.addState(false, false, node4);
		omega.addInternalTransition(node1, e1, node2);
		omega.addInternalTransition(node2, e2, node3);
		omega.addInternalTransition(node3, e3, node4);
		omega.addInternalTransition(node4, e4, node3);
		
		//////////////////////////////////////////////////////////////////////////////////////////////////////
		
		String before = omega.toString();
	
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
		
		//TerminationArgument termArg = bspm.getTerminationArgument();
		//System.out.println(termArg.getRankingFunction());
		String after = omega.toString();
		
		System.out.println(after.equals(before));
	} 
}
