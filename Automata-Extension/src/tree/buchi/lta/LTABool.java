package tree.buchi.lta;

import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;

public class LTABool implements IRankedLetter{
	private final boolean mLetter;
	
	public LTABool(final boolean letter) {
		this.mLetter = letter;
	}
	
	@Override
	public String toString() {
		return (this.mLetter == true) ? "⊤" : "⊥";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.mLetter == false) ? 0 : 1);
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
		LTABool other = (LTABool) obj;
		if (this.mLetter != other.mLetter)
			return false;
		return true;
	}
	
	public int getRank() {
		return 2;
	}
}
