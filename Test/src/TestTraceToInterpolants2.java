import java.util.TreeMap;

import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedRun;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWord;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.BasicIcfg;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.CfgSmtToolkit;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IIcfg;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IIcfgTransition;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IcfgLocation;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.SmtUtils.SimplificationTechnique;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.SmtUtils.XnfConversionTechnique;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.managedscript.ManagedScript;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicateUnifier;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.PredicateUnifier;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.tracecheck.ITraceCheckPreferences.AssertCodeBlockOrder;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.tracecheck.ITraceCheckPreferences.UnsatCores;
import de.uni_freiburg.informatik.ultimate.plugins.generator.appgraph.AnnotatedProgramPoint;
import de.uni_freiburg.informatik.ultimate.plugins.generator.codecheck.Activator;
import de.uni_freiburg.informatik.ultimate.plugins.generator.codecheck.CodeCheckSettings;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.RootNode;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.predicates.PredicateFactory;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.preferences.TraceAbstractionPreferenceInitializer.InterpolationTechnique;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.singletracecheck.InterpolatingTraceCheck;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.singletracecheck.InterpolatingTraceCheckCraig;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.singletracecheck.TraceCheckSpWp;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.singletracecheck.TraceCheckUtils;
import de.uni_freiburg.informatik.ultimate.test.mocks.UltimateMocks;

public class TestTraceToInterpolants2 {

	private CodeCheckSettings mGlobalSettings;
	private final ILogger mLogger;
	private final IUltimateServiceProvider mServices;
	private IPredicateUnifier mPredicateUnifier;
	private CfgSmtToolkit mCsToolkit;
	private PredicateFactory mPredicateFactory;
	private IIcfg<IcfgLocation> mOriginalRoot;
	private static final SimplificationTechnique SIMPLIFICATION_TECHNIQUE = SimplificationTechnique.SIMPLIFY_DDA;
	private static final XnfConversionTechnique XNF_CONVERSION_TECHNIQUE = XnfConversionTechnique.BOTTOM_UP_WITH_LOCAL_SIMPLIFICATION;

//	public void craigInterpol() {
//		final InterpolatingTraceCheck<IIcfgTransition<?>> tc = new InterpolatingTraceCheckCraig<>(
//				mPredicateUnifier.getTruePredicate(), mPredicateUnifier.getFalsePredicate(),
//				new TreeMap<Integer, IPredicate>(), errorRun.getWord(),
//				TraceCheckUtils.getSequenceOfProgramPoints(NestedWord.nestedWord(errorRun.getWord())),
//				mServices, mCsToolkit, mgdScriptTracechecks, mPredicateFactory, mPredicateUnifier,
//				AssertCodeBlockOrder.NOT_INCREMENTALLY, true, true, mGlobalSettings.getInterpolationMode(),
//				true, XNF_CONVERSION_TECHNIQUE, SIMPLIFICATION_TECHNIQUE, false);
//		if (tc.getInterpolantComputationStatus().wasComputationSuccesful()) {
//			return tc;
//		}
//	}
	public TestTraceToInterpolants2(final IUltimateServiceProvider services, IIcfg<IcfgLocation> root) {
		mServices = services;
		mLogger = mServices.getLoggingService().getLogger(Activator.PLUGIN_ID);
		mGlobalSettings = new CodeCheckSettings();
		mOriginalRoot = root;
		mCsToolkit = mOriginalRoot.getCfgSmtToolkit();
		mPredicateFactory = new PredicateFactory(mServices, mCsToolkit.getManagedScript(), mCsToolkit.getSymbolTable(),
				SIMPLIFICATION_TECHNIQUE, XNF_CONVERSION_TECHNIQUE);
		mPredicateUnifier = new PredicateUnifier(mLogger, mServices, mCsToolkit.getManagedScript(), mPredicateFactory,
				mOriginalRoot.getCfgSmtToolkit().getSymbolTable(), SIMPLIFICATION_TECHNIQUE, XNF_CONVERSION_TECHNIQUE);
	}

	private InterpolatingTraceCheck<IIcfgTransition<?>> createTraceCheck(
			final NestedRun<IIcfgTransition<?>, AnnotatedProgramPoint> errorRun,
			final ManagedScript mgdScriptTracechecks) {
		switch (mGlobalSettings.getInterpolationMode()) {
		case Craig_TreeInterpolation:
		case Craig_NestedInterpolation:
			try {
				final InterpolatingTraceCheck<IIcfgTransition<?>> tc = new InterpolatingTraceCheckCraig<>(
						mPredicateUnifier.getTruePredicate(), mPredicateUnifier.getFalsePredicate(),
						new TreeMap<Integer, IPredicate>(), errorRun.getWord(),
						TraceCheckUtils.getSequenceOfProgramPoints(NestedWord.nestedWord(errorRun.getWord())),
						mServices, mCsToolkit, mgdScriptTracechecks, mPredicateFactory, mPredicateUnifier,
						AssertCodeBlockOrder.NOT_INCREMENTALLY, true, true, mGlobalSettings.getInterpolationMode(),
						true, XNF_CONVERSION_TECHNIQUE, SIMPLIFICATION_TECHNIQUE, false);
				if (tc.getInterpolantComputationStatus().wasComputationSuccesful()) {
					return tc;
				}
			} catch (final Exception e) {
				mLogger.error("First Tracecheck threw exception " + e.getMessage());
				if (!mGlobalSettings.isUseFallbackForSeparateSolverForTracechecks()) {
					throw e;
				}
			}
			/*
			 * The fallback traceCheck is always the normal solver (i.e. the csToolkit that
			 * was set in RCFGBuilder settings with forward predicates betim interpolation.
			 *
			 * The fallback interpolation mode is hardcoded for now
			 */
			return new TraceCheckSpWp<>(mPredicateUnifier.getTruePredicate(), mPredicateUnifier.getFalsePredicate(),
					new TreeMap<Integer, IPredicate>(), errorRun.getWord(), mCsToolkit,
					AssertCodeBlockOrder.NOT_INCREMENTALLY, UnsatCores.CONJUNCT_LEVEL, true, mServices, true,
					mPredicateFactory, mPredicateUnifier, InterpolationTechnique.ForwardPredicates,
					mCsToolkit.getManagedScript(), XNF_CONVERSION_TECHNIQUE, SIMPLIFICATION_TECHNIQUE,
					TraceCheckUtils.getSequenceOfProgramPoints(NestedWord.nestedWord(errorRun.getWord())), true);
		case ForwardPredicates:
		case BackwardPredicates:
		case FPandBP:
		case FPandBPonlyIfFpWasNotPerfect:
			// return LBool.UNSAT if trace is infeasible
			try {
				return new TraceCheckSpWp<>(mPredicateUnifier.getTruePredicate(), mPredicateUnifier.getFalsePredicate(),
						new TreeMap<Integer, IPredicate>(), errorRun.getWord(), mCsToolkit,
						AssertCodeBlockOrder.NOT_INCREMENTALLY, mGlobalSettings.getUseUnsatCores(),
						mGlobalSettings.isUseLiveVariables(), mServices, true, mPredicateFactory, mPredicateUnifier,
						mGlobalSettings.getInterpolationMode(), mgdScriptTracechecks, XNF_CONVERSION_TECHNIQUE,
						SIMPLIFICATION_TECHNIQUE,
						TraceCheckUtils.getSequenceOfProgramPoints(NestedWord.nestedWord(errorRun.getWord())), true);
			} catch (final Exception e) {
				if (!mGlobalSettings.isUseFallbackForSeparateSolverForTracechecks()) {
					throw e;
				}

				return new TraceCheckSpWp<>(mPredicateUnifier.getTruePredicate(), mPredicateUnifier.getFalsePredicate(),
						new TreeMap<Integer, IPredicate>(), errorRun.getWord(), mCsToolkit,
						AssertCodeBlockOrder.NOT_INCREMENTALLY, UnsatCores.CONJUNCT_LEVEL, true, mServices, true,
						mPredicateFactory, mPredicateUnifier, mGlobalSettings.getInterpolationMode(),
						mCsToolkit.getManagedScript(), XNF_CONVERSION_TECHNIQUE, SIMPLIFICATION_TECHNIQUE,
						TraceCheckUtils.getSequenceOfProgramPoints(NestedWord.nestedWord(errorRun.getWord())), true);
			}
		default:
			throw new UnsupportedOperationException(
					"Unsupported interpolation mode: " + mGlobalSettings.getInterpolationMode());
		}
	}

	public void test() {

	}

	public static void main(String[] args) {
		IUltimateServiceProvider mock = UltimateMocks.createUltimateServiceProviderMock();
		IIcfg<IcfgLocation> root = new BasicIcfg<>("mOriginalRoot", null, null);
		new TestTraceToInterpolants2(mock, root);
	}

}
