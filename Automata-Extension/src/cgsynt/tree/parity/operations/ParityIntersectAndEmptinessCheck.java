package cgsynt.tree.parity.operations;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import cgsynt.tree.parity.IParityState;
import cgsynt.tree.parity.ParityIntersectRule;
import cgsynt.tree.parity.ParityIntersectState;
import cgsynt.tree.parity.ParityIntersectTree;
import cgsynt.tree.parity.ParityTreeAutomaton;
import cgsynt.tree.parity.ParityTreeAutomatonRule;
import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;

public class ParityIntersectAndEmptinessCheck<LETTER extends IRankedLetter, STATE1 extends IParityState, STATE2 extends IParityState, STATE3 extends IParityState> {

	private boolean result;
	private boolean resultComputed;
	private ParityIntersectTree<LETTER, STATE1, STATE2, STATE3> tree;
	private Set<ParityIntersectState<STATE1, STATE2, STATE3>> goodEvenStates;
	private Set<ParityIntersectState<STATE1, STATE2, STATE3>> goodStates;

	private Map<ParityIntersectState<STATE1, STATE2, STATE3>, Integer> maxOdd1;
	private Map<ParityIntersectState<STATE1, STATE2, STATE3>, Integer> maxOdd2;
	private Map<ParityIntersectState<STATE1, STATE2, STATE3>, Integer> maxOdd3;

	private Map<ParityIntersectState<STATE1, STATE2, STATE3>, Integer> minEven1;
	private Map<ParityIntersectState<STATE1, STATE2, STATE3>, Integer> minEven2;
	private Map<ParityIntersectState<STATE1, STATE2, STATE3>, Integer> minEven3;

	private Stack<ParityIntersectRule<LETTER, STATE1, STATE2, STATE3>> goodTransitions;
	private Stack<ParityIntersectState<STATE1, STATE2, STATE3>> goodTransitionsState;

	public ParityIntersectAndEmptinessCheck(ParityIntersectTree<LETTER, STATE1, STATE2, STATE3> tree) {
		this.tree = tree;
		this.goodEvenStates = computeEvenStates(tree);
		this.goodStates = new HashSet<>();
		this.maxOdd1 = new HashMap<>();
		this.maxOdd2 = new HashMap<>();
		this.maxOdd3 = new HashMap<>();
		this.minEven1 = new HashMap<>();
		this.minEven2 = new HashMap<>();
		this.minEven3 = new HashMap<>();

		this.goodTransitions = new Stack<>();
		this.goodTransitionsState = new Stack<>();
		this.resultComputed = false;
		initializeMaxOdd();
		initializeMinEven();
	}

	private Set<ParityIntersectState<STATE1, STATE2, STATE3>> computeEvenStates(
			ParityIntersectTree<LETTER, STATE1, STATE2, STATE3> tree) {
		Set<ParityIntersectState<STATE1, STATE2, STATE3>> evenStates = new HashSet<>();
		for (ParityIntersectState<STATE1, STATE2, STATE3> state : tree.getStates()) {
			if (state.state1.getRank() % 2 == 0) {
				evenStates.add(state.copy(true, false, false));
			}
			if (state.state2.getRank() % 2 == 0) {
				evenStates.add(state.copy(false, true, false));
			}
			if (state.state3.getRank() % 2 == 0) {
				evenStates.add(state.copy(false, false, true));
			}
			if (state.state1.getRank() % 2 == 0 && state.state2.getRank() % 2 == 0) {
				evenStates.add(state.copy(true, true, false));
			}
			if (state.state2.getRank() % 2 == 0 && state.state3.getRank() % 2 == 0) {
				evenStates.add(state.copy(false, true, true));
			}
			if (state.state1.getRank() % 2 == 0 && state.state3.getRank() % 2 == 0) {
				evenStates.add(state.copy(true, false, true));
			}
			if (state.state1.getRank() % 2 == 0 && state.state2.getRank() % 2 == 0 && state.state3.getRank() % 2 == 0) {
				evenStates.add(state.copy(true, true, true));
			}
		}
		return evenStates;
	}

	private void initializeMaxOdd() {

		for (ParityIntersectState<STATE1, STATE2, STATE3> state : tree.getAltStates()) {
			if (goodEvenStates.contains(state.copy(true, false, false)))
				maxOdd1.put(state, 0);
			else
				maxOdd1.put(state, state.state1.getRank());
			if (goodEvenStates.contains(state.copy(false, true, false)))
				maxOdd2.put(state, 0);
			else
				maxOdd2.put(state, state.state2.getRank());
			if (goodEvenStates.contains(state.copy(false, false, true)))
				maxOdd3.put(state, 0);
			else
				maxOdd3.put(state, state.state3.getRank());

		}

	}

	private void initializeMinEven() {

		for (ParityIntersectState<STATE1, STATE2, STATE3> state : tree.getAltStates()) {
			if (goodEvenStates.contains(state.copy(true, false, false)))
				minEven1.put(state, state.state1.getRank());
			else
				minEven1.put(state, 0);
			if (goodEvenStates.contains(state.copy(false, true, false)))
				minEven2.put(state, state.state2.getRank());
			else
				minEven2.put(state, 0);
			if (goodEvenStates.contains(state.copy(false, false, true)))
				minEven3.put(state, state.state3.getRank());
			else
				minEven3.put(state, 0);

		}

	}

	public void computeResult() {
		if (resultComputed)
			return;
	}

	private void updateMaxOdd(ParityIntersectState<STATE1, STATE2, STATE3> state, int oddValue,
			Map<ParityIntersectState<STATE1, STATE2, STATE3>, Integer> maxOdd) {
		maxOdd.put(state, oddValue);
		Collection<ParityIntersectRule<LETTER, STATE1, STATE2, STATE3>> ruleToSrc = tree.getForChildMap(state);
		if (ruleToSrc != null) {
			for (ParityIntersectRule<LETTER, STATE1, STATE2, STATE3> rule : ruleToSrc) {
				boolean isGoodTransition1 = true;
				boolean isGoodTransition2 = true;
				boolean isGoodTransition3 = true;
				boolean isGoodTransition12 = true;
				boolean isGoodTransition13 = true;
				boolean isGoodTransition23 = true;
				boolean isGoodTransition123 = true;
				int maxOddValue1_1 = 0;
				int maxOddValue1_12 = 0;
				int maxOddValue1_13 = 0;
				int maxOddValue1_123 = 0;
				int maxOddValue2_2 = 0;
				int maxOddValue2_12 = 0;
				int maxOddValue2_23 = 0;
				int maxOddValue2_123 = 0;
				int maxOddValue3_3 = 0;
				int maxOddValue3_13 = 0;
				int maxOddValue3_23 = 0;
				int maxOddValue3_123 = 0;
				for (ParityIntersectState<STATE1, STATE2, STATE3> dest : rule.getDests()) {
					if (!goodStateCheck(dest, true, false, false)
							&& !goodEvenStates.contains(dest.copy(true, false, false))) {
						isGoodTransition1 = false;
					}
					if (!goodStateCheck(dest, false, true, false)
							&& !goodEvenStates.contains(dest.copy(false, true, false))) {
						isGoodTransition2 = false;
					}
					if (!goodStateCheck(dest, false, false, true)
							&& !goodEvenStates.contains(dest.copy(false, false, true))) {
						isGoodTransition3 = false;
					}
					if (!goodStateCheck(dest, true, true, false)
							&& !goodEvenStates.contains(dest.copy(true, true, false))) {
						isGoodTransition12 = false;
					}
					if (!goodStateCheck(dest, true, false, true)
							&& !goodEvenStates.contains(dest.copy(true, false, true))) {
						isGoodTransition13 = false;
					}
					if (!goodStateCheck(dest, false, true, true)
							&& !goodEvenStates.contains(dest.copy(false, true, true))) {
						isGoodTransition23 = false;
					}
					if (!goodStateCheck(dest, true, true, true)
							&& !goodEvenStates.contains(dest.copy(true, true, true))) {
						isGoodTransition123 = false;
					}

					if (!goodStates.contains(dest.copy(true, false, false))
							&& !goodEvenStates.contains(dest.copy(true, false, false))) {
						maxOddValue1_1 = Math.max(maxOddValue1_1, maxOdd1.get(dest.copy(true, false, false)));
					}
					if (!goodStates.contains(dest.copy(false, true, false))
							&& !goodEvenStates.contains(dest.copy(false, true, false))) {
						maxOddValue2_2 = Math.max(maxOddValue2_2, maxOdd2.get(dest.copy(false, true, false)));
					}
					if (!goodStates.contains(dest.copy(false, false, true))
							&& !goodEvenStates.contains(dest.copy(false, false, true))) {
						maxOddValue3_3 = Math.max(maxOddValue1_1, maxOdd3.get(dest.copy(false, false, true)));
					}
					if (!goodStates.contains(dest.copy(true, false, true))
							&& !goodEvenStates.contains(dest.copy(true, false, true))) {
						maxOddValue1_1 = Math.max(maxOddValue1_1, maxOdd1.get(dest.copy(true, false, true)));
						maxOddValue1_13 = Math.max(maxOddValue1_13, maxOdd1.get(dest.copy(true, false, true)));
						maxOddValue3_3 = Math.max(maxOddValue3_3, maxOdd3.get(dest.copy(true, false, true)));
						maxOddValue3_13 = Math.max(maxOddValue3_13, maxOdd3.get(dest.copy(true, false, true)));
					}
					if (!goodStates.contains(dest.copy(true, true, false))
							&& !goodEvenStates.contains(dest.copy(true, true, false))) {
						maxOddValue1_1 = Math.max(maxOddValue1_1, maxOdd1.get(dest.copy(true, true, false)));
						maxOddValue1_12 = Math.max(maxOddValue1_12, maxOdd1.get(dest.copy(true, true, false)));
						maxOddValue2_2 = Math.max(maxOddValue2_2, maxOdd2.get(dest.copy(true, true, false)));
						maxOddValue2_12 = Math.max(maxOddValue2_12, maxOdd2.get(dest.copy(true, true, false)));
					}
					if (!goodStates.contains(dest.copy(false, true, true))
							&& !goodEvenStates.contains(dest.copy(false, true, true))) {
						maxOddValue2_2 = Math.max(maxOddValue2_2, maxOdd2.get(dest.copy(false, true, true)));
						maxOddValue2_23 = Math.max(maxOddValue2_23, maxOdd2.get(dest.copy(false, true, true)));
						maxOddValue3_3 = Math.max(maxOddValue3_3, maxOdd3.get(dest.copy(false, true, true)));
						maxOddValue3_23 = Math.max(maxOddValue3_23, maxOdd3.get(dest.copy(false, true, true)));
					}
					if (!goodStates.contains(dest.copy(true, true, true))
							&& !goodEvenStates.contains(dest.copy(true, true, true))) {
						maxOddValue1_1 = Math.max(maxOddValue1_1, maxOdd1.get(dest.copy(true, true, true)));
						maxOddValue1_12 = Math.max(maxOddValue1_12, maxOdd1.get(dest.copy(true, true, true)));
						maxOddValue1_13 = Math.max(maxOddValue1_13, maxOdd1.get(dest.copy(true, true, true)));
						maxOddValue1_123 = Math.max(maxOddValue1_123, maxOdd1.get(dest.copy(true, true, true)));

						maxOddValue2_2 = Math.max(maxOddValue2_2, maxOdd2.get(dest.copy(true, true, true)));
						maxOddValue2_12 = Math.max(maxOddValue2_12, maxOdd2.get(dest.copy(true, true, true)));
						maxOddValue2_23 = Math.max(maxOddValue2_23, maxOdd2.get(dest.copy(true, true, true)));
						maxOddValue2_123 = Math.max(maxOddValue2_123, maxOdd2.get(dest.copy(true, true, true)));

						maxOddValue3_3 = Math.max(maxOddValue3_3, maxOdd3.get(dest.copy(true, true, true)));
						maxOddValue3_13 = Math.max(maxOddValue3_13, maxOdd3.get(dest.copy(true, true, true)));
						maxOddValue3_23 = Math.max(maxOddValue3_23, maxOdd3.get(dest.copy(true, true, true)));
						maxOddValue3_123 = Math.max(maxOddValue3_123, maxOdd3.get(dest.copy(true, true, true)));
					}

				}
				

			}
		}
	}

	private void findAllGoodStates() {
		while (!goodTransitions.isEmpty()) {
			ParityIntersectRule<LETTER, STATE1, STATE2, STATE3> nextRule = goodTransitions.pop();
			ParityIntersectState<STATE1, STATE2, STATE3> src = goodTransitionsState.pop();

		}
	}

	private void computeGoodTree() {

	}

	private void initializeGoodTransitions() {
		Set<ParityIntersectRule<LETTER, STATE1, STATE2, STATE3>> allRules = tree.getRules();
		for (ParityIntersectRule<LETTER, STATE1, STATE2, STATE3> rule : allRules) {
			List<ParityIntersectState<STATE1, STATE2, STATE3>> dests = rule.getDests();
			ParityIntersectState<STATE1, STATE2, STATE3> src = rule.getSource();
			boolean isGoodTransition1 = true;
			boolean isGoodTransition2 = true;
			boolean isGoodTransition3 = true;
			boolean isGoodTransition12 = true;
			boolean isGoodTransition13 = true;
			boolean isGoodTransition23 = true;
			boolean isGoodTransition123 = true;
			for (ParityIntersectState<STATE1, STATE2, STATE3> dest : dests) {
				if (dest.state1.getRank() % 2 != 0) {
					isGoodTransition1 = false;
					isGoodTransition12 = false;
					isGoodTransition13 = false;
					isGoodTransition123 = false;
				}
				if (dest.state2.getRank() % 2 != 0) {
					isGoodTransition2 = false;
					isGoodTransition12 = false;
					isGoodTransition23 = false;
					isGoodTransition123 = false;
				}
				if (dest.state3.getRank() % 2 != 0) {
					isGoodTransition3 = false;
					isGoodTransition13 = false;
					isGoodTransition23 = false;
					isGoodTransition123 = false;
				}
			}
			if (isGoodTransition1) {
				goodTransitions.add(rule);
				goodTransitionsState.add(src.copy(true, false, false));
			}
			if (isGoodTransition2) {
				goodTransitions.add(rule);
				goodTransitionsState.add(src.copy(false, true, false));
			}
			if (isGoodTransition3) {
				goodTransitions.add(rule);
				goodTransitionsState.add(src.copy(false, false, true));
			}
			if (isGoodTransition12) {
				goodTransitions.add(rule);
				goodTransitionsState.add(src.copy(true, true, false));
			}
			if (isGoodTransition13) {
				goodTransitions.add(rule);
				goodTransitionsState.add(src.copy(true, false, true));
			}
			if (isGoodTransition23) {
				goodTransitions.add(rule);
				goodTransitionsState.add(src.copy(false, true, true));
			}
			if (isGoodTransition123) {
				goodTransitions.add(rule);
				goodTransitionsState.add(src.copy(true, true, true));
			}
		}
	}

	private boolean goodStateCheck(ParityIntersectState<STATE1, STATE2, STATE3> state, boolean isGood1, boolean isGood2,
			boolean isGood3) {
		if (isGood1 && !isGood2 && !isGood3) {
			return goodStates.contains(state.copy(isGood1, !isGood2, !isGood3))
					|| goodStates.contains(state.copy(isGood1, !isGood2, isGood3))
					|| goodStates.contains(state.copy(isGood1, isGood2, !isGood3))
					|| goodStates.contains(state.copy(isGood1, isGood2, isGood3));
		}
		if (!isGood1 && isGood2 && !isGood3) {
			return goodStates.contains(state.copy(!isGood1, isGood2, !isGood3))
					|| goodStates.contains(state.copy(!isGood1, isGood2, isGood3))
					|| goodStates.contains(state.copy(isGood1, isGood2, !isGood3))
					|| goodStates.contains(state.copy(isGood1, isGood2, isGood3));
		}
		if (!isGood1 && !isGood2 && isGood3) {
			return goodStates.contains(state.copy(!isGood1, !isGood2, isGood3))
					|| goodStates.contains(state.copy(!isGood1, isGood2, isGood3))
					|| goodStates.contains(state.copy(isGood1, !isGood2, isGood3))
					|| goodStates.contains(state.copy(isGood1, isGood2, isGood3));
		}
		if (isGood1 && isGood2 && !isGood3) {
			return goodStates.contains(state.copy(isGood1, isGood2, !isGood3))
					|| goodStates.contains(state.copy(isGood1, isGood2, isGood3));
		}
		if (isGood1 && !isGood2 && isGood3) {
			return goodStates.contains(state.copy(isGood1, !isGood2, isGood3))
					|| goodStates.contains(state.copy(isGood1, isGood2, isGood3));
		}
		if (!isGood1 && isGood2 && isGood3) {
			return goodStates.contains(state.copy(!isGood1, isGood2, isGood3))
					|| goodStates.contains(state.copy(isGood1, isGood2, isGood3));
		}
		if (isGood1 && isGood2 && isGood3) {
			return goodStates.contains(state.copy(isGood1, isGood2, isGood3));
		}
		return false;
	}

}
