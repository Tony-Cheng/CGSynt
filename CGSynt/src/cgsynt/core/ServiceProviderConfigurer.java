package cgsynt.core;

import cgsynt.core.service.CustomServiceProvider;
import de.uni_freiburg.informatik.ultimate.automata.AutomatonDefinitionPrinter.Format;
import de.uni_freiburg.informatik.ultimate.core.coreplugin.services.PreferenceLayer;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger.LogLevel;
import de.uni_freiburg.informatik.ultimate.core.preferences.RcpPreferenceProvider;
import de.uni_freiburg.informatik.ultimate.lassoranker.AnalysisType;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.SmtUtils.SimplificationTechnique;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.SmtUtils.XnfConversionTechnique;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.SolverBuilder.SolverMode;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.tracecheck.ITraceCheckPreferences.AssertCodeBlockOrder;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.tracecheck.ITraceCheckPreferences.UnsatCores;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.tracecheck.TraceCheckReasonUnknown.RefinementStrategyExceptionBlacklist;
import de.uni_freiburg.informatik.ultimate.plugins.generator.buchiautomizer.BuchiAutomizer;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.preferences.RcfgPreferenceInitializer;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.TraceAbstraction;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.preferences.TraceAbstractionPreferenceInitializer;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.preferences.TAPreferences.Artifact;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.preferences.TAPreferences.Concurrency;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.preferences.TAPreferences.InterpolantAutomatonEnhancement;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.preferences.TraceAbstractionPreferenceInitializer.AbstractInterpretationMode;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.preferences.TraceAbstractionPreferenceInitializer.HoareAnnotationPositions;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.preferences.TraceAbstractionPreferenceInitializer.HoareTripleChecks;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.preferences.TraceAbstractionPreferenceInitializer.InterpolantAutomaton;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.preferences.TraceAbstractionPreferenceInitializer.InterpolationTechnique;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.preferences.TraceAbstractionPreferenceInitializer.Minimization;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.preferences.TraceAbstractionPreferenceInitializer.RefinementStrategy;

public class ServiceProviderConfigurer {
	public static IUltimateServiceProvider configureTerminationServiceProvider() {
		CustomServiceProvider serviceProvider = new CustomServiceProvider(LogLevel.OFF);
		
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
		
		RcpPreferenceProvider buchiPref = new RcpPreferenceProvider(BuchiAutomizer.class.getPackage().getName());
		
		buchiPref.put("Rank analysis", AnalysisType.LINEAR);
		buchiPref.put("GNTA analysis", AnalysisType.LINEAR);
		buchiPref.put("Number of GNTA directions", 10);
		buchiPref.put("Template benchmark mode", false);
		buchiPref.put("Try to simplify termination arguments", false);
		buchiPref.put("Try twofold refinement", false);
		
		PreferenceLayer taLayer = new PreferenceLayer(taPref, ServiceProviderConfigurer.class);
		PreferenceLayer buchiLayer = new PreferenceLayer(buchiPref, ServiceProviderConfigurer.class);
		
		serviceProvider.addPreferenceProvider(taLayer, TraceAbstraction.class.getPackage().getName());
		serviceProvider.addPreferenceProvider(buchiLayer, BuchiAutomizer.class.getPackage().getName());
		
		return serviceProvider;
	}
}
