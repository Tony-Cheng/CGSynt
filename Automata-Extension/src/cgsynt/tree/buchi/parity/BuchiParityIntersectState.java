package cgsynt.tree.buchi.parity;

import java.util.HashMap;
import java.util.Map;

import cgsynt.tree.parity.IExtendedParityState;
import cgsynt.tree.parity.IParityState;

public class BuchiParityIntersectState<STATE1, STATE2 extends IParityState> implements IExtendedParityState {
	private static int LABEL = 0;

	private final BuchiParityPair<STATE1, STATE2> mState;
	private final int mK;
	private final Map<BuchiParityPair<STATE1, STATE2>, Integer> visitedStates;
	private int mStateLabel;

	public BuchiParityIntersectState(BuchiParityPair<STATE1, STATE2> state) {
		super();
		this.mState = state;
		this.mK = state.getState2().getRank();
		this.visitedStates = new HashMap<>();
		this.mStateLabel = LABEL++;
	}

	private BuchiParityIntersectState(BuchiParityPair<STATE1, STATE2> state, int k) {
		super();
		this.mState = state;
		this.mK = k;
		this.visitedStates = new HashMap<>();
	}

	public BuchiParityIntersectState<STATE1, STATE2> nextState(BuchiParityPair<STATE1, STATE2> state, int k) {
		BuchiParityIntersectState<STATE1, STATE2> nextState = new BuchiParityIntersectState<>(state,
				Math.max(k, this.mK));
		nextState.visitedStates.putAll(visitedStates);
		nextState.visitedStates.put(state, nextState.mK);
		return nextState;
	}

	public Map<BuchiParityPair<STATE1, STATE2>, Integer> getVisitedStates() {
		return visitedStates;
	}

	public BuchiParityPair<STATE1, STATE2> getState() {
		return mState;
	}

	public int getK() {
		return mK;
	}

	public int getRank() {
		return mState.getState2().getRank();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + mK;
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
		BuchiParityIntersectState<STATE1, STATE2> other = (BuchiParityIntersectState<STATE1, STATE2>) obj;
		if (mK != other.mK)
			return false;
		if (mState == null) {
			if (other.mState != null)
				return false;
		} else if (!mState.equals(other.mState))
			return false;
		if (visitedStates == null) {
			if (other.visitedStates != null)
				return false;
		}
		if (!other.visitedStates.keySet().containsAll(visitedStates.keySet())) {
			return false;
		}
		if (other.visitedStates.keySet().size() != visitedStates.keySet().size()) {
			return false;
		}
		for (BuchiParityPair<STATE1, STATE2> pair : visitedStates.keySet()) {
			if (other.visitedStates.get(pair) != visitedStates.get(pair)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void setRank(int newRank) {
		assert mState.getState2() instanceof IExtendedParityState;

		((IExtendedParityState) mState).setRank(newRank);
	}

	public IParityState makeCopy() {
		return new BuchiParityIntersectState<STATE1, STATE2>(this.mState);
	}

	@Override
	public String toString() {
		return "BuchiParityIntersectState [state=" + mState + ", k=" + mK + ", \nvisitedStates=" + visitedStates + "]";
	}

}
