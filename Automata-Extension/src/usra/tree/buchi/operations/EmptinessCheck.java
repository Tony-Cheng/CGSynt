package usra.tree.buchi.operations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;
import usra.tree.buchi.BuchiTreeAutomaton;
import usra.tree.buchi.BuchiTreeAutomatonRule;
import usra.tree.buchi.BüchiTreeAutomatonRule;

/**
 * Check whether or not a Buchi tree is empty.
 * 
 * @param <LETTER>
 * @param <STATE>
 */
public class EmptinessCheck<LETTER extends IRankedLetter, STATE> {

	private BuchiTreeAutomaton<LETTER, STATE> mtree;
	private BuchiTreeAutomaton<LETTER, STATE> mtree2;

	private boolean result;
	private boolean resultComputed;

	/**
	 * A set of final states.
	 */
	private Set<STATE> finalStates;

	/**
	 * States that are the roots of good subtree embedded.
	 */
	private Set<STATE> goodStates;

	private Set<STATE> visitedStates;

	/**
	 * Transitions whose destination is a list consists of all acceptance states or
	 * roots of some good subtree embedded.
	 */
	private Stack<BuchiTreeAutomatonRule<LETTER, STATE>> goodTransitions;

	public EmptinessCheck(BuchiTreeAutomaton<LETTER, STATE> mtree) {
		this.mtree = mtree.mkcpy();
		this.mtree2 = mtree.mkcpy();
		finalStates = mtree.getFinalStates();
		goodStates = new HashSet<STATE>();
		goodTransitions = new Stack<>();
		resultComputed = false;
	}

	/**
	 * Add all transitions whose destination is a list consists of all acceptance
	 * states or roots of some good subtree embedded to the stack goodTransitions.
	 */
	private void initializeGoodTransitions() {
		Set<BuchiTreeAutomatonRule<LETTER, STATE>> allRules = mtree.getRules();
		for (BuchiTreeAutomatonRule<LETTER, STATE> rule : allRules) {
			List<STATE> dests = rule.getDest();
			boolean isGoodTransition = true;
			for (STATE dest : dests) {
				if (!finalStates.contains(dest)) {
					isGoodTransition = false;
					break;
				}
			}
			if (isGoodTransition)
				goodTransitions.add(rule);
		}
	}

	/**
	 * Add all states that are the roots of some good subtree embedded to the set
	 * goodStates.
	 */
	private void findAllGoodStates() {
		while (!goodTransitions.isEmpty()) {
			BuchiTreeAutomatonRule<LETTER, STATE> nextRule = goodTransitions.pop();
			STATE src = nextRule.getSource();
			if (!goodStates.contains(src)) {
				goodStates.add(src);
				Collection<BuchiTreeAutomatonRule<LETTER, STATE>> ruleToSrc = mtree.getChildMap().get(src);
				if (ruleToSrc != null && !finalStates.contains(src)) {
					for (BuchiTreeAutomatonRule<LETTER, STATE> rule : ruleToSrc) {
						boolean isGoodTransition = true;
						for (STATE dest : rule.getDest()) {
							if (!finalStates.contains(dest) && !goodStates.contains(dest)) {
								isGoodTransition = false;
								break;
							}
						}
						if (isGoodTransition) {
							goodTransitions.add(rule);
						}
					}
				}
			}
		}
	}

	/**
	 * Return true if a final state is removed and false otherwise. Also, remove
	 * initial or final states that are not the roots of some good subtree embedded.
	 * 
	 * @return
	 */
	private boolean removeNotGoodStates() {
		boolean finalStateRemoved = false;
		Iterator<STATE> iterInitStates = mtree.getInitStates().iterator();
		while (iterInitStates.hasNext()) {
			STATE nextInitState = iterInitStates.next();
			if (!goodStates.contains(nextInitState)) {
				iterInitStates.remove();
				removeState(nextInitState);
			}
		}

		Iterator<STATE> iterFinalStates = finalStates.iterator();
		while (iterFinalStates.hasNext()) {
			STATE nextFinalState = iterFinalStates.next();
			if (!goodStates.contains(nextFinalState)) {
				iterFinalStates.remove();
				removeState(nextFinalState);
				finalStateRemoved = true;
			}
		}
		return finalStateRemoved;
	}

	/**
	 * Remove the state s and its transitions from the tree.
	 * 
	 * @param s
	 */
	private void removeState(STATE s) {
		Collection<BuchiTreeAutomatonRule<LETTER, STATE>> rulesToS = mtree.getChildMap().get(s);
		if (rulesToS != null) {
			for (BuchiTreeAutomatonRule<LETTER, STATE> rule : rulesToS) {
				for (STATE dest : rule.getDest()) {
					if (!dest.equals(s) && mtree.getChildMap().get(dest) != null
							&& mtree.getChildMap().get(dest).contains(rule)) {
						mtree.getChildMap().get(dest).remove(rule);
					}
				}
				if (mtree.getSourceMap().get(rule.getSource()) != null
						&& mtree.getSourceMap().get(rule.getSource()).contains(rule)) {
					mtree.getSourceMap().get(rule.getSource()).remove(rule);
				}
			}
			mtree.getChildMap().remove(s);
		}

		Collection<BuchiTreeAutomatonRule<LETTER, STATE>> rulesFromS = mtree.getSourceMap().get(s);
		if (rulesFromS != null) {
			for (BuchiTreeAutomatonRule<LETTER, STATE> rule : rulesFromS) {
				for (STATE dest : rule.getDest()) {
					if (!dest.equals(s) && mtree.getChildMap().get(dest) != null
							&& mtree.getChildMap().get(dest).contains(rule)) {
						mtree.getChildMap().get(dest).remove(rule);
					}
				}
			}
			mtree.getSourceMap().remove(s);
		}
	}

	/**
	 * Compute whether or not the automaton is empty.
	 */
	public void computeResult() {
		if (resultComputed)
			return;
		initializeGoodTransitions();
		findAllGoodStates();
		while (!goodStates.isEmpty()) {
			if (!removeNotGoodStates()) {
				if (mtree.getInitStates().isEmpty()) {
					resultComputed = true;
					result = true;
					return;
				} else {
					resultComputed = true;
					result = false;
					return;
				}
			} else {
				if (mtree.getInitStates().isEmpty()) {
					resultComputed = true;
					result = true;
					return;
				}
				goodStates.clear();
				goodTransitions.clear();
				initializeGoodTransitions();
				findAllGoodStates();
			}
		}
		resultComputed = true;
		result = true;
		return;
	}

	/**
	 * Return the true if the automaton is empty and false otherwise.
	 * 
	 * @return
	 */
	public boolean getResult() {
		return result;
	}

	/**
	 * Return a set counter examples rooted at s.
	 * 
	 * @param s
	 * @param alphabet
	 * @return
	 */
	private <ALPHA> Set<List<ALPHA>> explore(STATE s, List<ALPHA> alphabet) {
		if (visitedStates.contains(s) || goodStates.contains(s)) {
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
	 * Return a set of counterexamples root at the initial state.
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

	/**
	 * Return a set of counter example root at s.
	 * 
	 * @param s
	 * @return
	 */
	private <ALPHA> Set<List<ALPHA>> explore(STATE s) {
		if (visitedStates.contains(s) || goodStates.contains(s)) {
			return new HashSet<>();
		} else if (!mtree2.getRulesBySource(s).isEmpty()) {
			Set<List<ALPHA>> allS = new HashSet<>();
			allS.add(new ArrayList<>());
			return allS;
		} else {
			Set<List<ALPHA>> allS = new HashSet<>();
			visitedStates.add(s);
			for (BuchiTreeAutomatonRule<LETTER, STATE> rule : mtree2.getRulesBySource(s)) {
				BüchiTreeAutomatonRule<LETTER, STATE, ALPHA> advancedRule = (BüchiTreeAutomatonRule<LETTER, STATE, ALPHA>) rule;
				for (ALPHA alpha : advancedRule.getAlphabet()) {
					STATE q = advancedRule.getState(alpha);
					if (q != null) {
						Set<List<ALPHA>> S = explore(q);
						if (!S.isEmpty()) {
							for (List<ALPHA> list : S) {
								list.add(alpha);
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
	 * Return a set of counter examples rooted at the initial states.
	 * 
	 * @return
	 */
	public <ALPHA> Set<List<ALPHA>> findCounterExamples() {
		visitedStates = new HashSet<>();
		Set<List<ALPHA>> allS = new HashSet<>();
		for (STATE state : mtree2.getInitStates()) {
			allS.addAll(explore(state));
		}
		for (List<ALPHA> list : allS) {
			Collections.reverse(list);
		}
		return allS;
	}
}
