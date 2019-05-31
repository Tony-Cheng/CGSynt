import java.util.Collection;
import java.util.TreeMap;

import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedRun;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWord;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.CfgSmtToolkit;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IIcfgTransition;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.SmtUtils.SimplificationTechnique;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.SmtUtils.XnfConversionTechnique;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.managedscript.ManagedScript;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicateUnifier;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.tracecheck.ITraceCheckPreferences.AssertCodeBlockOrder;
import de.uni_freiburg.informatik.ultimate.plugins.generator.appgraph.AnnotatedProgramPoint;
import de.uni_freiburg.informatik.ultimate.plugins.generator.codecheck.CodeCheckSettings;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.predicates.PredicateFactory;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.singletracecheck.InterpolatingTraceCheck;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.singletracecheck.InterpolatingTraceCheckCraig;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.singletracecheck.TraceCheckUtils;
import de.uni_freiburg.informatik.ultimate.smtinterpol.DefaultLogger;
import de.uni_freiburg.informatik.ultimate.test.mocks.UltimateMocks;

public class TestInterpolating {
	private static final SimplificationTechnique SIMPLIFICATION_TECHNIQUE = SimplificationTechnique.SIMPLIFY_DDA;
	private static final XnfConversionTechnique XNF_CONVERSION_TECHNIQUE =
			XnfConversionTechnique.BOTTOM_UP_WITH_LOCAL_SIMPLIFICATION;

	private final ILogger mLogger;
	private final IUltimateServiceProvider mServices;

	private IPredicateUnifier mPredicateUnifier;


	private CodeCheckSettings mGlobalSettings;

	private CfgSmtToolkit mCsToolkit;

	private PredicateFactory mPredicateFactory;
	
	public TestInterpolating() {
		mServices = UltimateMocks.createUltimateServiceProviderMock();
		mLogger = (ILogger) new DefaultLogger();
	}
	
	public InterpolatingTraceCheck<IIcfgTransition<?>> createTraceCheck(
			final NestedRun<IIcfgTransition<?>, AnnotatedProgramPoint> errorRun,
			final ManagedScript mgdScriptTracechecks) {
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
			
			return null;
	}
	
	public static void main(String args[]) {
		new TestInterpolating();
	}
}
