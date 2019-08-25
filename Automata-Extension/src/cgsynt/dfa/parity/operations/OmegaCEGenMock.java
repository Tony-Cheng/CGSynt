package cgsynt.dfa.parity.operations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cgsynt.dfa.parity.intersect.DfaParityIntersectAutomaton;
import cgsynt.dfa.parity.intersect.DfaParityIntersectRule;
import cgsynt.dfa.parity.intersect.DfaParityIntersectState;
import cgsynt.dfa.parity.intersect.operations.DfaParityCounterexample;
import cgsynt.tree.parity.IParityState;

public class OmegaCEGenMock<LETTER, STATE1, STATE2 extends IParityState> {

	private DfaParityIntersectAutomaton<LETTER, STATE1, STATE2> mAut;
	private boolean mResultComputed;
	private int mMaxLen;
	private Map<DfaParityIntersectState<STATE1, STATE2>, Integer> mVisitedStates;
	private List<DfaParityCounterexample<LETTER, STATE1, STATE2>> mResult;

	public OmegaCEGenMock(DfaParityIntersectAutomaton<LETTER, STATE1, STATE2> aut, int maxLen) {
		this.mAut = aut;
		this.mResultComputed = false;
		this.mMaxLen = maxLen;
	}

	public void computeResult() {
		if (mResultComputed) {
			return;
		}
		this.mVisitedStates = new HashMap<>();
		this.mResult = new ArrayList<>();
		for (DfaParityIntersectState<STATE1, STATE2> initialState : mAut.getInitialStates()) {
			List<DfaParityCounterexample<LETTER, STATE1, STATE2>> counterexamples = generateCounterexamples(initialState,
					mMaxLen);
			for (int i = counterexamples.size() - 1; i >= 0; i--) {
				if (counterexamples.get(i).repeatedState != null) {
					counterexamples.remove(i);
				}
			}
			mResult.addAll(counterexamples);
		}
		mResultComputed = true;
	}

	public List<DfaParityCounterexample<LETTER, STATE1, STATE2>> getResult() {
		if (!mResultComputed)
			return null;
		return mResult;
	}

	private List<DfaParityCounterexample<LETTER, STATE1, STATE2>> generateCounterexamples(
			DfaParityIntersectState<STATE1, STATE2> state, int len) {
		List<DfaParityCounterexample<LETTER, STATE1, STATE2>> counterexamples = new ArrayList<>();
		if (mVisitedStates.containsKey(state) && mVisitedStates.get(state) > 0) {
			DfaParityCounterexample<LETTER, STATE1, STATE2> counterexample = new DfaParityCounterexample<>(state.getRank());
			counterexample.repeatedState = state;
			// counterexample.loopStates.push(state);
			counterexamples.add(counterexample);
		}
		if (len == 0) {
			return counterexamples;
		}
		if (!mVisitedStates.containsKey(state)) {
			mVisitedStates.put(state, 0);
		}
		mVisitedStates.put(state, mVisitedStates.get(state) + 1);
		for (DfaParityIntersectRule<LETTER, STATE1, STATE2> transition : mAut.internalSuccessors(state)) {
			List<DfaParityCounterexample<LETTER, STATE1, STATE2>> destCounterexamples = generateCounterexamples(
					transition.getSucc(), len - 1);
			for (int i = 0; i < destCounterexamples.size(); i++) {
				if (state.equals(destCounterexamples.get(i).repeatedState)
						&& destCounterexamples.get(i).maxRepeatingNumber % 2 == 1) {
					DfaParityCounterexample<LETTER, STATE1, STATE2> copy = destCounterexamples.get(i).makeCopy();
					copy.repeatedState = null;
					copy.loopStates.push(state);
					copy.loopTransitions.push(transition.getLetter());
					copy.stemStates.push(state);
					counterexamples.add(copy);
				}
				if (!state.equals(destCounterexamples.get(i).repeatedState) || mVisitedStates.get(state) > 1) {
					destCounterexamples.get(i).maxRepeatingNumber = Math
							.max(destCounterexamples.get(i).maxRepeatingNumber, state.getRank());

					if (destCounterexamples.get(i).repeatedState == null) {
						destCounterexamples.get(i).stemTransitions.push(transition.getLetter());
						destCounterexamples.get(i).stemStates.push(state);
						counterexamples.add(destCounterexamples.get(i));
					} else {
						destCounterexamples.get(i).loopTransitions.push(transition.getLetter());
						destCounterexamples.get(i).loopStates.push(state);
						counterexamples.add(destCounterexamples.get(i));
					}
				}
			}
		}
		mVisitedStates.put(state, mVisitedStates.get(state) - 1);
		return counterexamples;
	}
}
