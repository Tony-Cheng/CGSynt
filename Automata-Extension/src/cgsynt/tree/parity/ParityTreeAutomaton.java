package cgsynt.tree.parity;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;

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

	public Set<LETTER> getAlphabet() {
		return mAlphabet;
	}

	public Set<STATE> getStates() {
		return mStates;
	}

	public int size() {
		return mStates.size();
	}

	public void addRules(ParityTreeAutomatonRule<LETTER, STATE>... rules) {
		for (ParityTreeAutomatonRule<LETTER, STATE> rule : rules) {
			addRule(rule);
		}
	}

	public void addRule(ParityTreeAutomatonRule<LETTER, STATE> rule) {
		// TODO Auto-generated method stub
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
		if (!mParentsMap.containsKey(src)) {
			mParentsMap.put(src, new HashMap<LETTER, Collection<ParityTreeAutomatonRule<LETTER, STATE>>>());
		}
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

		if (!mSourceMap.containsKey(src)) {
			mSourceMap.put(src, new HashSet<>());
		}
		final HashSet<ParityTreeAutomatonRule<LETTER, STATE>> rulesBySource = (HashSet<ParityTreeAutomatonRule<LETTER, STATE>>) mSourceMap
				.get(src);
		rulesBySource.add(rule);

		// mRulesContainChildren
		for (STATE child : rule.getDest()) {
			if (!mChildMap.containsKey(child)) {
				mChildMap.put(child, new HashSet<>());
			}
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
		mStates.add(state);
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

		result.append("States:\n");
		for (STATE state : mStates) {
			result.append(state.toString());
			result.append("\n");
		}
		result.append("\n");

		result.append("Initial States:\n");
		for (STATE state : mInitStates) {
			result.append(state.toString());
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
			result.append(rule.toString());
			result.append("\n");
		}
		result.append("\n");

		result.append("\n");
		return result.toString();

	}
	
	public boolean contains(STATE state) {
		return mStates.contains(state);
	}

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
	
	public IParityState fetchEqualState(STATE query) {
		for (STATE state : mStates) {
			if (state.equals(query))
				return state;
		}
		
		return null;
	}
}
