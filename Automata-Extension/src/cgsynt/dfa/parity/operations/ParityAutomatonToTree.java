package cgsynt.dfa.parity.operations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cgsynt.dfa.parity.ParityAutomaton;
import cgsynt.tree.buchi.lta.RankedBool;
import cgsynt.tree.parity.IParityState;
import cgsynt.tree.parity.ParityState;
import cgsynt.tree.parity.ParityTreeAutomaton;
import cgsynt.tree.parity.ParityTreeAutomatonRule;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.transitions.OutgoingInternalTransition;

public class ParityAutomatonToTree<LETTER, STATE extends IParityState> {
	private ParityAutomaton<LETTER, STATE> mInAutomaton;
	private List<LETTER> mLetterOrder;
	private STATE mDeadState;
	
	private ParityTreeAutomaton<RankedBool, STATE> mOutAutomaton;
	private STATE mDummyState;
	private List<STATE> mDummyDestList;
	
	public ParityAutomatonToTree(final ParityAutomaton<LETTER, STATE> automaton, List<LETTER> letterOrder, STATE deadState, STATE dummyState){
		mInAutomaton = automaton;
		mLetterOrder = letterOrder;
		mDeadState = deadState;
		
		mOutAutomaton = new ParityTreeAutomaton<>(letterOrder.size());
		mOutAutomaton.addState(deadState);
		
		List<STATE> deadStateList = new ArrayList<>();
		for (int i = 0; i < letterOrder.size(); i++) 
			deadStateList.add(deadState);
		ParityTreeAutomatonRule<RankedBool, STATE> deadTrue = 
				new ParityTreeAutomatonRule<>(RankedBool.TRUE, deadState, deadStateList);
		ParityTreeAutomatonRule<RankedBool, STATE> deadFalse = 
				new ParityTreeAutomatonRule<>(RankedBool.FALSE, deadState, deadStateList);
		
		mOutAutomaton.addRule(deadTrue);
		mOutAutomaton.addRule(deadFalse);
		
		mDummyState = dummyState;
		
		mDummyDestList = new ArrayList<>();
		for (int i = 0; i < letterOrder.size(); i++)
			mDummyDestList.add(mDummyState);
		
		ParityTreeAutomatonRule<RankedBool, STATE> dummyLoop = 
				new ParityTreeAutomatonRule<>(RankedBool.TRUE, mDummyState, mDummyDestList);
		mOutAutomaton.addRule(dummyLoop);
		
		computeResult();
	}
	
	@SuppressWarnings("unchecked")
	public void computeResult() {
		for (STATE state : mInAutomaton.getStates()) {
			STATE newState = state;
			
			boolean existant = false;
			if (mOutAutomaton.contains(newState)) {
				newState = mOutAutomaton.fetchEqualState(newState);
				existant = true;
			}
			
			if (mInAutomaton.isInitial(state) && !existant)
				mOutAutomaton.addInitState(newState);
			else if (!existant)
				mOutAutomaton.addState(newState);
			
			
			if (mInAutomaton.isFinal(state)) {
				ParityTreeAutomatonRule<RankedBool, STATE> trueRule = 
						new ParityTreeAutomatonRule<>(RankedBool.TRUE, newState, createDestinationList(state));
				
				mOutAutomaton.addRule(trueRule);
			}
			
			ParityTreeAutomatonRule<RankedBool, STATE> trueRule = 
					new ParityTreeAutomatonRule<>(RankedBool.TRUE, newState, mDummyDestList);
			
			mOutAutomaton.addRule(trueRule);
			
			ParityTreeAutomatonRule<RankedBool, STATE> falseRule = 
					new ParityTreeAutomatonRule<>(RankedBool.FALSE, newState, createDestinationList(state));
			
			mOutAutomaton.addRule(falseRule);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<STATE> createDestinationList(STATE source){
		List<STATE> destList = new ArrayList<>();
		
		for (LETTER letter : mLetterOrder) {
			Iterable<OutgoingInternalTransition<LETTER, STATE>> successors = mInAutomaton.internalSuccessors(source);
			
			boolean found = false;
			for (OutgoingInternalTransition<LETTER, STATE> transition : successors) {
				if (transition.getLetter().equals(letter)) {
					STATE succState = transition.getSucc();
					
					boolean existant = false;
					if (mOutAutomaton.contains(succState)) {
						succState = mOutAutomaton.fetchEqualState(succState);
						existant = true;
					}
						
					if (mInAutomaton.isInitial(succState) && !existant)
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
	
	public ParityTreeAutomaton<RankedBool, STATE> getResult(){
		return mOutAutomaton;
	}
}
