package cgsynt.tree.parity;

public class ParityIntersectState<STATE1 extends IParityState, STATE2 extends IParityState, STATE3 extends IParityState> {
	public final STATE1 state1;
	public final STATE2 state2;
	public final STATE3 state3;
	public final boolean isGood1;
	public final boolean isGood2;
	public final boolean isGood3;

	public ParityIntersectState(STATE1 state1, STATE2 state2, STATE3 state3, boolean isGood1, boolean isGood2,
			boolean isGood3) {
		this.state1 = state1;
		this.state2 = state2;
		this.state3 = state3;
		this.isGood1 = isGood1;
		this.isGood2 = isGood2;
		this.isGood3 = isGood3;
	}

	public ParityIntersectState(STATE1 state1, STATE2 state2, STATE3 state3) {
		this.state1 = state1;
		this.state2 = state2;
		this.state3 = state3;
		this.isGood1 = true;
		this.isGood2 = true;
		this.isGood3 = true;
	}

	public ParityIntersectState<STATE1, STATE2, STATE3> getGoodIntersectState() {
		return new ParityIntersectState<>(state1, state2, state3, true, true, true);
	}

	public ParityIntersectState<STATE1, STATE2, STATE3> copy(boolean isGood1, boolean isGood2, boolean isGood3) {
		return new ParityIntersectState<>(state1, state2, state3, isGood1, isGood2, isGood3);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isGood1 ? 1231 : 1237);
		result = prime * result + (isGood2 ? 1231 : 1237);
		result = prime * result + (isGood3 ? 1231 : 1237);
		result = prime * result + ((state1 == null) ? 0 : state1.hashCode());
		result = prime * result + ((state2 == null) ? 0 : state2.hashCode());
		result = prime * result + ((state3 == null) ? 0 : state3.hashCode());
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
		ParityIntersectState other = (ParityIntersectState) obj;
		if (isGood1 != other.isGood1)
			return false;
		if (isGood2 != other.isGood2)
			return false;
		if (isGood3 != other.isGood3)
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
		if (state3 == null) {
			if (other.state3 != null)
				return false;
		} else if (!state3.equals(other.state3))
			return false;
		return true;
	}
}