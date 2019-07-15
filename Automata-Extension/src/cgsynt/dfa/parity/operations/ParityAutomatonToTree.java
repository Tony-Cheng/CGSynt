package cgsynt.dfa.parity.operations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cgsynt.dfa.parity.ParityAutomaton;
import cgsynt.tree.buchi.lta.RankedBool;
import cgsynt.tree.parity.ParityState;
import cgsynt.tree.parity.ParityTreeAutomaton;
import cgsynt.tree.parity.ParityTreeAutomatonRule;
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
	
	@SuppressWarnings("unchecked")
	public void computeResult() {
		Map<STATE, Integer> colouringFunction = mInAutomaton.getColouringFunction();
		
		for (STATE state : mInAutomaton.getStates()) {
			int colour = colouringFunction.get(state);
			
			ParityState<STATE> newState = new ParityState<>(state, colour);
			
			boolean existant = false;
			if (mOutAutomaton.contains(newState)) {
				newState = (ParityState<STATE>) mOutAutomaton.fetchEqualState(newState);
				existant = true;
			}
			
			if (mInAutomaton.isInitial(state) && !existant)
				mOutAutomaton.addInitState(newState);
			else if (!existant)
				mOutAutomaton.addState(newState);
			
			if (mInAutomaton.isFinal(state)) {
				ParityTreeAutomatonRule<RankedBool, ParityState<STATE>> trueRule = 
						new ParityTreeAutomatonRule<>(RankedBool.TRUE, newState, createDestinationList(state));
				
				mOutAutomaton.addRule(trueRule);
			}
			
			ParityTreeAutomatonRule<RankedBool, ParityState<STATE>> falseRule = 
					new ParityTreeAutomatonRule<>(RankedBool.FALSE, newState, createDestinationList(state));
			
			mOutAutomaton.addRule(falseRule);
		}
	}
	
	@SuppressWarnings("unchecked")
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
					boolean existant = false;
					if (mOutAutomaton.contains(succState)) {
						succState = (ParityState<STATE>) mOutAutomaton.fetchEqualState(succState);
						existant = true;
					}
						
					if (mInAutomaton.isInitial(state) && !existant)
						mOutAutomaton.addInitState(succState);
					else if (!existant)
						mOutAutomaton.addState(succState);
						
					destList.add(succState);
					
					found = true;
					break;
				}
			}
			
			if (!found)
				destList.add(mDeadState);
		}
		
		return destList;
	} 
	
	public ParityTreeAutomaton<RankedBool, ParityState<STATE>> getResult(){
		return mOutAutomaton;
	}
}
