import de.uni_freiburg.informatik.ultimate.logic.Annotation;
import de.uni_freiburg.informatik.ultimate.logic.Assignments;
import de.uni_freiburg.informatik.ultimate.logic.Logics;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Script.LBool;
import de.uni_freiburg.informatik.ultimate.logic.Sort;
import de.uni_freiburg.informatik.ultimate.logic.Term;
import de.uni_freiburg.informatik.ultimate.smtinterpol.DefaultLogger;
import de.uni_freiburg.informatik.ultimate.smtinterpol.smtlib2.SMTInterpol;

public class TestSMT {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Script script = new SMTInterpol(new DefaultLogger());

		script.setOption(":produce-assignments", true);

		script.setOption(":produce-assignments", true);

		script.setLogic(Logics.QF_UF);

		script.declareSort("U", 0);
		final Sort[] empty = {};
		final Sort U = script.sort("U");
		script.declareFun("x", empty, U);
		script.declareFun("y", empty, U);
		script.declareFun("f", new Sort[] { U }, U);

		// Build the formula (f(x) == f(y) /\ x == y) \/ x != y

		Term x = script.term("x");
		Term y = script.term("y");
		Term fx = script.term("f", x);
		Term fy = script.term("f", y);
		Term xeqy = script.term("=", x, y);
		Term fxeqfy = script.term("=", fx, fy);
		Term namedxeqy = script.annotate(xeqy, new Annotation(":named", "xeqy"));
		Term namedxneqy = script.annotate(script.term("not", xeqy), new Annotation(":named", "xneqy"));
		Term namedfxeqfy = script.annotate(fxeqfy, new Annotation(":named", "fxeqfy"));
		Term conj = script.term("and", namedfxeqfy, namedxeqy);
		Term disj = script.term("or", conj, namedxneqy);
		script.assertTerm(disj);
		
		LBool res = script.checkSat();
		
		Assignments ass = script.getAssignment();
		boolean isXeqY = ass.getAssignment("xeqy");
		boolean isXneqY = ass.getAssignment("xneqy");
		System.out.println(isXeqY + " " + isXneqY);
		
	}

}
