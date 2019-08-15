package cgsynt.tree.buchi.parity;

import java.util.HashMap;
import java.util.Map;

import cgsynt.tree.parity.IParityState;

public class BuchiParityIntersectState<STATE1, STATE2 extends IParityState> implements IParityState {
	private final BuchiParityPair<STATE1, STATE2> state;
	private final int k;
	private final Map<BuchiParityPair<STATE1, STATE2>, Integer> visitedStates;

	public BuchiParityIntersectState(BuchiParityPair<STATE1, STATE2> state) {
		super();
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
	}

	public int getK() {
		return k;
	}

	public int getRank() {
		return state.getState2().getRank();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + k;
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result + ((visitedStates == null) ? 0 : visitedStates.hashCode());
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
		if (k != other.k)
			return false;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		if (visitedStates == null) {
			if (other.visitedStates != null)
				return false;
		} else if (!visitedStates.equals(other.visitedStates))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BuchiParityIntersectState [state=" + state + ", k=" + k + ", \nvisitedStates=" + visitedStates
				+ "]";
	}

}
