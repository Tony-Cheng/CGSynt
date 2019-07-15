package cgsynt.dfa.parity.operations;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cgsynt.dfa.parity.ParityAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;

public class BuchiToParity<LETTER, STATE> {
	private NestedWordAutomaton<LETTER, STATE> mInAutomaton;
	private ParityAutomaton<LETTER, STATE> mOutAutomaton;
	
	public BuchiToParity(final NestedWordAutomaton<LETTER, STATE> automaton) {
		mInAutomaton = automaton;
		
		computeResult();
	}
	
	private void computeResult() {
		Set<STATE> allStates = mInAutomaton.getStates();
		
		Map<STATE, Integer> colouringFunction = new HashMap<>();
		for (STATE state : allStates) {
			if (mInAutomaton.isFinal(state)) 
				colouringFunction.put(state, 0);
			else 
				colouringFunction.put(state, 1);
		}
		
		mOutAutomaton = (ParityAutomaton<LETTER, STATE>) mInAutomaton;
		mOutAutomaton.setColouringFunction(colouringFunction);
	}
	
	public ParityAutomaton<LETTER, STATE> getResult(){
		return mOutAutomaton;
	}
}
