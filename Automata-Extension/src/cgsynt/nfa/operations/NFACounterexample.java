package cgsynt.nfa.operations;

import java.util.Stack;

public class NFACounterexample<LETTER, STATE> {
	public Stack<LETTER> loopTransitions;
	public Stack<STATE> loopStates;
	public Stack<LETTER> stemTransitions;
	public Stack<STATE> stemStates;
	public STATE repeatedState;
	public int maxRepeatingNumber;

	public NFACounterexample(int maxRepeatingNumber) {
		this.loopTransitions = new Stack<>();
		this.loopStates = new Stack<>();
		this.stemTransitions = new Stack<>();
		this.stemStates = new Stack<>();
		this.repeatedState = null;
		this.maxRepeatingNumber = maxRepeatingNumber;
	}

	public NFACounterexample<LETTER, STATE> makeCopy() {
		NFACounterexample<LETTER, STATE> copy = new NFACounterexample<>(maxRepeatingNumber);
		copy.repeatedState = this.repeatedState;
		copy.loopTransitions.addAll(this.loopTransitions);
		copy.loopStates.addAll(this.loopStates);
		copy.stemStates.addAll(this.stemStates);
		copy.stemTransitions.addAll(this.stemTransitions);
		return copy;
	}

}