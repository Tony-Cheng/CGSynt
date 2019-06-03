package cgsynt.trace2;

import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Term;

/**
 * A number in the trace.
 *
 */
public class Numerical extends Token {

	private final String num;

	public Numerical(String num) {
		this.num = num;
	}

	@Override
	public String getName(int id) {
		return num;
	}

	@Override
	public String getName() {
		return num;
	}

	@Override
	public Term getTerm(Script script, int id) {
		return script.numeral(num);
	}

	@Override
	public String toString() {
		return num;
	}

}
