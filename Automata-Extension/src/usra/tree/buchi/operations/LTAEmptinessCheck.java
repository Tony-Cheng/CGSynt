package usra.tree.buchi.operations;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;
import usra.tree.buchi.BuchiTreeAutomaton;
import usra.tree.buchi.BuchiTreeAutomatonRule;

public class LTAEmptinessCheck<LETTER extends IRankedLetter, STATE> {

	private BuchiTreeAutomaton<LETTER, STATE> mtree;
	private boolean resultComputed;
	private boolean result;
	private Set<STATE> visitedLeaves;

	public LTAEmptinessCheck(BuchiTreeAutomaton<LETTER, STATE> mtree) {
		this.mtree = mtree.mkcpy();
		visitedLeaves = new HashSet<STATE>();
		resultComputed = false;
	}

	public Set<STATE> computeInitLeaves() {
		Set<STATE> leaves = new HashSet<>();
		for (STATE state : mtree.getStates()) {
			if (mtree.getRulesBySource(state) == null || mtree.getRulesBySource(state).isEmpty()) {
				leaves.add(state);
			}
		}
		return leaves;

	}

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

	public boolean getResult() {
		return result;
	}

}
