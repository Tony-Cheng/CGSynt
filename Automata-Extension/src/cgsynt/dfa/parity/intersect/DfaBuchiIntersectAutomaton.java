package cgsynt.dfa.parity.intersect;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import cgsynt.dfa.parity.ParityAutomaton;
import cgsynt.tree.parity.IParityState;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.INestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.transitions.OutgoingInternalTransition;

public class DfaBuchiIntersectAutomaton<LETTER, STATE1, STATE2 extends IParityState> {

	private INestedWordAutomaton<LETTER, STATE1> dfa;
	private ParityAutomaton<LETTER, STATE2> parityAut;
	private Map<DfaBuchiIntersectState<STATE1, STATE2>, List<DfaBuchiIntersectRule<LETTER, STATE1, STATE2>>> sourceMap;

	public DfaBuchiIntersectAutomaton(INestedWordAutomaton<LETTER, STATE1> dfa,
			ParityAutomaton<LETTER, STATE2> parityAut) {
		super();
		this.dfa = dfa;
		this.parityAut = parityAut;
		computeSourceMap();
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
					sourceMap.put(new DfaBuchiIntersectState<>(dfaState, parityState), new ArrayList<>());
				}
				sourceMap.get(new DfaBuchiIntersectState<>(dfaState, parityState))
						.add(new DfaBuchiIntersectRule<>(dfaTransition.getSucc(), parityTransition.getSucc(), letter));
			}
		}
	}

}
