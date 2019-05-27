package usra.trace2;

import de.uni_freiburg.informatik.ultimate.logic.Term;

public abstract class Statement {
	
	private final boolean isAssignment;

	public Statement(boolean isAssignment) {
		this.isAssignment = isAssignment;
	}
	
	public boolean isAssignment() {
		return isAssignment;
	}
	
	public abstract Term getTerm(int id);
	
}
