package cgsynt.dfa.parity.operations;

import java.util.Stack;

public class ParityCounterexample<LETTER, STATE> {
	public Stack<LETTER> loopTransitions;
	public Stack<STATE> loopStates;
	public Stack<LETTER> stemTransitions;
	public Stack<STATE> stemStates;
	public STATE repeatedState;
	public int maxRepeatingNumber;

	public ParityCounterexample(int maxRepeatingNumber) {
		this.loopTransitions = new Stack<>();
		this.loopStates = new Stack<>();
		this.stemTransitions = new Stack<>();
		this.stemStates = new Stack<>();
		this.repeatedState = null;
		this.maxRepeatingNumber = maxRepeatingNumber;
	}

	public ParityCounterexample<LETTER, STATE> makeCopy() {
		ParityCounterexample<LETTER, STATE> copy = new ParityCounterexample<>(maxRepeatingNumber);
		copy.repeatedState = this.repeatedState;
		copy.loopTransitions.addAll(this.loopTransitions);
		copy.loopStates.addAll(this.loopStates);
		copy.stemStates.addAll(this.stemStates);
		copy.stemTransitions.addAll(this.stemTransitions);
		return copy;
	}

}