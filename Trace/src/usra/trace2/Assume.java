package usra.trace2;

import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Term;

public class Assume extends Statement {

	private final Script script;
	private final Formula formula;
	
	public Assume(Script script, Formula formula) {
		super(true);
		this.script = script;
		this.formula = formula;
	}

	@Override
	public Term getTerm(int id) {
		return formula.getTerm(id);
	}

}
