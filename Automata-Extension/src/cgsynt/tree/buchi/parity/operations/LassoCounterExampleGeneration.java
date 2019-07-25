package cgsynt.tree.buchi.parity.operations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cgsynt.tree.buchi.parity.BuchiParityIntersectAutomaton;
import cgsynt.tree.buchi.parity.BuchiParityIntersectState;
import cgsynt.tree.parity.IParityState;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.buchi.NestedLassoRun;
import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;

public class LassoCounterExampleGeneration<LETTER extends IRankedLetter, STATE1, STATE2 extends IParityState> {

	private BuchiParityIntersectAutomaton<LETTER, STATE1, STATE2> tree;
	int maxLength;
	private Set<BuchiParityIntersectState<STATE1, STATE2>> visitedStates;
	private List<NestedLassoRun<LETTER, BuchiParityIntersectState<STATE1, STATE2>>> result;
	private boolean resultComputed;
	
	public LassoCounterExampleGeneration(BuchiParityIntersectAutomaton<LETTER, STATE1, STATE2> tree, int maxLength) {
		this.tree = tree;
		this.maxLength = maxLength;
		this.visitedStates = new HashSet<>();
	}
	
	public void computeResult() {
		if (resultComputed)
			return;
		
	}
	
	private List<List<Integer>> generateCounterexamples(BuchiParityIntersectState<STATE1, STATE2> state ,int len) {
		if (len == 0) {
			return null;
		}
		return null;
	}
	
}
