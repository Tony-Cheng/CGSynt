package cgsynt.dfa.operations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.uni_freiburg.informatik.ultimate.automata.nestedword.INestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.transitions.OutgoingInternalTransition;

public class CounterexamplesGeneration<LETTER, STATE> {

	private INestedWordAutomaton<LETTER, STATE> nwa;
	private boolean resultComputed;
	private Set<List<LETTER>> result;
	private int k;
	private Set<List<LETTER>> visited;
	private int bs;
	private List<LETTER> alphabet;

	public CounterexamplesGeneration(INestedWordAutomaton<LETTER, STATE> nwa, int k, Set<List<LETTER>> visited, int bs,
			List<LETTER> alphabet) {
		this.nwa = nwa;
		resultComputed = false;
		this.k = k;
		this.visited = visited;
		this.bs = bs;
		this.alphabet = alphabet;
	}

	public void computeResult() {
		if (resultComputed)
			return;
		result = new HashSet<>();
		for (STATE initialState : nwa.getInitialStates()) {
			findCounterexamples(initialState, k, new ArrayList<>());
		}
		resultComputed = true;
	}

	public Set<List<LETTER>> getResult() {
		return result;
	}

	private void findCounterexamples(STATE state, int len, List<LETTER> counterexample) {
		if (bs > 0 && result.size() >= bs)
			return;
		if (!nwa.isFinal(state) && counterexample.size() > 0 && !visited.contains(counterexample)) {
			result.add(counterexample);
			visited.add(counterexample);
		}
		if (len == 0) {
			return;
		}
		Set<LETTER> alphabetCopy = new HashSet<>(this.alphabet);
		for (OutgoingInternalTransition<LETTER, STATE> next : nwa.internalSuccessors(state)) {
			List<LETTER> nextCounterexamples = new ArrayList<>();
			nextCounterexamples.addAll(counterexample);
			nextCounterexamples.add(next.getLetter());
			alphabetCopy.remove(next.getLetter());
			findCounterexamples(next.getSucc(), len - 1, nextCounterexamples);
		}
		for (LETTER letter : alphabetCopy) {
			List<LETTER> nextCounterexamples = new ArrayList<>();
			nextCounterexamples.addAll(counterexample);
			nextCounterexamples.add(letter);
			findCounterexamples(len - 1, nextCounterexamples);
		}
	}

	private void findCounterexamples(int len, List<LETTER> counterexample) {
		if (bs > 0 && result.size() >= bs)
			return;
		if (counterexample.size() > 0 && !visited.contains(counterexample)) {
			result.add(counterexample);
			visited.add(counterexample);
		}
		if (len == 0)
			return;
		for (LETTER letter : this.alphabet) {
			List<LETTER> nextCounterexamples = new ArrayList<>();
			nextCounterexamples.addAll(counterexample);
			nextCounterexamples.add(letter);
			findCounterexamples(len - 1, nextCounterexamples);
		}
	}

	private void findCounterexamplesRandom(int len, List<LETTER> counterexample) {
		if (len == 0)
			return;
		long size = 1;
		size = (long) ((Math.pow(alphabet.size(), len + 1) - 1) / (alphabet.size() - 1));
		long randNum = (long) (Math.random() * size);
		if (randNum == size - 1) {
			return;
		}
		int index = (int) (randNum % alphabet.size());
		counterexample.add(alphabet.get(index));
		findCounterexamplesRandom(len - 1, counterexample);
	}

	public void computeRandomly() {
		if (resultComputed)
			return;
		result = new HashSet<>();
		for (int i = 0; i < bs; i++) {
			List<LETTER> counterexample = new ArrayList<>();
			findCounterexamplesRandom(k, counterexample);
			if (counterexample.size() > 0) {
				result.add(counterexample);
				visited.add(counterexample);
			}
		}
		resultComputed = true;
	}
}
