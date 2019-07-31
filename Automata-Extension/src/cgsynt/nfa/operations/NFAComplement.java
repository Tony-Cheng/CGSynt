package cgsynt.nfa.operations;

import java.util.HashSet;
import java.util.Set;

import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.VpAlphabet;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.transitions.OutgoingInternalTransition;
import de.uni_freiburg.informatik.ultimate.automata.statefactory.StringFactory;

public class NFAComplement<LETTER> {

	private NestedWordAutomaton<LETTER, String> aut;
	private boolean resultComputed;
	private NestedWordAutomaton<LETTER, String> result;
	private AutomataLibraryServices services;

	public NFAComplement(NestedWordAutomaton<LETTER, String> aut, AutomataLibraryServices services) {
		this.aut = aut;
		this.resultComputed = false;
		this.services = services;
	}

	public void computeResult() {
		if (resultComputed)
			return;
		Set<LETTER> internalAlphabet = new HashSet<>();
		for (LETTER transition : aut.getAlphabet()) {
			internalAlphabet.add(transition);
		}
		VpAlphabet<LETTER> vpAlphabet = new VpAlphabet<>(internalAlphabet, new HashSet<>(), new HashSet<>());
		result = new NestedWordAutomaton<>(services, vpAlphabet, new StringFactory());
		for (String state : aut.getStates()) {
			boolean isInitial = false;
			boolean isFinal = true;
			if (aut.isFinal(state)) {
				isFinal = false;
			}
			if (aut.isInitial(state)) {
				isInitial = true;
			}
			result.addState(isInitial, isFinal, state);
		}
		for (String state : aut.getStates()) {
			for (OutgoingInternalTransition<LETTER, String> transition : aut.internalSuccessors(state)) {
				result.addInternalTransition(state, transition.getLetter(), transition.getSucc());
			}
		}
		resultComputed = true;
	}

	public NestedWordAutomaton<LETTER, String> getResult() {
		if (!resultComputed)
			return null;
		return result;
	}
}
