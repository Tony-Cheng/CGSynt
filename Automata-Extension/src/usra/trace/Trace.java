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
		this.names = -1;
	}

	/**
	 * Add a formula to the trace.
	 * 
	 * @param formula
	 */
	public void addFormula(FormulaInterface formula) {
		formulas.add(formula);
		if (formula.getV1() > names)
			names = formula.getV1();
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
