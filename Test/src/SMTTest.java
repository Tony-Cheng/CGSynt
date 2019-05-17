import de.uni_freiburg.informatik.ultimate.logic.Assignments;
import de.uni_freiburg.informatik.ultimate.logic.Logics;
import de.uni_freiburg.informatik.ultimate.logic.NoopScript;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Script.LBool;
import de.uni_freiburg.informatik.ultimate.logic.Sort;
import de.uni_freiburg.informatik.ultimate.logic.Term;

public class SMTTest {
	public SMTTest() {
		Script script = new NoopScript();
		script.setOption(":produce-assignment", true);
		script.setLogic(Logics.QF_NIA);
		
		declareVars(script);
		
		Term x = script.term("x");
		Term twoXPlusTwo = script.term("+", script.term("*", script.term("2"), x), script.term("2"));
		Term threeXPlusThree = script.term("+", script.term("*", script.term("3"), x), script.term("3"));
		
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
