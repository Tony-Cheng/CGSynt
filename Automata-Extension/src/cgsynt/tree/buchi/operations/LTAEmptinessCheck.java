package cgsynt.tree.buchi.operations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import cgsynt.tree.buchi.BuchiTreeAutomaton;
import cgsynt.tree.buchi.BuchiTreeAutomatonRule;
import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;

/**
 * Check whether or not a LTA is empty.
 *
 * @param <LETTER>
 * @param <STATE>
 */
public class LTAEmptinessCheck<LETTER extends IRankedLetter, STATE> {

	private BuchiTreeAutomaton<LETTER, STATE> mtree;
	private BuchiTreeAutomaton<LETTER, STATE> mtree2;
	private boolean resultComputed;
	private boolean result;
	private Set<STATE> visitedLeaves;
	private Set<STATE> visitedStates;

	/**
	 * Create a new LTAEmptinessCheck object.
	 * 
	 * @param mtree
	 *            the LTA
	 */
	public LTAEmptinessCheck(BuchiTreeAutomaton<LETTER, STATE> mtree) {
		this.mtree = mtree.mkcpy();
		this.mtree2 = mtree.mkcpy();
		visitedLeaves = new HashSet<STATE>();
		resultComputed = false;
	}

	/**
	 * Return the set of leaves in the automaton.
	 * 
	 * @return
	 */
	public Set<STATE> computeInitLeaves() {
		Set<STATE> leaves = new HashSet<>();
		for (STATE state : mtree.getStates()) {
			if (mtree.getRulesBySource(state) == null || mtree.getRulesBySource(state).isEmpty()) {
				leaves.add(state);
			}
		}
		return leaves;

	}

	/**
	 * Check if the LTA is empty and store the result in the variable result.
	 */
	public void computeResult() {
		if (resultComputed)
			return;
		Stack<STATE> curLeaves = new Stack<STATE>();
		curLeaves.addAll(computeInitLeaves());
		visitedLeaves.addAll(curLeaves);
		while (curLeaves.size() > 0) {
			STATE next = curLeaves.pop();
			curLeaves.addAll(removeTransitions(next));
		}
		boolean isEmpty = true;
		for (STATE state : mtree.getInitStates()) {
			if (mtree.getRulesBySource(state) != null && !mtree.getRulesBySource(state).isEmpty()) {
				isEmpty = false;
			}
		}
		result = isEmpty;
		resultComputed = true;
		return;
	}

	/**
	 * Remove all transitions that have state as a destination.
	 * 
	 * @param state
	 * @return
	 */
	private Set<STATE> removeTransitions(STATE state) {
		Set<STATE> newLeaves = new HashSet<STATE>();
		if (mtree.getChildMap().get(state) != null)
			for (BuchiTreeAutomatonRule<LETTER, STATE> rule : mtree.getChildMap().get(state)) {
				if (mtree.getSourceMap().get(rule.getSource()) != null
						&& mtree.getSourceMap().get(rule.getSource()).contains(rule)) {
					mtree.getSourceMap().get(rule.getSource()).remove(rule);
					if (mtree.getSourceMap().get(rule.getSource()).isEmpty()
							&& !visitedLeaves.contains(rule.getSource()) && !newLeaves.contains(rule.getSource())) {
						newLeaves.add(rule.getSource());
						visitedLeaves.add(rule.getSource());
					}
				}
			}
		return newLeaves;

	}

	/**
	 * Return true if the LTA is empty and false otherwise.
	 * 
	 * @return
	 */
	public boolean getResult() {
		return result;
	}

	/**
	 * Return a counterexample for the subtree rooted at s.
	 * 
	 * @param s
	 * @param alphabet
	 * @return
	 */
	private <ALPHA> Set<List<ALPHA>> explore(STATE s, List<ALPHA> alphabet) {
		if (visitedStates.contains(s) || (mtree.getRulesBySource(s) != null && !mtree.getRulesBySource(s).isEmpty())) {
			return new HashSet<>();
		} else if (mtree2.getRulesBySource(s) == null) {
			Set<List<ALPHA>> allS = new HashSet<>();
			allS.add(new ArrayList<>());
			return allS;
		} else {
			Set<List<ALPHA>> allS = new HashSet<>();
			visitedStates.add(s);
			for (BuchiTreeAutomatonRule<LETTER, STATE> rule : mtree2.getRulesBySource(s)) {
				List<STATE> states = rule.getDest();
				for (int i = 0; i < rule.getArity(); i++) {
					STATE q = states.get(i);
					if (q != null) {
						Set<List<ALPHA>> S = explore(q, alphabet);
						if (!S.isEmpty()) {
							for (List<ALPHA> list : S) {
								list.add(alphabet.get(i));
							}
							allS.addAll(S);
							break;
						}
					}
				}
			}
			visitedStates.remove(s);
			return allS;
		}
	}

	/**
	 * Return a counterexample.
	 * 
	 * @param alphabet
	 * @return
	 */
	public <ALPHA> Set<List<ALPHA>> findCounterExamples(List<ALPHA> alphabet) {
		visitedStates = new HashSet<>();
		Set<List<ALPHA>> allS = new HashSet<>();
		for (STATE state : mtree2.getInitStates()) {
			allS.addAll(explore(state, alphabet));
		}
		for (List<ALPHA> list : allS) {
			Collections.reverse(list);
		}
		return allS;
	}

}
