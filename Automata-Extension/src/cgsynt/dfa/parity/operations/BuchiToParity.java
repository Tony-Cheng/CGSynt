package cgsynt.dfa.parity.operations;

import java.util.Set;

import cgsynt.dfa.parity.ParityAutomaton;
import cgsynt.tree.parity.ParityState;
import cgsynt.tree.parity.ParityStateFactory;
import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.transitions.OutgoingInternalTransition;

public class BuchiToParity<LETTER, STATE> {
	private NestedWordAutomaton<LETTER, STATE> mInAutomaton;
	private ParityAutomaton<LETTER, ParityState<STATE>> mOutAutomaton;
	
	private AutomataLibraryServices mService;
	
	public BuchiToParity(final NestedWordAutomaton<LETTER, STATE> automaton, AutomataLibraryServices service) {
		mInAutomaton = automaton;
		mService = service;
		
		computeResult();
	}
	
	@SuppressWarnings("unchecked")
	private void computeResult() {
		mOutAutomaton = new ParityAutomaton<>(mService, mInAutomaton.getVpAlphabet(), new ParityStateFactory());
	
		Set<STATE> allStates = mInAutomaton.getStates();
		for (STATE state : allStates) {
			ParityState<STATE> newState =
					new ParityState<>(state, (mInAutomaton.isFinal(state)) ? 2 : 1);
			
			if (!mOutAutomaton.contains(newState))
				mOutAutomaton.addState(mInAutomaton.isInitial(state), mInAutomaton.isFinal(state), newState);
			
			for (OutgoingInternalTransition<LETTER, STATE> transition : mInAutomaton.internalSuccessors(state)) {
				STATE oldSucc = transition.getSucc();
				
				ParityState<STATE> succ = 
						new ParityState<>(oldSucc, (mInAutomaton.isFinal(oldSucc) ? 2 : 1));
				
				if (mOutAutomaton.contains(succ))
					succ = (ParityState<STATE>) mOutAutomaton.fetchEqualState(succ);
				else 
					mOutAutomaton.addState(mInAutomaton.isInitial(oldSucc), mInAutomaton.isFinal(oldSucc), succ);
				
				LETTER letter = transition.getLetter();
				
				mOutAutomaton.addInternalTransition(newState, letter, succ);
			}
		}
	}
	
	public ParityAutomaton<LETTER, ParityState<STATE>> getResult(){
		return mOutAutomaton;
	}
}
