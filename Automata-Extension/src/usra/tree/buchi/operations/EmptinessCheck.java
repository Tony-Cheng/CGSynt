package usra.tree.buchi.operations;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;
import usra.tree.buchi.BuchiTreeAutomaton;
import usra.tree.buchi.BuchiTreeAutomatonRule;

public class EmptinessCheck<LETTER extends IRankedLetter, STATE> {

	private BuchiTreeAutomaton<LETTER, STATE> mtree;
	private Set<STATE> finalStates;
	/**
	 * States that are the roots of good subtree embedded.
	 */
	private Set<STATE> goodStates;
	/**
	 * Transitions whose destination is a list consists of all acceptance states or
	 * roots of some good subtree embedded.
	 */
	private Stack<BuchiTreeAutomatonRule<LETTER, STATE>> goodTransitions;

	public EmptinessCheck(BuchiTreeAutomaton<LETTER, STATE> mtree) {
		this.mtree = mtree.mkcpy();
		finalStates = mtree.getFinalStates();
		goodStates = new HashSet<STATE>();
		goodTransitions = new Stack<>();
	}

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
	 * Return true if a final state is removed and false otherwise.
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

	public boolean computeResult() {
		initializeGoodTransitions();
		findAllGoodStates();
		while (!goodStates.isEmpty()) {
			if (!removeNotGoodStates()) {
				if (mtree.getInitStates().isEmpty()) {
					return true;
				} else {
					return false;
				}
			} else {
				if (mtree.getInitStates().isEmpty()) {
					return true;
				}
				goodStates.clear();
				goodTransitions.clear();
				initializeGoodTransitions();
				findAllGoodStates();
			}
		}
		return true;
	}
}
