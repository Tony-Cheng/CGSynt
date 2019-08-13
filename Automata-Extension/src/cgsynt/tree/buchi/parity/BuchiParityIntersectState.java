package cgsynt.tree.buchi.parity;

import cgsynt.tree.parity.IParityState;

public class BuchiParityIntersectState<STATE1, STATE2 extends IParityState> implements IParityState {
	private final STATE1 state1;
	private final STATE2 state2;
	private final int k;

	public BuchiParityIntersectState(STATE1 state1, STATE2 state2, int k) {
		super();
		this.state1 = state1;
		this.state2 = state2;
		this.k = k;
	}

	public STATE1 getState1() {
		return state1;
	}

	public STATE2 getState2() {
		return state2;
	}

	public int getK() {
		return k;
	}

	public int getRank() {
		return state2.getRank();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + k;
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
		BuchiParityIntersectState other = (BuchiParityIntersectState) obj;
		if (k != other.k)
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

}
