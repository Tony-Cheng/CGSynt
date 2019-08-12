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
import de.uni_freiburg.informatik.ultimate.util.datastructures.relation.IsContained;
import de.uni_freiburg.informatik.ultimate.util.datastructures.relation.NestedMap2;

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
		
		RepCondenser<IParityState> stateCondenser = new RepCondenser<>(new ArrayList<>(this.getStates()));
		Map<String, String> stateMapping = stateCondenser.getMapping();
		
		RepCondenser<LETTER> letterCondenser = new RepCondenser<>(new ArrayList<>(this.mPrivateAlphabet.getInternalAlphabet()));
		Map<String, String> letterMapping = letterCondenser.getMapping();
		
		rep += "States:\n";
		for (String key : stateMapping.keySet()) 
			rep += stateMapping.get(key) + "\n";
			
		rep += "\nInital States:\n";
		for (STATE initState : this.getInitialStates())
			rep += stateMapping.get(initState.toString()) + "\n";
		
		rep += "\nFinal States:\n";
		for (STATE finalState : this.getFinalStates())
			rep += stateMapping.get(finalState.toString()) + "\n";
		
		rep += "\nAlphabet:\n";
		for (String key : letterMapping.keySet())
			rep += "LETTER " + letterMapping.get(key).toLowerCase() + "\n";
		
		rep += "\nTransitions:\n";
		for (STATE source : this.getStates()) {
			NestedMap2<LETTER, STATE, IsContained> outgoingLetters = this.mInternalOut.get(source);
			
			for (LETTER letter : outgoingLetters.keySet()) {
				Map<STATE, IsContained> finalLayer = outgoingLetters.get(letter);
				
				STATE dest = new ArrayList<>(finalLayer.keySet()).get(0);
				
				rep += "(" + stateMapping.get(source.toString()) + " | "
				+ "LETTER " + letterMapping.get(letter.toString()).toLowerCase() + " | "
				+ stateMapping.get(dest.toString()) + ")\n";
			}
		}
		
		rep += "\nState Mapping:\n";
		for (STATE state : this.getStates())
			rep += stateMapping.get(state.toString()) + " = " + state.toString() + "\n";
		
		rep += "\nLetter Mapping: \n";
		for (LETTER letter : this.mPrivateAlphabet.getInternalAlphabet())
			rep += "LETTER " + letterMapping.get(letter.toString()).toLowerCase() + " = " + letter.toString() + "\n";
		
		return rep;
	}
}
