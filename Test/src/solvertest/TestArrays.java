package solvertest;

import de.uni_freiburg.informatik.ultimate.logic.Assignments;
import de.uni_freiburg.informatik.ultimate.logic.Logics;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Sort;
import de.uni_freiburg.informatik.ultimate.logic.Term;
import de.uni_freiburg.informatik.ultimate.logic.Script.LBool;
import de.uni_freiburg.informatik.ultimate.smtinterpol.DefaultLogger;
import de.uni_freiburg.informatik.ultimate.smtinterpol.smtlib2.SMTInterpol;

public class TestArrays {
	public static void main(String args[]) {
		Script script = new SMTInterpol(new DefaultLogger());
		script.setOption(":produce-assignments", true);
		script.setLogic(Logics.AUFLIA);
		
		script.declareFun("A", new Sort[0], script.sort("Array", script.sort("Int"), script.sort("Int")));
		
		script.declareFun("x", new Sort[0], script.sort("Int"));
		Term A = script.term("A");
		Term x = script.term("x");
		
		Term a0ex = script.term("=", script.term("select", A, x), x);
		script.assertTerm(a0ex);
		
		LBool res = script.checkSat();
		System.out.println(res);
		
		Assignments assign = script.getAssignment();
		System.out.println(assign);
	}
}
