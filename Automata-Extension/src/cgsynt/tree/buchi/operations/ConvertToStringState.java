package cgsynt.tree.buchi.operations;

import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.transitions.OutgoingInternalTransition;
import de.uni_freiburg.informatik.ultimate.automata.statefactory.StringFactory;

public class ConvertToStringState<LETTER, STATE> {

	private final NestedWordAutomaton<LETTER, STATE> aut;

	public ConvertToStringState(NestedWordAutomaton<LETTER, STATE> aut) {
		this.aut = aut;
	}

	public NestedWordAutomaton<LETTER, String> convert(AutomataLibraryServices service) {
		NestedWordAutomaton<LETTER, String> stringAut = new NestedWordAutomaton<>(service, aut.getVpAlphabet(),
				new StringFactory());
		for (STATE state : aut.getStates()) {
			boolean isFinal = aut.isFinal(state);
			boolean isInitial = aut.isInitial(state);
			stringAut.addState(isInitial, isFinal, state.toString());
		}

		for (STATE state : aut.getStates()) {
			for (OutgoingInternalTransition<LETTER, STATE> transition : aut.internalSuccessors(state)) {
				stringAut.addInternalTransition(state.toString(), transition.getLetter(),
						transition.getSucc().toString());
			}
		}
		return stringAut;

	}
}
