package cgsynt.tree.parity;

import java.util.List;

import cgsynt.tree.buchi.BuchiTreeAutomatonRule;
import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;
import de.uni_freiburg.informatik.ultimate.util.HashUtils;

/**
 * A transition rule for the parity tree automaton.
 *
 * @param <LETTER>
 * @param <STATE>
 */
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
	 * Return the source state of the rule.
	 * 
	 * @return
	 */
	public STATE getSource() {
		return mSrc;
	}

	/**
	 * Return the transition letter of the rule.
	 * 
	 * @return
	 */
	public LETTER getLetter() {
		return mLetter;
	}

	/**
	 * Return the destination of the rule.
	 * 
	 * @return
	 */
	public List<STATE> getDest() {
		return mDest;
	}

	/**
	 * Return the arity (number of states in the destination) of the rule.
	 * 
	 * @return
	 */
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
