package cgsynt.tree.buchi.parity.operations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import cgsynt.tree.buchi.parity.BuchiParityIntersectAutomaton;
import cgsynt.tree.buchi.parity.BuchiParityIntersectRule;
import cgsynt.tree.buchi.parity.BuchiParityIntersectState;
import cgsynt.tree.parity.IParityState;
import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;

public class BuchiParityCounterexampleGeneration<LETTER extends IRankedLetter, STATE1, STATE2 extends IParityState, TransitionLETTER> {

	private BuchiParityIntersectAutomaton<LETTER, STATE1, STATE2> tree;
	int maxLength;
	private List<TransitionLETTER> transitionAlphabet;
	private Set<BuchiParityIntersectState<STATE1, STATE2>> visitedStates;
	private List<Triplet<STATE1, STATE2, TransitionLETTER>> result;
	private boolean resultComputed;

	public BuchiParityCounterexampleGeneration(BuchiParityIntersectAutomaton<LETTER, STATE1, STATE2> tree,
			int maxLength, List<TransitionLETTER> transitionAlphabet) {
		this.tree = tree;
		this.maxLength = maxLength;
		this.visitedStates = new HashSet<>();
		this.transitionAlphabet = transitionAlphabet;

	}

	public void computeResult() {
		if (resultComputed)
			return;
		List<Triplet<STATE1, STATE2, TransitionLETTER>> tripletCounterexamples = new ArrayList<>();
		for (BuchiParityIntersectState<STATE1, STATE2> initialState : tree.getInitStates()) {
			tripletCounterexamples.addAll(generateCounterexamples(initialState, maxLength));
		}
		result = tripletCounterexamples;
		resultComputed = true;
	}

	public List<Stack<TransitionLETTER>> getResultTransition() {
		if (!resultComputed)
			return null;
		List<Stack<TransitionLETTER>> resultTransitions = new ArrayList<>();
		for (int i = 0; i < result.size(); i++) {
			resultTransitions.add(result.get(i).transitions);
		}
		return resultTransitions;
	}

	public List<Stack<BuchiParityIntersectState<STATE1, STATE2>>> getResultStates() {
		if (!resultComputed)
			return null;
		List<Stack<BuchiParityIntersectState<STATE1, STATE2>>> resultStates = new ArrayList<>();
		for (int i = 0; i < result.size(); i++) {
			resultStates.add(result.get(i).states);
		}
		return resultStates;
	}

	private List<Triplet<STATE1, STATE2, TransitionLETTER>> generateCounterexamples(
			BuchiParityIntersectState<STATE1, STATE2> state, int len) {
		if (len == 0) {
			return null;
		}
		if (visitedStates.contains(state)) {
			Triplet<STATE1, STATE2, TransitionLETTER> triplet = new Triplet<>(state);
			List<Triplet<STATE1, STATE2, TransitionLETTER>> triplets = new ArrayList<>();
			triplets.add(triplet);
			return triplets;
		}
		visitedStates.add(state);
		List<Triplet<STATE1, STATE2, TransitionLETTER>> allTriplets = new ArrayList<>();
		for (BuchiParityIntersectRule<LETTER, STATE1, STATE2> transition : tree.getForSourceMap(state)) {
			List<BuchiParityIntersectState<STATE1, STATE2>> dests = transition.getDests();
			for (int i = 0; i < transitionAlphabet.size(); i++) {
				List<Triplet<STATE1, STATE2, TransitionLETTER>> triplets = generateCounterexamples(dests.get(i), len - 1);
				for (int j = 0; j < triplets.size(); j++) {
					if (triplets.get(j).repeatedState.equals(state)) {
						triplets.get(j).states.push(triplets.get(j).states.peek());
						triplets.get(j).transitions.push(null);
					}
					triplets.get(j).states.push(state);
					triplets.get(j).transitions.push(transitionAlphabet.get(i));
				}
				allTriplets.addAll(triplets);
			}
		}
		visitedStates.remove(state);
		return allTriplets;
	}

}

class Triplet<STATE1, STATE2 extends IParityState, TransitionLETTER> {
	public final Stack<TransitionLETTER> transitions;
	public final BuchiParityIntersectState<STATE1, STATE2> repeatedState;
	public final Stack<BuchiParityIntersectState<STATE1, STATE2>> states;

	public Triplet(BuchiParityIntersectState<STATE1, STATE2> repeatedState) {
		this.transitions = new Stack<>();
		this.states = new Stack<>();
		this.repeatedState = repeatedState;
		this.states.push(repeatedState);
	}

	public Triplet(Stack<TransitionLETTER> transitions, Stack<BuchiParityIntersectState<STATE1, STATE2>> states,
			BuchiParityIntersectState<STATE1, STATE2> repeatedState) {
		this.transitions = transitions;
		this.states = states;
		this.repeatedState = repeatedState;
	}

}