package cgsynt.tree.parity;

import java.util.List;

import cgsynt.tree.buchi.BuchiTreeAutomatonRule;
import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;
import de.uni_freiburg.informatik.ultimate.util.HashUtils;

public class ParityTreeAutomatonRule<LETTER extends IRankedLetter, STATE extends IParityState> {

	private final LETTER mLetter;
	private final STATE mSrc;
	private final List<STATE> mDest;

	/**
	 * Construct a rule: letter(src) -> dest
	 * 
	 * @param letter
	 * @param src
	 * @param dest
	 */
	public ParityTreeAutomatonRule(final LETTER letter, final STATE src, final List<STATE> dest) {
		if (letter.getRank() != dest.size()) {
			throw new Error();
		}
		this.mLetter = letter;
		this.mSrc = src;
		this.mDest = dest;
	}

	/**
	 * Get
	 * 
	 * @return
	 */
	public STATE getSource() {
		return mSrc;
	}

	public LETTER getLetter() {
		return mLetter;
	}

	public List<STATE> getDest() {
		return mDest;
	}

	public int getArity() {
		return mLetter.getRank();
	}

	@Override
	public String toString() {
		return "(" + mSrc.toString() + " | " + mLetter.toString() + " | " + mDest.toString() + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mDest == null) ? 0 : mDest.hashCode());
		result = prime * result + ((mLetter == null) ? 0 : mLetter.hashCode());
		result = prime * result + ((mSrc == null) ? 0 : mSrc.hashCode());
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
		ParityTreeAutomatonRule other = (ParityTreeAutomatonRule) obj;
		if (mDest == null) {
			if (other.mDest != null)
				return false;
		} else if (!mDest.equals(other.mDest))
			return false;
		if (mLetter == null) {
			if (other.mLetter != null)
				return false;
		} else if (!mLetter.equals(other.mLetter))
			return false;
		if (mSrc == null) {
			if (other.mSrc != null)
				return false;
		} else if (!mSrc.equals(other.mSrc))
			return false;
		return true;
	}

}
