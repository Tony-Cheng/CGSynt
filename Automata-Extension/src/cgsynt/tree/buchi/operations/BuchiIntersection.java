package cgsynt.tree.buchi.operations;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cgsynt.tree.buchi.BuchiTreeAutomaton;
import cgsynt.tree.buchi.BuchiTreeAutomatonRule;
import cgsynt.tree.buchi.IntersectState;
import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;

/**
 * A class for computing the intersection of two Buchi tree.
 * 
 * @param <LETTER>
 * @param <STATE>
 */
public class BuchiIntersection<LETTER extends IRankedLetter, STATE1, STATE2> {
	private final BuchiTreeAutomaton<LETTER, STATE1> tree1;
	private final BuchiTreeAutomaton<LETTER, STATE2> tree2;
	private final BuchiTreeAutomaton<LETTER, IntersectState<STATE1, STATE2>> result;

	/**
	 * Create a new BuchiIntersectTree that intersects tree1 and tree2.
	 * 
	 * @param tree1
	 *            a Buchi tree
	 * @param tree2
	 *            a Buchi tree
	 */
	public BuchiIntersection(BuchiTreeAutomaton<LETTER, STATE1> tree1, BuchiTreeAutomaton<LETTER, STATE2> tree2) {
		this.tree1 = tree1;
		this.tree2 = tree2;
		assert tree1.getRank() == tree2.getRank();
		result = new BuchiTreeAutomaton<LETTER, IntersectState<STATE1, STATE2>>(tree1.getRank());
	}

	/**
	 * Compute the alphabet of the resulting automaton.
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
	 * Compute the initial states of the resulting automaton.
	 */
	private void computeInitState() {
		Set<STATE1> initStates1 = tree1.getInitStates();
		Set<STATE2> initStates2 = tree2.getInitStates();
		for (STATE1 state1 : initStates1) {
			for (STATE2 state2 : initStates2) {
				IntersectState<STATE1, STATE2> newState = new IntersectState<>(state1, state2, 1);
				result.addInitState(newState);
			}
		}
	}

	/**
	 * Compute the final states of the resulting automaton.
	 */
	private void computeFinalState() {
		Set<STATE1> states1 = tree1.getStates();
		Set<STATE2> finalStates2 = tree2.getFinalStates();
		for (STATE1 state1 : states1) {
			for (STATE2 state2 : finalStates2) {
				IntersectState<STATE1, STATE2> newFinalState = new IntersectState<>(state1, state2, 2);
				result.addFinalState(newFinalState);
			}
		}
	}

	/**
	 * Compute the transitions of the resulting automaton.
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
					List<IntersectState<STATE1, STATE2>> destResultMode1 = new ArrayList<>();
					List<IntersectState<STATE1, STATE2>> destResultMode2 = new ArrayList<>();
					for (int i = 0; i < dest1.size(); i++) {
						IntersectState<STATE1, STATE2> newState1 = new IntersectState<>(dest1.get(i), dest2.get(i), 1);
						IntersectState<STATE1, STATE2> newState2 = new IntersectState<>(dest1.get(i), dest2.get(i), 2);
						destResultMode1.add(newState1);
						destResultMode2.add(newState2);
					}
					if (tree1.isFinalState(source1)) {
						IntersectState<STATE1, STATE2> newSource = new IntersectState<>(source1, source2, 1);
						BuchiTreeAutomatonRule<LETTER, IntersectState<STATE1, STATE2>> newRule = new BuchiTreeAutomatonRule<>(
								letter, newSource, destResultMode2);
						result.addRule(newRule);

					} else {
						IntersectState<STATE1, STATE2> newSource = new IntersectState<>(source1, source2, 1);
						BuchiTreeAutomatonRule<LETTER, IntersectState<STATE1, STATE2>> newRule = new BuchiTreeAutomatonRule<>(
								letter, newSource, destResultMode1);
						result.addRule(newRule);

					}
					if (tree2.isFinalState(source2)) {
						IntersectState<STATE1, STATE2> newSource = new IntersectState<>(source1, source2, 2);
						BuchiTreeAutomatonRule<LETTER, IntersectState<STATE1, STATE2>> newRule = new BuchiTreeAutomatonRule<>(
								letter, newSource, destResultMode1);
						result.addRule(newRule);

					} else {
						IntersectState<STATE1, STATE2> newSource = new IntersectState<>(source1, source2, 2);
						BuchiTreeAutomatonRule<LETTER, IntersectState<STATE1, STATE2>> newRule = new BuchiTreeAutomatonRule<>(
								letter, newSource, destResultMode2);
						result.addRule(newRule);

					}
				}
			}

		}

	}

	/**
	 * Return the resulting automaton.
	 * 
	 * @return the intersection of two Buchi automata.
	 */
	public BuchiTreeAutomaton<LETTER, IntersectState<STATE1, STATE2>> computeResult() {
		computeAlphabet();
		computeInitState();
		computeFinalState();
		computeTransitions();
		return result;
	}
}
