package cgsynt.nfa.operations;

public class NFAIntersectedState<STATE1, STATE2> {

	public final STATE1 state1;
	public final STATE2 state2;
	public final int mode;

	public NFAIntersectedState(STATE1 state1, STATE2 state2, int mode) {
		this.state1 = state1;
		this.state2 = state2;
		this.mode = mode;
	}
}
