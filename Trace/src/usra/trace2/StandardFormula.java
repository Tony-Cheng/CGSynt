package usra.trace2;

import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Term;

public class StandardFormula extends Formula {

	private final String var1;
	private final String var2;
	private final Script script;
	private final String operator;

	public StandardFormula(Script script, String operator, String var1, String var2) {
		this.script = script;
		this.var1 = var1;
		this.var2 = var2;
		this.operator = operator;
	}

	@Override
	public Term getTerm(int id) {
		return script.term(operator, script.term(var1 + "_" + id), script.term(var2 + "_" + id));
	}

}
