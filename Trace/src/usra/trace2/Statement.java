package usra.trace2;

import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Term;

public abstract class Statement {

	private final boolean isAssignment;

	public Statement(boolean isAssignment) {
		this.isAssignment = isAssignment;
	}

	/**
	 * Return true if the statement is an assignment statement and false otherwise.
	 * @return
	 */
	public boolean isAssignment() {
		return isAssignment;
	}

	/**
	 * Return a term that represents this statement.
	 * @param script
	 * @param id
	 * @return
	 */
	public abstract Term getTerm(Script script, int id);

	@Override
	public String toString() {
		return "";
	}
}
