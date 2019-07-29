package cgsynt.tree.parity.operations;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import cgsynt.tree.buchi.BuchiTreeAutomatonRule;
import cgsynt.tree.parity.IParityState;
import cgsynt.tree.parity.ParityTreeAutomaton;
import cgsynt.tree.parity.ParityTreeAutomatonRule;
import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;

public class ParityEmptinessCheck<LETTER extends IRankedLetter, STATE extends IParityState> {

	/**
	 * True if the parity automaton is empty and false otherwise.
	 */
	private boolean result;

	/**
	 * True if the result is computed and false otherwise.
	 */
	private boolean resultComputed;

	/**
	 * A parity tree to perform the emptiness check on.
	 */
	private ParityTreeAutomaton<LETTER, STATE> tree;

	/**
	 * A state is a good even state if the state is even and there's a subtree root
	 * at that state such that the maximum number along every path of that subtree
	 * is even.
	 */
	private Set<STATE> goodEvenStates;

	/**
	 * A state is a good state if it's the source of a transition where the
	 * destination states are either good states or good even states. If the state
	 * is a good even state, then it must satisfy the additional requirement that
	 * its maxOdd value is greater than its minEven value to be a good state.
	 */
	private Set<STATE> goodStates;

	/**
	 * The smallest of the largest odd number of subtrees rooted at the key where
	 * the leaves are good even states. Moreover, this number is zero if the minEven
	 * of the key is greater than smallest max odd number.
	 */
	private Map<STATE, Integer> maxOdd;

	/**
	 * The largest of the smallest even number along every path of subtrees rooted
	 * at the key where the leaves are good even states.
	 */
	private Map<STATE, Integer> minEven;

	/**
	 * A transition is good if the destination states are either good states or good
	 * even states.
	 */
	private Stack<ParityTreeAutomatonRule<LETTER, STATE>> goodTransitions;

	/**
	 * Construct an object for checking the emptiness of a parity automaton.
	 * 
	 * @param tree
	 */
	public ParityEmptinessCheck(ParityTreeAutomaton<LETTER, STATE> tree) {
		resultComputed = false;
		this.tree = tree;
		this.goodEvenStates = computeEvenStates(tree);
		this.goodStates = new HashSet<>();
		this.goodTransitions = new Stack<>();
		this.maxOdd = initializeMaxOdd();
		this.minEven = initializeMinEven();
	}

	/**
	 * Initialize the map maxOdd.
	 * 
	 * @return
	 */
	private Map<STATE, Integer> initializeMaxOdd() {
		Map<STATE, Integer> maxOdd = new HashMap<>();
		for (STATE state : tree.getStates()) {
			if (goodEvenStates.contains(state))
				maxOdd.put(state, 0);
			else
				maxOdd.put(state, state.getRank());
		}
		return maxOdd;
	}

	/**
	 * Initialize the map minEven.
	 * 
	 * @return
	 */
	private Map<STATE, Integer> initializeMinEven() {
		Map<STATE, Integer> minEven = new HashMap<>();
		for (STATE state : tree.getStates()) {
			if (goodEvenStates.contains(state))
				minEven.put(state, state.getRank());
			else
				minEven.put(state, 0);
		}
		return minEven;
	}

	/**
	 * Return a set of all even states in the tree.
	 * 
	 * @param tree
	 * @return
	 */
	private Set<STATE> computeEvenStates(ParityTreeAutomaton<LETTER, STATE> tree) {
		Set<STATE> evenStates = new HashSet<>();
		for (STATE state : tree.getStates()) {
			if (state.getRank() % 2 == 0) {
				evenStates.add(state);
			}
		}
		return evenStates;
	}

	/**
	 * Compute whether the tree is empty or not.
	 */
	public void computeResult() {
		if (resultComputed)
			return;
		computeGoodTree();
		goodStates.clear();
		goodTransitions.clear();
		if (computeDecentTree()) {
			result = true;
			resultComputed = true;
			return;
		} else {
			result = false;
			resultComputed = false;
			return;
		}
	}

	/**
	 * Return true if the tree is empty and false otherwise.
	 * 
	 * @return
	 */
	public boolean getResult() {
		return result;
	}

	/**
	 * Remove all the states that are not the root of some subtrees whose
	 * destination states are all good even states.
	 * 
	 * @return
	 */
	private boolean computeDecentTree() {
		initializeDecentTransitions();
		findAllDecentStates();
		removeInitialNotDecentStates();
		if (tree.getInitStates().isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Find all transitions whose destination consists of good even states.
	 */
	private void initializeDecentTransitions() {
		Set<ParityTreeAutomatonRule<LETTER, STATE>> allRules = tree.getRules();
		for (ParityTreeAutomatonRule<LETTER, STATE> rule : allRules) {
			List<STATE> dests = rule.getDest();
			boolean isGoodTransition = true;
			for (STATE dest : dests) {
				if (!goodEvenStates.contains(dest)) {
					isGoodTransition = false;
					break;
				}
			}
			if (isGoodTransition)
				goodTransitions.add(rule);
		}
	}

	/**
	 * Find all states that are the root of some subtrees whose destinations are
	 * good even states.
	 */
	private void findAllDecentStates() {
		while (!goodTransitions.isEmpty()) {
			ParityTreeAutomatonRule<LETTER, STATE> nextRule = goodTransitions.pop();
			STATE src = nextRule.getSource();
			if (!goodStates.contains(src)) {
				goodStates.add(src);
				Collection<ParityTreeAutomatonRule<LETTER, STATE>> ruleToSrc = tree.getChildMap().get(src);
				if (ruleToSrc != null && !goodEvenStates.contains(src)) {
					for (ParityTreeAutomatonRule<LETTER, STATE> rule : ruleToSrc) {
						boolean isGoodTransition = true;
						for (STATE dest : rule.getDest()) {
							if (!goodEvenStates.contains(dest) && !goodStates.contains(dest)) {
								isGoodTransition = false;
								break;
							}
						}
						if (isGoodTransition) {
							goodTransitions.add(rule);
						}
					}
				}
			}
		}
	}

	/**
	 * Remove all initial states that are not decent states.
	 */
	private void removeInitialNotDecentStates() {
		Iterator<STATE> iterInitStates = tree.getInitStates().iterator();
		while (iterInitStates.hasNext()) {
			STATE nextInitState = iterInitStates.next();
			if (!goodStates.contains(nextInitState)) {
				iterInitStates.remove();
			}
		}
	}

	/**
	 * Remove all states that are not good even states.
	 */
	private void computeGoodTree() {
		initializeGoodTransitions();
		findAllGoodStates();
		while (!goodStates.isEmpty()) {
			if (!removeEvenNotGoodStates()) {
				return;
			} else {
				goodStates.clear();
				goodTransitions.clear();
				initializeGoodTransitions();
				maxOdd = initializeMaxOdd();
				minEven = initializeMinEven();
				findAllGoodStates();
			}
		}
	}

	/**
	 * Find all transitions whose destination consists of good even states.
	 */
	private void initializeGoodTransitions() {
		Set<ParityTreeAutomatonRule<LETTER, STATE>> allRules = tree.getRules();
		for (ParityTreeAutomatonRule<LETTER, STATE> rule : allRules) {
			List<STATE> dests = rule.getDest();
			boolean isGoodTransition = true;
			for (STATE dest : dests) {
				if (!goodEvenStates.contains(dest)) {
					isGoodTransition = false;
					break;
				}
			}
			if (isGoodTransition)
				goodTransitions.add(rule);
		}
	}

	/**
	 * Update the max odd value of a state.
	 * 
	 * @param state
	 * @param oddValue
	 */
	private void updateMaxOdd(STATE state, int oddValue) {
		maxOdd.put(state, oddValue);
		if (oddValue <= minEven.get(state)) {
			maxOdd.put(state, 0);
		}
		Collection<ParityTreeAutomatonRule<LETTER, STATE>> ruleToSrc = tree.getChildMap().get(state);
		if (ruleToSrc != null) {
			for (ParityTreeAutomatonRule<LETTER, STATE> rule : ruleToSrc) {
				boolean isGoodTransition = true;
				int maxOddValue = oddValue;
				for (STATE dest : rule.getDest()) {
					if (!goodEvenStates.contains(dest) && !goodStates.contains(dest)) {
						isGoodTransition = false;
						break;
					}
					if (maxOdd.get(dest) > maxOddValue) {
						maxOddValue = maxOdd.get(dest);
					}
				}
				if (isGoodTransition
						&& (!goodEvenStates.contains(rule.getSource()) || rule.getSource().getRank() >= maxOddValue)) {
					if (goodStates.contains(rule.getSource())) {
						if (maxOdd.get(rule.getSource()) > maxOddValue) {
							updateMaxOdd(rule.getSource(), maxOddValue);
						}
					} else {
						goodTransitions.add(rule);
					}
				}
			}
		}
	}

	/**
	 * Update the min even value of a state.
	 * 
	 * @param state
	 * @param evenValue
	 */
	private void updateMinEven(STATE state, int evenValue) {
		minEven.put(state, evenValue);
		if (evenValue >= maxOdd.get(state)) {
			updateMaxOdd(state, 0);
		}
		Collection<ParityTreeAutomatonRule<LETTER, STATE>> ruleToSrc = tree.getChildMap().get(state);
		if (ruleToSrc != null) {
			for (ParityTreeAutomatonRule<LETTER, STATE> rule : ruleToSrc) {
				boolean isGoodTransition = true;
				int maxOddValue = 0;
				int minEvenValue = Integer.MAX_VALUE;
				for (STATE dest : rule.getDest()) {
					if (!goodEvenStates.contains(dest) && !goodStates.contains(dest)) {
						isGoodTransition = false;
						break;
					}
					if (maxOdd.get(dest) > maxOddValue) {
						maxOddValue = maxOdd.get(dest);
					}
					if (minEven.get(dest) < minEvenValue) {
						minEvenValue = minEven.get(dest);
					}
				}
				if (isGoodTransition
						&& (!goodEvenStates.contains(rule.getSource()) || rule.getSource().getRank() >= maxOddValue)) {
					if (goodStates.contains(rule.getSource())) {
						if (minEven.get(rule.getSource()) < minEvenValue) {
							updateMinEven(rule.getSource(), minEvenValue);
						}
					} else {
						goodTransitions.add(rule);
					}
				}
			}
		}
	}

	/**
	 * Find all good states.
	 */
	private void findAllGoodStates() {
		while (!goodTransitions.isEmpty()) {
			ParityTreeAutomatonRule<LETTER, STATE> nextRule = goodTransitions.pop();
			STATE src = nextRule.getSource();
			if (goodStates.contains(src)) {
				int oddValue = src.getRank();
				for (STATE state : nextRule.getDest()) {
					if (maxOdd.get(state) > oddValue) {
						oddValue = maxOdd.get(state);
					}
				}
				if (oddValue < maxOdd.get(src)) {
					updateMaxOdd(src, oddValue);
				}
				int evenValue = Integer.MAX_VALUE;
				for (STATE state : nextRule.getDest()) {
					if (minEven.get(state) < evenValue) {
						evenValue = minEven.get(state);
					}
				}
				if (evenValue > minEven.get(src)) {
					updateMinEven(src, evenValue);
				}
				continue;
			}
			goodStates.add(src);
			if (goodEvenStates.contains(src)) {
				int evenValue = Integer.MAX_VALUE;
				for (STATE state : nextRule.getDest()) {
					if (minEven.get(state) < evenValue) {
						evenValue = minEven.get(state);
					}
				}
				if (evenValue > minEven.get(src)) {
					updateMinEven(src, evenValue);
				}
				continue;
			}
			int oddValue = maxOdd.get(src);
			for (STATE state : nextRule.getDest()) {
				if (maxOdd.get(state) > oddValue) {
					oddValue = maxOdd.get(state);
				}
			}
			int evenValue = Integer.MAX_VALUE;
			for (STATE state : nextRule.getDest()) {
				if (minEven.get(state) < evenValue) {
					evenValue = minEven.get(state);
				}
			}
			updateMaxOdd(src, oddValue);
			if (evenValue > minEven.get(src)) {
				updateMinEven(src, evenValue);
			}
			if (evenValue >= maxOdd.get(src)) {
				updateMaxOdd(src, 0);
			}
		}
	}

	/**
	 * Remove all good even states that are not good states.
	 * 
	 * @return
	 */
	private boolean removeEvenNotGoodStates() {
		boolean evenStateRemoved = false;

		Iterator<STATE> iterEvenStates = goodEvenStates.iterator();
		while (iterEvenStates.hasNext()) {
			STATE nextEvenState = iterEvenStates.next();
			if (!goodStates.contains(nextEvenState)) {
				iterEvenStates.remove();
				evenStateRemoved = true;
			}
		}
		return evenStateRemoved;
	}
}
