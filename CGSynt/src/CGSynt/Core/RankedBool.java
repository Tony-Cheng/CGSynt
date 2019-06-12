package CGSynt.Core;

import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;

public class RankedBool implements IRankedLetter {

	private static int rank = 1;
	private final boolean bool;

	public static final RankedBool TRUE = new RankedBool(true);
	public static final RankedBool FALSE = new RankedBool(false);

	public static void setRank(int rank) {
		RankedBool.rank = rank;
	}

	private RankedBool(boolean bool) {
		this.bool = bool;
	}

	public boolean getBool() {
		return bool;
	}

	@Override
	public int getRank() {
		return rank;
	}

}
