package usra.trace.operations;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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

public class TraceToSMTFormula {
	private TraceInterface mTrace;
	private Script mScript;

	/**
	 * Operation to convert a Trace into an SMT Script
	 * 
	 * @param trace
	 *            The trace to convert.
	 */
	public TraceToSMTFormula(TraceInterface trace) {
		this.mTrace = trace;

		this.compute();
	}

	/**
	 * Return the result of the operation.
	 * 
	 * @return mScript The converted SMT Script
	 */
	public Script getResult() {
		return mScript;
	}

	/**
	 * Convert the Trace to an SMT Script
	 */
	private void compute() {
		mScript = new SMTInterpol(new DefaultLogger());
		mScript.setOption(":produce-proofs", true);
		mScript.setOption(":interactive-mode", true);
		mScript.setLogic(Logics.QF_LIA);

		this.defineVariables();

		int stNum = 1;
		for (FormulaInterface form : mTrace.getFormulas()) {
			Term stTerm = this.processFormula(form, stNum, 0);
			stTerm = this.andUnChangedVars(form, stTerm, stNum);

			Term stAnnotated = mScript.annotate(stTerm, new Annotation(":named", "s_" + stNum));

			mScript.assertTerm(stAnnotated);

			stNum++;
		}
	}

	/**
	 * Process a formula recursively, if the flag is set then the stNum of the
	 * variables used for calculating an assignment value will remain the same.
	 * 
	 * @param form
	 *            The formula that is to be processed.
	 * @param stNum
	 *            The statement number of the given variable.
	 * @param flag
	 *            The flag for whether or not to decrease the stNum.
	 * @return
	 */
	private Term processFormula(FormulaInterface form, int stNum, int flag) {
		if (form.getV2() == null) {
			Token v1 = form.getV1();

			if (v1.getType() == Token.NUMERICAL) {
				return mScript.numeral("" + v1.getValue());
			} else if (v1.getType() == Token.VARIABLE) {
				return mScript.term("v_" + v1.getValue() + "_" + stNum);
			}
		} else {
			int newStNum = (flag == 1) ? stNum : stNum - 1;
			Term sub = processFormula(form.getV2(), newStNum, 1);

			Token v1 = form.getV1();
			String varName = "v_" + v1.getValue() + "_" + stNum;

			Term cur;
			if (!form.getOperation().equals("=="))
				cur = mScript.term(form.getOperation(), mScript.term(varName), sub);
			else
				cur = mScript.term("=", mScript.term(varName), sub);

			return cur;
		}

		return null;
	}

	/**
	 * Ensure that after a statement is entered all the unchanged variables have the
	 * same values as before.
	 * 
	 * @param form
	 *            The current statement.
	 * @param stTerm
	 *            The term that is to be modified.
	 * @return The modified term.
	 */
	private Term andUnChangedVars(FormulaInterface form, Term stTerm, int stNum) {
		Term cur = stTerm;
		int compVarNum = form.getV1().getValue();

		if (!form.getOperation().equals("=")) {
			String nameWOStNum = "v_" + form.getV1().getValue() + "_";

			Term oldEq = mScript.term("=", mScript.term(nameWOStNum + stNum), mScript.term(nameWOStNum + (stNum - 1)));

			cur = mScript.term("and", cur, oldEq);
		}

		for (int i = 1; i < mTrace.getNames() + 1; i++) {
			if (i != compVarNum) {
				String nameWOStNum = "v_" + i + "_";

				Term oldEq = mScript.term("=", mScript.term(nameWOStNum + stNum),
						mScript.term(nameWOStNum + (stNum - 1)));

				cur = mScript.term("and", cur, oldEq);
			}
		}

		return cur;
	}

	/**
	 * Define all the SMT variables that will be used.
	 */
	private void defineVariables() {
		for (int v = 1; v < mTrace.getNames() + 1; v++) {
			for (int stNum = 0; stNum <= mTrace.getFormulas().size(); stNum++) {
				mScript.declareFun("v_" + v + "_" + stNum, new Sort[0], mScript.sort("Int"));
			}
		}
	}
}