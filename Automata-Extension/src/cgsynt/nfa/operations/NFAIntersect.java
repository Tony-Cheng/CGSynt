package cgsynt.nfa.operations;

import java.util.HashSet;
import java.util.Set;

import cgsynt.automaton.factory.IntersectStateFactory;
import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.VpAlphabet;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.transitions.OutgoingInternalTransition;
import de.uni_freiburg.informatik.ultimate.automata.statefactory.IEmptyStackStateFactory;

public class NFAIntersect<LETTER, STATE1, STATE2> {

	private NestedWordAutomaton<LETTER, STATE1> aut1;
	private NestedWordAutomaton<LETTER, STATE2> aut2;
	private boolean resultComputed;
	private NestedWordAutomaton<LETTER, NFAIntersectedState<STATE1, STATE2>> result;
	private AutomataLibraryServices services;
	private IEmptyStackStateFactory<STATE1> stateFactory1;
	private IEmptyStackStateFactory<STATE2> stateFactory2;

	public NFAIntersect(NestedWordAutomaton<LETTER, STATE1> aut1, NestedWordAutomaton<LETTER, STATE2> aut2,
			AutomataLibraryServices services, IEmptyStackStateFactory<STATE1> stateFactory1,
			IEmptyStackStateFactory<STATE2> stateFactory2) {
		this.aut1 = aut1;
		this.aut2 = aut2;
		this.resultComputed = false;
		this.services = services;
		this.stateFactory1 = stateFactory1;
		this.stateFactory2 = stateFactory2;
	}

	public void computeResult() {
		if (resultComputed) {
			return;
		}
		Set<LETTER> internalAlphabet = new HashSet<>();
		for (LETTER transition : aut1.getAlphabet()) {
			if (aut2.getAlphabet().contains(transition))
				internalAlphabet.add(transition);
		}
		VpAlphabet<LETTER> vpAlphabet = new VpAlphabet<>(internalAlphabet, new HashSet<>(), new HashSet<>());
		result = new NestedWordAutomaton<>(services, vpAlphabet,
				new IntersectStateFactory<>(stateFactory1, stateFactory2));
		for (STATE1 state1 : aut1.getStates()) {
			for (STATE2 state2 : aut2.getStates()) {
				boolean isInitial = false;
				boolean isFinal = false;
				if (aut2.isFinal(state2)) {
					isFinal = true;
				}
				if (aut1.isInitial(state1) && aut2.isInitial(state2)) {
					isInitial = true;
				}
				if (isFinal) {
					result.addState(false, true, new NFAIntersectedState<>(state1, state2, 2));
				} else {
					result.addState(false, false, new NFAIntersectedState<>(state1, state2, 2));
				}
				if (isInitial) {
					result.addState(true, false, new NFAIntersectedState<>(state1, state2, 1));
				} else {
					result.addState(false, false, new NFAIntersectedState<>(state1, state2, 1));
				}
			}
		}
		for (STATE1 state1 : aut1.getStates()) {
			for (STATE2 state2 : aut2.getStates()) {
				for (LETTER letter : result.getAlphabet()) {
					if (aut1.internalSuccessors(state1, letter) != null
							&& aut2.internalSuccessors(state2, letter) != null) {
						for (OutgoingInternalTransition<LETTER, STATE1> transition1 : aut1.internalSuccessors(state1,
								letter)) {
							for (OutgoingInternalTransition<LETTER, STATE2> transition2 : aut2
									.internalSuccessors(state2, letter)) {
								
							}
						}
					}
				}
			}
		}
		resultComputed = true;
	}

}
