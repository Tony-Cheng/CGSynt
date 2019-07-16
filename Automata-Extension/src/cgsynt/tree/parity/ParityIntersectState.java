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
}