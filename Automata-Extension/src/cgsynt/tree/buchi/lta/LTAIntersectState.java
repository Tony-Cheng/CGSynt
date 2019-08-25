package cgsynt.tree.buchi.lta;

/**
 * A state of the intersection between two LTAs.
 *
 * @param <STATE1>
 * @param <STATE2>
 */
public class LTAIntersectState<STATE1, STATE2> {
	@Override
	public String toString() {
		return "LTAIntersectState [state1=" + state1 + ", state2=" + state2 + "]";
	}

	/**
	 * A state in one of the LTAs.
	 */
	private final STATE1 state1;

	/**
	 * A state in the other LTA.
	 */
	private final STATE2 state2;

	/**
	 * Return the state in one of the LTAs.
	 * 
	 * @return
	 */
	public STATE1 getState1() {
		return state1;
	}

	/**
	 * Return the state in the other LTA.
	 * 
	 * @return
	 */
	public STATE2 getState2() {
		return state2;
	}

	public LTAIntersectState(STATE1 state1, STATE2 state2) {
		super();
		this.state1 = state1;
		this.state2 = state2;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((state1 == null) ? 0 : state1.hashCode());
		result = prime * result + ((state2 == null) ? 0 : state2.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LTAIntersectState other = (LTAIntersectState) obj;
		if (state1 == null) {
			if (other.state1 != null)
				return false;
		} else if (!state1.equals(other.state1))
			return false;
		if (state2 == null) {
			if (other.state2 != null)
				return false;
		} else if (!state2.equals(other.state2))
			return false;
		return true;
	}

}
