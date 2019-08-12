package cgsynt.dfa.parity.intersect;

import cgsynt.tree.parity.IParityState;

public class DfaBuchiIntersectState<STATE1, STATE2 extends IParityState> {

	public final STATE1 state1;
	public final STATE2 state2;

	public DfaBuchiIntersectState(STATE1 state1, STATE2 state2) {
		super();
		this.state1 = state1;
		this.state2 = state2;
	}

}
