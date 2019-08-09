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
	
	private ParityTreeAutomaton<RankedBool, STATE> mOutAutomaton;
	
	private STATE mOffState;
	private STATE mShutdownState;
	private List<STATE> mShutdownDestList;
	
	public ParityAutomatonToTree(final ParityAutomaton<LETTER, STATE> automaton, List<LETTER> letterOrder, STATE shutdownState, STATE offState){
		mInAutomaton = automaton;
		mLetterOrder = letterOrder;
		mOffState = offState;
		mShutdownState = shutdownState;
		
		mOutAutomaton = new ParityTreeAutomaton<>(letterOrder.size());
		mOutAutomaton.addState(mOffState);
		mOutAutomaton.addState(mShutdownState);
		
		mShutdownDestList = new ArrayList<>();
		for (int i = 0; i < letterOrder.size(); i++)
			mShutdownDestList.add(shutdownState);
		
		ParityTreeAutomatonRule<RankedBool, STATE> shutdownToShutdownTrue = 
				new ParityTreeAutomatonRule<>(RankedBool.TRUE, mShutdownState, mShutdownDestList);
		mOutAutomaton.addRule(shutdownToShutdownTrue);
		
		ParityTreeAutomatonRule<RankedBool, STATE> shutdownToShutdownFalse = 
				new ParityTreeAutomatonRule<>(RankedBool.FALSE, mShutdownState, mShutdownDestList);
		mOutAutomaton.addRule(shutdownToShutdownFalse);
		
		List<STATE> offDestList = new ArrayList<>();
		for (int i = 0; i < letterOrder.size(); i++)
			offDestList.add(mOffState);
		
		ParityTreeAutomatonRule<RankedBool, STATE> shutdownToOffTrue = 
				new ParityTreeAutomatonRule<>(RankedBool.TRUE, mShutdownState, offDestList);
		mOutAutomaton.addRule(shutdownToOffTrue);
		
		ParityTreeAutomatonRule<RankedBool, STATE> shutdownToOffFalse = 
				new ParityTreeAutomatonRule<>(RankedBool.FALSE, mShutdownState, offDestList);
		mOutAutomaton.addRule(shutdownToOffFalse);
		
		ParityTreeAutomatonRule<RankedBool, STATE> offToOffFalse = 
				new ParityTreeAutomatonRule<>(RankedBool.FALSE, mOffState, offDestList);
		mOutAutomaton.addRule(offToOffFalse);
		
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
			
			ParityTreeAutomatonRule<RankedBool, STATE> trueRule = 
					new ParityTreeAutomatonRule<>(RankedBool.TRUE, newState, createDestinationList(state));
			mOutAutomaton.addRule(trueRule);
			
			ParityTreeAutomatonRule<RankedBool, STATE> falseRule = 
					new ParityTreeAutomatonRule<>(RankedBool.FALSE, newState, createDestinationList(state));
			mOutAutomaton.addRule(falseRule);
			
			ParityTreeAutomatonRule<RankedBool, STATE> trueShutdownRule = 
					new ParityTreeAutomatonRule<>(RankedBool.TRUE, newState, mShutdownDestList);
			mOutAutomaton.addRule(trueShutdownRule);
			
			ParityTreeAutomatonRule<RankedBool, STATE> falseShutdownRule = 
					new ParityTreeAutomatonRule<>(RankedBool.FALSE, newState, mShutdownDestList);
			mOutAutomaton.addRule(falseShutdownRule);
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
				destList.add(mShutdownState);
		}
		
		return destList;
	} 
	
	public ParityTreeAutomaton<RankedBool, STATE> getResult(){
		return mOutAutomaton;
	}
}
