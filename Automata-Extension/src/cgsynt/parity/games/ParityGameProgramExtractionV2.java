package cgsynt.parity.games;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cgsynt.tree.parity.IParityState;
import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;

/**
 * Print the correct program found in QuasiTimeEmptinessCheckV2.
 *
 * @param <LETTER>
 * @param <STATE>
 */
public class ParityGameProgramExtractionV2<LETTER extends IRankedLetter, STATE extends IParityState> {

	private final IParityGameState source;
	private final Map<IParityGameState, IParityGameState> nonEmptyProof;
	private List<String> transitionAlphabet;
	private int id;
	private Map<IParityGameState, Integer> idMap;
	private Set<IParityGameState> visited;
	private ParityGame<LETTER, STATE> nonEmptyParityGame;
	private Set<IParityGameState> deadStates;

	/**
	 * Initialize the program extraction class.
	 * 
	 * @param source
	 *            The source state of the winning path in the parity game.
	 * @param nonEmptyProof
	 *            A winning path in the parity game.
	 * @param transitionAlphabet
	 *            A list of string representations of program statements.
	 * @param nonEmptyParityGame
	 *            A parity game containing a winning move.
	 * @param deadStates
	 *            A set of states in the parity game representing the dead states in
	 *            the program automaton.
	 */
	public ParityGameProgramExtractionV2(IParityGameState source, Map<IParityGameState, IParityGameState> nonEmptyProof,
			List<String> transitionAlphabet, ParityGame<LETTER, STATE> nonEmptyParityGame,
			Set<IParityGameState> deadStates) {
		this.source = source;
		this.nonEmptyProof = nonEmptyProof;
		this.transitionAlphabet = transitionAlphabet;
		this.nonEmptyParityGame = nonEmptyParityGame;
		this.deadStates = deadStates;
	}

	/**
	 * Print the correct program.
	 */
	public void printProgram() {
		this.id = 0;
		this.idMap = new HashMap<>();
		this.visited = new HashSet<>();
		System.out.println("Source: state " + idMap.get(printProgramAdam(this.source)));
		// printStates();
	}

	private void printStates() {
		for (IParityGameState state : idMap.keySet()) {
			EvaState<LETTER, IParityState> evaState = (EvaState<LETTER, IParityState>) state;
			System.out.println("state " + idMap.get(state) + ": " + evaState.getRule().getSource());
		}
	}

	private IParityGameState printProgramAdam(IParityGameState state) {
		if (!nonEmptyProof.containsKey(state) || nonEmptyProof.get(state) == null) {
			for (IParityGameState next : nonEmptyParityGame.getTransitions().get(state)) {
				if (idMap.containsKey(next) && deadStates.contains(next))
					return next;
			}
			for (IParityGameState next : nonEmptyParityGame.getTransitions().get(state)) {
				if (deadStates.contains(next)) {
					printProgramEva(next);
					return next;
				}
			}
			for (IParityGameState next : nonEmptyParityGame.getTransitions().get(state)) {
				if (idMap.containsKey(next))
					return next;
			}
			for (IParityGameState next : nonEmptyParityGame.getTransitions().get(state)) {
				printProgramEva(next);
				return next;
			}
		} else {
			printProgramEva(nonEmptyProof.get(state));
			return nonEmptyProof.get(state);
		}
		return null;
	}

	private void printProgramEva(IParityGameState state) {
		if (visited.contains(state)) {
			return;
		}
		visited.add(state);
		EvaState<LETTER, IParityState> evaState = (EvaState) state;
		idMap.put(state, id);
		id++;
		List<IParityGameState> destStates = new ArrayList<>();
		for (int i = 0; i < evaState.getRule().getDest().size(); i++) {
			destStates.add(printProgramAdam(new AdamState<>(evaState.getRule().getDest().get(i))));
		}
		if (!deadStates.contains(state)) {
			System.out.println("state " + idMap.get(state));
			for (int i = 0; i < transitionAlphabet.size(); i++) {
				if (!deadStates.contains(destStates.get(i))) {
					System.out.println("	" + transitionAlphabet.get(i) + ": state " + idMap.get(destStates.get(i)));
				}
			}
		}

	}

}
