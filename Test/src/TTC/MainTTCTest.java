package TTC;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWord;
import de.uni_freiburg.informatik.ultimate.boogie.ast.Unit;
import de.uni_freiburg.informatik.ultimate.boogie.parser.BoogieParser;
import de.uni_freiburg.informatik.ultimate.cdt.codan.CDTController;
import de.uni_freiburg.informatik.ultimate.cdt.codan.UltimateCChecker;
import de.uni_freiburg.informatik.ultimate.core.coreplugin.services.ToolchainStorage;
import de.uni_freiburg.informatik.ultimate.core.lib.models.WrapperNode;
import de.uni_freiburg.informatik.ultimate.core.model.models.IElement;
import de.uni_freiburg.informatik.ultimate.core.model.preferences.IPreferenceProvider;
import de.uni_freiburg.informatik.ultimate.core.model.services.IBacktranslationService;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger;
import de.uni_freiburg.informatik.ultimate.core.model.services.IProgressAwareTimer;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.logic.Logics;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Term;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretationv2.tool.AbstractInterpreter;
import de.uni_freiburg.informatik.ultimate.plugins.generator.appgraph.AnnotatedProgramPoint;
import de.uni_freiburg.informatik.ultimate.plugins.generator.appgraph.ImpRootNode;
import de.uni_freiburg.informatik.ultimate.plugins.generator.appgraph.RCFG2AnnotatedRCFG;
import de.uni_freiburg.informatik.ultimate.plugins.generator.codecheck.Activator;
import de.uni_freiburg.informatik.ultimate.plugins.generator.codecheck.CodeCheckSettings;
import de.uni_freiburg.informatik.ultimate.plugins.generator.codecheck.preferences.CodeCheckPreferenceInitializer;
import de.uni_freiburg.informatik.ultimate.plugins.generator.codecheck.preferences.CodeCheckPreferenceInitializer.Checker;
import de.uni_freiburg.informatik.ultimate.plugins.generator.codecheck.preferences.CodeCheckPreferenceInitializer.EdgeCheckOptimization;
import de.uni_freiburg.informatik.ultimate.plugins.generator.codecheck.preferences.CodeCheckPreferenceInitializer.PredicateUnification;
import de.uni_freiburg.informatik.ultimate.plugins.generator.codecheck.preferences.CodeCheckPreferenceInitializer.RedirectionStrategy;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.BoogieIcfgContainer;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.BoogieIcfgLocation;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.CfgBuilder;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.predicates.PredicateFactory;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.preferences.TraceAbstractionPreferenceInitializer.InterpolationTechnique;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.singletracecheck.InterpolatingTraceCheck;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.singletracecheck.InterpolatingTraceCheckCraig;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.singletracecheck.TraceCheckUtils;
import de.uni_freiburg.informatik.ultimate.smtinterpol.DefaultLogger;
import de.uni_freiburg.informatik.ultimate.smtinterpol.smtlib2.SMTInterpol;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.absint.IAbstractInterpretationResult;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.boogie.Boogie2SMT;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.boogie.BoogieDeclarations;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.CfgSmtToolkit;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IIcfg;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IIcfgTransition;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IcfgEdge;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IcfgLocation;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.SmtUtils.SimplificationTechnique;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.SmtUtils.XnfConversionTechnique;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.SolverBuilder.SolverMode;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.managedscript.ManagedScript;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicateUnifier;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.PredicateUnifier;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.tracecheck.ITraceCheckPreferences.AssertCodeBlockOrder;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.tracecheck.ITraceCheckPreferences.UnsatCores;

public class MainTTCTest {
	private static final SimplificationTechnique SIMPLIFICATION_TECHNIQUE = SimplificationTechnique.SIMPLIFY_DDA;
	private static final XnfConversionTechnique XNF_CONVERSION_TECHNIQUE =
			XnfConversionTechnique.BOTTOM_UP_WITH_LOCAL_SIMPLIFICATION;
	
	private CodeCheckSettings mGlobalSettings;
	private IUltimateServiceProvider service;
	
	private String[] pluginIDs;
	
	public MainTTCTest() {
		// Setup Controller
		UltimateCChecker cChecker = null;
		try {
			cChecker = new UltimateCChecker();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		CDTController cdtController;
		try {
			cdtController = new CDTController(cChecker);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		IUltimateServiceProvider generator = new ToolchainStorage();
		
		pluginIDs = new String[]{
				de.uni_freiburg.informatik.ultimate.plugins.generator.codecheck.Activator.PLUGIN_ID
		};
		
		service = generator.registerDefaultPreferenceLayer(this.getClass(), pluginIDs);
		ILogger logger = service.getLoggingService().getControllerLogger();
		
		SettingsManager settingsManager = new SettingsManager(logger);
		PluginFactory fact = new PluginFactory(settingsManager, logger);
		ToolChainManager toolManager = new ToolChainManager();
		
		IPreferenceProvider pref = service.getPreferenceProvider(pluginIDs[0]);
		pref.put("SMT solver", "Internal_SMTInterpol");
		pref.put("Size of a code block", "SingleStatement"); // Possible options: , SequenceOfStatements, LoopFreeBlock
		pref.put("the checking algorithm to use", "ULTIMATE");
		pref.put("interpolation mode", "Craig_NestedInterpolation");
		pref.put("Predicate Unification Mode", "PER_VERIFICATION");
		pref.put("EdgeCheck Optimization Mode", "NONE");
		pref.put("The redirection strategy for Impulse", "No_Strategy");
		pref.put("Choose which separate solver to use for tracechecks", "Internal_SMTInterpol");
		pref.put("Use unsat cores in FP/BP interpolation", "IGNORE");
		
		BoogieParser bp = new BoogieParser();
		bp.setServices(service);
		
		String[] files = {"res/t6.bpl"};
		
		Unit unit = (Unit)parseBoogie(bp, files);
		
		BoogieDeclarations decs = new BoogieDeclarations(unit, logger);
		Script script = new SMTInterpol(new DefaultLogger());
		script.setLogic(Logics.QF_AUFLIRA);
		ManagedScript mScript = new ManagedScript(service, script);
		Boogie2SMT b2smt = new Boogie2SMT(mScript, decs, false, service, false);
		BoogieIcfgContainer icfg = new BoogieIcfgContainer(service, decs, b2smt, null);
		
		// Setup for interpolation
		CfgSmtToolkit toolkit = icfg.getCfgSmtToolkit();
		
		PredicateFactory predFact = new PredicateFactory(service, 
				toolkit.getManagedScript(),
				toolkit.getSymbolTable(),
				SIMPLIFICATION_TECHNIQUE, XNF_CONVERSION_TECHNIQUE);
		
		IPredicateUnifier unifier = new PredicateUnifier(logger, service, toolkit.getManagedScript(),
				predFact, toolkit.getSymbolTable(), SIMPLIFICATION_TECHNIQUE,
				XNF_CONVERSION_TECHNIQUE);
		
		readPreferencePage();
		
		final IProgressAwareTimer timer = service.getProgressMonitorService().getChildTimer(0.2);
		final IAbstractInterpretationResult<?, IcfgEdge, IcfgLocation> result =
				AbstractInterpreter.runFuture(icfg, timer, service, false, logger);
		
		Map<IcfgLocation, Term> initialPredicates = result.getLoc2Term().entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		
		RTwo r2ar = new RTwo(toolkit, predFact, logger,
				unifier.getTruePredicate(), initialPredicates);
		
		ImpRootNode graphRoot = r2ar.convert(icfg);
		
		ArrayList<AnnotatedProgramPoint> procRootsToCheck = new ArrayList<>();
		procRootsToCheck.addAll(graphRoot.getOutgoingNodes());
		System.out.println(procRootsToCheck.size());
		
		// Interpolation
//		InterpolatingTraceCheck<IIcfgTransition<?>> tc = new InterpolatingTraceCheckCraig<>(
//				unifier.getTruePredicate(), unifier.getFalsePredicate(),
//				new TreeMap<Integer, IPredicate>(), errorRun.getWord(),
//				TraceCheckUtils.getSequenceOfProgramPoints(NestedWord.nestedWord(errorRun.getWord())),
//				service, toolkit, mScript, predFact, unifier,
//				AssertCodeBlockOrder.NOT_INCREMENTALLY, true, true, mGlobalSettings.getInterpolationMode(),
//				true, XNF_CONVERSION_TECHNIQUE, SIMPLIFICATION_TECHNIQUE, false);
	}
	
	private void readPreferencePage() {
		final IPreferenceProvider prefs = service.getPreferenceProvider(pluginIDs[0]);

		mGlobalSettings = new CodeCheckSettings();

		final Checker checker = prefs.getEnum(CodeCheckPreferenceInitializer.LABEL_CHECKER, Checker.class);

		mGlobalSettings.setChecker(checker);

		mGlobalSettings.setMemoizeNormalEdgeChecks(
				prefs.getBoolean(CodeCheckPreferenceInitializer.LABEL_MEMOIZENORMALEDGECHECKS,
						CodeCheckPreferenceInitializer.DEF_MEMOIZENORMALEDGECHECKS));
		mGlobalSettings.setMemoizeReturnEdgeChecks(
				prefs.getBoolean(CodeCheckPreferenceInitializer.LABEL_MEMOIZERETURNEDGECHECKS,
						CodeCheckPreferenceInitializer.DEF_MEMOIZERETURNEDGECHECKS));

		mGlobalSettings.setInterpolationMode(
				prefs.getEnum(CodeCheckPreferenceInitializer.LABEL_INTERPOLATIONMODE, InterpolationTechnique.class));

		mGlobalSettings.setUseInterpolantconsolidation(
				prefs.getBoolean(CodeCheckPreferenceInitializer.LABEL_INTERPOLANTCONSOLIDATION,
						CodeCheckPreferenceInitializer.DEF_INTERPOLANTCONSOLIDATION));

		mGlobalSettings.setPredicateUnification(
				prefs.getEnum(CodeCheckPreferenceInitializer.LABEL_PREDICATEUNIFICATION, PredicateUnification.class));

		mGlobalSettings.setEdgeCheckOptimization(
				prefs.getEnum(CodeCheckPreferenceInitializer.LABEL_EDGECHECKOPTIMIZATION, EdgeCheckOptimization.class));

		mGlobalSettings.setIterations(prefs.getInt(CodeCheckPreferenceInitializer.LABEL_ITERATIONS,
				CodeCheckPreferenceInitializer.DEF_ITERATIONS));

		mGlobalSettings.setDotGraphPath(prefs.getString(CodeCheckPreferenceInitializer.LABEL_GRAPHWRITERPATH,
				CodeCheckPreferenceInitializer.DEF_GRAPHWRITERPATH));

		mGlobalSettings.setRedirectionStrategy(
				prefs.getEnum(CodeCheckPreferenceInitializer.LABEL_REDIRECTION, RedirectionStrategy.class));

		mGlobalSettings.setDefaultRedirection(prefs.getBoolean(CodeCheckPreferenceInitializer.LABEL_DEF_RED,
				CodeCheckPreferenceInitializer.DEF_DEF_RED));
		mGlobalSettings.setRemoveFalseNodes(prefs.getBoolean(CodeCheckPreferenceInitializer.LABEL_RmFALSE,
				CodeCheckPreferenceInitializer.DEF_RmFALSE));
		mGlobalSettings.setCheckSatisfiability(prefs.getBoolean(CodeCheckPreferenceInitializer.LABEL_CHK_SAT,
				CodeCheckPreferenceInitializer.DEF_CHK_SAT));

		/*
		 * Settings concerning the solver for trace checks
		 */
		mGlobalSettings.setUseSeparateSolverForTracechecks(
				prefs.getBoolean(CodeCheckPreferenceInitializer.LABEL_USESEPARATETRACECHECKSOLVER,
						CodeCheckPreferenceInitializer.DEF_USESEPARATETRACECHECKSOLVER));

		mGlobalSettings.setUseFallbackForSeparateSolverForTracechecks(
				prefs.getBoolean(CodeCheckPreferenceInitializer.LABEL_USEFALLBACKFORSEPARATETRACECHECKSOLVER,
						CodeCheckPreferenceInitializer.DEF_USEFALLBACKFORSEPARATETRACECHECKSOLVER));

		mGlobalSettings.setChooseSeparateSolverForTracechecks(
				prefs.getEnum(CodeCheckPreferenceInitializer.LABEL_CHOOSESEPARATETRACECHECKSOLVER, SolverMode.class));

		mGlobalSettings.setSeparateSolverForTracechecksCommand(
				prefs.getString(CodeCheckPreferenceInitializer.LABEL_SEPARATETRACECHECKSOLVERCOMMAND,
						CodeCheckPreferenceInitializer.DEF_SEPARATETRACECHECKSOLVERCOMMAND));

		mGlobalSettings.setSeparateSolverForTracechecksTheory(
				prefs.getString(CodeCheckPreferenceInitializer.LABEL_SEPARATETRACECHECKSOLVERTHEORY,
						CodeCheckPreferenceInitializer.DEF_SEPARATETRACECHECKSOLVERTHEORY));

		/*
		 * Settings concerning betim interpolation
		 */
		mGlobalSettings
				.setUseLiveVariables(prefs.getBoolean(CodeCheckPreferenceInitializer.LABEL_LIVE_VARIABLES, true));
		mGlobalSettings
				.setUseUnsatCores(prefs.getEnum(CodeCheckPreferenceInitializer.LABEL_UNSAT_CORES, UnsatCores.class));

		/*
		 * Abstract interpretataion settings
		 */
		mGlobalSettings.setUseAbstractInterpretation(
				prefs.getBoolean(CodeCheckPreferenceInitializer.LABEL_USE_ABSTRACT_INTERPRETATION,
						CodeCheckPreferenceInitializer.DEF_USE_ABSTRACT_INTERPRETATION));

	}
	
	private IElement parseBoogie(BoogieParser parser, String... path) {
		File[] files = new File[path.length];
		
		int i = 0;
		for (String pathName : path) {
			files[i] = new File(pathName);
			i++;
		}
		
		IElement ast = null;
		
		try {
			ast = parser.parseAST(files);
		} catch(IOException ioe) {
			ioe.printStackTrace();
			System.exit(1);
		}
		
		return ast;
	}
	
	public static void main(String[] args) {
		new MainTTCTest();
	}
}
