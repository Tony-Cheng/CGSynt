package cgsynt.tree.parity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;

import cgsynt.RepCondenser;
import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;

/**
 * A parity tree automaton.
 *
 * @param <LETTER>
 * @param <STATE>
 */
public class ParityTreeAutomaton<LETTER extends IRankedLetter, STATE extends IParityState> {

	private final Set<LETTER> mAlphabet;

	private final Map<List<STATE>, Map<LETTER, Collection<ParityTreeAutomatonRule<LETTER, STATE>>>> mChildrenMap;

	private final Map<LETTER, Collection<ParityTreeAutomatonRule<LETTER, STATE>>> mLettersMap;

	private final Map<STATE, Map<LETTER, Collection<ParityTreeAutomatonRule<LETTER, STATE>>>> mParentsMap;

	private final Set<ParityTreeAutomatonRule<LETTER, STATE>> mRules;

	private final Map<STATE, Collection<ParityTreeAutomatonRule<LETTER, STATE>>> mSourceMap;

	private final Map<STATE, Collection<ParityTreeAutomatonRule<LETTER, STATE>>> mChildMap;

	private final Set<STATE> mStates;

	private final Set<STATE> mInitStates;

	private final int rank;

	public ParityTreeAutomaton(int rank) {
		mChildrenMap = new HashMap<>();
		mParentsMap = new HashMap<>();
		mAlphabet = new HashSet<>();
		mLettersMap = new HashMap<>();
		mSourceMap = new HashMap<>();
		mRules = new HashSet<>();
		mStates = new HashSet<>();
		mInitStates = new HashSet<>();
		this.rank = rank;
		mChildMap = new HashMap<>();
	}

	/**
	 * Return the alphabet.
	 * 
	 * @return
	 */
	public Set<LETTER> getAlphabet() {
		return mAlphabet;
	}

	/**
	 * Return all the states.
	 * 
	 * @return
	 */
	public Set<STATE> getStates() {
		return mStates;
	}

	/**
	 * Return the number of states.
	 * 
	 * @return
	 */
	public int size() {
		return mStates.size();
	}

	/**
	 * Add rules to this automaton.
	 * 
	 * @param rules
	 */
	public void addRules(ParityTreeAutomatonRule<LETTER, STATE>... rules) {
		for (ParityTreeAutomatonRule<LETTER, STATE> rule : rules) {
			addRule(rule);
		}
	}

	/**
	 * Add a rule to this automaton.
	 * 
	 * @param rule
	 */
	public void addRule(ParityTreeAutomatonRule<LETTER, STATE> rule) {
		if (mRules.contains(rule)) {
			return;
		}
		mRules.add(rule);
		final LETTER letter = rule.getLetter();
		final List<STATE> dest = rule.getDest();
		final STATE src = rule.getSource();

		assert letter.getRank() == rank;
		assert letter.getRank() == rule.getDest().size();
		if (letter.getRank() != rule.getDest().size()) {
			System.err.println(letter + " " + rule);
		}
		addLetter(letter);
		addState(src);
		for (final STATE state : dest) {
			addState(state);
		}

		// children
		if (!mChildrenMap.containsKey(dest)) {
			mChildrenMap.put(dest, new HashMap<LETTER, Collection<ParityTreeAutomatonRule<LETTER, STATE>>>());
		}
		final Map<LETTER, Collection<ParityTreeAutomatonRule<LETTER, STATE>>> childLetter = mChildrenMap.get(dest);
		if (!childLetter.containsKey(letter)) {
			childLetter.put(letter, new HashSet<ParityTreeAutomatonRule<LETTER, STATE>>());
		}
		final HashSet<ParityTreeAutomatonRule<LETTER, STATE>> children = (HashSet<ParityTreeAutomatonRule<LETTER, STATE>>) childLetter
				.get(letter);
		children.add(rule);

		// parents
		final Map<LETTER, Collection<ParityTreeAutomatonRule<LETTER, STATE>>> parentLetter = mParentsMap.get(src);
		if (!parentLetter.containsKey(letter)) {
			parentLetter.put(letter, new HashSet<ParityTreeAutomatonRule<LETTER, STATE>>());
		}
		final Set<ParityTreeAutomatonRule<LETTER, STATE>> parents = (Set<ParityTreeAutomatonRule<LETTER, STATE>>) parentLetter
				.get(letter);
		parents.add(rule);

		// LettersMap
		if (!mLettersMap.containsKey(letter)) {
			mLettersMap.put(letter, new HashSet<>());
		}
		HashSet<ParityTreeAutomatonRule<LETTER, STATE>> rulesByLetter = (HashSet<ParityTreeAutomatonRule<LETTER, STATE>>) mLettersMap
				.get(letter);
		rulesByLetter.add(rule);

		// SourceRules

		final HashSet<ParityTreeAutomatonRule<LETTER, STATE>> rulesBySource = (HashSet<ParityTreeAutomatonRule<LETTER, STATE>>) mSourceMap
				.get(src);
		rulesBySource.add(rule);

		// mRulesContainChildren
		for (STATE child : rule.getDest()) {
			addState(child);
			mChildMap.get(child).add(rule);
		}

	}

	/**
	 * Add a state to this automaton.
	 * 
	 * @param state
	 *            a state in this automaton
	 */
	public void addState(STATE state) {
		if (!mStates.contains(state)) {
			mStates.add(state);
			if (!mParentsMap.containsKey(state)) {
				mParentsMap.put(state, new HashMap<LETTER, Collection<ParityTreeAutomatonRule<LETTER, STATE>>>());
			}
			if (!mSourceMap.containsKey(state)) {
				mSourceMap.put(state, new HashSet<>());
			}
			if (!mChildMap.containsKey(state)) {
				mChildMap.put(state, new HashSet<>());
			}
		}
	}

	/**
	 * Add a letter to the alphabet.
	 * 
	 * @param letter
	 *            a letter in the alphabet.
	 */
	public void addLetter(LETTER letter) {
		mAlphabet.add(letter);
	}

	public int getNumberOfRules() {
		return mRules.size();
	}

	public Iterable<ParityTreeAutomatonRule<LETTER, STATE>> getSuccessors(LETTER letter) {
		return mLettersMap.get(letter);
	}

	/**
	 * Add a state to the set of initial states.
	 * 
	 * @param state
	 *            an initial state
	 */
	public void addInitState(final STATE state) {
		mInitStates.add(state);
		addState(state);
	}

	/**
	 * Return the initial states.
	 * 
	 * @return the initial states
	 */
	public Set<STATE> getInitStates() {
		return mInitStates;
	}

	/**
	 * Get the set of transition rules for this automaton
	 * 
	 * @return A set of transitions
	 */
	public Set<ParityTreeAutomatonRule<LETTER, STATE>> getRules() {
		return mRules;
	}

	/**
	 * Get the rank of this tree automaton.
	 * 
	 * @return The rank of the automaton
	 */
	public int getRank() {
		return rank;
	}

	/**
	 * Get rule by source.
	 * 
	 * @param
	 * @return
	 */
	public Collection<ParityTreeAutomatonRule<LETTER, STATE>> getRulesBySource(final STATE src) {
		return mSourceMap.get(src);
	}

	/**
	 * Get the map that maps automaton states to the collection of destination
	 * states reachable from that state.
	 * 
	 * @return A source map
	 */
	public Map<STATE, Collection<ParityTreeAutomatonRule<LETTER, STATE>>> getSourceMap() {
		return mSourceMap;
	}

	/**
	 * Get the map that maps automaton states to the collection of source states for
	 * a specified state.
	 * 
	 * @return A child map
	 */
	public Map<STATE, Collection<ParityTreeAutomatonRule<LETTER, STATE>>> getChildMap() {
		return mChildMap;
	}

	/**
	 * Make a copy of this automaton.
	 * 
	 * @return
	 */
	public ParityTreeAutomaton<LETTER, STATE> mkcpy() {
		ParityTreeAutomaton<LETTER, STATE> copy = new ParityTreeAutomaton<>(rank);
		copy.mChildrenMap.putAll(mChildrenMap);
		copy.mParentsMap.putAll(mParentsMap);
		copy.mAlphabet.addAll(mAlphabet);
		copy.mLettersMap.putAll(mLettersMap);
		for (STATE state : mSourceMap.keySet()) {
			copy.mSourceMap.put(state, new HashSet<>());
			copy.mSourceMap.get(state).addAll(mSourceMap.get(state));
		}
		copy.mRules.addAll(mRules);
		copy.mStates.addAll(mStates);
		copy.mInitStates.addAll(mInitStates);
		copy.mChildMap.putAll(mChildMap);
		return copy;

	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		RepCondenser<STATE> condenser = new RepCondenser<>(new ArrayList<>(this.mStates));
		Map<String, String> map = condenser.getMapping();
		
		result.append("States:\n");
		for (STATE state : mStates) {
			result.append(map.get(state.toString()));
			result.append("\n");
		}
		result.append("\n");

		result.append("Initial States:\n");
		for (STATE state : mInitStates) {
			result.append(map.get(state.toString()));
			result.append("\n");
		}
		result.append("\n");

		result.append("Alphabet:\n");
		for (LETTER letter : mAlphabet) {
			result.append(letter.toString());
			result.append("\n");
		}
		result.append("\n");

		result.append("Transitions:\n");
		for (ParityTreeAutomatonRule<LETTER, STATE> rule : mRules) {
			String dests = "";
			for (int i = 0; i < rule.getDest().size(); i++) {
				dests += "(" + map.get(rule.getDest().get(i).toString()) + ": " + rule.getDest().get(i).getRank() + ") ";
			}
		
			result.append("((" + map.get(rule.getSource().toString()) + ": " + rule.getSource().getRank() + ") | " + rule.getLetter().toString() + " | " + dests + ")");
			result.append("\n");
		}
		result.append("\n");

		List<String> values = new ArrayList<>(map.values());
		Collections.sort(values);
		result.append("Mappings:\n");
		for (String value : values) {
			String matchingKey = "";
		    for (Entry<String, String> entry : map.entrySet()) {
		        if (Objects.equals(value, entry.getValue())) {
		        	matchingKey = entry.getKey();
		        }
		    }
		    
		    result.append(value + " = " + matchingKey + "\n");
		}
		
		result.append("\n");
		
		return result.toString();
	}

	/**
	 * Return true if automaton contains this state and false otherwise.
	 * 
	 * @param state
	 * @return
	 */
	public boolean contains(STATE state) {
		return mStates.contains(state);
	}

	/**
	 * Remove a state from the automaton.
	 * 
	 * @param state
	 */
	public void removeState(STATE state) {
		for (ParityTreeAutomatonRule<LETTER, STATE> rule : mSourceMap.get(state)) {
			mChildrenMap.get(rule.getDest()).get(rule.getLetter()).remove(rule);
			mLettersMap.get(rule.getLetter()).remove(rule);
			mParentsMap.get(rule.getSource()).get(rule.getLetter()).remove(rule);
			mRules.remove(rule);
		}

		for (ParityTreeAutomatonRule<LETTER, STATE> rule : mChildMap.get(state)) {
			mChildrenMap.get(rule.getDest()).get(rule.getLetter()).remove(rule);
			mLettersMap.get(rule.getLetter()).remove(rule);
			mParentsMap.get(rule.getSource()).get(rule.getLetter()).remove(rule);
			mRules.remove(rule);
		}
		mSourceMap.remove(state);
		mChildMap.remove(state);
		mStates.remove(state);
		if (mInitStates.contains(state))
			mInitStates.remove(state);
	}

	public STATE fetchEqualState(IParityState query) {
		for (STATE state : mStates) {
			if (state.equals(query))
				return state;
		}

		return null;
	}
}
