package cgsynt.parity.games;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cgsynt.tree.parity.IParityState;
import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;

public class ParityGameEmptinessCheck<LETTER extends IRankedLetter, STATE extends IParityState> {
	
	private ParityGame<LETTER, STATE> parityGame;
	private Map<IParityGameState, IParityGameState> nonEmptyTree;
	private IParityGameState nonEmptyTreeSource;
	private boolean resultComputed;
	private boolean result;

	public ParityGameEmptinessCheck(ParityGame<LETTER, STATE> parityGame) {
		this.parityGame = parityGame;
		this.resultComputed = false;
	}
	
	public void computeResult() {
		if (resultComputed)
			return;
		this.resultComputed = true;
		this.result = true;
		for (IParityGameState state : parityGame.getInitialStates()) {
			Map<IParityGameState, Integer> visitedStates = new HashMap<>();
			List<Integer> visitedRanks = new ArrayList<>();
			Map<IParityGameState, IParityGameState> nonEmptyTree = new HashMap<>();
			if (!isEmptyAdam(visitedStates, visitedRanks, state, nonEmptyTree)) {
				this.nonEmptyTree = nonEmptyTree;
				this.nonEmptyTreeSource = state;
				this.result = false;
			}
		}
	}
	
	public boolean getResult() {
		return result;
	}

	private boolean isEmptyEva(Map<IParityGameState, Integer> visitedStates, List<Integer> visitedRanks,
			IParityGameState state, Map<IParityGameState, IParityGameState> nonEmptyTree) {
		if (visitedStates.containsKey(state)) {
			int maxRank = visitedRanks.get(visitedStates.get(state));
			for (int i = visitedStates.get(state) + 1; i < visitedRanks.size(); i++) {
				if (visitedRanks.get(i) > maxRank) {
					maxRank = visitedRanks.get(i);
				}
			}
			if (maxRank % 2 == 0) {
				return false;
			}
			return true;
		}
		visitedStates.put(state, visitedRanks.size());
		visitedRanks.add(state.getRank());
		boolean isEmpty = false;
		for (IParityGameState nextState : parityGame.getTransitions().get(state)) {
			if (isEmptyAdam(visitedStates, visitedRanks, nextState, nonEmptyTree)) {
				isEmpty = true;
			}
		}
		visitedStates.remove(state);
		visitedRanks.remove(visitedRanks.size() - 1);
		return isEmpty;
	}

	private boolean isEmptyAdam(Map<IParityGameState, Integer> visitedStates, List<Integer> visitedRanks,
			IParityGameState state, Map<IParityGameState, IParityGameState> nonEmptyTree) {
		boolean isEmpty = true;
		for (IParityGameState nextState : parityGame.getTransitions().get(state)) {
			if (!isEmptyEva(visitedStates, visitedRanks, nextState, nonEmptyTree)) {
				isEmpty = false;
				nonEmptyTree.put(state, nextState);
			}
		}
		return isEmpty;
	}
	
	public Map<IParityGameState, IParityGameState> getNonEmptyTree() {
		return nonEmptyTree;
	}

	public IParityGameState getNonEmptyTreeSource() {
		return nonEmptyTreeSource;
	}
}
