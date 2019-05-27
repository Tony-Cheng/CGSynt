package usra.trace2;

import de.uni_freiburg.informatik.ultimate.logic.Term;

public class CraigInterpolant {

	private final Term interpolant;
	
	public CraigInterpolant(Term interpolant) {
		this.interpolant = interpolant;
	}
	
	public Term getTerm() {
		return interpolant;
	}
}
