package cgsynt.tree.buchi.parity;

import java.util.ArrayList;
import java.util.List;

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
		int k = buchiAut.getStates().size() * parityAut.getStates().size();
		for (LETTER letter : buchiAut.getAlphabet()) {
			if (!parityAut.getAlphabet().contains(letter))
				continue;
			for (BuchiTreeAutomatonRule<LETTER, STATE1> buchiRule : buchiAut.getSuccessors(letter)) {
				for (ParityTreeAutomatonRule<LETTER, STATE2> parityRule : parityAut.getSuccessors(letter)) {
					for (int i = 0; i < k; i++) {
						if (buchiAut.isFinalState(buchiRule.getSource())) {
							BuchiParityIntersectState<STATE1, STATE2> source = new BuchiParityIntersectState<STATE1, STATE2>(
									buchiRule.getSource(), parityRule.getSource(), k);
							List<BuchiParityIntersectState<STATE1, STATE2>> dests = new ArrayList<>();
							for (int j = 0; j < buchiRule.getDest().size(); j++) {
								dests.add(new BuchiParityIntersectState<STATE1, STATE2>(buchiRule.getDest().get(j),
										parityRule.getDest().get(j), 0));
							}
							this.addRule(new BuchiParityIntersectRule<>(source, dests, letter));
						} else {
							BuchiParityIntersectState<STATE1, STATE2> source = new BuchiParityIntersectState<STATE1, STATE2>(
									buchiRule.getSource(), parityRule.getSource(), k);
							List<BuchiParityIntersectState<STATE1, STATE2>> dests = new ArrayList<>();
							for (int j = 0; j < buchiRule.getDest().size(); j++) {
								dests.add(new BuchiParityIntersectState<STATE1, STATE2>(buchiRule.getDest().get(j),
										parityRule.getDest().get(j), k + 1));
							}
							this.addRule(new BuchiParityIntersectRule<>(source, dests, letter));
						}
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
						buchiState, parityState, 0);
				this.addInitState(initialState);
			}
		}
	}
}
