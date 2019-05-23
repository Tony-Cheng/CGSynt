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
	 * Add formulas to the trace.
	 * 
	 * @param formulas
	 */
	public void addFormulas(Formula... forms) {
		for (Formula formula : forms) {
			formulas.add(formula);
			incrementName(formula);
		}
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

	@Override
	public TraceInterface mkcpy() {
		Trace copy = new Trace();
		copy.names = this.names;
		copy.formulas.addAll(formulas);
		return copy;
	}

	@Override
	public String toString() {
		String res = "";
		for (int i = 0; i < formulas.size(); i++) {
			res = res + formulas.get(i).toString() + " | ";
		}
		return res;

	}

}
