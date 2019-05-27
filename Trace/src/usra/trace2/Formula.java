package usra.trace2;

import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Term;

public abstract class Formula {

	/**
	 * Return the term that represents this formula.
	 * 
	 * @param script
	 * @param id
	 * @return
	 */
	public abstract Term getTerm(Script script, int id);

}
