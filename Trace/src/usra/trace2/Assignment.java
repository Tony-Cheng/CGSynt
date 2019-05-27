package usra.trace2;

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
		return script.term("=", script.term(variable.getName(id)), formula.getTerm(script, id - 1));
	}
	
	public Variable getVariable() {
		return variable;
	}

}
