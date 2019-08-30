package cgsynt.parity.games;

import cgsynt.tree.parity.IParityState;
import cgsynt.tree.parity.ParityTreeAutomatonRule;
import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;

/**
 * The eva staet in the paper:
 * https://pdfs.semanticscholar.org/518d/e2ba06edf8eb09af78594946f741a38def26.pdf
 *
 * @param <LETTER>
 * @param <STATE>
 */
public class EvaState<LETTER extends IRankedLetter, STATE extends IParityState> implements IParityGameState {
	private ParityTreeAutomatonRule<LETTER, STATE> rule;

	public EvaState(ParityTreeAutomatonRule<LETTER, STATE> rule) {
		super();
		this.rule = rule;
	}

	public ParityTreeAutomatonRule<LETTER, STATE> getRule() {
		return rule;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((rule == null) ? 0 : rule.hashCode());
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
		EvaState other = (EvaState) obj;
		if (rule == null) {
			if (other.rule != null)
				return false;
		} else if (!rule.equals(other.rule))
			return false;
		return true;
	}

	@Override
	public boolean isEva() {
		return true;
	}

	@Override
	public int getRank() {
		return rule.getSource().getRank();
	}

}
