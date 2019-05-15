package tree.buchi.operations;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;
import tree.buchi.BuchiTreeAutomaton;
import tree.buchi.BuchiTreeAutomatonRule;

public class EmptinessCheck<LETTER extends IRankedLetter, STATE> {

	private BuchiTreeAutomaton<LETTER, STATE> mtree;
	private Map<STATE, Boolean> isValidTree;
	private Set<STATE> visited;
	private Set<STATE> toDelete;

	public EmptinessCheck(BuchiTreeAutomaton<LETTER, STATE> mtree) {
		this.mtree = mtree.mkcpy();
		isValidTree = new HashMap<>();
		visited = new HashSet<>();
		toDelete = new HashSet<>();
	}

	/**
	 * Return true if the tree rooted at curState is a good subtree embedded and
	 * false otherwise.
	 * 
	 * @param starting
	 *            Whether or not this is the root of the good subtree embedded that
	 *            we are checking.
	 * @param curState
	 *            the current state in the good subtree embedded.
	 * @return
	 */
	private boolean eliminateNode(boolean starting, STATE curState) {
		if (starting == false && mtree.isFinalState(curState)) {
			return true;
		}
		if (isValidTree.containsKey(curState)) {
			return isValidTree.get(curState);
		}
		if (visited.contains(curState)) {
			isValidTree.put(curState, false);
			return false;
		}
		Collection<BuchiTreeAutomatonRule<LETTER, STATE>> rules = mtree.getRulesBySource(curState);
		if (rules == null || rules.isEmpty()) {
			isValidTree.put(curState, false);
			toDelete.add(curState);
			return false;
		}
		visited.add(curState);
		for (BuchiTreeAutomatonRule<LETTER, STATE> rule : rules) {
			boolean validTree = true;
			for (STATE dest : rule.getDest()) {
				if (!eliminateNode(false, dest)) {
					validTree = false;
				}
			}
			if (validTree) {
				visited.remove(curState);
				isValidTree.put(curState, true);
				return true;
			}
		}
		visited.remove(curState);
		isValidTree.put(curState, false);
		toDelete.add(curState);
		return false;
	}

	public boolean getResult() {
		Set<STATE> allStates = mtree.getStates();
		Set<STATE> initialStates = mtree.getInitStates();
		Set<STATE> finalStates = mtree.getFinalStates();
		Map<STATE, Collection<BuchiTreeAutomatonRule<LETTER, STATE>>> sourceMap = mtree.getSourceMap();
		Map<STATE, Collection<BuchiTreeAutomatonRule<LETTER, STATE>>> childMap = mtree.getChildMap();
		while (!allStates.isEmpty()) {
			boolean allValidTree = true;
			for (STATE next : allStates) {
				if (!eliminateNode(true, next)) {
					allValidTree = false;
					break;
				}
			}
			if (allValidTree) {
				if (initialStates.isEmpty()) {
					return true;
				}
				return false;
			} else {
				for (STATE next : toDelete) {
					if (initialStates.contains(next)) {
						initialStates.remove(next);
					}
					if (finalStates.contains(next)) {
						finalStates.remove(next);
					}
					if (sourceMap.containsKey(next)) {
						sourceMap.remove(next);
					}
					if (allStates.contains(next)) {
						allStates.remove(next);
					}
					if (childMap.containsKey(next)) {
						for (BuchiTreeAutomatonRule<LETTER, STATE> rule : childMap.get(next)) {
							STATE source = rule.getSource();
							if (sourceMap.containsKey(source) && sourceMap.get(source).contains(rule)) {
								sourceMap.get(source).remove(rule);
							}
						}
					}
				}
				toDelete.clear();
				visited.clear();
				isValidTree.clear();
			}
		}
		return true;
	}
}
