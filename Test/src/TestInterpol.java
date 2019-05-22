import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.smtinterpol.DefaultLogger;
import de.uni_freiburg.informatik.ultimate.smtinterpol.smtlib2.SMTInterpol;

public class TestInterpol {
	public static void main(String[] args) {
		Script s = new SMTInterpol(new DefaultLogger());
	}
}
