package cgsynt.interpol;

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
import de.uni_freiburg.informatik.ultimate.test.mocks.UltimateMocks;

public class TraceGlobalVariables {

	private ManagedScript managedScript;
	private VariableFactory variableFactory;
	private IUltimateServiceProvider service;
	private TraceToInterpolants traceInterpolator;

	public TraceGlobalVariables() throws Exception {
		this.service = UltimateMocks.createUltimateServiceProviderMock(LogLevel.OFF);
		LogProxy logger = new DefaultLogger();
		Script interpolator;
		try {
			// logger.setLoglevel(LogProxy.LOGLEVEL_TRACE);
			// interpolator = new LoggingScript("maxArray.smt2", true);
			logger.setLoglevel(LogProxy.LOGLEVEL_OFF);
			interpolator = new SMTInterpol(logger);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		managedScript = new ManagedScript(service, interpolator);
		managedScript.getScript().setOption(":produce-proofs", true);
		managedScript.getScript().setLogic(Logics.QF_AUFNIRA);
		variableFactory = new VariableFactory(managedScript);
		traceInterpolator = new TraceToInterpolants(managedScript, service, variableFactory.getSymbolTable());
	}

	public TraceGlobalVariables(IUltimateServiceProvider provider) throws Exception{
		this.service = provider;
		LogProxy logger = new DefaultLogger();
		Script interpolator;
		try {
			// logger.setLoglevel(LogProxy.LOGLEVEL_TRACE);
			// interpolator = new LoggingScript("maxArray.smt2", true);
			logger.setLoglevel(LogProxy.LOGLEVEL_OFF);
			interpolator = new SMTInterpol(logger);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		managedScript = new ManagedScript(service, interpolator);
		managedScript.getScript().setOption(":produce-proofs", true);
		managedScript.getScript().setLogic(Logics.QF_AUFNIRA);
		variableFactory = new VariableFactory(managedScript);
		traceInterpolator = new TraceToInterpolants(managedScript, service, variableFactory.getSymbolTable());
	}
	
	public TraceToInterpolants getTraceInterpolator() {
		return traceInterpolator;
	}

	public BasicPredicateFactory getPredicateFactory() {
		return traceInterpolator.getPredicateFactory();
	}

	public ManagedScript getManagedScript() {
		return managedScript;
	}

	public VariableFactory getVariableFactory() {
		return variableFactory;
	}

	public IUltimateServiceProvider getService() {
		return service;
	}

}
