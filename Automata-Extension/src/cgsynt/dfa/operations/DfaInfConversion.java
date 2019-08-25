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

/**
 * Convert a proof DFA into a Buchi automaton which accepts the inf of the DFA.
 * @author tcheng
 *
 * @param <LETTER> The letter type of the input DFA.
 * @param <STATE> The state type of the input DFA.
 */
public class DfaInfConversion<LETTER, STATE> {

	private NestedWordAutomaton<LETTER, STATE> mResult;
	private boolean mResultComputed;

	public DfaInfConversion(INestedWordAutomaton<LETTER, STATE> aut, final AutomataLibraryServices services,
			final IEmptyStackStateFactory<STATE> emptyStateFactory) {
		this.mResult = copy(aut, services, emptyStateFactory);
		this.mResultComputed = false;
		
		computeResult();
	}

	/**
	 * Compute the result of the DFA to inf(DFA) representation conversion.
	 */
	private void computeResult() {
		if (mResultComputed)
			return;
		Set<STATE> visited = new HashSet<>();
		Stack<STATE> toVisit = new Stack<>();
		for (STATE finalState : mResult.getFinalStates()) {
			toVisit.push(finalState);
			visited.add(finalState);
		}

		while (!toVisit.isEmpty()) {
			STATE next = toVisit.pop();
			for (IncomingInternalTransition<LETTER, STATE> transition : mResult.internalPredecessors(next)) {
				if (!visited.contains(transition.getPred())) {
					toVisit.add(transition.getPred());
					visited.add(transition.getPred());
				}
			}
		}
		Set<STATE> toRemove = new HashSet<>();
		for (STATE state : mResult.getStates()) {
			if (!visited.contains(state)) {
				toRemove.add(state);
			}
		}

		for (STATE state : toRemove) {
			mResult.removeState(state);
		}

		this.mResultComputed = true;
	}

	/**
	 * Get the resulting inf(DFA) from this class.
	 * @return The Buchi representing the inf(DFA).
	 */
	public NestedWordAutomaton<LETTER, STATE> getResult() {
		if (!mResultComputed) {
			return null;
		}
		return mResult;
	}

	/**
	 * Make a copy of the input DFA.
	 * @param aut The input DFA.
	 * @param services The AutomataLibraryServices.
	 * @param emptyStateFactory An implementation of IEmptyStateFactory.
	 * @return A copy of the input DFA.
	 */
	private NestedWordAutomaton<LETTER, STATE> copy(INestedWordAutomaton<LETTER, STATE> aut,
			final AutomataLibraryServices services,
			final IEmptyStackStateFactory<STATE> emptyStateFactory) {
		NestedWordAutomaton<LETTER, STATE> copy = new NestedWordAutomaton<LETTER, STATE>(services, aut.getVpAlphabet(),
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
