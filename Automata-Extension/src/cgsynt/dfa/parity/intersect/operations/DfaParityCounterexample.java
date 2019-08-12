package cgsynt.dfa.parity.intersect.operations;

import java.util.Stack;

import cgsynt.dfa.parity.intersect.DfaBuchiIntersectState;
import cgsynt.tree.parity.IParityState;

public class DfaParityCounterexample<LETTER, STATE1, STATE2 extends IParityState> {
	public Stack<LETTER> loopTransitions;
	public Stack<DfaBuchiIntersectState<STATE1, STATE2>> loopStates;
	public Stack<LETTER> stemTransitions;
	public Stack<DfaBuchiIntersectState<STATE1, STATE2>> stemStates;
	public DfaBuchiIntersectState<STATE1, STATE2> repeatedState;
	public int maxRepeatingNumber;

	public DfaParityCounterexample(int maxRepeatingNumber) {
		this.loopTransitions = new Stack<>();
		this.loopStates = new Stack<>();
		this.stemTransitions = new Stack<>();
		this.stemStates = new Stack<>();
		this.repeatedState = null;
		this.maxRepeatingNumber = maxRepeatingNumber;
	}

	public DfaParityCounterexample<LETTER, STATE1, STATE2> makeCopy() {
		DfaParityCounterexample<LETTER, STATE1, STATE2> copy = new DfaParityCounterexample<>(maxRepeatingNumber);
		copy.repeatedState = this.repeatedState;
		copy.loopTransitions.addAll(this.loopTransitions);
		copy.loopStates.addAll(this.loopStates);
		copy.stemStates.addAll(this.stemStates);
		copy.stemTransitions.addAll(this.stemTransitions);
		return copy;
	}

}