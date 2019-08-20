package cgsynt.tree.buchi.parity;

import java.util.HashMap;
import java.util.Map;

import cgsynt.tree.parity.IExtendedParityState;
import cgsynt.tree.parity.IParityState;

public class BuchiParityIntersectStateV2<STATE1, STATE2 extends IParityState> implements IExtendedParityState {
	private static int LABEL = 0;

	private final BuchiParityPair<STATE1, STATE2> mState;
	private final int mN;
	private int mStateLabel;
	private boolean isFinal;

	public BuchiParityIntersectStateV2(BuchiParityPair<STATE1, STATE2> state, int n, boolean isFinal) {
		super();
		this.mState = state;
		this.mN = n;
		this.isFinal = isFinal;
	}

	public BuchiParityIntersectStateV2<STATE1, STATE2> nextState(BuchiParityPair<STATE1, STATE2> state) {
		if (!isFinal) {
			return new BuchiParityIntersectStateV2<>(state, Math.max(state.getState2().getRank(), this.mN), isFinal);
		} else {
			return new BuchiParityIntersectStateV2<>(state, state.getState2().getRank(), isFinal);
		}
	}

	public BuchiParityPair<STATE1, STATE2> getState() {
		return mState;
	}

	public int getN() {
		return mN;
	}

	public int getRank() {

		return mState.getState2().getRank();
	}

	@Override
	public void setRank(int newRank) {
		assert mState.getState2() instanceof IExtendedParityState;

		((IExtendedParityState) mState).setRank(newRank);
	}

	public IParityState makeCopy() {
		BuchiParityIntersectStateV2<STATE1, STATE2> newState = new BuchiParityIntersectStateV2<STATE1, STATE2>(
				this.mState, mN, isFinal);
		return newState;
	}

	@Override
	public String toString() {
		return "BuchiParityIntersectState [state=" + mState + ", k=" + mN + "]";
	}

}
