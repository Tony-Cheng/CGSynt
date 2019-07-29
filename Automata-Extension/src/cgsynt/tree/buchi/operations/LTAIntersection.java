package cgsynt.tree.buchi.operations;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cgsynt.tree.buchi.BuchiTreeAutomaton;
import cgsynt.tree.buchi.BuchiTreeAutomatonRule;
import cgsynt.tree.buchi.lta.LTAIntersectState;
import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;

/**
 * Compute the intersection of two LTAs.
 *
 * @param <LETTER>
 * @param <STATE1>
 * @param <STATE2>
 */
public class LTAIntersection<LETTER extends IRankedLetter, STATE1, STATE2> {
	private final BuchiTreeAutomaton<LETTER, STATE1> tree1;
	private final BuchiTreeAutomaton<LETTER, STATE2> tree2;
	private final BuchiTreeAutomaton<LETTER, LTAIntersectState<STATE1, STATE2>> result;

	public LTAIntersection(BuchiTreeAutomaton<LETTER, STATE1> tree1, BuchiTreeAutomaton<LETTER, STATE2> tree2) {
		this.tree1 = tree1;
		this.tree2 = tree2;
		assert tree1.getRank() == tree2.getRank();
		result = new BuchiTreeAutomaton<LETTER, LTAIntersectState<STATE1, STATE2>>(tree1.getRank());
	}

	/**
	 * Compute the alphabet for the LTA intersection automaton.
	 */
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

	/**
	 * Compute the initial states.
	 */
	private void computeInitState() {
		Set<STATE1> initStates1 = tree1.getInitStates();
		Set<STATE2> initStates2 = tree2.getInitStates();
		for (STATE1 state1 : initStates1) {
			for (STATE2 state2 : initStates2) {
				LTAIntersectState<STATE1, STATE2> newState = new LTAIntersectState<>(state1, state2);
				result.addInitState(newState);
			}
		}
	}

	/**
	 * Compute the final states.
	 */
	private void computeFinalState() {
		result.setAllStatesFinal();
	}

	/**
	 * Compute the transitions.
	 */
	private void computeTransitions() {
		for (LETTER letter : tree1.getAlphabet()) {
			Iterable<BuchiTreeAutomatonRule<LETTER, STATE1>> rules1 = tree1.getSuccessors(letter);
			Iterable<BuchiTreeAutomatonRule<LETTER, STATE2>> rules2 = tree2.getSuccessors(letter);
			if (rules1 == null)
				continue;
			if (rules2 == null)
				continue;
			for (BuchiTreeAutomatonRule<LETTER, STATE1> rule1 : rules1) {
				for (BuchiTreeAutomatonRule<LETTER, STATE2> rule2 : rules2) {
					STATE1 source1 = rule1.getSource();
					STATE2 source2 = rule2.getSource();
					List<STATE1> dest1 = rule1.getDest();
					List<STATE2> dest2 = rule2.getDest();
					List<LTAIntersectState<STATE1, STATE2>> destResult = new ArrayList<>();
					for (int i = 0; i < dest1.size(); i++) {
						LTAIntersectState<STATE1, STATE2> newState = new LTAIntersectState<>(dest1.get(i),
								dest2.get(i));
						destResult.add(newState);
					}
					LTAIntersectState<STATE1, STATE2> newSource = new LTAIntersectState<>(source1, source2);
					BuchiTreeAutomatonRule<LETTER, LTAIntersectState<STATE1, STATE2>> newRule = new BuchiTreeAutomatonRule<>(
							letter, newSource, destResult);
					result.addRule(newRule);
				}
			}

		}

	}

	/**
	 * Compute the LTA that is the result of the intersection.
	 * 
	 * @return
	 */
	public BuchiTreeAutomaton<LETTER, LTAIntersectState<STATE1, STATE2>> computeResult() {
		computeAlphabet();
		computeInitState();
		computeTransitions();
		computeFinalState();
		return result;
	}
}
