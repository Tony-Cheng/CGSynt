package cgsynt.tree.buchi.parity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import cgsynt.tree.buchi.BuchiTreeAutomaton;
import cgsynt.tree.buchi.BuchiTreeAutomatonRule;
import cgsynt.tree.parity.IParityState;
import cgsynt.tree.parity.ParityTreeAutomaton;
import cgsynt.tree.parity.ParityTreeAutomatonRule;
import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;

public class BuchiParityIntersectAutomaton<LETTER extends IRankedLetter, STATE1, STATE2 extends IParityState>
		extends ParityTreeAutomaton<LETTER, BuchiParityIntersectState<STATE1, STATE2>> {
	public BuchiParityIntersectAutomaton(BuchiTreeAutomaton<LETTER, STATE1> buchiAut,
			ParityTreeAutomaton<LETTER, STATE2> parityAut) {
		super(buchiAut.getRank());
		assert buchiAut.getRank() == parityAut.getRank();
		this.computeInitialStates(buchiAut, parityAut);
		this.computeRules(buchiAut, parityAut);
	}

	private void computeRules(BuchiTreeAutomaton<LETTER, STATE1> buchiAut,
			ParityTreeAutomaton<LETTER, STATE2> parityAut) {
		Stack<BuchiParityIntersectState<STATE1, STATE2>> toVisit = new Stack<>();
		Set<BuchiParityIntersectState<STATE1, STATE2>> visited = new HashSet<>();
		toVisit.addAll(this.getInitStates());
		visited.addAll(this.getInitStates());
		while (!toVisit.isEmpty()) {
			BuchiParityIntersectState<STATE1, STATE2> next = toVisit.pop();
			for (BuchiTreeAutomatonRule<LETTER, STATE1> buchiRule : buchiAut
					.getRulesBySource(next.getState().getState1())) {
				for (ParityTreeAutomatonRule<LETTER, STATE2> parityRule : parityAut
						.getRulesBySource(next.getState().getState2())) {
					if (!buchiRule.getLetter().equals(parityRule.getLetter())) {
						continue;
					}
					List<BuchiParityIntersectState<STATE1, STATE2>> dests = new ArrayList<>();
					boolean isGoodTransition = true;
					for (int i = 0; i < buchiRule.getDest().size(); i++) {
						BuchiParityPair<STATE1, STATE2> pair = new BuchiParityPair<>(buchiRule.getDest().get(i),
								parityRule.getDest().get(i));
						BuchiParityIntersectState<STATE1, STATE2> dest = null;
						if (buchiAut.isFinalState(pair.getState1())) {
							dest = new BuchiParityIntersectState<>(pair);
						} else if (next.getVisitedStates().containsKey(pair)
								&& next.getVisitedStates().get(pair) < next.getK()) {
							dest = next.nextState(pair, next.getK());
						} else if (!next.getVisitedStates().containsKey(pair)) {
							dest = next.nextState(pair, next.getK());
						}
						if (dest != null && !visited.contains(dest)) {
							visited.add(dest);
							toVisit.push(dest);
						}
						if (dest == null) {
							isGoodTransition = false;
						}
						dests.add(dest);
					}
					if (isGoodTransition) {
						this.addRule(new ParityTreeAutomatonRule<>(buchiRule.getLetter(), next, dests));
					}
				}
			}
		}
	}

	private void computeInitialStates(BuchiTreeAutomaton<LETTER, STATE1> buchiAut,
			ParityTreeAutomaton<LETTER, STATE2> parityAut) {
		for (STATE1 buchiState : buchiAut.getInitStates()) {
			for (STATE2 parityState : parityAut.getInitStates()) {
				BuchiParityIntersectState<STATE1, STATE2> initialState = new BuchiParityIntersectState<STATE1, STATE2>(
						new BuchiParityPair<STATE1, STATE2>(buchiState, parityState));
				this.addInitState(initialState);
			}
		}
	}
}
