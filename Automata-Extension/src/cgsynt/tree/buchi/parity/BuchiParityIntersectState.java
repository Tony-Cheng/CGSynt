package cgsynt.tree.buchi.parity;

import cgsynt.tree.parity.IParityState;

public class BuchiParityIntersectState<STATE1, STATE2 extends IParityState> {
	private final STATE1 state1;
	private final STATE2 state2;
	private final boolean isGood1;
	private final boolean isGood2;

	public BuchiParityIntersectState(STATE1 state1, STATE2 state2) {
		this.state1 = state1;
		this.state2 = state2;
		this.isGood1 = true;
		this.isGood2 = true;
	}

	public BuchiParityIntersectState(STATE1 state1, STATE2 state2, boolean isGood1, boolean isGood2) {
		this.state1 = state1;
		this.state2 = state2;
		this.isGood1 = isGood1;
		this.isGood2 = isGood2;
	}

	public BuchiParityIntersectState<STATE1, STATE2> getGoodIntersectState() {
		return new BuchiParityIntersectState<STATE1, STATE2>(state1, state2);
	}

	public BuchiParityIntersectState<STATE1, STATE2> copy(boolean isGood1, boolean isGood2) {
		return new BuchiParityIntersectState<STATE1, STATE2>(state1, state2, isGood1, isGood2);
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
		BuchiParityIntersectState other = (BuchiParityIntersectState) obj;
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

	public STATE1 getState1() {
		return state1;
	}

	public STATE2 getState2() {
		return state2;
	}

	public boolean isGood1() {
		return isGood1;
	}

	public boolean isGood2() {
		return isGood2;
	}

}
