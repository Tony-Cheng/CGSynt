import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;

public class TestRankedAlphabet implements IRankedLetter {
	private final char letter;

	@Override
	public String toString() {
		return "" + letter;
	}

	public TestRankedAlphabet(char letter) {
		super();
		this.letter = letter;
	}

	@Override
	public int getRank() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + letter;
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
		TestRankedAlphabet other = (TestRankedAlphabet) obj;
		if (letter != other.letter)
			return false;
		return true;
	}

}
