package cgsynt.tree.buchi.parity.operations;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import cgsynt.tree.buchi.parity.BuchiParityIntersectAutomaton;
import cgsynt.tree.buchi.parity.BuchiParityIntersectRule;
import cgsynt.tree.buchi.parity.BuchiParityIntersectState;
import cgsynt.tree.parity.IParityState;
import cgsynt.tree.parity.ParityTreeAutomaton;
import cgsynt.tree.parity.ParityTreeAutomatonRule;
import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;

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
				this.goodLeavesStates.add(state.copy(false, false));
			}
			if (tree.isFinal(state)) {
				this.goodBuchiStates.add(state.copy(false, false));
				this.goodLeavesStates.add(state.copy(false, false));
			}
		}
		return;
	}

	private void updateMaxOdd(BuchiParityIntersectState<STATE1, STATE2> state, int oddValue) {
		maxOdd.put(state, oddValue);
		if (oddValue <= minEven.get(state)) {
			maxOdd.put(state, 0);
		}
		Collection<BuchiParityIntersectRule<LETTER, STATE1, STATE2>> ruleToSrc = tree.getForChildMap(state);
		if (ruleToSrc != null) {
			for (BuchiParityIntersectRule<LETTER, STATE1, STATE2> rule : ruleToSrc) {
				boolean isGoodTransitionParity = true;
				boolean isGoodTransitionBuchi = true;
				boolean isGoodTransitionBuchiParity = true;
				int maxOddValueBuchi = 0;
				int maxOddValueParity = 0;
				int maxOddValueBuchiParity = 0;
				int maxOddValueNone = 0;
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
						isGoodTransitionParity = false;
				}
				if (isGoodTransitionBuchi) {
					if (goodStates.contains(rule.getSource().copy(true, false))) {
						if (maxOdd.get(rule.getSource().copy(true, false)) > maxOddValueBuchi) {
							updateMaxOdd(rule.getSource().copy(true, false), maxOddValueBuchi);
						}
					} else {
						goodTransitions.add(rule);
						goodTransitionsState.add(state.copy(true, false));
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
						goodTransitionsState.add(state.copy(false, true));
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
						goodTransitionsState.add(state.copy(true, true));
					}
				}

				if (goodStates.contains(rule.getSource().copy(false, false))) {
					if (maxOdd.get(rule.getSource().copy(false, false)) > maxOddValueNone) {
						updateMaxOdd(rule.getSource().copy(true, true), maxOddValueNone);
					}
				} else {
					goodTransitions.add(rule);
					goodTransitionsState.add(state.copy(false, false));
				}
			}
		}
	}

}
