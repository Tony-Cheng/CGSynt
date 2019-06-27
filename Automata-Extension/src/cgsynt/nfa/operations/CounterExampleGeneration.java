package cgsynt.nfa.operations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.transitions.OutgoingInternalTransition;

public class CounterExampleGeneration<LETTER, STATE> {

	private NestedWordAutomaton<LETTER, STATE> nwa;
	private boolean resultComputed;
	private Set<List<LETTER>> result;
	private int k;

	public CounterExampleGeneration(NestedWordAutomaton<LETTER, STATE> nwa, int k) {
		this.nwa = nwa;
		resultComputed = false;
		this.k = k;
	}

	public void computeResult() {
		if (resultComputed)
			return;
		result = new HashSet<>();
		for (STATE initialState : nwa.getInitialStates()) {
			findCounterexamples(initialState, k * nwa.getStates().size(), new ArrayList<>());
		}
		resultComputed = true;
	}

	public Set<List<LETTER>> getResult() {
		return result;
	}

	private void findCounterexamples(STATE state, int len, List<LETTER> counterexample) {
		if (!nwa.isFinal(state))
			result.add(counterexample);
		if (len == 0) {
			return;
		}
		for (OutgoingInternalTransition<LETTER, STATE> next : nwa.internalSuccessors(state)) {
			List<LETTER> nextCounterexamples = new ArrayList<>();
			nextCounterexamples.addAll(counterexample);
			nextCounterexamples.add(next.getLetter());
			findCounterexamples(next.getSucc(), len - 1, nextCounterexamples);
		}
	}
}
