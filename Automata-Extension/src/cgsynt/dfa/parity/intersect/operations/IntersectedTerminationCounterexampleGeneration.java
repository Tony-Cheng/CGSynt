package cgsynt.dfa.parity.intersect.operations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cgsynt.dfa.parity.intersect.DfaParityIntersectAutomaton;
import cgsynt.dfa.parity.intersect.DfaParityIntersectRule;
import cgsynt.dfa.parity.intersect.DfaParityIntersectState;
import cgsynt.tree.parity.IParityState;

public class IntersectedTerminationCounterexampleGeneration<LETTER, STATE1, STATE2 extends IParityState> {

	private DfaParityIntersectAutomaton<LETTER, STATE1, STATE2> aut;
	private boolean resultComputed;
	private int maxLen;
	private Map<DfaParityIntersectState<STATE1, STATE2>, Integer> visitedStates;
	private List<DfaParityCounterexample<LETTER, STATE1, STATE2>> result;

	public IntersectedTerminationCounterexampleGeneration(DfaParityIntersectAutomaton<LETTER, STATE1, STATE2> aut,
			int maxLen) {
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
		for (DfaParityIntersectState<STATE1, STATE2> initialState : aut.getInitialStates()) {
			List<DfaParityCounterexample<LETTER, STATE1, STATE2>> counterexamples = generateCounterexamples(initialState,
					maxLen);
			for (int i = counterexamples.size() - 1; i >= 0; i--) {
				if (counterexamples.get(i).repeatedState != null) {
					counterexamples.remove(i);
				}
			}
			result.addAll(counterexamples);
		}
		resultComputed = true;
	}

	public List<DfaParityCounterexample<LETTER, STATE1, STATE2>> getResult() {
		if (!resultComputed)
			return null;
		return result;
	}

	private List<DfaParityCounterexample<LETTER, STATE1, STATE2>> generateCounterexamples(
			DfaParityIntersectState<STATE1, STATE2> state, int len) {
		List<DfaParityCounterexample<LETTER, STATE1, STATE2>> counterexamples = new ArrayList<>();
		if (visitedStates.containsKey(state) && visitedStates.get(state) > 0 && aut.isFinal(state)) {
			DfaParityCounterexample<LETTER, STATE1, STATE2> counterexample = new DfaParityCounterexample<>(
					state.getRank());
			counterexample.repeatedState = state;
			// counterexample.loopStates.push(state);
			counterexamples.add(counterexample);
		}
		if (len == 0) {
			return counterexamples;
		}
		if (!visitedStates.containsKey(state)) {
			visitedStates.put(state, 0);
		}
		visitedStates.put(state, visitedStates.get(state) + 1);
		for (DfaParityIntersectRule<LETTER, STATE1, STATE2> transition : aut.internalSuccessors(state)) {
			List<DfaParityCounterexample<LETTER, STATE1, STATE2>> destCounterexamples = generateCounterexamples(
					transition.getSucc(), len - 1);
			for (int i = 0; i < destCounterexamples.size(); i++) {
				if (state.equals(destCounterexamples.get(i).repeatedState)
						&& destCounterexamples.get(i).maxRepeatingNumber % 2 == 1) {
					DfaParityCounterexample<LETTER, STATE1, STATE2> copy = destCounterexamples.get(i).makeCopy();
					copy.repeatedState = null;
					copy.loopStates.push(state);
					copy.loopTransitions.push(transition.getLetter());
					copy.stemStates.push(state);
					counterexamples.add(copy);
				}
				if (!state.equals(destCounterexamples.get(i).repeatedState) || visitedStates.get(state) > 1) {
					destCounterexamples.get(i).maxRepeatingNumber = Math
							.max(destCounterexamples.get(i).maxRepeatingNumber, state.getRank());

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
