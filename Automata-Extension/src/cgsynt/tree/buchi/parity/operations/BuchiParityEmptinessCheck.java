package cgsynt.tree.buchi.parity.operations;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import cgsynt.tree.buchi.parity.BuchiParityIntersectAutomaton;
import cgsynt.tree.buchi.parity.BuchiParityIntersectRule;
import cgsynt.tree.buchi.parity.BuchiParityIntersectState;
import cgsynt.tree.parity.IParityState;
import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;

/**
 * An emptiness check operation for the intersection between a buchi tree
 * automaton and a parity tree automaton.
 *
 * @param <LETTER>
 * @param <STATE1>
 * @param <STATE2>
 */
public class BuchiParityEmptinessCheck<LETTER extends IRankedLetter, STATE1, STATE2 extends IParityState> {

	private boolean result;
	private boolean resultComputed;
	private BuchiParityIntersectAutomaton<LETTER, STATE1, STATE2> tree;

	private Set<BuchiParityIntersectState<STATE1, STATE2>> goodLeavesStates;
	private Set<BuchiParityIntersectState<STATE1, STATE2>> goodParityStates;
	private Set<BuchiParityIntersectState<STATE1, STATE2>> goodBuchiStates;

	private Set<BuchiParityIntersectState<STATE1, STATE2>> goodStates;

	private Map<BuchiParityIntersectState<STATE1, STATE2>, Integer> maxOdd;
	private Map<BuchiParityIntersectState<STATE1, STATE2>, Integer> minEven;

	private Stack<BuchiParityIntersectRule<LETTER, STATE1, STATE2>> goodTransitions;
	private Stack<BuchiParityIntersectState<STATE1, STATE2>> goodTransitionsState;

	public BuchiParityEmptinessCheck(BuchiParityIntersectAutomaton<LETTER, STATE1, STATE2> tree) {
		resultComputed = false;
		this.tree = tree;
		this.goodLeavesStates = new HashSet<>();
		this.goodBuchiStates = new HashSet<>();
		this.goodParityStates = new HashSet<>();
		this.goodStates = new HashSet<>();
		this.goodTransitions = new Stack<>();
		this.goodTransitionsState = new Stack<>();
		computeLeavesStates(tree);
		this.maxOdd = initializeMaxOdd();
		this.minEven = initializeMinEven();

	}

	private Map<BuchiParityIntersectState<STATE1, STATE2>, Integer> initializeMaxOdd() {
		Map<BuchiParityIntersectState<STATE1, STATE2>, Integer> maxOdd = new HashMap<>();
		for (BuchiParityIntersectState<STATE1, STATE2> state : tree.getAltStates()) {
			if (goodParityStates.contains(state))
				maxOdd.put(state, 0);
			else
				maxOdd.put(state, state.getState2().getRank());
		}
		return maxOdd;
	}

	private Map<BuchiParityIntersectState<STATE1, STATE2>, Integer> initializeMinEven() {
		Map<BuchiParityIntersectState<STATE1, STATE2>, Integer> minEven = new HashMap<>();
		for (BuchiParityIntersectState<STATE1, STATE2> state : tree.getAltStates()) {
			if (goodParityStates.contains(state))
				minEven.put(state, state.getState2().getRank());
			else
				minEven.put(state, 0);
		}
		return minEven;
	}

	private void computeLeavesStates(BuchiParityIntersectAutomaton<LETTER, STATE1, STATE2> tree) {
		for (BuchiParityIntersectState<STATE1, STATE2> state : tree.getStates()) {
			if (tree.isEven(state)) {
				this.goodParityStates.add(state.copy(false, false));
				this.goodLeavesStates.add(state);
			}
			if (tree.isFinal(state)) {
				this.goodBuchiStates.add(state.copy(false, false));
				this.goodLeavesStates.add(state);
			}
		}
		return;
	}

	private void updateMaxOdd(BuchiParityIntersectState<STATE1, STATE2> state, int oddValue) {
		if (state.isGood1() && maxOdd.get(state.copy(true, false)) > oddValue) {
			maxOdd.put(state.copy(true, false), oddValue);
			if (oddValue <= minEven.get(state.copy(true, false))) {
				maxOdd.put(state.copy(true, false), 0);
			}
		}
		if (state.isGood2() && maxOdd.get(state.copy(false, true)) > oddValue) {
			maxOdd.put(state.copy(false, true), oddValue);
			if (oddValue <= minEven.get(state.copy(false, true))) {
				maxOdd.put(state.copy(false, true), 0);
			}
		}
		if (state.isGood1() && state.isGood2() && maxOdd.get(state.copy(true, true)) > oddValue) {
			maxOdd.put(state.copy(true, true), oddValue);
			if (oddValue <= minEven.get(state.copy(true, true))) {
				maxOdd.put(state.copy(true, true), 0);
			}
		}
		if (maxOdd.get(state.copy(false, false)) > oddValue) {
			maxOdd.put(state.copy(false, false), oddValue);
			if (oddValue <= minEven.get(state.copy(false, false))) {
				maxOdd.put(state.copy(false, false), 0);
			}
		}
		Collection<BuchiParityIntersectRule<LETTER, STATE1, STATE2>> ruleToSrc = tree.getForChildMap(state);
		if (ruleToSrc != null) {
			for (BuchiParityIntersectRule<LETTER, STATE1, STATE2> rule : ruleToSrc) {
				boolean isGoodTransitionParity = true;
				boolean isGoodTransitionBuchi = true;
				boolean isGoodTransitionBuchiParity = true;
				int maxOddValueBuchi = rule.getSource().getState2().getRank();
				int maxOddValueParity = rule.getSource().getState2().getRank();
				int maxOddValueBuchiParity = rule.getSource().getState2().getRank();
				int maxOddValueNone = rule.getSource().getState2().getRank();
				for (BuchiParityIntersectState<STATE1, STATE2> dest : rule.getDests()) {
					boolean isGoodTransitionParityp = false;
					boolean isGoodTransitionBuchip = false;
					boolean isGoodTransitionBuchiParityp = false;
					int maxOddValueBuchip = Integer.MAX_VALUE;
					int maxOddValueParityp = Integer.MAX_VALUE;
					int maxOddValueBuchiParityp = Integer.MAX_VALUE;
					int maxOddValueNonep = Integer.MAX_VALUE;

					if (goodStates.contains(dest.copy(false, true))) {
						isGoodTransitionParityp = true;
						maxOddValueParityp = Math.min(maxOddValueParityp, maxOdd.get(dest.copy(false, true)));
						maxOddValueNonep = Math.min(maxOddValueNonep, maxOdd.get(dest.copy(false, true)));
					}
					if (goodStates.contains(dest.copy(true, false))) {
						isGoodTransitionBuchip = true;
						maxOddValueBuchip = Math.min(maxOddValueBuchip, maxOdd.get(dest.copy(true, false)));
						maxOddValueNonep = Math.min(maxOddValueNonep, maxOdd.get(dest.copy(true, false)));
					}
					if (goodParityStates.contains(dest.copy(false, false))) {
						isGoodTransitionParityp = true;
						maxOddValueParityp = Math.min(maxOddValueParityp, maxOdd.get(dest.copy(false, false)));
					}
					if (goodBuchiStates.contains(dest.copy(false, false))) {
						isGoodTransitionBuchip = true;
						maxOddValueBuchip = Math.min(maxOddValueBuchip, maxOdd.get(dest.copy(false, false)));
					}
					if (goodBuchiStates.contains(dest.copy(false, false))
							&& goodParityStates.contains(dest.copy(false, false))) {
						isGoodTransitionBuchiParityp = true;
						maxOddValueBuchiParityp = Math.min(maxOddValueBuchiParityp,
								maxOdd.get(dest.copy(false, false)));
					}
					if (goodBuchiStates.contains(dest.copy(false, false))
							&& goodStates.contains(state.copy(false, true))) {
						isGoodTransitionBuchiParityp = true;
						maxOddValueBuchiParityp = Math.min(maxOddValueBuchiParityp,
								Math.max(maxOdd.get(dest.copy(false, true)), maxOdd.get(dest.copy(false, false))));
					}
					if (goodParityStates.contains(dest.copy(false, false))
							&& goodStates.contains(state.copy(true, false))) {
						isGoodTransitionBuchiParityp = true;
						maxOddValueBuchiParityp = Math.min(maxOddValueBuchiParityp,
								Math.max(maxOdd.get(dest.copy(true, false)), maxOdd.get(dest.copy(false, false))));
					}
					if (goodStates.contains(state.copy(true, true))) {
						isGoodTransitionBuchiParityp = true;
						maxOddValueBuchiParityp = Math.min(maxOddValueBuchiParityp, maxOdd.get(dest.copy(true, true)));
						maxOddValueNonep = Math.min(maxOddValueNonep, maxOdd.get(dest.copy(true, true)));

					}
					maxOddValueNonep = Math.min(maxOddValueNonep, maxOdd.get(dest.copy(false, false)));
					maxOddValueBuchi = Math.max(maxOddValueBuchi, maxOddValueBuchip);
					maxOddValueParity = Math.max(maxOddValueParity, maxOddValueParityp);
					maxOddValueBuchiParity = Math.max(maxOddValueBuchiParity, maxOddValueBuchiParityp);
					maxOddValueNone = Math.max(maxOddValueNone, maxOddValueNonep);
					if (!isGoodTransitionParityp)
						isGoodTransitionParity = false;
					if (!isGoodTransitionBuchip)
						isGoodTransitionBuchi = false;
					if (!isGoodTransitionBuchiParityp)
						isGoodTransitionBuchiParity = false;
				}
				if (isGoodTransitionBuchi) {
					if (goodStates.contains(rule.getSource().copy(true, false))) {
						if (maxOdd.get(rule.getSource().copy(true, false)) > maxOddValueBuchi) {
							updateMaxOdd(rule.getSource().copy(true, false), maxOddValueBuchi);
						}
					} else {
						goodTransitions.add(rule);
						goodTransitionsState.add(rule.getSource().copy(true, false));
					}
				}
				if (isGoodTransitionParity && (!goodParityStates.contains(rule.getSource().copy(false, false))
						|| rule.getSource().getState2().getRank() >= maxOddValueParity)) {
					if (goodStates.contains(rule.getSource().copy(false, true))) {
						if (maxOdd.get(rule.getSource().copy(false, true)) > maxOddValueParity) {
							updateMaxOdd(rule.getSource().copy(false, true), maxOddValueParity);
						}
					} else {
						goodTransitions.add(rule);
						goodTransitionsState.add(rule.getSource().copy(false, true));
					}
				}
				if (isGoodTransitionBuchiParity && (!goodParityStates.contains(rule.getSource().copy(false, false))
						|| rule.getSource().getState2().getRank() >= maxOddValueParity)) {
					if (goodStates.contains(rule.getSource().copy(true, true))) {
						if (maxOdd.get(rule.getSource().copy(true, true)) > maxOddValueBuchiParity) {
							updateMaxOdd(rule.getSource().copy(true, true), maxOddValueBuchiParity);
						}
					} else {
						goodTransitions.add(rule);
						goodTransitionsState.add(rule.getSource().copy(true, true));
					}
				}

				if (goodStates.contains(rule.getSource().copy(false, false))) {
					if (maxOdd.get(rule.getSource().copy(false, false)) > maxOddValueNone) {
						updateMaxOdd(rule.getSource().copy(false, false), maxOddValueNone);
					}
				} else {
					goodTransitions.add(rule);
					goodTransitionsState.add(state.copy(false, false));
				}
			}
		}
	}

	private void updateMinEven(BuchiParityIntersectState<STATE1, STATE2> state, int evenValue) {
		if (state.isGood1() && minEven.get(state.copy(true, false)) < evenValue) {
			minEven.put(state.copy(true, false), evenValue);
			if (maxOdd.get(state.copy(true, false)) <= minEven.get(state.copy(true, false))) {
				updateMaxOdd(state.copy(true, false), 0);
			}
		}
		if (state.isGood2() && minEven.get(state.copy(false, true)) < evenValue) {
			minEven.put(state.copy(false, true), evenValue);
			if (maxOdd.get(state.copy(false, true)) <= minEven.get(state.copy(false, true))) {
				updateMaxOdd(state.copy(false, true), 0);
			}
		}
		if (state.isGood1() && state.isGood2() && minEven.get(state.copy(true, true)) < evenValue) {
			minEven.put(state.copy(true, true), evenValue);
			if (maxOdd.get(state.copy(true, true)) <= minEven.get(state.copy(true, true))) {
				updateMaxOdd(state.copy(true, true), 0);
			}
		}
		if (minEven.get(state.copy(false, false)) < evenValue) {
			minEven.put(state.copy(false, false), evenValue);
			if (maxOdd.get(state.copy(false, false)) <= minEven.get(state.copy(false, false))) {
				updateMaxOdd(state.copy(false, false), 0);
			}
		}
		Collection<BuchiParityIntersectRule<LETTER, STATE1, STATE2>> ruleToSrc = tree.getForChildMap(state);
		if (ruleToSrc != null) {
			for (BuchiParityIntersectRule<LETTER, STATE1, STATE2> rule : ruleToSrc) {
				boolean isGoodTransitionParity = true;
				boolean isGoodTransitionBuchi = true;
				boolean isGoodTransitionBuchiParity = true;
				int maxOddValueBuchi = rule.getSource().getState2().getRank();
				int maxOddValueParity = rule.getSource().getState2().getRank();
				int maxOddValueBuchiParity = rule.getSource().getState2().getRank();
				int maxOddValueNone = rule.getSource().getState2().getRank();
				int minEvenValueBuchi = Integer.MAX_VALUE;
				int minEvenValueParity = Integer.MAX_VALUE;
				int minEvenValueBuchiParity = Integer.MAX_VALUE;
				int minEvenValueNone = Integer.MAX_VALUE;
				for (BuchiParityIntersectState<STATE1, STATE2> dest : rule.getDests()) {
					boolean isGoodTransitionParityp = false;
					boolean isGoodTransitionBuchip = false;
					boolean isGoodTransitionBuchiParityp = false;
					int maxOddValueBuchip = Integer.MAX_VALUE;
					int maxOddValueParityp = Integer.MAX_VALUE;
					int maxOddValueBuchiParityp = Integer.MAX_VALUE;
					int maxOddValueNonep = Integer.MAX_VALUE;
					int minEvenValueBuchip = 0;
					int minEvenValueParityp = 0;
					int minEvenValueBuchiParityp = 0;
					int minEvenValueNonep = 0;

					if (goodStates.contains(dest.copy(false, true))) {
						isGoodTransitionParityp = true;
						maxOddValueParityp = Math.min(maxOddValueParityp, maxOdd.get(dest.copy(false, true)));
						maxOddValueNonep = Math.min(maxOddValueNonep, maxOdd.get(dest.copy(false, true)));
						minEvenValueParityp = Math.max(minEvenValueParityp, minEven.get(dest.copy(false, true)));
						minEvenValueNonep = Math.max(minEvenValueNonep, minEven.get(dest.copy(false, true)));
					}
					if (goodStates.contains(dest.copy(true, false))) {
						isGoodTransitionBuchip = true;
						maxOddValueBuchip = Math.min(maxOddValueBuchip, maxOdd.get(dest.copy(true, false)));
						maxOddValueNonep = Math.min(maxOddValueNonep, maxOdd.get(dest.copy(true, false)));
						minEvenValueBuchip = Math.max(minEvenValueBuchip, minEven.get(dest.copy(true, false)));
						minEvenValueNonep = Math.max(minEvenValueNonep, minEven.get(dest.copy(true, false)));
					}
					if (goodParityStates.contains(dest.copy(false, false))) {
						isGoodTransitionParityp = true;
						maxOddValueParityp = Math.min(maxOddValueParityp, maxOdd.get(dest.copy(false, false)));
						minEvenValueParityp = Math.max(minEvenValueParityp, minEven.get(dest.copy(false, false)));
					}
					if (goodBuchiStates.contains(dest.copy(false, false))) {
						isGoodTransitionBuchip = true;
						maxOddValueBuchip = Math.min(maxOddValueBuchip, maxOdd.get(dest.copy(false, false)));
						minEvenValueBuchip = Math.max(minEvenValueBuchip, minEven.get(dest.copy(false, false)));
					}
					if (goodBuchiStates.contains(dest.copy(false, false))
							&& goodParityStates.contains(dest.copy(false, false))) {
						isGoodTransitionBuchiParityp = true;
						maxOddValueBuchiParityp = Math.min(maxOddValueBuchiParityp,
								maxOdd.get(dest.copy(false, false)));
						minEvenValueBuchiParityp = Math.max(minEvenValueBuchiParityp,
								minEven.get(dest.copy(false, false)));
					}
					if (goodBuchiStates.contains(dest.copy(false, false))
							&& goodStates.contains(state.copy(false, true))) {
						isGoodTransitionBuchiParityp = true;
						maxOddValueBuchiParityp = Math.min(maxOddValueBuchiParityp,
								Math.max(maxOdd.get(dest.copy(false, true)), maxOdd.get(dest.copy(false, false))));
						minEvenValueBuchiParityp = Math.max(minEvenValueBuchiParityp,
								Math.max(minEven.get(dest.copy(false, true)), minEven.get(dest.copy(false, false))));

					}
					if (goodParityStates.contains(dest.copy(false, false))
							&& goodStates.contains(state.copy(true, false))) {
						isGoodTransitionBuchiParityp = true;
						maxOddValueBuchiParityp = Math.min(maxOddValueBuchiParityp,
								Math.max(maxOdd.get(dest.copy(true, false)), maxOdd.get(dest.copy(false, false))));
						minEvenValueBuchiParityp = Math.max(minEvenValueBuchiParityp,
								Math.max(minEven.get(dest.copy(true, false)), minEven.get(dest.copy(false, false))));
					}
					if (goodStates.contains(state.copy(true, true))) {
						isGoodTransitionBuchiParityp = true;
						maxOddValueBuchiParityp = Math.min(maxOddValueBuchiParityp, maxOdd.get(dest.copy(true, true)));
						maxOddValueNonep = Math.min(maxOddValueNonep, maxOdd.get(dest.copy(true, true)));
						minEvenValueBuchiParityp = Math.max(minEvenValueBuchiParityp,
								minEven.get(dest.copy(true, true)));
						minEvenValueNonep = Math.max(minEvenValueNonep, minEven.get(dest.copy(true, true)));
					}
					maxOddValueNonep = Math.min(maxOddValueNonep, maxOdd.get(dest.copy(false, false)));
					maxOddValueBuchi = Math.max(maxOddValueBuchi, maxOddValueBuchip);
					maxOddValueParity = Math.max(maxOddValueParity, maxOddValueParityp);
					maxOddValueBuchiParity = Math.max(maxOddValueBuchiParity, maxOddValueBuchiParityp);
					maxOddValueNone = Math.max(maxOddValueNone, maxOddValueNonep);

					minEvenValueNonep = Math.max(maxOddValueNonep, minEven.get(dest.copy(false, false)));
					minEvenValueBuchi = Math.min(minEvenValueBuchi, minEvenValueBuchip);
					minEvenValueParity = Math.min(minEvenValueParity, minEvenValueParityp);
					minEvenValueBuchiParity = Math.min(minEvenValueBuchiParity, minEvenValueBuchiParityp);
					minEvenValueNone = Math.min(minEvenValueNone, maxOddValueNonep);

					if (!isGoodTransitionParityp)
						isGoodTransitionParity = false;
					if (!isGoodTransitionBuchip)
						isGoodTransitionBuchi = false;
					if (!isGoodTransitionBuchiParityp)
						isGoodTransitionBuchiParity = false;
				}
				if (isGoodTransitionBuchi) {
					if (goodStates.contains(rule.getSource().copy(true, false))) {
						if (minEven.get(rule.getSource().copy(true, false)) < minEvenValueBuchi) {
							updateMinEven(rule.getSource().copy(true, false), minEvenValueBuchi);
						}
					} else {
						goodTransitions.add(rule);
						goodTransitionsState.add(rule.getSource().copy(true, false));
					}
				}
				if (isGoodTransitionParity && (!goodParityStates.contains(rule.getSource().copy(false, false))
						|| rule.getSource().getState2().getRank() >= maxOddValueParity)) {
					if (goodStates.contains(rule.getSource().copy(false, true))) {
						if (minEven.get(rule.getSource().copy(false, true)) < minEvenValueParity) {
							updateMinEven(rule.getSource().copy(false, true), minEvenValueParity);
						}
					} else {
						goodTransitions.add(rule);
						goodTransitionsState.add(rule.getSource().copy(false, true));
					}
				}
				if (isGoodTransitionBuchiParity && (!goodParityStates.contains(rule.getSource().copy(false, false))
						|| rule.getSource().getState2().getRank() >= maxOddValueParity)) {
					if (goodStates.contains(rule.getSource().copy(true, true))) {
						if (minEven.get(rule.getSource().copy(true, true)) < minEvenValueBuchiParity) {
							updateMinEven(rule.getSource().copy(true, true), minEvenValueBuchiParity);
						}
					} else {
						goodTransitions.add(rule);
						goodTransitionsState.add(rule.getSource().copy(true, true));
					}
				}

				if (goodStates.contains(rule.getSource().copy(false, false))) {
					if (maxOdd.get(rule.getSource().copy(false, false)) < minEvenValueNone) {
						updateMinEven(rule.getSource().copy(false, false), minEvenValueNone);
					}
				} else {
					goodTransitions.add(rule);
					goodTransitionsState.add(rule.getSource().copy(false, false));
				}
			}
		}
	}

	private void initializeGoodTransitions() {
		Set<BuchiParityIntersectRule<LETTER, STATE1, STATE2>> allRules = tree.getRules();
		for (BuchiParityIntersectRule<LETTER, STATE1, STATE2> rule : allRules) {
			List<BuchiParityIntersectState<STATE1, STATE2>> dests = rule.getDests();
			boolean isGoodTransitionBuchi = true;
			boolean isGoodTransitionParity = true;
			for (BuchiParityIntersectState<STATE1, STATE2> dest : dests) {
				if (!goodBuchiStates.contains(dest.copy(false, false))) {
					isGoodTransitionBuchi = false;
				}
				if (!goodParityStates.contains(dest.copy(false, false))) {
					isGoodTransitionParity = false;
				}
			}
			if (isGoodTransitionBuchi) {
				goodTransitions.add(rule);
				goodTransitionsState.add(rule.getSource().copy(true, false));
			}
			if (isGoodTransitionParity) {
				goodTransitions.add(rule);
				goodTransitionsState.add(rule.getSource().copy(false, true));
			}
			if (isGoodTransitionBuchi && isGoodTransitionParity) {
				goodTransitions.add(rule);
				goodTransitionsState.add(rule.getSource().copy(true, true));
			}
		}
	}

	private boolean removeLeavesNotGoodStates() {
		boolean stateRemoved = false;

		Iterator<BuchiParityIntersectState<STATE1, STATE2>> iterStates = goodLeavesStates.iterator();
		while (iterStates.hasNext()) {
			BuchiParityIntersectState<STATE1, STATE2> nextState = iterStates.next();
			if (!goodStates.contains(nextState)) {
				iterStates.remove();
				if (goodParityStates.contains(nextState.copy(false, false))) {
					goodParityStates.remove(nextState.copy(false, false));
				}
				if (goodBuchiStates.contains(nextState.copy(false, false))) {
					goodBuchiStates.remove(nextState.copy(false, false));
				}
				stateRemoved = true;
			}
		}
		return stateRemoved;
	}

	private void findAllGoodStates() {
		while (!goodTransitions.isEmpty()) {
			BuchiParityIntersectRule<LETTER, STATE1, STATE2> nextRule = goodTransitions.pop();
			BuchiParityIntersectState<STATE1, STATE2> src = goodTransitionsState.pop();
			if (goodStates.contains(src)) {
				int oddValue = src.getState2().getRank();
				for (BuchiParityIntersectState<STATE1, STATE2> state : nextRule.getDests()) {
					if (maxOdd.get(state) > oddValue) {
						oddValue = maxOdd.get(state);
					}
				}
				if (oddValue < maxOdd.get(src)) {
					updateMaxOdd(src, oddValue);
				}
				int evenValue = Integer.MAX_VALUE;
				for (BuchiParityIntersectState<STATE1, STATE2> state : nextRule.getDests()) {
					if (minEven.get(state) < evenValue) {
						evenValue = minEven.get(state);
					}
				}
				if (evenValue > minEven.get(src)) {
					updateMinEven(src, evenValue);
				}
				continue;
			}
			if (src.isGood1()) {
				goodStates.add(src.copy(true, false));
			}
			if (src.isGood2()) {
				goodStates.add(src.copy(false, true));
			}
			if (src.isGood1() && src.isGood2()) {
				goodStates.add(src.copy(true, true));
			}
			goodStates.add(src.copy(false, false));
			if (goodParityStates.contains(src)) {
				int evenValue = Integer.MAX_VALUE;
				for (BuchiParityIntersectState<STATE1, STATE2> state : nextRule.getDests()) {
					if (minEven.get(state) < evenValue) {
						evenValue = minEven.get(state);
					}
				}
				if (evenValue > minEven.get(src)) {
					updateMinEven(src, evenValue);
				}
				continue;
			}
			if (goodBuchiStates.contains(src)) {
				int oddValue = src.getState2().getRank();
				for (BuchiParityIntersectState<STATE1, STATE2> state : nextRule.getDests()) {
					if (maxOdd.get(state) > oddValue) {
						oddValue = maxOdd.get(state);
					}
				}
				if (oddValue < maxOdd.get(src)) {
					updateMaxOdd(src, oddValue);
				}
				int evenValue = Integer.MAX_VALUE;
				for (BuchiParityIntersectState<STATE1, STATE2> state : nextRule.getDests()) {
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
			for (BuchiParityIntersectState<STATE1, STATE2> state : nextRule.getDests()) {
				if (maxOdd.get(state) > oddValue) {
					oddValue = maxOdd.get(state);
				}
			}
			int evenValue = Integer.MAX_VALUE;
			for (BuchiParityIntersectState<STATE1, STATE2> state : nextRule.getDests()) {
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

	private void computeGoodTree() {
		initializeGoodTransitions();
		findAllGoodStates();
		while (!goodStates.isEmpty()) {
			if (!removeLeavesNotGoodStates()) {
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

	private void initializeDecentTransitions() {
		Set<BuchiParityIntersectRule<LETTER, STATE1, STATE2>> allRules = tree.getRules();
		for (BuchiParityIntersectRule<LETTER, STATE1, STATE2> rule : allRules) {
			List<BuchiParityIntersectState<STATE1, STATE2>> dests = rule.getDests();
			boolean isGoodTransition = true;
			for (BuchiParityIntersectState<STATE1, STATE2> dest : dests) {
				if (!goodLeavesStates.contains(dest)) {
					isGoodTransition = false;
					break;
				}
			}
			if (isGoodTransition)
				goodTransitions.add(rule);
		}
	}

	private void removeInitialNotDecentStates() {
		Iterator<BuchiParityIntersectState<STATE1, STATE2>> iterInitStates = tree.getInitStates().iterator();
		while (iterInitStates.hasNext()) {
			BuchiParityIntersectState<STATE1, STATE2> nextInitState = iterInitStates.next();
			if (!goodStates.contains(nextInitState)) {
				iterInitStates.remove();
			}
		}
	}

	private void findAllDecentStates() {
		while (!goodTransitions.isEmpty()) {
			BuchiParityIntersectRule<LETTER, STATE1, STATE2> nextRule = goodTransitions.pop();
			BuchiParityIntersectState<STATE1, STATE2> src = nextRule.getSource();
			if (!goodStates.contains(src)) {
				goodStates.add(src);
				Collection<BuchiParityIntersectRule<LETTER, STATE1, STATE2>> ruleToSrc = tree.getForChildMap(src);
				if (ruleToSrc != null && !goodLeavesStates.contains(src)) {
					for (BuchiParityIntersectRule<LETTER, STATE1, STATE2> rule : ruleToSrc) {
						boolean isGoodTransition = true;
						for (BuchiParityIntersectState<STATE1, STATE2> dest : rule.getDests()) {
							if (!goodLeavesStates.contains(dest) && !goodStates.contains(dest)) {
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
}
