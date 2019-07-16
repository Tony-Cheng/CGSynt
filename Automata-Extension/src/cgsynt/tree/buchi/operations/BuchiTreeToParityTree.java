package cgsynt.tree.buchi.operations;

import cgsynt.tree.buchi.BuchiTreeAutomaton;
import cgsynt.tree.parity.ParityState;
import cgsynt.tree.parity.ParityTreeAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;

public class BuchiTreeToParityTree<LETTER extends IRankedLetter, STATE> {
	private BuchiTreeAutomaton<LETTER, STATE> mInAutomaton;
	private ParityTreeAutomaton<LETTER, ParityState<STATE>> mOutAutomaton;
	
	public BuchiTreeToParityTree(BuchiTreeAutomaton<LETTER, STATE> automaton) {
		mInAutomaton = automaton;
		
		computeResult();
	}
	
	private void computeResult() {
		for (STATE state : mInAutomaton.getStates()) {
			
		}
	}
	
	public ParityTreeAutomaton<LETTER, ParityState<STATE>> getResult() {
		return mOutAutomaton;
	}
}
