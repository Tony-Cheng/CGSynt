import usra.trace.Formula;
import usra.trace.FormulaFactory;
import usra.trace.Token;
import usra.trace.TokenFactory;
import usra.trace.Trace;

public class TestTraceOp {
	public static void main(String args[]) {
		 TokenFactory tf = new TokenFactory();
		 
		 Token x = tf.createVariable();
		
		 Formula f1 = FormulaFactory.assign(x, 0);
		 Formula f2 = FormulaFactory.assume(x, tf.createNumerical(2));
		 
		 Trace t = new Trace();
		 t.addFormula(f1);
		 t.addFormula(f2);
	}
}
