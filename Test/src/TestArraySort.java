import de.uni_freiburg.informatik.ultimate.logic.Logics;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Sort;
import de.uni_freiburg.informatik.ultimate.smtinterpol.smtlib2.SMTInterpol;

public class TestArraySort {
	public static void main(String args[]) {
		Script script = new SMTInterpol();
		script.setLogic(Logics.AUFNIRA);
		
		Sort sort = script.sort("Array");
	}
}
