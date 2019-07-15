package cgsynt.nfa.parity;

import java.util.Map;

import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.VpAlphabet;
import de.uni_freiburg.informatik.ultimate.automata.statefactory.IEmptyStackStateFactory;

public class ParityAutomaton<LETTER, STATE> extends NestedWordAutomaton<LETTER, STATE>{
	private Map<STATE, Integer> mColouringFunction;
	
	public ParityAutomaton(AutomataLibraryServices services, VpAlphabet<LETTER> vpAlphabet,
			Map<STATE, Integer> colouringFunction, IEmptyStackStateFactory<STATE> emptyStateFactory) {
		super(services, vpAlphabet, emptyStateFactory);
		mColouringFunction = colouringFunction;
	}

	public Map<STATE, Integer> getColouringFunction(){
		return mColouringFunction;
	}
}
