package cgsynt.tree.buchi.operations;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cgsynt.tree.buchi.BuchiTreeAutomaton;
import cgsynt.tree.buchi.BuchiTreeAutomatonRule;
import cgsynt.tree.buchi.LTAIntersectState;
import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;

public class LTAIntersection<LETTER extends IRankedLetter, STATE> {
	private final BuchiTreeAutomaton<LETTER, STATE> tree1;
	private final BuchiTreeAutomaton<LETTER, STATE> tree2;
	private final BuchiTreeAutomaton<LETTER, LTAIntersectState<STATE>> result;

	public LTAIntersection(BuchiTreeAutomaton<LETTER, STATE> tree1, BuchiTreeAutomaton<LETTER, STATE> tree2) {
		this.tree1 = tree1;
		this.tree2 = tree2;
		assert tree1.getRank() == tree2.getRank();
		result = new BuchiTreeAutomaton<LETTER, LTAIntersectState<STATE>>(tree1.getRank());
	}

	private void computeAlphabet() {
		Set<LETTER> alpha1 = tree1.getAlphabet();
		Set<LETTER> alpha2 = tree2.getAlphabet();
		for (LETTER letter : alpha1) {
			result.addLetter(letter);
		}
		for (LETTER letter : alpha2) {
			result.addLetter(letter);
		}
	}

	private void computeInitState() {
		Set<STATE> initStates1 = tree1.getInitStates();
		Set<STATE> initStates2 = tree2.getInitStates();
		for (STATE state1 : initStates1) {
			for (STATE state2 : initStates2) {
				LTAIntersectState<STATE> newState = new LTAIntersectState<>(state1, state2);
				result.addInitState(newState);
			}
		}
	}

	private void computeFinalState() {
		Set<STATE> states1 = tree1.getStates();
		Set<STATE> finalStates2 = tree2.getFinalStates();
		for (STATE state1 : states1) {
			for (STATE state2 : finalStates2) {
				LTAIntersectState<STATE> newFinalState = new LTAIntersectState<>(state1, state2);
				result.addFinalState(newFinalState);
			}
		}
	}

	private void computeTransitions() {
		for (LETTER letter : tree1.getAlphabet()) {
			Iterable<BuchiTreeAutomatonRule<LETTER, STATE>> rules1 = tree1.getSuccessors(letter);
			Iterable<BuchiTreeAutomatonRule<LETTER, STATE>> rules2 = tree2.getSuccessors(letter);
			if (rules1 == null)
				continue;
			if (rules2 == null)
				continue;
			for (BuchiTreeAutomatonRule<LETTER, STATE> rule1 : rules1) {
				for (BuchiTreeAutomatonRule<LETTER, STATE> rule2 : rules2) {
					STATE source1 = rule1.getSource();
					STATE source2 = rule2.getSource();
					List<STATE> dest1 = rule1.getDest();
					List<STATE> dest2 = rule2.getDest();
					List<LTAIntersectState<STATE>> destResult = new ArrayList<>();
					for (int i = 0; i < dest1.size(); i++) {
						LTAIntersectState<STATE> newState = new LTAIntersectState<>(dest1.get(i), dest2.get(i));
						destResult.add(newState);
					}
					LTAIntersectState<STATE> newSource = new LTAIntersectState<>(source1, source2);
					BuchiTreeAutomatonRule<LETTER, LTAIntersectState<STATE>> newRule = new BuchiTreeAutomatonRule<>(
							letter, newSource, destResult);
					result.addRule(newRule);
				}
			}

		}

	}

	public BuchiTreeAutomaton<LETTER, LTAIntersectState<STATE>> computeResult() {
		computeAlphabet();
		computeInitState();
		computeFinalState();
		computeTransitions();
		return result;
	}
}
