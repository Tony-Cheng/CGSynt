package cgsynt.alternating.parity;

import java.util.Set;

import cgsynt.tree.parity.IParityState;

public class AlternatingParityAutomatonRule<LETTER, STATE extends IParityState> {
	private STATE source;
	private LETTER letter;
	private Set<Set<STATE>> dests;

	public AlternatingParityAutomatonRule(STATE source, LETTER letter, Set<Set<STATE>> dests) {
		super();
		this.source = source;
		this.letter = letter;
		this.dests = dests;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dests == null) ? 0 : dests.hashCode());
		result = prime * result + ((letter == null) ? 0 : letter.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
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
		AlternatingParityAutomatonRule other = (AlternatingParityAutomatonRule) obj;
		if (dests == null) {
			if (other.dests != null)
				return false;
		} else if (!dests.equals(other.dests))
			return false;
		if (letter == null) {
			if (other.letter != null)
				return false;
		} else if (!letter.equals(other.letter))
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AlternatingParityAutomatonRule [source=" + source + ", letter=" + letter + ", dests=" + dests + "]";
	}

}
