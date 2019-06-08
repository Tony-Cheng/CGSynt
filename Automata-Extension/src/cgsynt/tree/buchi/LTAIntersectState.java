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

}
