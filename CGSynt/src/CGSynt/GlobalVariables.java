package CGSynt;

import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.managedscript.ManagedScript;

public class GlobalVariables {

	private static GlobalVariables globalVar = new GlobalVariables();
	private ManagedScript managedScript;

	public ManagedScript getManagedScript() {
		return managedScript;
	}

	public static GlobalVariables getGlobalVariables() {
		return globalVar;
	}

}
