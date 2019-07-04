package cgsynt.tree.buchi;

/**
 * A state in the intersection of two Buchi trees.
 * 
 * @param <STATE>
 */
public class IntersectState<STATE1, STATE2> {
	@Override
	public String toString() {
		return "IntersectState [state1=" + state1 + ", state2=" + state2 + ", mode=" + mode + "]";
	}

	/**
	 * A state in the first Buchi tree.
	 */
	private final STATE1 state1;
	/**
	 * A state in the second Buchi tree.
	 */
	private final STATE2 state2;
	/**
	 * mode = 1 if the automaton is looking for a final state in the first Buchi
	 * tree. mode = 2 if the automaton is looking for a final state in the second
	 * Buchi tree.
	 */
	private final int mode;

	/**
	 * Create a state in the intersection of two Buchi trees.
	 * 
	 * @param state1
	 *            a state in the first Buchi tree
	 * @param state2
	 *            a state in the second Buchi tree
	 * @param mode
	 *            1 if the automaton is looking for a final state in the first tree
	 *            and 2 if the automaton is looking for a final state in the second
	 *            tree
	 */
	public IntersectState(STATE1 state1, STATE2 state2, int mode) {
		this.state1 = state1;
		this.state2 = state2;
		this.mode = mode;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + mode;
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
		IntersectState other = (IntersectState) obj;
		if (mode != other.mode)
			return false;
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

	public STATE1 getState1() {
		return state1;
	}

	public STATE2 getState2() {
		return state2;
	}

	public int getMode() {
		return mode;
	}

}
