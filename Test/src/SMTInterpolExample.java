import de.uni_freiburg.informatik.ultimate.logic.Annotation;
import de.uni_freiburg.informatik.ultimate.logic.Logics;
import de.uni_freiburg.informatik.ultimate.logic.SMTLIBException;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Script.LBool;
import de.uni_freiburg.informatik.ultimate.logic.Sort;
import de.uni_freiburg.informatik.ultimate.logic.Term;
import de.uni_freiburg.informatik.ultimate.smtinterpol.DefaultLogger;
import de.uni_freiburg.informatik.ultimate.smtinterpol.smtlib2.SMTInterpol;

public class SMTInterpolExample {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {

			// Craig interpolant for the traces x:=0 y:=0 x++ x==-1
			final Script s = new SMTInterpol(new DefaultLogger());
			s.setOption(":produce-proofs", true);
			s.setLogic(Logics.QF_LIA);
			s.declareFun("x0", new Sort[0], s.sort("Int"));
			s.declareFun("x1", new Sort[0], s.sort("Int"));
			s.declareFun("x2", new Sort[0], s.sort("Int"));
			s.declareFun("x3", new Sort[0], s.sort("Int"));

			s.declareFun("y0", new Sort[0], s.sort("Int"));
			s.declareFun("y1", new Sort[0], s.sort("Int"));
			s.declareFun("y2", new Sort[0], s.sort("Int"));
			s.declareFun("y3", new Sort[0], s.sort("Int"));

			s.assertTerm(s.annotate(s.term("=", s.term("x0"), s.numeral("0")), new Annotation(":named", "phi_1")));
			s.assertTerm(s.annotate(s.term("=", s.term("x1"), s.term("x0")), new Annotation(":named", "phi_2")));
			s.assertTerm(s.annotate(s.term("=", s.term("y1"), s.numeral("0")), new Annotation(":named", "phi_3")));
			s.assertTerm(s.annotate(s.term("=", s.term("y2"), s.term("y1")), new Annotation(":named", "phi_4")));
			s.assertTerm(s.annotate(s.term("=", s.term("y3"), s.term("y2")), new Annotation(":named", "phi_5")));
			s.assertTerm(s.annotate(s.term("=", s.term("x2"), s.term("+", s.term("x1"), s.numeral("1"))),
					new Annotation(":named", "phi_6")));
			s.assertTerm(s.annotate(s.term("=", s.term("x3"), s.term("x2")), new Annotation(":named", "phi_7")));
			s.assertTerm(s.annotate(s.term("=", s.term("x3"), s.numeral("-1")), new Annotation(":named", "phi_8")));

			if (s.checkSat() == LBool.UNSAT) {
				Term[] interpolants;
				interpolants = s.getInterpolants(
						new Term[] { s.term("phi_1"), s.term("phi_2"), s.term("phi_3"), s.term("phi_4"),
								s.term("phi_5"), s.term("phi_6"), s.term("phi_7"), s.term("phi_8") });
				System.out.println(interpolants);
				for (Term term : interpolants) {
					System.out.println(term);
				}
			}
		} catch (final SMTLIBException ex) {
			System.out.println("unknown");
			ex.printStackTrace(System.err);
		}
	}

}
