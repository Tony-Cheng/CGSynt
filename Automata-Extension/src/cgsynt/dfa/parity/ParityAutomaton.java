package cgsynt.dfa.parity;

import java.util.Set;

import cgsynt.tree.parity.IParityState;
import cgsynt.tree.parity.ParityStateFactory;
import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.VpAlphabet;
import de.uni_freiburg.informatik.ultimate.automata.statefactory.IEmptyStackStateFactory;

/**
 * Wrapper class for NestedWordAutomaton that ensures that each state is a ParityState 
 * */
public class ParityAutomaton<LETTER, STATE extends IParityState> extends NestedWordAutomaton<LETTER, STATE>{
	@SuppressWarnings("unchecked")
	public ParityAutomaton(AutomataLibraryServices services, VpAlphabet<LETTER> vpAlphabet,
			ParityStateFactory emptyStateFactory) {
		super(services, vpAlphabet, (IEmptyStackStateFactory<STATE>)emptyStateFactory);
	}
	
	public IParityState fetchEqualState(STATE query) {
		Set<STATE> allStates = this.getStates();
		
		for (STATE state : allStates) {
			if (state.equals(query))
				return state;
		}

		return null;
	}
}
