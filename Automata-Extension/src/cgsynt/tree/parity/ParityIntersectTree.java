package cgsynt.tree.parity;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;

public class ParityIntersectTree<LETTER extends IRankedLetter, STATE1 extends IParityState, STATE2 extends IParityState, STATE3 extends IParityState> {
	private final Map<ParityIntersectState<STATE1, STATE2, STATE3>, Collection<ParityTreeAutomatonRule<LETTER, STATE1>>> mSourceMap1;
	private final Map<ParityIntersectState<STATE1, STATE2, STATE3>, Collection<ParityTreeAutomatonRule<LETTER, STATE2>>> mSourceMap2;
	private final Map<ParityIntersectState<STATE1, STATE2, STATE3>, Collection<ParityTreeAutomatonRule<LETTER, STATE3>>> mSourceMap3;
	private final Set<ParityTreeAutomatonRule<LETTER, STATE1>> mRules1;
	private final Set<ParityTreeAutomatonRule<LETTER, STATE2>> mRules2;
	private final Set<ParityTreeAutomatonRule<LETTER, STATE3>> mRules3;
	private final Map<ParityIntersectState<STATE1, STATE2, STATE3>, Collection<ParityTreeAutomatonRule<LETTER, STATE1>>> mChildMap1;
	private final Map<ParityIntersectState<STATE1, STATE2, STATE3>, Collection<ParityTreeAutomatonRule<LETTER, STATE1>>> mChildMap2;
	private final Map<ParityIntersectState<STATE1, STATE2, STATE3>, Collection<ParityTreeAutomatonRule<LETTER, STATE1>>> mChildMap3;
	private final Set<ParityIntersectState<STATE1, STATE2, STATE3>> mStates;
	private final Set<ParityIntersectState<STATE1, STATE2, STATE3>> mInitStates;

	public ParityIntersectTree(ParityTreeAutomaton<LETTER, STATE1> tree1, ParityTreeAutomaton<LETTER, STATE2> tree2,
			ParityTreeAutomaton<LETTER, STATE3> tree3) {
		this.mSourceMap1 = new HashMap<>();
		this.mSourceMap2 = new HashMap<>();
		this.mSourceMap3 = new HashMap<>();
		this.mRules1 = tree1.getRules();
		this.mRules2 = tree2.getRules();
		this.mRules3 = tree3.getRules();
		this.mChildMap1 = new HashMap<>();
		this.mChildMap2 = new HashMap<>();
		this.mChildMap3 = new HashMap<>();
		this.mStates = new HashSet<>();
		this.mInitStates = new HashSet<>();

	}

	private void addInitStates(Set<STATE1> init1, Set<STATE2> init2, Set<STATE3> init3) {
		for (STATE1 state1 : init1) {
			for (STATE2 state2 : init2) {
				for (STATE3 state3 : init3) {
					ParityIntersectState<STATE1, STATE2, STATE3> intersectState = new ParityIntersectState<>(state1,
							state2, state3, false, false, false);
					addState(intersectState);
					addState(intersectState);
				}
			}
		}
	}

	private void addState(ParityIntersectState<STATE1, STATE2, STATE3> state) {
		if (mStates.contains(state))
			mStates.add(state);
	}

}
