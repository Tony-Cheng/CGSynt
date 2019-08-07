package cgsynt.nfa.operations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cgsynt.dfa.parity.ParityAutomaton;
import cgsynt.tree.parity.IParityState;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.transitions.OutgoingInternalTransition;

public class NFACounterexampleGeneration<LETTER> {

	private ParityAutomaton<LETTER, IParityState> aut;
	private boolean resultComputed;
	private int maxLen;
	private Map<IParityState, Integer> visitedStates;
	private List<NFACounterexample<LETTER, IParityState>> result;

	public NFACounterexampleGeneration(ParityAutomaton<LETTER, IParityState> aut, int maxLen) {
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
		for (IParityState initialState : aut.getInitialStates()) {
			List<NFACounterexample<LETTER, IParityState>> counterexamples = generateCounterexamples(initialState,
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

	public List<NFACounterexample<LETTER, IParityState>> getResult() {
		if (!resultComputed)
			return null;
		return result;
	}

	private List<NFACounterexample<LETTER, IParityState>> generateCounterexamples(IParityState state, int len) {
		List<NFACounterexample<LETTER, IParityState>> counterexamples = new ArrayList<>();
		if (visitedStates.containsKey(state) && visitedStates.get(state) > 0) {
			NFACounterexample<LETTER, IParityState> counterexample = new NFACounterexample<>(state.getRank());
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
		for (OutgoingInternalTransition<LETTER, IParityState> transition : aut.internalSuccessors(state)) {
			List<NFACounterexample<LETTER, IParityState>> destCounterexamples = generateCounterexamples(
					transition.getSucc(), len - 1);
			for (int i = 0; i < destCounterexamples.size(); i++) {
				if (state.equals(destCounterexamples.get(i).repeatedState)
						&& destCounterexamples.get(i).maxRepeatingNumber % 2 == 0) {
					NFACounterexample<LETTER, IParityState> copy = destCounterexamples.get(i).makeCopy();
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