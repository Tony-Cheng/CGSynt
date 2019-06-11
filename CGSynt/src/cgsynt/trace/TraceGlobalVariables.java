package cgsynt.trace;

import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.logic.Logics;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.managedscript.ManagedScript;
import de.uni_freiburg.informatik.ultimate.smtinterpol.DefaultLogger;
import de.uni_freiburg.informatik.ultimate.smtinterpol.smtlib2.SMTInterpol;
import de.uni_freiburg.informatik.ultimate.test.mocks.ConsoleLogger;
import de.uni_freiburg.informatik.ultimate.test.mocks.UltimateMocks;

public class TraceGlobalVariables {

	private static TraceGlobalVariables globalVar = new TraceGlobalVariables();
	private ManagedScript managedScript;
	private VariableFactory variableFactory;
	private IUltimateServiceProvider service;

	public TraceGlobalVariables() {
		service = UltimateMocks.createUltimateServiceProviderMock();
		managedScript = new ManagedScript(service, new SMTInterpol(new DefaultLogger()));
		managedScript.getScript().setOption(":produce-proofs", true);
		managedScript.getScript().setLogic(Logics.QF_LIA);
		variableFactory = new VariableFactory(managedScript.getScript());
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
