package cgsynt.tree.parity;

import java.util.HashSet;
import java.util.Set;

import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;

public class ParityTreeRemoveAllLeaves<LETTER extends IRankedLetter, STATE extends IParityState> {

	private ParityTreeAutomaton<LETTER, STATE> aut;
	private boolean resultComputed;

	public ParityTreeRemoveAllLeaves(ParityTreeAutomaton<LETTER, STATE> aut) {
		this.aut = aut.copy();
	}

	public void computeResult() {
		if (resultComputed)
			return;
		resultComputed = true;
		while (removeLeaves()) {

		}
	}

	private boolean removeLeaves() {
		boolean nodeRemoved = false;
		Set<STATE> toRemove = new HashSet<>();
		for (STATE state : this.aut.getStates()) {
			if (aut.getSourceMap().get(state) == null || aut.getSourceMap().get(state).isEmpty()) {
				toRemove.add(state);
				nodeRemoved = true;
			}
		}
		for (STATE state : toRemove) {
			aut.removeState(state);
		}
		return nodeRemoved;
	}

	public ParityTreeAutomaton<LETTER, STATE> getResult() {
		return aut;
	}
}
