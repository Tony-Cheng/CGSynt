package cgsynt.dfa.parity.intersect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cgsynt.RepCondenser;
import cgsynt.dfa.parity.ParityAutomaton;
import cgsynt.tree.parity.IParityState;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.INestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.transitions.OutgoingInternalTransition;
import de.uni_freiburg.informatik.ultimate.util.datastructures.relation.IsContained;
import de.uni_freiburg.informatik.ultimate.util.datastructures.relation.NestedMap2;

public class DfaParityIntersectAutomaton<LETTER, STATE1, STATE2 extends IParityState> {

	private INestedWordAutomaton<LETTER, STATE1> dfa;
	private ParityAutomaton<LETTER, STATE2> parityAut;
	private Map<DfaParityIntersectState<STATE1, STATE2>, Set<DfaParityIntersectRule<LETTER, STATE1, STATE2>>> sourceMap;
	private Set<DfaParityIntersectState<STATE1, STATE2>> initialStates;

	public DfaParityIntersectAutomaton(INestedWordAutomaton<LETTER, STATE1> dfa,
			ParityAutomaton<LETTER, STATE2> parityAut) {
		super();
		this.dfa = dfa;
		this.parityAut = parityAut;
		this.sourceMap = new HashMap<>();
		computeSourceMap();
		computeInitialStates();
	}

	private void computeSourceMap() {
		for (LETTER letter : dfa.getAlphabet()) {
			for (STATE2 parityState : parityAut.getStates()) {
				if (parityAut.internalSuccessors(parityState, letter) == null)
					continue;
				for (STATE1 dfaState : dfa.getStates()) {
					if (dfa.internalSuccessors(dfaState, letter) == null)
						continue;
					addAllRules(dfaState, parityState, letter);
				}
			}
		}
	}

	private void addAllRules(STATE1 dfaState, STATE2 parityState, LETTER letter) {
		for (OutgoingInternalTransition<LETTER, STATE1> dfaTransition : dfa.internalSuccessors(dfaState, letter)) {
			for (OutgoingInternalTransition<LETTER, STATE2> parityTransition : parityAut.internalSuccessors(parityState,
					letter)) {
				if (!sourceMap.containsKey(new DfaParityIntersectState<>(dfaState, parityState))) {
					sourceMap.put(new DfaParityIntersectState<>(dfaState, parityState), new HashSet<>());
				}
				sourceMap.get(new DfaParityIntersectState<>(dfaState, parityState))
						.add(new DfaParityIntersectRule<>(new DfaParityIntersectState<>(dfaState, parityState),
								new DfaParityIntersectState<>(dfaTransition.getSucc(), parityTransition.getSucc()),
								letter));
			}
		}
	}

	public Set<DfaParityIntersectRule<LETTER, STATE1, STATE2>> internalSuccessors(
			DfaParityIntersectState<STATE1, STATE2> state) {
		return sourceMap.get(state);

	}

	public boolean isFinal(DfaParityIntersectState<STATE1, STATE2> state) {
		return dfa.isFinal(state.state1);
	}

	private void computeInitialStates() {
		this.initialStates = new HashSet<>();
		for (STATE1 state1 : dfa.getInitialStates()) {
			for (STATE2 state2 : parityAut.getInitialStates()) {
				this.initialStates.add(new DfaParityIntersectState<STATE1, STATE2>(state1, state2));
			}
		}
	}

	public Set<DfaParityIntersectState<STATE1, STATE2>> getInitialStates() {
		return initialStates;
	}
	
	private Set<DfaParityIntersectState<STATE1, STATE2>> getStates(){
		// Done this way because there is possibly a state that has no outgoing transitions.
		Set<DfaParityIntersectState<STATE1, STATE2>> stateSet = new HashSet<>();
		for (Set<DfaParityIntersectRule<LETTER, STATE1, STATE2>> transitionSet : this.sourceMap.values()) {
			for (DfaParityIntersectRule<LETTER, STATE1, STATE2> transition : transitionSet) {
				stateSet.add(transition.source);
				stateSet.add(transition.dest);
			}
		}
		
		return stateSet;
	}
	
	private Set<DfaParityIntersectState<STATE1, STATE2>> getFinalStates(){
		Set<DfaParityIntersectState<STATE1, STATE2>> finalStates = new HashSet<>();
		
		for (DfaParityIntersectState<STATE1, STATE2> state : this.getStates()) {
			if (this.dfa.isFinal(state.state1))
				finalStates.add(state);
		}
			
		return finalStates;
	}
	
	@Override
	public String toString() {
		String rep = ""; 
		
		RepCondenser<DfaParityIntersectState<STATE1, STATE2>> stateCondenser = new RepCondenser<>(new ArrayList<>(this.getStates()));
		Map<String, String> stateMapping = stateCondenser.getMapping();
		
		RepCondenser<LETTER> letterCondenser = new RepCondenser<>(new ArrayList<>(this.dfa.getAlphabet()));
		Map<String, String> letterMapping = letterCondenser.getMapping();
		
		rep += "States:\n";
		for (String key : stateMapping.keySet()) 
			rep += stateMapping.get(key) + "\n";
			
		rep += "\nInital States:\n";
		for (DfaParityIntersectState<STATE1, STATE2> initState : this.getInitialStates())
			rep += stateMapping.get(initState.toString()) + "\n";
		
		rep += "\nFinal States:\n";
		for (DfaParityIntersectState<STATE1, STATE2> finalState : this.getFinalStates())
			rep += stateMapping.get(finalState.toString()) + "\n";
		
		rep += "\nAlphabet:\n";
		for (String key : letterMapping.keySet())
			rep += "LETTER " + letterMapping.get(key).toLowerCase() + "\n";
		
		rep += "\nTransitions:\n";
		for (DfaParityIntersectState<STATE1, STATE2> source : this.getStates()) {
			Set<DfaParityIntersectRule<LETTER, STATE1, STATE2>> transitions = this.sourceMap.get(source);
				
			for (DfaParityIntersectRule<LETTER, STATE1, STATE2> transition : transitions) {
				rep += "(" + stateMapping.get(source.toString()) + ": " + source.getRank() + " | "
					+ "LETTER " + letterMapping.get(transition.getLetter().toString()).toLowerCase() + " | "
					+ stateMapping.get(transition.getSucc().toString()) + ": " + transition.getSucc().getRank() + ")\n";
			}
		}
		
		rep += "\nState Mapping:\n";
		for (DfaParityIntersectState<STATE1, STATE2> state : this.getStates())
			rep += stateMapping.get(state.toString()) + " = " + state.toString() + "\n";
		
		rep += "\nLetter Mapping: \n";
		for (LETTER letter : this.dfa.getAlphabet())
			rep += "LETTER " + letterMapping.get(letter.toString()).toLowerCase() + " = " + letter.toString() + "\n";
		
		return rep;
	}
}
