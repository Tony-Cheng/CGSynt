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
		this.states = new HashSet<>();
		this.initialStates = new HashSet<>();
		this.transitions = new HashMap<>();
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


	public Set<IParityGameState> getStates() {
		return states;
	}

	public Set<IParityGameState> getInitialStates() {
		return initialStates;
	}

	public Map<IParityGameState, Set<IParityGameState>> getTransitions() {
		return transitions;
	}

}
