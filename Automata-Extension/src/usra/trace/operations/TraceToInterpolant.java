package usra.trace.operations;

import de.uni_freiburg.informatik.ultimate.logic.Annotation;
import de.uni_freiburg.informatik.ultimate.logic.Logics;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Sort;
import de.uni_freiburg.informatik.ultimate.logic.Term;
import de.uni_freiburg.informatik.ultimate.smtinterpol.DefaultLogger;
import de.uni_freiburg.informatik.ultimate.smtinterpol.smtlib2.SMTInterpol;
import usra.trace.FormulaInterface;
import usra.trace.Token;
import usra.trace.TraceInterface;

public class TraceToInterpolant {
	private TraceInterface mTrace;
	private Script mScript;
	private Term[] mInterpolants;
	
	/**
	 * Operation to calculate the Interpolant's for a trace.
	 * @param trace 
	 * 		The trace who's interpolant's to calculate.
	 */
	public TraceToInterpolant(TraceInterface trace) {
		this.mTrace = trace;
		
		this.compute();
	}
	
	/**
	 * Return the result of the operation.
	 * @return mInterpolants
	 * 		The list of interpolant's for this trace.
	 */
	public Term[] getResult() {
		return mInterpolants;
	}
	
	/**
	 * Compute the interpolant's for the given trace.
	 */
	private void compute() {
		mScript = new SMTInterpol(new DefaultLogger());
		mScript.setOption(":produce-proofs", true);
		mScript.setLogic(Logics.QF_LIA);
		
		this.defineVariables();
		
		int stNum = 0;
		for (FormulaInterface form : mTrace.getFormulas()) {
			Term stTerm = this.processFormula(form, stNum, 0);
			stTerm = this.andUnChangedVars(form, stTerm);
			
			Term stAnnotated = mScript.annotate(stTerm, new Annotation(":named", "s_" + stNum));
			
			mScript.assertTerm(stAnnotated);
			
			stNum++;
		}
		
		int numberOfStatements = mTrace.getFormulas().size();
		Term[] annotations = new Term[numberOfStatements];
		
		for (int i = 0; i < numberOfStatements; i++) {
			annotations[i] = mScript.term("s_" + i);
		}
		
		mInterpolants = mScript.getInterpolants(annotations);
	}
	
	/**
	 * Process a formula recursively, if the flag is set then the stNum of the variables used for 
	 * calculating an assignment value will remain the same.
	 * @param form	The formula that is to be processed.
	 * @param stNum	The statement number of the given variable.
	 * @param flag	The flag for whether or not to decrease the stNum.
	 * @return
	 */
	private Term processFormula(FormulaInterface form, int stNum, int flag) {		
		if (form.getV2() == null) {
			Token v1 = form.getV1();
			
			if (v1.getType() == Token.NUMERICAL) {
				return mScript.numeral("" + v1.getValue()); 
			}
			else if(v1.getType() == Token.VARIABLE) {
				return mScript.term("v_" + v1.getValue() + "_" + stNum);
			}
		}
		else {
			int newStNum = (flag == 1) ? stNum: stNum - 1;
			Term sub = processFormula(form.getV2(), newStNum, 1);
			
			Token v1 = form.getV1();
			String varName = "v_" + v1.getValue() + "_" + stNum;
			
			Term cur = mScript.term(form.getOperation(), mScript.term(varName), sub);
			
			return cur;
		}
		
		return null;
	}
	
	/**
	 * Ensure that after a statement is entered all the unchanged 
	 * variables have the same values as before.
	 * @param form	 The current statement.
	 * @param stTerm The term that is to be modified.
	 * @return		 The modified term.
	 */
	private Term andUnChangedVars(FormulaInterface form, Term stTerm) {
		Term cur = stTerm;
		int compVarNum = form.getV1().getValue();
		
		int stNum = form.getV1().getValue();
		
		if (!form.getOperation().equals("=")) {
			String nameWOStNum = "v_" + form.getV1().getValue() + "_";
			
			Term oldEq = 
					mScript.term("=", mScript.term(nameWOStNum + stNum), mScript.term(nameWOStNum + (stNum - 1)));
			
			cur = mScript.term("and", cur, oldEq);
		}
		
		for (int i = 0; i < mTrace.getNames(); i++) {
			if (i != compVarNum) {
				String nameWOStNum = "v_" + i + "_";
				
				Term oldEq =
						mScript.term("=", mScript.term(nameWOStNum + stNum), mScript.term(nameWOStNum + (stNum - 1)));
				
				cur = mScript.term("and", cur, oldEq);
			}
		}
		
		return cur;
	}
	
	/**
	 * Define all the SMT variables that will be used.
	 */
	private void defineVariables() {
		for (int v = 0; v < mTrace.getNames(); v++) {
			for (int stNum = 0; stNum < mTrace.getFormulas().size(); stNum++) {
				mScript.declareFun("v_" + v + "_" + stNum, new Sort[0], mScript.sort("Int"));
			}
		}
	}
}