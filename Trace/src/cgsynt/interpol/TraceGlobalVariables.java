package cgsynt.interpol;

import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.logic.Logics;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.managedscript.ManagedScript;
import de.uni_freiburg.informatik.ultimate.smtinterpol.DefaultLogger;
import de.uni_freiburg.informatik.ultimate.smtinterpol.LogProxy;
import de.uni_freiburg.informatik.ultimate.smtinterpol.smtlib2.SMTInterpol;
import de.uni_freiburg.informatik.ultimate.test.mocks.UltimateMocks;

public class TraceGlobalVariables {

	private static TraceGlobalVariables globalVar;
	private ManagedScript managedScript;
	private VariableFactory variableFactory;
	private IUltimateServiceProvider service;

	private TraceGlobalVariables() {
		this.service = UltimateMocks.createUltimateServiceProviderMock();
		LogProxy logger = new DefaultLogger();
		logger.setLoglevel(LogProxy.LOGLEVEL_OFF);
		SMTInterpol interpolator = new SMTInterpol(logger);
		managedScript = new ManagedScript(service, interpolator);
		managedScript.getScript().setOption(":produce-proofs", true);
		managedScript.getScript().setLogic(Logics.QF_ALIA);
		variableFactory = new VariableFactory(managedScript.getScript());
	}

	public static void reset() {
		globalVar = new TraceGlobalVariables();
	}

	public ManagedScript getManagedScript() {
		return managedScript;
	}

	public VariableFactory getVariableFactory() {
		return variableFactory;
	}

	public static TraceGlobalVariables getGlobalVariables() {
		return globalVar;
	}

	public IUltimateServiceProvider getService() {
		return service;
	}

}
