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
	private final Set<ParityIntersectState<STATE1, STATE2, STATE3>> mAltStates;
	private final Set<ParityIntersectState<STATE1, STATE2, STATE3>> mInitStates;

	public ParityIntersectTree(ParityTreeAutomaton<LETTER, STATE1> tree1, ParityTreeAutomaton<LETTER, STATE2> tree2,
			ParityTreeAutomaton<LETTER, STATE3> tree3) {
		this.mSourceMap = new HashMap<>();
		this.mRules = new HashSet<>();
		this.mChildMap = new HashMap<>();
		this.mStates = new HashSet<>();
		this.mInitStates = new HashSet<>();
		this.mAltStates = new HashSet<>();
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
					ParityIntersectState<STATE1, STATE2, STATE3> intersectState = new ParityIntersectState<>(state1,
							state2, state3);
					addState(intersectState);
					addAltState(intersectState.copy(false, false, false));
					addAltState(intersectState.copy(false, false, true));
					addAltState(intersectState.copy(false, true, false));
					addAltState(intersectState.copy(false, true, true));
					addAltState(intersectState.copy(true, false, false));
					addAltState(intersectState.copy(true, false, true));
					addAltState(intersectState.copy(true, true, false));
				}
			}
		}
	}

	private void initializeInitStates(Set<STATE1> init1, Set<STATE2> init2, Set<STATE3> init3) {
		for (STATE1 state1 : init1) {
			for (STATE2 state2 : init2) {
				for (STATE3 state3 : init3) {
					ParityIntersectState<STATE1, STATE2, STATE3> intersectState = new ParityIntersectState<>(state1,
							state2, state3);
					mInitStates.add(intersectState);
				}
			}
		}
	}

	private void addState(ParityIntersectState<STATE1, STATE2, STATE3> state) {
		if (mStates.contains(state))
			return;
		mStates.add(state);
		mAltStates.add(state);
		mSourceMap.put(state, new HashSet<>());
		mChildMap.put(state, new HashSet<>());
	}

	private void addAltState(ParityIntersectState<STATE1, STATE2, STATE3> state) {
		if (mStates.contains(state))
			return;
		mAltStates.add(state);
		mSourceMap.put(state, new HashSet<>());
		mChildMap.put(state, new HashSet<>());
	}

	public Set<ParityIntersectState<STATE1, STATE2, STATE3>> getStates() {
		return mStates;
	}

	public Set<ParityIntersectState<STATE1, STATE2, STATE3>> getAltStates() {
		return mAltStates;
	}

	public Set<ParityIntersectRule<LETTER, STATE1, STATE2, STATE3>> getRules() {
		return mRules;
	}

}
