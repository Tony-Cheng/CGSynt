package cgsynt.dfa.parity;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import cgsynt.RepCondenser;
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
	private VpAlphabet<LETTER> mPrivateAlphabet;
	
	@SuppressWarnings("unchecked")	
	public ParityAutomaton(AutomataLibraryServices services, VpAlphabet<LETTER> vpAlphabet,
			ParityStateFactory emptyStateFactory) {
		super(services, vpAlphabet, (IEmptyStackStateFactory<STATE>)emptyStateFactory);
	
		mPrivateAlphabet = vpAlphabet;
	}
	
	public IParityState fetchEqualState(STATE query) {
		Set<STATE> allStates = this.getStates();
		
		for (STATE state : allStates) {
			if (state.equals(query))
				return state;
		}

		return null;
	}
	
	public String toString() {
		String rep = ""; 
		
		RepCondenser<IParityState> condenser = new RepCondenser<>(new ArrayList<>(this.getStates()));
		Map<String, String> mapping = condenser.getMapping();
		
		rep += "States:\n";
		for (String key : mapping.keySet()) 
			rep += mapping.get(key) + "\n";
		
		rep += "\nAlphabet:\n";
		for (LETTER letter : this.mPrivateAlphabet.getInternalAlphabet())
			rep += letter.toString() + "\n";
		
		rep += "\nInital States:\n";
		for (STATE initState : this.getInitialStates())
			rep += mapping.get(initState.toString()) + "\n";
		
		rep += "\nFinal States:\n";
		
		return rep;
	}
}
