package cgsynt.tree.parity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;

public class ParityIntersectTree<LETTER extends IRankedLetter, STATE1 extends IParityState, STATE2 extends IParityState, STATE3 extends IParityState> {
	private final Map<ParityIntersectState<STATE1, STATE2, STATE3>, Collection<ParityIntersectRule<LETTER, STATE1, STATE2, STATE3>>> mSourceMap;
	private final Set<ParityIntersectRule<LETTER, STATE1, STATE2, STATE3>> mRules;
	private final Map<ParityIntersectState<STATE1, STATE2, STATE3>, Collection<ParityIntersectRule<LETTER, STATE1, STATE2, STATE3>>> mChildMap;
	private final Set<ParityIntersectState<STATE1, STATE2, STATE3>> mStates;
	private final Set<ParityIntersectState<STATE1, STATE2, STATE3>> mInitStates;

	public ParityIntersectTree(ParityTreeAutomaton<LETTER, STATE1> tree1, ParityTreeAutomaton<LETTER, STATE2> tree2,
			ParityTreeAutomaton<LETTER, STATE3> tree3) {
		this.mSourceMap = new HashMap<>();
		this.mRules = new HashSet<>();
		this.mChildMap = new HashMap<>();
		this.mStates = new HashSet<>();
		this.mInitStates = new HashSet<>();
		initializeStates(tree1.getStates(), tree2.getStates(), tree3.getStates());
		initializeInitStates(tree1.getInitStates(), tree2.getInitStates(), tree3.getInitStates());
		initializeRules(tree1, tree2, tree3);
	}

	private void initializeRules(ParityTreeAutomaton<LETTER, STATE1> tree1, ParityTreeAutomaton<LETTER, STATE2> tree2,
			ParityTreeAutomaton<LETTER, STATE3> tree3) {
		for (LETTER letter : tree1.getAlphabet()) {
			if (tree1.getSuccessors(letter) == null || tree2.getSuccessors(letter) == null
					|| tree3.getSuccessors(letter) == null) {
				continue;
			}

			for (ParityTreeAutomatonRule<LETTER, STATE1> rule1 : tree1.getSuccessors(letter)) {
				for (ParityTreeAutomatonRule<LETTER, STATE2> rule2 : tree2.getSuccessors(letter)) {
					for (ParityTreeAutomatonRule<LETTER, STATE3> rule3 : tree3.getSuccessors(letter)) {
						addRule(rule1, rule2, rule3);
					}
				}
			}

		}
	}

	private void addRule(ParityTreeAutomatonRule<LETTER, STATE1> rule1, ParityTreeAutomatonRule<LETTER, STATE2> rule2,
			ParityTreeAutomatonRule<LETTER, STATE3> rule3) {
		STATE1 src1 = rule1.getSource();
		STATE2 src2 = rule2.getSource();
		STATE3 src3 = rule3.getSource();
		ParityIntersectState<STATE1, STATE2, STATE3> intersectSrc = new ParityIntersectState<>(src1, src2, src3);
		ParityIntersectRule<LETTER, STATE1, STATE2, STATE3> intersectRule = new ParityIntersectRule<>(rule1, rule2,
				rule3);
		mSourceMap.get(intersectSrc).add(intersectRule);
		mRules.add(intersectRule);
		for (int i = 0; i < rule1.getDest().size(); i++) {
			ParityIntersectState<STATE1, STATE2, STATE3> intersectDest = new ParityIntersectState<>(
					rule1.getDest().get(i), rule2.getDest().get(i), rule3.getDest().get(i));
			mChildMap.get(intersectDest).add(intersectRule);

		}

	}

	public Collection<ParityIntersectRule<LETTER, STATE1, STATE2, STATE3>> getForSourceMap(
			ParityIntersectState<STATE1, STATE2, STATE3> intersectState) {
		return mSourceMap.get(intersectState.getGoodIntersectState());
	}

	public Collection<ParityIntersectRule<LETTER, STATE1, STATE2, STATE3>> getForChildMap(
			ParityIntersectState<STATE1, STATE2, STATE3> intersectState) {
		return mChildMap.get(intersectState.getGoodIntersectState());
	}

	private void initializeStates(Set<STATE1> states1, Set<STATE2> states2, Set<STATE3> states3) {
		for (STATE1 state1 : states1) {
			for (STATE2 state2 : states2) {
				for (STATE3 state3 : states3) {
					ParityIntersectState<STATE1, STATE2, STATE3> intersectState1 = new ParityIntersectState<>(state1,
							state2, state3, false, false, false);
					ParityIntersectState<STATE1, STATE2, STATE3> intersectState2 = new ParityIntersectState<>(state1,
							state2, state3, false, false, true);
					ParityIntersectState<STATE1, STATE2, STATE3> intersectState3 = new ParityIntersectState<>(state1,
							state2, state3, false, true, false);
					ParityIntersectState<STATE1, STATE2, STATE3> intersectState4 = new ParityIntersectState<>(state1,
							state2, state3, false, true, true);
					ParityIntersectState<STATE1, STATE2, STATE3> intersectState5 = new ParityIntersectState<>(state1,
							state2, state3, true, false, false);
					ParityIntersectState<STATE1, STATE2, STATE3> intersectState6 = new ParityIntersectState<>(state1,
							state2, state3, true, false, true);
					ParityIntersectState<STATE1, STATE2, STATE3> intersectState7 = new ParityIntersectState<>(state1,
							state2, state3, true, true, false);
					ParityIntersectState<STATE1, STATE2, STATE3> intersectState8 = new ParityIntersectState<>(state1,
							state2, state3, true, true, true);
					List<ParityIntersectState<STATE1, STATE2, STATE3>> states = new ArrayList<>();
					states.add(intersectState1);
					states.add(intersectState2);
					states.add(intersectState3);
					states.add(intersectState4);
					states.add(intersectState5);
					states.add(intersectState6);
					states.add(intersectState7);
					states.add(intersectState8);
					for (int i = 0; i < 8; i++) {
						addState(states.get(i));
						mSourceMap.put(states.get(i), new HashSet<>());
						mChildMap.put(states.get(i), new HashSet<>());
					}
				}
			}
		}
	}

	private void initializeInitStates(Set<STATE1> init1, Set<STATE2> init2, Set<STATE3> init3) {
		for (STATE1 state1 : init1) {
			for (STATE2 state2 : init2) {
				for (STATE3 state3 : init3) {
					ParityIntersectState<STATE1, STATE2, STATE3> intersectState = new ParityIntersectState<>(state1,
							state2, state3, false, false, false);
					mInitStates.add(intersectState);
				}
			}
		}
	}

	private void addState(ParityIntersectState<STATE1, STATE2, STATE3> state) {
		if (mStates.contains(state))
			mStates.add(state);
	}

}
