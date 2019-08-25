package cgsynt.nfa.operations;

import java.util.HashSet;
import java.util.Set;

import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.VpAlphabet;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.transitions.OutgoingInternalTransition;
import de.uni_freiburg.informatik.ultimate.automata.statefactory.IEmptyStackStateFactory;


public class NFAComplement<LETTER, STATE> {

	private NestedWordAutomaton<LETTER, STATE> aut;
	private boolean resultComputed;
	private NestedWordAutomaton<LETTER, STATE> result;
	private AutomataLibraryServices services;
	private IEmptyStackStateFactory<STATE> stateFactory;

	public NFAComplement(NestedWordAutomaton<LETTER, STATE> aut, AutomataLibraryServices services,
			IEmptyStackStateFactory<STATE> stateFactory) {
		this.aut = aut;
		this.resultComputed = false;
		this.services = services;
		this.stateFactory = stateFactory;
	}

	public void computeResult() {
		if (resultComputed)
			return;
		Set<LETTER> internalAlphabet = new HashSet<>();
		for (LETTER transition : aut.getAlphabet()) {
			internalAlphabet.add(transition);
		}
		VpAlphabet<LETTER> vpAlphabet = new VpAlphabet<>(internalAlphabet, new HashSet<>(), new HashSet<>());
		result = new NestedWordAutomaton<>(services, vpAlphabet, stateFactory);
		for (STATE state : aut.getStates()) {
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
		for (STATE state : aut.getStates()) {
			for (OutgoingInternalTransition<LETTER, STATE> transition : aut.internalSuccessors(state)) {
				result.addInternalTransition(state, transition.getLetter(), transition.getSucc());
			}
		}
		resultComputed = true;
	}

	public NestedWordAutomaton<LETTER, STATE> getResult() {
		if (!resultComputed)
			return null;
		return result;
	}
}
