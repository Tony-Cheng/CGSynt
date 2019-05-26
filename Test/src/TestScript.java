import de.uni_freiburg.informatik.ultimate.logic.Annotation;
import de.uni_freiburg.informatik.ultimate.logic.Logics;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Sort;
import de.uni_freiburg.informatik.ultimate.logic.Term;
import de.uni_freiburg.informatik.ultimate.logic.TermVariable;
import de.uni_freiburg.informatik.ultimate.smtinterpol.DefaultLogger;
import de.uni_freiburg.informatik.ultimate.smtinterpol.smtlib2.SMTInterpol;

public class TestScript {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		final Script s = new SMTInterpol(new DefaultLogger());
		s.setOption(":produce-proofs", true);
		s.setOption(":interactive-mode", true);
		s.setLogic(Logics.QF_LIA);
		s.declareFun("x", new Sort[0], s.sort("Int"));
		s.declareFun("y", new Sort[0], s.sort("Int"));
		s.assertTerm(s.annotate(s.term("=", s.term("x"), s.term("y")), new Annotation(":named", "phi_1")));
		Term[] terms = s.getAssertions();
		System.out.println(terms.length);
	}

}
