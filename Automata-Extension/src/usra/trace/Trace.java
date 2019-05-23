package usra.trace;

import java.util.ArrayList;
import java.util.List;

/**
 * A program trace.
 *
 */
public class Trace implements TraceInterface {

	/**
	 * A list of formulas that represents a trace.
	 */
	private List<FormulaInterface> formulas;

	/**
	 * The number of variables in the trace.
	 */
	private int names;

	public Trace() {
		this.formulas = new ArrayList<>();
		this.names = 0;
	}

	/**
	 * Add a formula to the trace.
	 * 
	 * @param formula
	 */
	public void addFormula(FormulaInterface formula) {
		formulas.add(formula);
		incrementName(formula);
	}

	/**
	 * Increment the variable names if necessary.
	 * 
	 * @param formula
	 */
	private void incrementName(FormulaInterface formula) {
		if (formula.getV1().getType() == Token.VARIABLE && formula.getV1().getValue() > names)
			names = formula.getV1().getValue();
		if (formula.getV2() != null) {
			incrementName(formula.getV2());
		}
	}

	@Override
	public int getNames() {
		return names;
	}

	@Override
	public List<FormulaInterface> getFormulas() {
		return formulas;
	}

}
