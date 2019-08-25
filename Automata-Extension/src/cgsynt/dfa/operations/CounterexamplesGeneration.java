package cgsynt.dfa.operations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.uni_freiburg.informatik.ultimate.automata.nestedword.INestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.transitions.OutgoingInternalTransition;

/**
 * Extract counter examples from the proof of correctness PI.
 * @author tcheng
 *
 * @param <LETTER> The letter type of the automaton from which counterexamples are extracted.
 * @param <STATE> The state type of the automaton from which counterexamples are extracted.
 */
public class CounterexamplesGeneration<LETTER, STATE> {

	public static int NO_BATCH = -1;
	private INestedWordAutomaton<LETTER, STATE> nwa;
	private boolean resultComputed;
	private Set<List<LETTER>> result;
	private int k;
	private Set<List<LETTER>> visited;
	private int bs;
	private List<LETTER> alphabet;

	/**
	 * Constructor
	 * @param nwa The dfa from which correctness counterexamples can be drawn.
	 * @param k The maximum length of the counterexamples to get.
	 * @param visited The set of counterexamples which have already been tried but have been shown to be incorrect.
	 * @param alphabet The alphabet of the automaton from which counterexamples are drawn.
	 */
	public CounterexamplesGeneration(INestedWordAutomaton<LETTER, STATE> nwa, int k, Set<List<LETTER>> visited,
			List<LETTER> alphabet) {
		this.nwa = nwa;
		resultComputed = false;
		this.k = k;
		this.visited = visited;
		this.bs = -1;
		this.alphabet = alphabet;
	}

	public CounterexamplesGeneration(INestedWordAutomaton<LETTER, STATE> nwa, int k, List<LETTER> alphabet) {
		this.nwa = nwa;
		resultComputed = false;
		this.k = k;
		this.visited = new HashSet<>();
		this.bs = -1;
		this.alphabet = alphabet;
	}

	public CounterexamplesGeneration(INestedWordAutomaton<LETTER, STATE> nwa, int k, Set<List<LETTER>> visited, int bs,
			List<LETTER> alphabet) {
		this.nwa = nwa;
		resultComputed = false;
		this.k = k;
		this.visited = visited;
		this.bs = bs;
		this.alphabet = alphabet;
	}

	/**
	 * Compute the all the counterexamples for the given proof automaton (PI).
	 */
	public void computeResult() {
		if (resultComputed)
			return;
		result = new HashSet<>();
		for (STATE initialState : nwa.getInitialStates()) {
			findCounterexamples(initialState, k, new ArrayList<>());
		}
		resultComputed = true;
	}

	/**
	 * Get the set of generated counterexamples.
	 * @return The set of counterexamples.
	 */
	public Set<List<LETTER>> getResult() {
		return result;
	}

	/**
	 * Find counterexamples of a specific length given an initial state.
	 * @param state The initial state from where a counterexample is to start.
	 * @param len The required length of the counterexample.
	 * @param counterexample The running List of counterexamples calculated so far.
	 */
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

	/**
	 * Find a counterexamples of a specific length starting from an arbitrary state.
	 * @param len The length of the counterexample to be found.
	 * @param counterexample The running List of counterexamples calculated so far.
	 */
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

	/**
	 * Find counterexamples of a specific length randomly.
	 * @param len The target length for the counterexamples.
	 * @param counterexample The running List of counterexamples computed so far.
	 */
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

	/**
	 * Compute a List of counterexamples randomly.
	 */
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
