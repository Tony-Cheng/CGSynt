package usra.trace2;

import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Term;

public abstract class Token {
	public abstract String getName(int id);

	public abstract String getName();
	
	public abstract Term getTerm(Script script, int id);

}
