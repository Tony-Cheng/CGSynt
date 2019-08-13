package cgsynt.tree.buchi.parity;

import cgsynt.tree.parity.IParityState;

/**
 * A representation of a state in the intersection between a buchi tree
 * automaton and a parity tree automaton.
 *
 * @param <STATE1>
 * @param <STATE2>
 */
public class BuchiParityHybridIntersectState<STATE1, STATE2 extends IParityState> {
	@Override
	public String toString() {
		return "BuchiParityIntersectState [state1=" + state1 + ", state2=" + state2 + ", isGood1=" + isGood1
				+ ", isGood2=" + isGood2 + "]";
	}

	/**
	 * A state in the buchi tree automaton.
	 */
	private final STATE1 state1;

	/**
	 * A state in the parity tree automaton.
	 */
	private final STATE2 state2;

	/**
	 * Whether or not state1 is a good state in the emptiness check.
	 */
	private final boolean isGood1;

	/**
	 * Whether or not state2 is a good state in the emptiness check.
	 */
	private final boolean isGood2;

	public BuchiParityHybridIntersectState(STATE1 state1, STATE2 state2) {
		this.state1 = state1;
		this.state2 = state2;
		this.isGood1 = true;
		this.isGood2 = true;
	}

	public BuchiParityHybridIntersectState(STATE1 state1, STATE2 state2, boolean isGood1, boolean isGood2) {
		this.state1 = state1;
		this.state2 = state2;
		this.isGood1 = isGood1;
		this.isGood2 = isGood2;
	}

	/**
	 * Return a copy of this state where state1 and state2 are considered good.
	 * 
	 * @return
	 */
	public BuchiParityHybridIntersectState<STATE1, STATE2> getGoodIntersectState() {
		return new BuchiParityHybridIntersectState<STATE1, STATE2>(state1, state2);
	}

	/**
	 * Make a copy of this state.
	 * 
	 * @param isGood1
	 * @param isGood2
	 * @return
	 */
	public BuchiParityHybridIntersectState<STATE1, STATE2> copy(boolean isGood1, boolean isGood2) {
		return new BuchiParityHybridIntersectState<STATE1, STATE2>(state1, state2, isGood1, isGood2);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isGood1 ? 1231 : 1237);
		result = prime * result + (isGood2 ? 1231 : 1237);
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
		BuchiParityHybridIntersectState other = (BuchiParityHybridIntersectState) obj;
		if (isGood1 != other.isGood1)
			return false;
		if (isGood2 != other.isGood2)
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

	/**
	 * Return the buchi state.
	 * 
	 * @return
	 */
	public STATE1 getState1() {
		return state1;
	}

	/**
	 * Return the parity state.
	 * 
	 * @return
	 */
	public STATE2 getState2() {
		return state2;
	}

	/**
	 * Return whether or not the buchi state is considered good in the emptiness
	 * check.
	 * 
	 * @return
	 */
	public boolean isGood1() {
		return isGood1;
	}

	/**
	 * Return whether or not the parity state is considered good in the emptiness
	 * check.
	 * 
	 * @return
	 */
	public boolean isGood2() {
		return isGood2;
	}

}
