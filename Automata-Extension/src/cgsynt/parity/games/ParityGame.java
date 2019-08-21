package cgsynt.parity.games;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cgsynt.tree.parity.IParityState;
import cgsynt.tree.parity.ParityTreeAutomaton;
import cgsynt.tree.parity.ParityTreeAutomatonRule;
import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;

public class ParityGame<LETTER extends IRankedLetter, STATE extends IParityState> {

	private Set<IParityGameState> states;
	private Set<IParityGameState> initialStates;
	private Map<IParityGameState, Set<IParityGameState>> transitions;

	public ParityGame(ParityTreeAutomaton<LETTER, STATE> aut) {
		this.addAdamStates(aut);
		this.addEvaStates(aut);
		this.addTransitions(aut);
	}

	private void addAdamStates(ParityTreeAutomaton<LETTER, STATE> aut) {
		for (STATE state : aut.getStates()) {
			states.add(new AdamState<>(state));
		}
		for (STATE state : aut.getInitStates()) {
			initialStates.add(new AdamState<>(state));
		}
	}

	private void addEvaStates(ParityTreeAutomaton<LETTER, STATE> aut) {
		for (ParityTreeAutomatonRule<LETTER, STATE> rule : aut.getRules()) {
			states.add(new EvaState<>(rule));
		}
	}

	private void addTransitions(ParityTreeAutomaton<LETTER, STATE> aut) {
		for (IParityGameState state : states) {
			transitions.put(state, new HashSet<>());
			if (state.isEva()) {
				this.addEvaTransitions(state);
			} else {
				this.addAdamTransitions(aut, state);
			}
		}
	}

	private void addEvaTransitions(IParityGameState state) {
		EvaState<LETTER, STATE> evaState = (EvaState) state;
		for (STATE dest : evaState.getRule().getDest()) {
			transitions.get(evaState).add(new AdamState<STATE>(dest));
		}
	}

	private void addAdamTransitions(ParityTreeAutomaton<LETTER, STATE> aut, IParityGameState state) {
		AdamState<STATE> adamState = (AdamState) state;
		for (ParityTreeAutomatonRule<LETTER, STATE> rule : aut.getRulesBySource(adamState.getState())) {
			transitions.get(adamState).add(new EvaState<LETTER, STATE>(rule));
		}
	}

	public boolean isEmpty() {
		for (IParityGameState state : initialStates) {
			Map<IParityGameState, Integer> visitedStates = new HashMap<>();
			List<Integer> visitedRanks = new ArrayList<>();
			if (!isEmptyAdam(visitedStates, visitedRanks, state)) {
				return false;
			}
		}
		return true;
	}

	private boolean isEmptyEva(Map<IParityGameState, Integer> visitedStates, List<Integer> visitedRanks,
			IParityGameState state) {
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
		boolean isEmpty = true;
		for (IParityGameState nextState : transitions.get(state)) {
			if (!isEmptyAdam(visitedStates, visitedRanks, nextState)) {
				isEmpty = false;
			}
		}
		visitedStates.remove(state);
		visitedRanks.remove(visitedRanks.size());
		return isEmpty;
	}

	private boolean isEmptyAdam(Map<IParityGameState, Integer> visitedStates, List<Integer> visitedRanks,
			IParityGameState state) {
		boolean isEmpty = false;
		for (IParityGameState nextState : transitions.get(state)) {
			if (isEmptyEva(visitedStates, visitedRanks, nextState)) {
				isEmpty = true;
			}
		}
		return isEmpty;
	}
}
