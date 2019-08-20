package cgsynt.alternating.parity.operations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cgsynt.alternating.parity.AlternatingParityAutomaton;
import cgsynt.tree.parity.IParityState;
import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;

public class AlternatingParityEmptinessCheck<LETTER extends IRankedLetter, STATE extends IParityState> {

	private AlternatingParityAutomaton<LETTER, STATE> aut;
	private boolean result;
	private boolean resultComputed;

	public AlternatingParityEmptinessCheck(AlternatingParityAutomaton<LETTER, STATE> aut) {
		this.aut = aut;
		this.resultComputed = false;
	}

	public boolean getResult() {
		if (resultComputed) {
			return result;
		}
		return false;
	}

	public void computeResult() {
		if (resultComputed)
			return;
		List<Set<STATE>> finalList = computeFinalList();
		result = false; 
		Set<STATE> acc = reduce(finalList, new HashSet<>(), new HashSet<>());
		for (STATE state : aut.getInitialStates()) {
			if (acc.contains(state)) {
				result = true;
			}
		}
		this.resultComputed = true;
	}
	
	private List<Set<STATE>> computeFinalList() {
		List<Set<STATE>> finalList = new ArrayList<>();
		int maxRank = 0;
		for (STATE state : aut.getStates()) {
			if (state.getRank() > maxRank) {
				maxRank = state.getRank();
			}
		}
		if (maxRank % 2 == 0) {
			maxRank += 2;
		}
		else {
			maxRank += 1;
		}
		for (int i = 0; i < maxRank; i++) {
			finalList.add(new HashSet<>());
		}
		for (STATE state : aut.getStates()) {
			int rank = maxRank - state.getRank() - 1;
			finalList.get(rank).add(state);
		}
		return finalList;
	}

	private Set<STATE> reduce(List<Set<STATE>> finalList, Set<STATE> visitedStates, Set<STATE> avoidStates) {
		if (finalList.isEmpty()) {
			return visitedStates;
		}
		Set<STATE> oldY = new HashSet<>();
		Set<STATE> newY = dual(remove(finalList), union(visitedStates, oldY),
				subtract(union(avoidStates, finalList.get(0)), oldY));
		while (!newY.equals(oldY)) {
			oldY = newY;
			newY = dual(remove(finalList), union(visitedStates, oldY),
					subtract(union(avoidStates, finalList.get(0)), oldY));
		}
		return newY;
	}

	private Set<STATE> dual(List<Set<STATE>> finalList, Set<STATE> visitedStates, Set<STATE> avoidStates) {
		Set<STATE> result = new HashSet<>();
		result.addAll(this.aut.getStates());
		result.removeAll(reduce(finalList, avoidStates, visitedStates));
		return result;
	}

	private Set<STATE> union(Set<STATE> A, Set<STATE> B) {
		Set<STATE> union = new HashSet<>();
		union.addAll(A);
		union.addAll(B);
		return union;
	}

	private Set<STATE> subtract(Set<STATE> minuEnd, Set<STATE> subtrahend) {
		Set<STATE> difference = new HashSet<>();
		difference.addAll(minuEnd);
		difference.removeAll(subtrahend);
		return difference;
	}

	private List<Set<STATE>> remove(List<Set<STATE>> A) {
		List<Set<STATE>> copy = new ArrayList<>();
		copy.addAll(A);
		copy.remove(0);
		return copy;
	}

}
