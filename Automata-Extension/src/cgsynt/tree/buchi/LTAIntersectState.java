package cgsynt.tree.buchi;

public class LTAIntersectState<STATE> {
	private final STATE state1;
	private final STATE state2;
	public STATE getState1() {
		return state1;
	}
	public STATE getState2() {
		return state2;
	}
	public LTAIntersectState(STATE state1, STATE state2) {
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
