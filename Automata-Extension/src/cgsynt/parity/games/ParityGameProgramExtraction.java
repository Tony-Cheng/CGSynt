package cgsynt.parity.games;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cgsynt.tree.parity.IParityState;
import cgsynt.tree.parity.ParityTreeAutomatonRule;
import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;

/**
 * Print the correct program found in the operation ParityGameEmptinessCheck.
 *
 * @param <LETTER>
 */
public class ParityGameProgramExtraction<LETTER extends IRankedLetter> {

	private final IParityGameState source;
	private final Map<IParityGameState, IParityGameState> nonEmptyTree;
	private List<String> transitionAlphabet;
	private int id;
	private Map<IParityGameState, Integer> idMap;
	private Set<IParityGameState> visited;

	public ParityGameProgramExtraction(IParityGameState source, Map<IParityGameState, IParityGameState> nonEmptyTree,
			List<String> transitionAlphabet) {
		this.source = source;
		this.nonEmptyTree = nonEmptyTree;
		this.transitionAlphabet = transitionAlphabet;
	}

	public void printProgram() {
		this.id = 0;
		this.idMap = new HashMap<>();
		this.visited = new HashSet<>();
		printProgramAdam(this.source);
		printStates();
	}
	
	private void printStates() {
		for (IParityGameState state : idMap.keySet()) {
			EvaState<LETTER, IParityState> evaState = (EvaState<LETTER, IParityState>) state;
			System.out.println("state " + idMap.get(state) + ": " + evaState.getRule().getSource());
		}
	}

	 private IParityGameState printProgramAdam(IParityGameState state) {
	 printProgramEva(nonEmptyTree.get(state));
	 return nonEmptyTree.get(state);
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
		System.out.println("state " + idMap.get(state));
		for (int i = 0; i < transitionAlphabet.size(); i++) {
			System.out.println("	" + transitionAlphabet.get(i) + ": state " + idMap.get(destStates.get(i)));
		}

	}

}
