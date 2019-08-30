package cgsynt.alternating.parity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cgsynt.tree.parity.IParityState;
import cgsynt.tree.parity.ParityTreeAutomaton;
import cgsynt.tree.parity.ParityTreeAutomatonRule;
import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;

/**
 * This class is deprecated.
 *
 * @param <LETTER>
 * @param <STATE>
 */
public class AlternatingParityAutomaton<LETTER extends IRankedLetter, STATE extends IParityState> {
	private Set<STATE> states;
	private Set<STATE> initialStates;
	private Set<AlternatingParityAutomatonRule<LETTER, STATE>> rules;
	private Map<STATE, Set<AlternatingParityAutomatonRule<LETTER, STATE>>> sourceMap;
	private Map<LETTER, Set<AlternatingParityAutomatonRule<LETTER, STATE>>> letterMap;
	private Map<STATE, Map<LETTER, AlternatingParityAutomatonRule<LETTER, STATE>>> parentMap;

	public AlternatingParityAutomaton(ParityTreeAutomaton<LETTER, STATE> treeAut) {
		this.initialStates = new HashSet<>();
		this.states = new HashSet<>();
		this.initialStates.addAll(treeAut.getInitStates());
		this.states.addAll(treeAut.getStates());
		this.computeRules(treeAut);
	}

	private void computeRules(ParityTreeAutomaton<LETTER, STATE> treeAut) {
		this.rules = new HashSet<>();
		this.sourceMap = new HashMap<>();
		this.parentMap = new HashMap<>();
		this.letterMap = new HashMap<>();
		for (STATE state : treeAut.getStates()) {
			sourceMap.put(state, new HashSet<>());
			parentMap.put(state, new HashMap<>());
			for (LETTER letter : treeAut.getAlphabet()) {
				if (!letterMap.containsKey(letter)) {
					letterMap.put(letter, new HashSet<>());
				}
				if (!treeAut.getParentsMap().get(state).containsKey(letter)) {
					continue;
				}
				Set<Set<STATE>> dests = new HashSet<>();
				for (ParityTreeAutomatonRule<LETTER, STATE> rule : treeAut.getRules()) {
					Set<STATE> dest = new HashSet<>();
					dest.addAll(rule.getDest());
					dests.add(dest);
				}
				AlternatingParityAutomatonRule<LETTER, STATE> rule = new AlternatingParityAutomatonRule<>(state, letter,
						dests);
				rules.add(rule);
				sourceMap.get(state).add(rule);
				parentMap.get(state).put(letter, rule);
				letterMap.get(letter).add(rule);
			}
		}
	}

	public Set<STATE> getStates() {
		return states;
	}

	public Set<STATE> getInitialStates() {
		return initialStates;
	}
}
