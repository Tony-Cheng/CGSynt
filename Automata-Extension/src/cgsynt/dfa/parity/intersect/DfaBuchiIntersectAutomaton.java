package cgsynt.dfa.parity.intersect;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cgsynt.dfa.parity.ParityAutomaton;
import cgsynt.tree.parity.IParityState;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.INestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.transitions.OutgoingInternalTransition;

public class DfaBuchiIntersectAutomaton<LETTER, STATE1, STATE2 extends IParityState> {

	private INestedWordAutomaton<LETTER, STATE1> dfa;
	private ParityAutomaton<LETTER, STATE2> parityAut;
	private Map<DfaBuchiIntersectState<STATE1, STATE2>, Set<DfaBuchiIntersectRule<LETTER, STATE1, STATE2>>> sourceMap;
	private Set<DfaBuchiIntersectState<STATE1, STATE2>> initialStates;

	public DfaBuchiIntersectAutomaton(INestedWordAutomaton<LETTER, STATE1> dfa,
			ParityAutomaton<LETTER, STATE2> parityAut) {
		super();
		this.dfa = dfa;
		this.parityAut = parityAut;
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
				if (!sourceMap.containsKey(new DfaBuchiIntersectState<>(dfaState, parityState))) {
					sourceMap.put(new DfaBuchiIntersectState<>(dfaState, parityState), new HashSet<>());
				}
				sourceMap.get(new DfaBuchiIntersectState<>(dfaState, parityState))
						.add(new DfaBuchiIntersectRule<>(new DfaBuchiIntersectState<>(dfaState, parityState),
								new DfaBuchiIntersectState<>(dfaTransition.getSucc(), parityTransition.getSucc()),
								letter));
			}
		}
	}

	public Set<DfaBuchiIntersectRule<LETTER, STATE1, STATE2>> internalSuccessors(
			DfaBuchiIntersectState<STATE1, STATE2> state) {
		return sourceMap.get(state);

	}

	public boolean isFinal(DfaBuchiIntersectState<STATE1, STATE2> state) {
		return dfa.isFinal(state.state1);
	}

	private void computeInitialStates() {
		this.initialStates = new HashSet<>();
		for (STATE1 state1 : dfa.getInitialStates()) {
			for (STATE2 state2 : parityAut.getInitialStates()) {
				this.initialStates.add(new DfaBuchiIntersectState<STATE1, STATE2>(state1, state2));
			}
		}
	}

	public Set<DfaBuchiIntersectState<STATE1, STATE2>> getInitialStates() {
		return initialStates;
	}
}
