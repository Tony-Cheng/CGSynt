package usra.trace2;

import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Term;

/**
 * A formula that has two variables and one operator.
 *
 */
public class StandardFormula extends Formula {

	private final Token var1;
	private final Token var2;
	private final String operator;

	public StandardFormula(String operator, Token var1, Token var2) {
		this.var1 = var1;
		this.var2 = var2;
		this.operator = operator;
	}

	@Override
	public Term getTerm(Script script, int id) {
		return script.term(operator, var1.getTerm(script, id), var2.getTerm(script, id));
	}

}
