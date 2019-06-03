import cgsynt.Formula;
import cgsynt.FormulaFactory;
import cgsynt.Token;
import cgsynt.TokenFactory;
import cgsynt.Trace;
import cgsynt.trace.operations.TraceToSMTFormula;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Term;

public class TestTraceOp {
	public static void main(String args[]) {
		 TokenFactory tf = new TokenFactory();
		 
		 Token x = tf.createVariable();
		
		 Formula f1 = FormulaFactory.assign(x, 0);
		 Formula f2 = FormulaFactory.assume(x, tf.createNumerical(2));
		 
		 Trace t = new Trace();
		 t.addFormulas(f1, f2); 
		 
		 TraceToSMTFormula op = new TraceToSMTFormula(t);
		 Script s = op.getResult();
		 
		 Term[] terms = s.getAssertions();
		 for (Term term : terms) {
			 System.out.println(term);
		 }
	}
}
