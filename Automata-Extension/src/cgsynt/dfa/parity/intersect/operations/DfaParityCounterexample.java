package cgsynt.dfa.parity.intersect.operations;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import cgsynt.RepCondenser;
import cgsynt.dfa.parity.intersect.DfaParityIntersectState;
import cgsynt.tree.parity.IParityState;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IcfgInternalTransition;

public class DfaParityCounterexample<LETTER, STATE1, STATE2 extends IParityState> {
	public Stack<LETTER> loopTransitions;
	public Stack<DfaParityIntersectState<STATE1, STATE2>> loopStates;
	public Stack<LETTER> stemTransitions;
	public Stack<DfaParityIntersectState<STATE1, STATE2>> stemStates;
	public DfaParityIntersectState<STATE1, STATE2> repeatedState;
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
	
	private List<DfaParityIntersectState<STATE1, STATE2>> getStates(){
		List<DfaParityIntersectState<STATE1, STATE2>> states = new LinkedList<>();
		
		for (DfaParityIntersectState<STATE1, STATE2> stemState : this.stemStates) {
			if (!states.contains(stemState))
				states.add(stemState);
		}
		for (DfaParityIntersectState<STATE1, STATE2> loopState : this.loopStates) {
			if (!states.contains(loopState))
				states.add(loopState);
		}
		return states;
	}
	
	private List<LETTER> getLetters(){
		List<LETTER> letters = new LinkedList<>();
		
		for (LETTER letter : this.stemTransitions) {
			if (!letters.contains(letter))
				letters.add(letter);
		}
		for (LETTER letter : this.loopTransitions) {
			if (!letters.contains(letter))
				letters.add(letter);
		}
		
		return letters;
	}

	@Override
	public String toString() {
		String rep = "";
		RepCondenser<DfaParityIntersectState<STATE1, STATE2>> stateCondenser =
				new RepCondenser<>(this.getStates());
		RepCondenser<LETTER> letterCondenser =
				new RepCondenser<>(this.getLetters());
		
		Map<String, String> stateMap = stateCondenser.getMapping();
		Map<String, String> letterMap = letterCondenser.getMapping();
		
		rep += "Stem:\n";
		Iterator<DfaParityIntersectState<STATE1, STATE2>> stemStateIterator = this.stemStates.iterator();
		Iterator<LETTER> stemLetterIterator = this.stemTransitions.iterator();
		
		if (!this.stemStates.isEmpty()) {
			rep += stateMap.get(stemStateIterator.next().toString());
			while (stemStateIterator.hasNext())
				rep += " <- " + stateMap.get(stemStateIterator.next().toString());
			rep += "\n   ";
			
			if (!this.stemTransitions.isEmpty()) {
				rep += letterMap.get(stemLetterIterator.next().toString()).toLowerCase();
				while (stemLetterIterator.hasNext()) {
					rep += "    " + letterMap.get(stemLetterIterator.next().toString()).toLowerCase();
				}
				rep += "\n";
			}
		}
		else {
			rep += "No Stem\n";
		}
		
		rep += "\nLoop:\n";
		Iterator<DfaParityIntersectState<STATE1, STATE2>> loopStateIterator = this.loopStates.iterator();
		Iterator<LETTER> loopLetterIterator = this.loopTransitions.iterator();
		
		rep += stateMap.get(loopStateIterator.next().toString());
		while (loopStateIterator.hasNext()) 
			rep += " <- " + stateMap.get(loopStateIterator.next().toString());
		rep += "\n   ";
		
		rep += letterMap.get(loopLetterIterator.next().toString()).toLowerCase();
		while (loopLetterIterator.hasNext())
			rep += "    " + letterMap.get(loopLetterIterator.next().toString()).toLowerCase();
		rep += "\n\n State Mapping:\n";
		
		for (DfaParityIntersectState<STATE1, STATE2> state : this.getStates()) {
			rep += stateMap.get(state.toString()) + " -> " + state.toString() + "\n";
		}
		rep += "\nLetters\n";
		for (LETTER letter : this.getLetters()) {
			rep += letterMap.get(letter.toString()).toLowerCase() + " -> " + letter.toString() + "\n";
		}
		
		return rep;
	}
}