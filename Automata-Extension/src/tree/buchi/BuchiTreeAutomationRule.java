package tree.buchi;

import java.util.List;

import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;
import de.uni_freiburg.informatik.ultimate.util.HashUtils;

public class BuchiTreeAutomationRule<LETTER extends IRankedLetter, STATE> {
	private final LETTER mLetter;
	private final STATE mSrc;
	private final List<STATE> mDest;

	public BuchiTreeAutomationRule(final LETTER letter, final STATE src, final List<STATE> dest) {
		assert letter.getRank() == dest.size();
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
	public boolean equals(final Object x) {
		if (!(x instanceof BuchiTreeAutomationRule)) {
			return false;
		}
		@SuppressWarnings("unchecked")
		final BuchiTreeAutomationRule<LETTER, STATE> t = (BuchiTreeAutomationRule<LETTER, STATE>) x;
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
