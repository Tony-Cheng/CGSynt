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
	private Map<IParityGameState, Set<IParityGameState>> inverseTransitions;

	public ParityGame(ParityTreeAutomaton<LETTER, STATE> aut) {
		this.states = new HashSet<>();
		this.initialStates = new HashSet<>();
		this.transitions = new HashMap<>();
		this.inverseTransitions = new HashMap<>();
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
			inverseTransitions.put(state, new HashSet<>());
		}
		for (IParityGameState state : states) {
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
			AdamState<STATE> adamDest = new AdamState<STATE>(dest);
			transitions.get(evaState).add(adamDest);
			inverseTransitions.get(adamDest).add(evaState);
		}
	}

	private void addAdamTransitions(ParityTreeAutomaton<LETTER, STATE> aut, IParityGameState state) {
		AdamState<STATE> adamState = (AdamState) state;
		for (ParityTreeAutomatonRule<LETTER, STATE> rule : aut.getRulesBySource(adamState.getState())) {
			EvaState<LETTER, STATE> evaState = new EvaState<LETTER, STATE>(rule);
			transitions.get(adamState).add(evaState);
			inverseTransitions.get(evaState).add(adamState);
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

	public Map<IParityGameState, Set<IParityGameState>> getInverseTransitions() {
		return inverseTransitions;
	}

	private ParityGame() {
		this.states = new HashSet<>();
		this.initialStates = new HashSet<>();
		this.transitions = new HashMap<>();
		this.inverseTransitions = new HashMap<>();
	}

	public ParityGame<LETTER, STATE> copy() {
		ParityGame<LETTER, STATE> parityGame = new ParityGame<>();
		parityGame.states.addAll(this.states);
		parityGame.initialStates.addAll(this.initialStates);
		for (IParityGameState state : this.transitions.keySet()) {
			parityGame.transitions.put(state, new HashSet<>());
			parityGame.transitions.get(state).addAll(transitions.get(state));
		}
		for (IParityGameState state : this.inverseTransitions.keySet()) {
			parityGame.inverseTransitions.put(state, new HashSet<>());
			parityGame.inverseTransitions.get(state).addAll(inverseTransitions.get(state));
		}
		return parityGame;
	}

	public void removeStates(Set<IParityGameState> states) {
		this.states.removeAll(states);
		this.initialStates.removeAll(states);
		Set<IParityGameState> dests = new HashSet<>();
		Set<IParityGameState> sources = new HashSet<>();
		for (IParityGameState state : states) {
			dests.addAll(transitions.get(state));
			sources.addAll(inverseTransitions.get(state));
		}
		for (IParityGameState state : dests) {
			inverseTransitions.get(state).removeAll(states);
		}
		for (IParityGameState state : sources) {
			transitions.get(state).removeAll(states);
		}
		for (IParityGameState state : states) {
			transitions.remove(state);
			inverseTransitions.remove(state);
		}
	}

}
