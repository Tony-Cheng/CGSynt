package cgsynt.tree.buchi.operations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cgsynt.tree.buchi.BuchiTreeAutomaton;
import cgsynt.tree.buchi.BuchiTreeAutomatonRule;
import cgsynt.tree.parity.ParityState;
import cgsynt.tree.parity.ParityTreeAutomaton;
import cgsynt.tree.parity.ParityTreeAutomatonRule;
import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;

public class BuchiTreeToParityTree<LETTER extends IRankedLetter, STATE> {
	private BuchiTreeAutomaton<LETTER, STATE> mInAutomaton;
	private ParityTreeAutomaton<LETTER, ParityState<STATE>> mOutAutomaton;
	
	public BuchiTreeToParityTree(BuchiTreeAutomaton<LETTER, STATE> automaton) {
		mInAutomaton = automaton;
		mOutAutomaton = new ParityTreeAutomaton<>(2);
		
		computeResult();
	}
	
	private void computeResult() {
		for (STATE state : mInAutomaton.getStates()) {
			int sourceColour = (mInAutomaton.isFinalState(state)) ? 0 : 1;
			
			ParityState<STATE> newSourceState = new ParityState<>(state, sourceColour);
			if (mOutAutomaton.contains(newSourceState))
				newSourceState = mOutAutomaton.fetchEqualState(newSourceState);
			else if (mInAutomaton.isInitState(state))
				mOutAutomaton.addInitState(newSourceState);
			else
				mOutAutomaton.addState(newSourceState);
			
			Collection<BuchiTreeAutomatonRule<LETTER, STATE>> rules = mInAutomaton.getRulesBySource(state);
			for (BuchiTreeAutomatonRule<LETTER, STATE> rule : rules) {
				List<STATE> dests = rule.getDest();
				
				List<ParityState<STATE>> newDests = new ArrayList<>();
				for (STATE destState : dests) {
					int destColour = (mInAutomaton.isFinalState(destState)) ? 0 : 1;
					
					ParityState<STATE> newDestState = new ParityState<>(destState, destColour);
					if (mOutAutomaton.contains(newDestState))
						newDestState = mOutAutomaton.fetchEqualState(newDestState);
					else if (mInAutomaton.isInitState(destState))
						mOutAutomaton.addInitState(newDestState);
					else
						mOutAutomaton.addState(newDestState);
					
					newDests.add(newDestState);
				}
				
				ParityTreeAutomatonRule<LETTER, ParityState<STATE>> newRule = 
						new ParityTreeAutomatonRule<>(rule.getLetter(), newSourceState, newDests);
				
				mOutAutomaton.addRule(newRule);
			}
		}
	}
	
	public ParityTreeAutomaton<LETTER, ParityState<STATE>> getResult() {
		return mOutAutomaton;
	}
}
