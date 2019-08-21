package cgsynt.parity.games;

import cgsynt.tree.parity.IParityState;

public class AdamState<STATE extends IParityState> implements IParityGameState {
	private STATE state;

	public AdamState(STATE state) {
		super();
		this.state = state;
	}

	public STATE getState() {
		return state;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		AdamState other = (AdamState) obj;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		return true;
	}

	@Override
	public boolean isEva() {
		return false;
	}

	@Override
	public int getRank() {
		return state.getRank();
	}

}
