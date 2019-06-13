package cgsynt.tree.buchi.operations;

import cgsynt.tree.buchi.BuchiTreeAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.transitions.OutgoingReturnTransition;
import de.uni_freiburg.informatik.ultimate.automata.statefactory.StringFactory;
import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;

public class ConvertToStringState<LETTER extends IRankedLetter, STATE> {

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
			for (OutgoingReturnTransition<LETTER, STATE> transition : aut.returnSuccessors(state)) {
				stringAut.addInternalTransition(state.toString(), transition.getLetter(),
						transition.getSucc().toString());
			}
		}
		return stringAut;

	}
}
