import de.uni_freiburg.informatik.ultimate.logic.Assignments;
import de.uni_freiburg.informatik.ultimate.logic.Logics;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Script.LBool;
import de.uni_freiburg.informatik.ultimate.logic.Sort;
import de.uni_freiburg.informatik.ultimate.logic.Term;
import de.uni_freiburg.informatik.ultimate.smtinterpol.DefaultLogger;
import de.uni_freiburg.informatik.ultimate.smtinterpol.smtlib2.*;

public class SMTTest {
	public SMTTest() {
		Script script = new SMTInterpol(new DefaultLogger());
		script.setOption(":produce-assignments", true);
		script.setLogic(Logics.QF_UFLIA);
		
		declareVars(script);
		
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
	
	private void declareVars(Script script) {
		Sort[] empty = {};
		Sort Int = script.sort("Int");
		
		script.declareFun("x", empty, Int);
	}
	
	public static void main(String args[]) {
		new SMTTest();
	}
}
