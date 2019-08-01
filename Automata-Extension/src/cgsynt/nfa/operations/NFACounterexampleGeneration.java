package cgsynt.nfa.operations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.transitions.OutgoingInternalTransition;

public class NFACounterexampleGeneration<LETTER, STATE> {

	private NestedWordAutomaton<LETTER, STATE> aut;
	private boolean resultComputed;
	private int maxLen;
	private Map<STATE, Integer> visitedStates;
	private List<NFACounterexample<LETTER, STATE>> result;

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
			List<NFACounterexample<LETTER, STATE>> counterexamples = generateCounterexamples(initialState, maxLen);
			for (int i = counterexamples.size() - 1; i >= 0; i--) {
				if (counterexamples.get(i).repeatedState != null) {
					counterexamples.remove(i);
				}
			}
			result.addAll(counterexamples);
		}
		resultComputed = true;
	}

	public List<NFACounterexample<LETTER, STATE>> getResult() {
		if (!resultComputed)
			return null;
		return result;
	}

	private List<NFACounterexample<LETTER, STATE>> generateCounterexamples(STATE state, int len) {
		List<NFACounterexample<LETTER, STATE>> counterexamples = new ArrayList<>();
		if (visitedStates.containsKey(state) && visitedStates.get(state) > 0) {
			NFACounterexample<LETTER, STATE> counterexample = new NFACounterexample<>();
			counterexample.repeatedState = state;
			counterexample.loopStates.push(state);
			if (aut.isFinal(state)) {
				counterexample.loopContainFinalState = true;
			}
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
			List<NFACounterexample<LETTER, STATE>> destCounterexamples = generateCounterexamples(transition.getSucc(),
					len - 1);
			for (int i = 0; i < destCounterexamples.size(); i++) {
				if (state.equals(destCounterexamples.get(i).repeatedState)
						&& destCounterexamples.get(i).loopContainFinalState) {
					NFACounterexample<LETTER, STATE> copy = destCounterexamples.get(i).makeCopy();
					copy.repeatedState = null;
					copy.loopStates.push(state);
					copy.loopTransitions.push(transition.getLetter());
					copy.stemStates.push(state);
					counterexamples.add(copy);
				}
				if (!state.equals(destCounterexamples.get(i).repeatedState) || visitedStates.get(state) > 1) {
					if (aut.isFinal(state)) {
						destCounterexamples.get(i).loopContainFinalState = true;
					}
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