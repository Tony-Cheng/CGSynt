package usra.trace2;

import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Term;

public class Assume extends Statement {

	private final Formula formula;

	public Assume(Formula formula) {
		super(false);
		this.formula = formula;
	}

	@Override
	public Term getTerm(Script script, int id) {
		return formula.getTerm(script, id);
	}
	
	

}
