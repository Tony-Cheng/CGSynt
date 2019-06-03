package cgsynt.trace2;

import de.uni_freiburg.informatik.ultimate.logic.Term;

public class CraigInterpolant {

	private final Term[] interpolant;

	public CraigInterpolant(Term[] interpolant) {
		this.interpolant = interpolant;
	}

	/**
	 * Return the terms of the interpolants.
	 * 
	 * @return
	 */
	public Term[] getTerms() {
		return interpolant;
	}
}
