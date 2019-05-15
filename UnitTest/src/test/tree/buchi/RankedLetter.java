package test.tree.buchi;

import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;

public class RankedLetter implements IRankedLetter {
	private final char mLetter;
	private final int mRank;
	
	@Override
	public String toString() {
		return "" + mLetter;
	}

	public RankedLetter(char letter, int rank) {
		this.mLetter = letter;
		this.mRank = rank;
	}

	@Override
	public int getRank() {
		return this.mRank;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + mLetter;
		result = prime * result + mRank;
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
		RankedLetter other = (RankedLetter) obj;
		if (mLetter != other.mLetter)
			return false;
		if (mRank != other.mRank)
			return false;
		return true;
	}
}
