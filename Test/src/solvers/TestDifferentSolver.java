package solvers;

import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger.LogLevel;
import de.uni_freiburg.informatik.ultimate.logic.Assignments;
import de.uni_freiburg.informatik.ultimate.logic.Logics;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Sort;
import de.uni_freiburg.informatik.ultimate.logic.Term;
import de.uni_freiburg.informatik.ultimate.logic.Script.LBool;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.SolverBuilder;
import de.uni_freiburg.informatik.ultimate.test.mocks.UltimateMocks;

public class TestDifferentSolver {
	public static void main(String args[]) {
		Script z3 = UltimateMocks.createSolver("./res/" + SolverBuilder.COMMAND_Z3_NO_TIMEOUT, LogLevel.OFF);
		
		z3.setOption(":produce-assignments", true);
		z3.setLogic(Logics.QF_UFLIA);
		
		z3.declareFun("x", new Sort[0], z3.sort("Int"));
		
		Term x = z3.term("x");
		Term twoXPlusTwo = z3.term("+", z3.term("*", z3.numeral("2"), x), z3.numeral("2"));
		Term threeXPlusThree = z3.term("+", z3.term("*", z3.numeral("3"), x), z3.numeral("3"));
		
		Term eq = z3.term("=", twoXPlusTwo, threeXPlusThree);
		
		z3.assertTerm(eq);
		LBool res = z3.checkSat();
		System.out.println(res);
		
		Assignments assign = z3.getAssignment();
		System.out.println(assign);
	}
}
