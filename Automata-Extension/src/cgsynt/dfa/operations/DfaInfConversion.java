package cgsynt.dfa.operations;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.INestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.VpAlphabet;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.transitions.IncomingInternalTransition;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.transitions.OutgoingInternalTransition;
import de.uni_freiburg.informatik.ultimate.automata.statefactory.IEmptyStackStateFactory;

public class DfaInfConversion<LETTER, STATE> {

	private NestedWordAutomaton<LETTER, STATE> result;
	private boolean resultComputed;

	public DfaInfConversion(INestedWordAutomaton<LETTER, STATE> aut, final AutomataLibraryServices services,
			final VpAlphabet<LETTER> vpAlphabet, final IEmptyStackStateFactory<STATE> emptyStateFactory) {
		this.result = copy(aut, services, vpAlphabet, emptyStateFactory);
		this.resultComputed = false;
	}

	public void computeResult() {
		if (resultComputed)
			return;
		Set<STATE> visited = new HashSet<>();
		Stack<STATE> toVisit = new Stack<>();
		for (STATE finalState : result.getFinalStates()) {
			toVisit.push(finalState);
			visited.add(finalState);
		}

		while (!toVisit.isEmpty()) {
			STATE next = toVisit.pop();
			for (IncomingInternalTransition<LETTER, STATE> transition : result.internalPredecessors(next)) {
				if (!visited.contains(transition.getPred())) {
					toVisit.add(transition.getPred());
					visited.add(transition.getPred());
				}
			}
		}
		Set<STATE> toRemove = new HashSet<>();
		for (STATE state : result.getStates()) {
			if (!visited.contains(state)) {
				toRemove.add(state);
			}
		}

		for (STATE state : toRemove) {
			result.removeState(state);
		}

		this.resultComputed = true;
	}

	public NestedWordAutomaton<LETTER, STATE> getResult() {
		if (!resultComputed) {
			return null;
		}
		return result;
	}

	private NestedWordAutomaton<LETTER, STATE> copy(INestedWordAutomaton<LETTER, STATE> aut,
			final AutomataLibraryServices services, final VpAlphabet<LETTER> vpAlphabet,
			final IEmptyStackStateFactory<STATE> emptyStateFactory) {
		NestedWordAutomaton<LETTER, STATE> copy = new NestedWordAutomaton<LETTER, STATE>(services, vpAlphabet,
				emptyStateFactory);
		for (STATE state : aut.getStates()) {
			copy.addState(aut.isInitial(state), aut.isFinal(state), state);
		}
		for (STATE state : aut.getStates()) {
			for (OutgoingInternalTransition<LETTER, STATE> transition : aut.internalSuccessors(state)) {
				copy.addInternalTransition(state, transition.getLetter(), transition.getSucc());
			}
		}
		return copy;

	}
}
