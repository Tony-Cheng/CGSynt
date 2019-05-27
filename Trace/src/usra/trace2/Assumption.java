package usra.trace2;

import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Term;

public class Assumption extends Statement {

	private final Formula formula;

	public Assumption(Formula formula) {
		super(false);
		this.formula = formula;
	}

	@Override
	public Term getTerm(Script script, int id) {
		return formula.getTerm(script, id);
	}
	
	

}
