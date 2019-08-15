package cgsynt.tree.buchi.parity;

<<<<<<< HEAD
import cgsynt.tree.parity.IExtendedParityState;
import cgsynt.tree.parity.IParityState;

public class BuchiParityIntersectState<STATE1, STATE2 extends IParityState> implements IExtendedParityState {
	private final STATE1 mState1;
	private final STATE2 mState2;
	private final int mK;
=======
import java.util.HashMap;
import java.util.Map;

import cgsynt.tree.parity.IParityState;

public class BuchiParityIntersectState<STATE1, STATE2 extends IParityState> implements IParityState {
	private final BuchiParityPair<STATE1, STATE2> state;
	private final int k;
	private final Map<BuchiParityPair<STATE1, STATE2>, Integer> visitedStates;
>>>>>>> master

	public BuchiParityIntersectState(BuchiParityPair<STATE1, STATE2> state) {
		super();
<<<<<<< HEAD
		this.mState1 = state1;
		this.mState2 = state2;
		this.mK = k;
	}

	public STATE1 getState1() {
		return mState1;
	}

	public STATE2 getState2() {
		return mState2;
=======
		this.state = state;
		this.k = state.getState2().getRank();
		this.visitedStates = new HashMap<>();
	}

	private BuchiParityIntersectState(BuchiParityPair<STATE1, STATE2> state, int k) {
		super();
		this.state = state;
		this.k = k;
		this.visitedStates = new HashMap<>();
	}

	public BuchiParityIntersectState<STATE1, STATE2> nextState(BuchiParityPair<STATE1, STATE2> state, int k) {
		BuchiParityIntersectState<STATE1, STATE2> nextState = new BuchiParityIntersectState<>(state,
				Math.max(k, this.k));
		nextState.visitedStates.putAll(visitedStates);
		nextState.visitedStates.put(state, nextState.k);
		return nextState;
	}

	public Map<BuchiParityPair<STATE1, STATE2>, Integer> getVisitedStates() {
		return visitedStates;
	}

	public BuchiParityPair<STATE1, STATE2> getState() {
		return state;
>>>>>>> master
	}

	public int getK() {
		return mK;
	}

	public int getRank() {
<<<<<<< HEAD
		return mState2.getRank();
=======
		return state.getState2().getRank();
>>>>>>> master
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
<<<<<<< HEAD
		result = prime * result + mK;
		result = prime * result + ((mState1 == null) ? 0 : mState1.hashCode());
		result = prime * result + ((mState2 == null) ? 0 : mState2.hashCode());
=======
		result = prime * result + k;
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result + ((visitedStates == null) ? 0 : visitedStates.hashCode());
>>>>>>> master
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
		BuchiParityIntersectState other = (BuchiParityIntersectState) obj;
		if (mK != other.mK)
			return false;
<<<<<<< HEAD
		if (mState1 == null) {
			if (other.mState1 != null)
				return false;
		} else if (!mState1.equals(other.mState1))
			return false;
		if (mState2 == null) {
			if (other.mState2 != null)
				return false;
		} else if (!mState2.equals(other.mState2))
=======
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		if (visitedStates == null) {
			if (other.visitedStates != null)
				return false;
		} else if (!visitedStates.equals(other.visitedStates))
>>>>>>> master
			return false;
		return true;
	}

	@Override
<<<<<<< HEAD
	public void setRank(int newRank) {
		assert mState2 instanceof IExtendedParityState;
		
		((IExtendedParityState)mState2).setRank(newRank);
	}

	public IParityState makeCpy() {
		return new BuchiParityIntersectState<STATE1, STATE2>(this.mState1, this.mState2, this.mK);
	}

	@Override
	public IParityState getSimpleRepresentation() {
 		return this.makeCpy();
	}
=======
	public String toString() {
		return "BuchiParityIntersectState [state=" + state + ", k=" + k + ", \nvisitedStates=" + visitedStates
				+ "]";
	}

>>>>>>> master
}
