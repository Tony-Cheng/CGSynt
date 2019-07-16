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

	private boolean result;
	private boolean resultComputed;
	private ParityTreeAutomaton<LETTER, STATE> tree;

	private Set<STATE> goodEvenStates;

	private Set<STATE> goodStates;

	private Map<STATE, Integer> maxOdd;

	private Stack<ParityTreeAutomatonRule<LETTER, STATE>> goodTransitions;

	public ParityEmptinessCheck(ParityTreeAutomaton<LETTER, STATE> tree) {
		resultComputed = false;
		this.tree = tree;
		this.goodEvenStates = computeEvenStates(tree);
		this.goodStates = new HashSet<>();
		this.maxOdd = initializeMaxOdd();
		this.goodTransitions = new Stack<>();
	}

	private Map<STATE, Integer> initializeMaxOdd() {
		Map<STATE, Integer> maxOdd = new HashMap<>();
		for (STATE state : tree.getStates()) {
			if (state.getRank() % 2 == 0)
				maxOdd.put(state, 0);
			else
				maxOdd.put(state, state.getRank());
		}
		return maxOdd;

	}

	private Set<STATE> computeEvenStates(ParityTreeAutomaton<LETTER, STATE> tree) {
		Set<STATE> evenStates = new HashSet<>();
		for (STATE state : tree.getStates()) {
			if (state.getRank() % 2 == 0) {
				evenStates.add(state);
			}
		}
		return evenStates;
	}

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
	
	
	public boolean getResult() {
		return result;
	}

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

	private void removeInitialNotDecentStates() {
		Iterator<STATE> iterInitStates = tree.getInitStates().iterator();
		while (iterInitStates.hasNext()) {
			STATE nextInitState = iterInitStates.next();
			if (!goodStates.contains(nextInitState)) {
				iterInitStates.remove();
				tree.removeState(nextInitState);
			}
		}
	}

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
				findAllGoodStates();
			}
		}
	}

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

	private void updateMaxOdd(STATE state, int oddValue) {
		maxOdd.put(state, oddValue);
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
						if (maxOdd.get(state) > maxOddValue) {
							updateMaxOdd(rule.getSource(), maxOddValue);
						}
					} else {
						goodTransitions.add(rule);
					}
				}
			}
		}
	}

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
				if (oddValue < maxOdd.get(src))
					updateMaxOdd(src, oddValue);
				continue;
			}
			goodStates.add(src);
			if (goodEvenStates.contains(src))
				continue;
			int oddValue = maxOdd.get(src);
			for (STATE state : nextRule.getDest()) {
				if (maxOdd.get(state) > oddValue) {
					oddValue = maxOdd.get(state);
				}
			}
			Collection<ParityTreeAutomatonRule<LETTER, STATE>> ruleToSrc = tree.getChildMap().get(src);
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
						goodTransitions.add(rule);
					}
				}
			}
		}
	}

	private boolean removeEvenNotGoodStates() {
		boolean evenStateRemoved = false;

		Iterator<STATE> iterEvenStates = goodEvenStates.iterator();
		while (iterEvenStates.hasNext()) {
			STATE nextEvenState = iterEvenStates.next();
			if (!goodStates.contains(nextEvenState)) {
				iterEvenStates.remove();
				goodEvenStates.remove(nextEvenState);
				evenStateRemoved = true;
			}
		}
		return evenStateRemoved;
	}
}
