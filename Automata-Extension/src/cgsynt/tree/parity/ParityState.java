package cgsynt.tree.parity;

/**
 * Represents a state in the parity tree automaton.
 *
 * @param <STATE>
 */
public class ParityState<STATE> implements IParityState {
	private static int LABEL = 0;

	private STATE mState;
	private int mRank;
	private int mStateLabel;

	public ParityState(STATE state, int rank) {
		this.mState = state;
		this.mRank = rank;
		this.mStateLabel = LABEL++;
	}

	/**
	 * Return the state that this parity state represents.
	 * 
	 * @return
	 */
	public STATE getState() {
		return mState;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + mRank;
		result = prime * result + ((mState == null) ? 0 : mState.hashCode());
		result = prime * result + mStateLabel;
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
		@SuppressWarnings("unchecked")
		ParityState<STATE> other = (ParityState<STATE>) obj;
		if (mRank != other.mRank)
			return false;
		if (mState == null) {
			if (other.mState != null)
				return false;
		} else if (!mState.equals(other.mState))
			return false;
		return true;
	}

	@Override
	public int getRank() {
		return mRank;
	}

	@Override
	public String toString() {
		return mState + ": " + mRank + ":" + mStateLabel;
	}

	public IParityState makeCopy() {
		return new ParityState<STATE>(this.mState, this.mRank);
	}
}
