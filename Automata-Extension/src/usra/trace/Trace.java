package usra.trace;

import java.util.ArrayList;
import java.util.List;

public class Trace implements TraceInterface {

	private List<FormulaInterface> formulas;
	private int names;

	public Trace() {
		this.formulas = new ArrayList<>();
		this.names = -1;
	}
	
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
