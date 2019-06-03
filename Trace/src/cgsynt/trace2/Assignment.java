package cgsynt.trace2;

import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Term;

public class Assignment extends Statement {

	private final Variable variable;
	private final Formula formula;

	public Assignment(Variable variable, Formula formula) {
		super(true);
		this.variable = variable;
		this.formula = formula;
	}

	@Override
	public Term getTerm(Script script, int id) {
		return script.term("=", variable.getTerm(script, id), formula.getTerm(script, id - 1));
	}

	/**
	 * Return the variable that is on the left hand side of the equation.
	 * 
	 * @return
	 */
	public Variable getVariable() {
		return variable;
	}

	@Override
	public String toString() {
		return variable.toString() + ":=" + formula.toString() + super.toString();
	}

}
