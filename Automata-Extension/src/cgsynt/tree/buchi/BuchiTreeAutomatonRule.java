package cgsynt.tree.buchi;

import java.util.List;

import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;
import de.uni_freiburg.informatik.ultimate.util.HashUtils;

/**
 * Rule of a BuchiTreeAutomaton.
 *
 * @param <LETTER> Letters of the automation.
 * @param <STATE> States of the automation.
 */
public class BuchiTreeAutomatonRule<LETTER extends IRankedLetter, STATE> {
	private final LETTER mLetter;
	private final STATE mSrc;
	private final List<STATE> mDest;

	/**
	 * Construct a rule: letter(src) -> dest
	 * @param letter
	 * @param src
	 * @param dest
	 */
	public BuchiTreeAutomatonRule(final LETTER letter, final STATE src, final List<STATE> dest) {
		if (letter.getRank() != dest.size()) {
			throw new Error();
		}
		this.mLetter = letter;
		this.mSrc = src;
		this.mDest = dest;
	}

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
	public boolean equals(final Object x) {
		if (!(x instanceof BuchiTreeAutomatonRule)) {
			return false;
		}
		@SuppressWarnings("unchecked")
		final BuchiTreeAutomatonRule<LETTER, STATE> t = (BuchiTreeAutomatonRule<LETTER, STATE>) x;
		if (!mSrc.equals(t.mSrc) || !mLetter.equals(t.mLetter) || t.mDest.size() != mDest.size()) {
			return false;
		}
		for (int i = 0; i < mDest.size(); ++i) {
			if (!mDest.get(i).equals(t.mDest.get(i))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		return HashUtils.hashHsieh(31, mDest, mSrc, mLetter);
	}

}
