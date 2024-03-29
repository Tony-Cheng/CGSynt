package cgsynt.tree.buchi.parity;

import java.util.HashMap;
import java.util.Map;

import cgsynt.tree.parity.IExtendedParityState;
import cgsynt.tree.parity.IParityState;

/**
 * A state in the intersection between a buchi and a parity automaton.
 *
 * @param <STATE1>
 * @param <STATE2>
 */
public class BuchiParityIntersectStateV2<STATE1, STATE2 extends IParityState> implements IExtendedParityState {
	private static int LABEL = 0;

	private final BuchiParityPair<STATE1, STATE2> mState;
	private final int mN;
	private int mStateLabel;
	private boolean isFinal;

	public BuchiParityIntersectStateV2(BuchiParityPair<STATE1, STATE2> state, boolean isFinal) {
		super();
		this.mState = state;
		this.mN = state.getState2().getRank();
		this.isFinal = isFinal;
	}

	private BuchiParityIntersectStateV2(BuchiParityPair<STATE1, STATE2> state, int n, boolean isFinal) {
		super();
		this.mState = state;
		this.mN = n;
		this.isFinal = isFinal;
	}

	public BuchiParityIntersectStateV2<STATE1, STATE2> nextState(BuchiParityPair<STATE1, STATE2> state,
			boolean isFinal) {
		return new BuchiParityIntersectStateV2<>(state, Math.max(state.getState2().getRank(), this.mN), isFinal);
	}

	public BuchiParityPair<STATE1, STATE2> getState() {
		return mState;
	}

	public int getN() {
		return mN;
	}

	public int getRank() {
		if (isFinal)
			return mN;
		else
			return 1;
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isFinal ? 1231 : 1237);
		result = prime * result + mN;
		result = prime * result + ((mState == null) ? 0 : mState.hashCode());
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
		BuchiParityIntersectStateV2 other = (BuchiParityIntersectStateV2) obj;
		if (isFinal != other.isFinal)
			return false;
		if (mN != other.mN)
			return false;
		if (mState == null) {
			if (other.mState != null)
				return false;
		} else if (!mState.equals(other.mState))
			return false;
		return true;
	}

	public boolean isFinal() {
		return isFinal;
	}

	@Override
	public String toString() {
		return "BuchiParityIntersectState [state=" + mState + ", k=" + mN + "]";
	}

}
