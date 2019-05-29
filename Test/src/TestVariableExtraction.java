import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Sort;
import de.uni_freiburg.informatik.ultimate.logic.Term;
import de.uni_freiburg.informatik.ultimate.smtinterpol.DefaultLogger;
import de.uni_freiburg.informatik.ultimate.smtinterpol.smtlib2.SMTInterpol;

public class TestVariableExtraction {
	public static void defineVars(Script script) {
		script.declareFun("x", new Sort[0], script.sort("Int"));
	}
	
	public static void main(String args[]) {
		Script script = new SMTInterpol(new DefaultLogger());
		defineVars(script);
		
		Term x = script.term("x");
		Term y = script.term("y");
		
		Term t = script.term("*", x, y);
		
		
		
	}
}
