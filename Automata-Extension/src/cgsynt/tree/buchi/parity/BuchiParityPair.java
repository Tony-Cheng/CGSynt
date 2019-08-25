package cgsynt.tree.buchi.parity;

import cgsynt.tree.parity.IParityState;

public class BuchiParityPair<STATE1, STATE2 extends IParityState> {

	private final STATE1 state1;
	private final STATE2 state2;

	public BuchiParityPair(STATE1 state1, STATE2 state2) {
		super();
		this.state1 = state1;
		this.state2 = state2;
	}

	@Override
	public String toString() {
		return "BuchiParityPair [state1=" + state1 + ", state2=" + state2 + "]";
	}

	public STATE1 getState1() {
		return state1;
	}


	public STATE2 getState2() {
		return state2;
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
		BuchiParityPair other = (BuchiParityPair) obj;
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
