package cgsynt.nfa.operations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.transitions.OutgoingInternalTransition;

public class NFACounterexampleGeneration<LETTER, STATE> {

	private NestedWordAutomaton<LETTER, STATE> aut;
	private boolean resultComputed;
	private int maxLen;
	private Map<STATE, Integer> visitedStates;
	private List<Counterexample<LETTER, STATE>> result;

	public NFACounterexampleGeneration(NestedWordAutomaton<LETTER, STATE> aut, int maxLen) {
		this.aut = aut;
		this.resultComputed = false;
		this.maxLen = maxLen;
	}

	public void computeResult() {
		if (resultComputed) {
			return;
		}
		this.visitedStates = new HashMap<>();
		this.result = new ArrayList<>();
		for (STATE initialState : aut.getInitialStates()) {
			List<Counterexample<LETTER, STATE>> counterexamples = generateCounterexamples(initialState, maxLen);
			for (int i = counterexamples.size() - 1; i >= 0; i--) {
				if (counterexamples.get(i).repeatedState != null) {
					counterexamples.remove(i);
				}
			}
			result.addAll(counterexamples);
		}
		resultComputed = true;
	}

	public List<Counterexample<LETTER, STATE>> getResult() {
		if (!resultComputed)
			return null;
		return result;
	}

	private List<Counterexample<LETTER, STATE>> generateCounterexamples(STATE state, int len) {
		List<Counterexample<LETTER, STATE>> counterexamples = new ArrayList<>();
		if (visitedStates.containsKey(state) && visitedStates.get(state) > 0) {
			Counterexample<LETTER, STATE> counterexample = new Counterexample<>();
			counterexample.repeatedState = state;
			counterexample.loopStates.push(state);
			counterexamples.add(counterexample);
		}
		if (len == 0) {
			return counterexamples;
		}
		if (!visitedStates.containsKey(state)) {
			visitedStates.put(state, 0);
		}
		visitedStates.put(state, visitedStates.get(state) + 1);
		for (OutgoingInternalTransition<LETTER, STATE> transition : aut.internalSuccessors(state)) {
			List<Counterexample<LETTER, STATE>> destCounterexamples = generateCounterexamples(transition.getSucc(),
					maxLen);
			for (int i = 0; i < destCounterexamples.size(); i++) {
				if (state.equals(destCounterexamples.get(i).repeatedState)) {
					Counterexample<LETTER, STATE> copy = destCounterexamples.get(i).makeCopy();
					copy.repeatedState = null;
					copy.loopStates.push(state);
					copy.loopTransitions.push(transition.getLetter());
					copy.stemStates.push(state);
					counterexamples.add(copy);
				}
				if (!state.equals(destCounterexamples.get(i).repeatedState) || visitedStates.get(state) > 1) {
					if (destCounterexamples.get(i).repeatedState == null) {
						destCounterexamples.get(i).stemTransitions.push(transition.getLetter());
						destCounterexamples.get(i).stemStates.push(state);
						counterexamples.add(destCounterexamples.get(i));
					} else {
						destCounterexamples.get(i).loopTransitions.push(transition.getLetter());
						destCounterexamples.get(i).loopStates.push(state);
						counterexamples.add(destCounterexamples.get(i));
					}
				}
			}
		}
		visitedStates.put(state, visitedStates.get(state) - 1);
		return counterexamples;
	}
}

class Counterexample<LETTER, STATE> {
	public Stack<LETTER> loopTransitions;
	public Stack<STATE> loopStates;
	public Stack<LETTER> stemTransitions;
	public Stack<STATE> stemStates;
	public STATE repeatedState;

	public Counterexample() {
		this.loopTransitions = new Stack<>();
		this.loopStates = new Stack<>();
		this.stemTransitions = new Stack<>();
		this.stemStates = new Stack<>();
		this.repeatedState = null;
	}

	public Counterexample<LETTER, STATE> makeCopy() {
		Counterexample<LETTER, STATE> copy = new Counterexample<>();
		copy.repeatedState = this.repeatedState;
		copy.loopTransitions.addAll(this.loopTransitions);
		copy.loopStates.addAll(this.loopStates);
		copy.stemStates.addAll(this.stemStates);
		copy.stemTransitions.addAll(this.stemTransitions);
		return copy;
	}

}