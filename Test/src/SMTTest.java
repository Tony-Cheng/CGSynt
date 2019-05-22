import de.uni_freiburg.informatik.ultimate.logic.Assignments;
import de.uni_freiburg.informatik.ultimate.logic.Logics;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Script.LBool;
import de.uni_freiburg.informatik.ultimate.logic.Sort;
import de.uni_freiburg.informatik.ultimate.logic.Term;
import de.uni_freiburg.informatik.ultimate.smtinterpol.DefaultLogger;
import de.uni_freiburg.informatik.ultimate.smtinterpol.smtlib2.*;

/**
 *	Runs the following SMT script:
 * 	
 * (declare-const x Int)
 * (assert (= (+ (* 2 x) 2) (+ (* 3 x) 3)))
 * (check-sat)
 * (get-model)
 */

public class SMTTest {
	public SMTTest() {
		satTest1();
		satTest2();
	}
	
	private void satTest1() {
		Script script = new SMTInterpol(new DefaultLogger());
		script.setOption(":produce-assignments", true);
		script.setLogic(Logics.QF_UFLIA);
		
		script.declareFun("x", new Sort[0], script.sort("Int"));
		
		Term x = script.term("x");
		Term twoXPlusTwo = script.term("+", script.term("*", script.numeral("2"), x), script.numeral("2"));
		Term threeXPlusThree = script.term("+", script.term("*", script.numeral("3"), x), script.numeral("3"));
		
		Term eq = script.term("=", twoXPlusTwo, threeXPlusThree);
		
		script.assertTerm(eq);
		LBool res = script.checkSat();
		System.out.println(res);
		
		Assignments assign = script.getAssignment();
		System.out.println(assign);
	}
	
	private void satTest2() {
		Script script = new SMTInterpol(new DefaultLogger());
		script.setOption(":produce-assignments", true);
		script.setLogic(Logics.QF_UFLIA);
		
		script.declareFun("x", new Sort[0], script.sort("Int"));
		script.declareFun("y", new Sort[0], script.sort("Int"));
		script.declareFun("z", new Sort[0], script.sort("Int"));
		
		script.declareFun("f", new Sort[]{script.sort("Int")}, script.sort("Int"));
		
		Term a1 = script.term("not", script.term("and", script.term("=", script.term("*", script.numeral("2"), script.term("x")), script.term("y")), script.term("=", script.term("f", script.term("x")), script.numeral("1"))));
		Term a2 = script.term("not", script.term("and", script.term("=", script.term("*", script.numeral("2"), script.term("z")), script.term("y")), script.term("=", script.term("f", script.term("z")), script.numeral("0"))));
	
		script.assertTerm(a1);
		script.assertTerm(a2);
		LBool res = script.checkSat();
		System.out.println(res);
		
		Assignments ass = script.getAssignment();
		System.out.println(ass);
	}
	
	public static void main(String args[]) {
		new SMTTest();
	}
}
