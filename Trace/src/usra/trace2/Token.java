package usra.trace2;

import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Term;

public abstract class Token {
	
	/**
	 * Return the name of this token given an id.
	 * @param id
	 * @return
	 */
	public abstract String getName(int id);

	/**
	 * Return the name of this token without the id.
	 * @return
	 */
	public abstract String getName();
	
	/**
	 * Return a term that represents this token.
	 * @param script
	 * @param id
	 * @return
	 */
	public abstract Term getTerm(Script script, int id);

}
