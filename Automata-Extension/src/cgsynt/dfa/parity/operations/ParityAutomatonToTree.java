package cgsynt.dfa.parity.operations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cgsynt.dfa.parity.ParityAutomaton;
import cgsynt.tree.buchi.lta.RankedBool;
import cgsynt.tree.parity.ParityState;
import cgsynt.tree.parity.ParityTreeAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.transitions.OutgoingInternalTransition;

public class ParityAutomatonToTree<LETTER, STATE> {
	private ParityAutomaton<LETTER, STATE> mInAutomaton;
	private List<LETTER> mLetterOrder;
	private ParityState<STATE> mDeadState;
	
	private ParityTreeAutomaton<RankedBool, ParityState<STATE>> mOutAutomaton;
	
	public ParityAutomatonToTree(final ParityAutomaton<LETTER, STATE> automaton, List<LETTER> letterOrder, ParityState<STATE> deadState){
		mInAutomaton = automaton;
		mLetterOrder = letterOrder;
		mDeadState = deadState;
		
		mOutAutomaton = new ParityTreeAutomaton<>(letterOrder.size());
		mOutAutomaton.addState(deadState);
		
		computeResult();
	}
	
	public void computeResult() {
		Map<STATE, Integer> colouringFunction = mInAutomaton.getColouringFunction();
		
		for (STATE state : mInAutomaton.getStates()) {
			int colour = colouringFunction.get(state);
			
			ParityState<STATE> newState = new ParityState<>(state, colour);
			if (!mOutAutomaton.contains(newState))
				
		}
	}
	
	public List<ParityState<STATE>> createDestinationList(STATE source){
		List<ParityState<STATE>> destList = new ArrayList<>();
		Map<STATE, Integer> colouringFunction = mInAutomaton.getColouringFunction();
		
		for (LETTER letter : mLetterOrder) {
			Iterable<OutgoingInternalTransition<LETTER, STATE>> successors = mInAutomaton.internalSuccessors(source);
			
			boolean found = false;
			for (OutgoingInternalTransition<LETTER, STATE> transition : successors) {
				if (transition.getLetter().equals(letter)) {
					STATE state = transition.getSucc();
					int colour = colouringFunction.get(transition.getSucc());
					
					ParityState<STATE> succState = new ParityState<>(state, colour);
					destList.add(succState);
					
					found = true;
					break;
				}
			}
			
			if (!found) {
				destList.add(mDeadState);
			}
		}
		
		return destList;
	} 
	
	public ParityTreeAutomaton<RankedBool, ParityState<STATE>> getResult(){
		return mOutAutomaton;
	}
}
