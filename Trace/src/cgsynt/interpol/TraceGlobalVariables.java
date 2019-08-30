package cgsynt.interpol;

import java.util.HashSet;
import java.util.Set;

import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger.LogLevel;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.logic.LoggingScript;
import de.uni_freiburg.informatik.ultimate.logic.Logics;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.managedscript.ManagedScript;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.BasicPredicateFactory;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.predicates.PredicateFactory;
import de.uni_freiburg.informatik.ultimate.smtinterpol.DefaultLogger;
import de.uni_freiburg.informatik.ultimate.smtinterpol.LogProxy;
import de.uni_freiburg.informatik.ultimate.smtinterpol.smtlib2.SMTInterpol;
import de.uni_freiburg.informatik.ultimate.test.mocks.ConsoleLogger;
import de.uni_freiburg.informatik.ultimate.test.mocks.UltimateMocks;

/**
 * Store the variables used for trace interpolation.
 *
 */
public class TraceGlobalVariables {

	private ManagedScript mManagedScript;
	private VariableFactory mVariableFactory;
	private IUltimateServiceProvider mService;
	private TraceToInterpolants mTraceInterpolator;
	
	private ILogger mLogger;
	private LogProxy mProxyLogger;
	
	public TraceGlobalVariables() throws Exception {
		mService = UltimateMocks.createUltimateServiceProviderMock(LogLevel.OFF);
		
		mProxyLogger = new DefaultLogger();
		mLogger = new ConsoleLogger();
		mProxyLogger.setLoglevel(LogProxy.LOGLEVEL_OFF);
		mLogger.setLevel(LogLevel.OFF);
		
		Script interpolator;
		try {
			interpolator = new SMTInterpol(mProxyLogger);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		mManagedScript = new ManagedScript(mService, interpolator);
		mManagedScript.getScript().setOption(":produce-proofs", true);
		mManagedScript.getScript().setLogic(Logics.QF_AUFNIRA);
		mVariableFactory = new VariableFactory(mManagedScript);
		
		Set<String> procs = new HashSet<>();
		procs.add("p1");
		mTraceInterpolator = new TraceToInterpolants(mManagedScript, mService, mVariableFactory.getSymbolTable(), procs);
	}

	public TraceGlobalVariables(IUltimateServiceProvider provider) throws Exception{
		mService = provider;
		
		mProxyLogger = new DefaultLogger();
		mLogger = new ConsoleLogger();
		mProxyLogger.setLoglevel(LogProxy.LOGLEVEL_OFF);
		mLogger.setLevel(LogLevel.OFF);
		
		Script interpolator;
		try {
			interpolator = new SMTInterpol(mProxyLogger);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		mManagedScript = new ManagedScript(mService, interpolator);
		mManagedScript.getScript().setOption(":produce-proofs", true);
		mManagedScript.getScript().setLogic(Logics.QF_AUFNIRA);
		mVariableFactory = new VariableFactory(mManagedScript);
		
		Set<String> procs = new HashSet<>();
		procs.add("p1");
		mTraceInterpolator = new TraceToInterpolants(mManagedScript, mService, mVariableFactory.getSymbolTable(), procs);
	}
	
	public TraceToInterpolants getTraceInterpolator() {
		return mTraceInterpolator;
	}

	public PredicateFactory getPredicateFactory() {
		return mTraceInterpolator.getPredicateFactory();
	}

	public ManagedScript getManagedScript() {
		return mManagedScript;
	}

	public VariableFactory getVariableFactory() {
		return mVariableFactory;
	}

	public IUltimateServiceProvider getService() {
		return mService;
	}
	
	public LogProxy getLogProxy() {
		return mProxyLogger;
	}
	
	public ILogger getLogger() {
		return mLogger;
	}
}
