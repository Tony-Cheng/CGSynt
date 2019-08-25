package cgsynt.tree.buchi.lta;

import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;

/**
 * An implementation of IRankedLetter using bookean values.
 *
 */
public class RankedBool implements IRankedLetter {

	private static int rank = 1;
	private final boolean bool;

	public static final RankedBool TRUE = new RankedBool(true);
	public static final RankedBool FALSE = new RankedBool(false);

	/**
	 * Set the rank of this alphabet.
	 * 
	 * @param rank
	 */
	public static void setRank(int rank) {
		RankedBool.rank = rank;
	}

	private RankedBool(boolean bool) {
		this.bool = bool;
	}

	/**
	 * Return the boolean that this letter represents.
	 * 
	 * @return
	 */
	public boolean getBool() {
		return bool;
	}

	@Override
	public int getRank() {
		return rank;
	}

	@Override
	public String toString() {
		return "" + bool;
	}

}
