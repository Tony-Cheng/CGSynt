package usra.trace2;

import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Term;

public class Assignment extends Statement {

	private final String variable;
	private final Formula formula;
	private final Script script;

	public Assignment(Script script, String variable, Formula formula) {
		super(true);
		this.variable = variable;
		this.formula = formula;
		this.script = script;
	}

	@Override
	public Term getTerm(int id) {
		return script.term("=", script.term(variable + "_" + id), formula.getTerm(id - 1));
	}

}
