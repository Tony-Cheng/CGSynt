package cgsynt.buchi.determinization;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import cgsynt.dfa.parity.ParityAutomaton;
import cgsynt.tree.parity.IParityState;
import cgsynt.tree.parity.ParityState;
import cgsynt.tree.parity.ParityStateFactory;
import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.INestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.VpAlphabet;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.transitions.OutgoingInternalTransition;

/**
 * Determinize a buchi automaton to create a parity automaton using the method
 * in the following paper: https://arxiv.org/pdf/0705.2205.pdf
 *
 * @param <LETTER>
 * @param <STATE>
 */
public class BuchiDeterminization<LETTER, STATE> {
	private INestedWordAutomaton<LETTER, STATE> buchiAut;
	private boolean resultComputed;
	private ParityAutomaton<LETTER, IParityState> result;
	private AutomataLibraryServices services;
	private VpAlphabet<LETTER> vpAlphabet;
	private ParityStateFactory emptyStateFactory;
	private Set<SafraTree<STATE>> visitedStates;
	private Stack<SafraTree<STATE>> toVisitStates;

	public BuchiDeterminization(INestedWordAutomaton<LETTER, STATE> buchiAut, AutomataLibraryServices services,
			ParityStateFactory emptyStateFactory) {
		this.buchiAut = buchiAut;
		this.resultComputed = false;
		this.services = services;
		this.vpAlphabet = buchiAut.getVpAlphabet();
		this.emptyStateFactory = emptyStateFactory;
	}

	@SuppressWarnings("unchecked")
	public void computeResult() {
		if (resultComputed)
			return;
		this.result = new ParityAutomaton<>(services, vpAlphabet, emptyStateFactory);
		SafraTree<STATE> initialTree = new SafraTree<>(buchiAut.getInitialStates(), buchiAut.getStates().size(), true);
		this.visitedStates = new HashSet<>();
		this.toVisitStates = new Stack<>();
		toVisitStates.push(initialTree);
		visitedStates.add(initialTree);
		result.addState(true, false, initialTree);
		while (!toVisitStates.isEmpty()) {
			SafraTree<STATE> next = toVisitStates.pop();
			for (LETTER letter : buchiAut.getAlphabet()) {
				SafraTree<STATE> copy = (SafraTree<STATE>)next.makeCpy();
				reset(copy);
				step1(copy, letter);
				step2(copy);
				step3(copy);
				step4(copy);
				step5(copy);
				step6(copy);
				if (!result.contains(copy))
					result.addState(false, false, copy);
				if (!result.containsInternalTransition(next, letter, copy))
					result.addInternalTransition(next, letter, copy);
				if (!visitedStates.contains(copy)) {
					toVisitStates.add(copy);
					visitedStates.add(copy);
				}
			}
		}
		resultComputed = true;
	}

	private void reset(SafraTree<STATE> tree) {
		tree.resetEF();
	}

	private void step1(SafraTree<STATE> tree, LETTER letter) {
		for (Integer node : tree.getStates()) {
			Set<STATE> newLabel = new HashSet<>();
			for (STATE state : tree.getLabels(node)) {
				for (OutgoingInternalTransition<LETTER, STATE> newState : buchiAut.internalSuccessors(state, letter)) {
					newLabel.add(newState.getSucc());
				}
			}
			tree.setLabels(node, newLabel);
		}
	}

	private void step2(SafraTree<STATE> tree) {
		Stack<Set<STATE>> intersectionLabels = new Stack<>();
		Stack<Integer> intersectionNodes = new Stack<>();
		for (Integer node : tree.getStates()) {
			Collection<STATE> finalStates = buchiAut.getFinalStates();
			Set<STATE> intersection = findIntersection(finalStates, tree.getLabels(node));
			for (STATE finalState : finalStates) {
				if (tree.getLabels(node).contains(finalState)) {
					intersection.add(finalState);
				}
			}
			if (!intersection.isEmpty()) {
				intersectionLabels.push(intersection);
				intersectionNodes.push(node);
			}
		}
		while (!intersectionNodes.isEmpty()) {
			tree.addNode(intersectionNodes.pop(), intersectionLabels.pop());
		}
	}

	private void step3(SafraTree<STATE> tree) {
		for (Integer node : tree.getStates()) {
			Set<Integer> siblings = tree.getSiblings(node);
			if (siblings != null) {
				for (Integer sibling : siblings) {
					if (!sibling.equals(sibling)) {
						Set<STATE> intersection = findIntersection(tree.getLabels(node), tree.getLabels(sibling));
						for (STATE state : intersection) {
							if (tree.getName(node) > tree.getName(sibling)) {
								tree.removeLabel(node, state);
							} else {
								tree.removeLabel(sibling, state);
							}
						}
					}
				}
			}
		}
	}

	private void step4(SafraTree<STATE> tree) {
		Set<Integer> greenNodes = new HashSet<>();
		for (Integer node : tree.getStates()) {
			Set<STATE> union = getUnion(tree, tree.getChildren(node));
			if (tree.getLabels(node).equals(union)) {
				greenNodes.add(node);
			}
		}
		for (Integer node : greenNodes) {
			tree.setGreenNode(node);
		}
	}

	private void step5(SafraTree<STATE> tree) {
		Set<Integer> toRemoveNodes = new HashSet<>();
		for (Integer node : tree.getStates()) {
			if (tree.getLabels(node).isEmpty()) {
				toRemoveNodes.add(node);
			}
		}
		for (Integer node : toRemoveNodes) {
			tree.removeNode(node);
		}
	}

	private void step6(SafraTree<STATE> tree) {
		tree.compressTree();
	}

	private Set<STATE> getUnion(SafraTree<STATE> tree, Set<Integer> nodes) {
		Set<STATE> union = new HashSet<>();
		for (Integer node : nodes) {
			union.addAll(tree.getLabels(node));
		}
		return union;
	}

	private Set<STATE> findIntersection(Collection<STATE> set1, Set<STATE> set2) {
		Set<STATE> intersection = new HashSet<>();
		for (STATE state1 : set1) {
			if (set2.contains(state1)) {
				intersection.add(state1);
			}
		}
		return intersection;
	}

	public ParityAutomaton<LETTER, IParityState> getResult() {
		if (!resultComputed)
			return null;
		return result;
	}

}
