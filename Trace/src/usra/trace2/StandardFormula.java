package usra.trace2;

import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Term;

public class StandardFormula extends Formula {

	private final Variable var1;
	private final Variable var2;
	private final String operator;

	public StandardFormula(String operator, Variable var1, Variable var2) {
		this.var1 = var1;
		this.var2 = var2;
		this.operator = operator;
	}

	@Override
	public Term getTerm(Script script, int id) {
		return script.term(operator, script.term(var1.getName(id)), script.term(var2.getName(id)));
	}

}
