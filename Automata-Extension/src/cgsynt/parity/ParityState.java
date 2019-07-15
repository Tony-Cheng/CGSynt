package cgsynt.parity;

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
	public int getRank() {
		return rank;
	}

}
