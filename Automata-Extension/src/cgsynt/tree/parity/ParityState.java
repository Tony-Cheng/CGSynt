package cgsynt.tree.parity;

public class ParityState<STATE> implements IParityState {
	private STATE state;
	private int rank;

	public ParityState(STATE state, int rank) {
		this.state = state;
		this.rank = rank;
	}

	public STATE getState() {
		return state;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + rank;
		result = prime * result + ((state == null) ? 0 : state.hashCode());
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
		@SuppressWarnings("unchecked")
		ParityState<STATE> other = (ParityState<STATE>) obj;
		if (rank != other.rank)
			return false;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		return true;
	}

	@Override
	public int getRank() {
		return rank;
	}

	@Override
	public String toString() {
		return state + ": " + rank;
	}
}
