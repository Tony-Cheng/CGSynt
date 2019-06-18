package cgsynt.tree.buchi;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.AutomataOperationCanceledException;
import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;
import de.uni_freiburg.informatik.ultimate.core.model.models.IElement;

/**
 * A buchi tree automaton.
 *
 * @param <LETTER>
 * @param <STATE>
 */
public class BuchiTreeAutomaton<LETTER extends IRankedLetter, STATE> implements IBuchiTreeAutomaton<LETTER, STATE> {
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mChildMap == null) ? 0 : mChildMap.hashCode());
		result = prime * result + ((mChildrenMap == null) ? 0 : mChildrenMap.hashCode());
		result = prime * result + ((mFinalStates == null) ? 0 : mFinalStates.hashCode());
		result = prime * result + ((mInitStates == null) ? 0 : mInitStates.hashCode());
		result = prime * result + ((mLettersMap == null) ? 0 : mLettersMap.hashCode());
		result = prime * result + ((mParentsMap == null) ? 0 : mParentsMap.hashCode());
		result = prime * result + ((mRules == null) ? 0 : mRules.hashCode());
		result = prime * result + ((mSourceMap == null) ? 0 : mSourceMap.hashCode());
		result = prime * result + rank;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BuchiTreeAutomaton other = (BuchiTreeAutomaton) obj;
		if (mChildMap == null) {
			if (other.mChildMap != null)
				return false;
		} else if (!mChildMap.equals(other.mChildMap))
			return false;
		if (mChildrenMap == null) {
			if (other.mChildrenMap != null)
				return false;
		} else if (!mChildrenMap.equals(other.mChildrenMap))
			return false;
		if (mFinalStates == null) {
			if (other.mFinalStates != null)
				return false;
		} else if (!mFinalStates.equals(other.mFinalStates))
			return false;
		if (mInitStates == null) {
			if (other.mInitStates != null)
				return false;
		} else if (!mInitStates.equals(other.mInitStates))
			return false;
		if (mLettersMap == null) {
			if (other.mLettersMap != null)
				return false;
		} else if (!mLettersMap.equals(other.mLettersMap))
			return false;
		if (mParentsMap == null) {
			if (other.mParentsMap != null)
				return false;
		} else if (!mParentsMap.equals(other.mParentsMap))
			return false;
		if (mRules == null) {
			if (other.mRules != null)
				return false;
		} else if (!mRules.equals(other.mRules))
			return false;
		if (mSourceMap == null) {
			if (other.mSourceMap != null)
				return false;
		} else if (!mSourceMap.equals(other.mSourceMap))
			return false;
		if (rank != other.rank)
			return false;
		return true;
	}

	private final Set<LETTER> mAlphabet;
	private final Map<List<STATE>, Map<LETTER, Collection<BuchiTreeAutomatonRule<LETTER, STATE>>>> mChildrenMap;
	private final Set<STATE> mFinalStates;
	private final Map<LETTER, Collection<BuchiTreeAutomatonRule<LETTER, STATE>>> mLettersMap;
	private final Map<STATE, Map<LETTER, Collection<BuchiTreeAutomatonRule<LETTER, STATE>>>> mParentsMap;
	private final Set<BuchiTreeAutomatonRule<LETTER, STATE>> mRules;
	private final Map<STATE, Collection<BuchiTreeAutomatonRule<LETTER, STATE>>> mSourceMap;
	private final Map<STATE, Collection<BuchiTreeAutomatonRule<LETTER, STATE>>> mChildMap;
	private final Set<STATE> mStates;
	private final Set<STATE> mInitStates;
	private final int rank;

	/**
	 * Create a BuchiTreeAutomaton.
	 */
	public BuchiTreeAutomaton(int rank) {
		mChildrenMap = new HashMap<>();
		mParentsMap = new HashMap<>();
		mAlphabet = new HashSet<>();
		mLettersMap = new HashMap<>();
		mSourceMap = new HashMap<>();
		mRules = new HashSet<>();
		mFinalStates = new HashSet<>();
		mStates = new HashSet<>();
		mInitStates = new HashSet<>();
		this.rank = rank;
		mChildMap = new HashMap<>();
	}

	@Override
	public Set<LETTER> getAlphabet() {
		// TODO Auto-generated method stub
		return mAlphabet;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return mStates.size();
	}

	@Override
	public String sizeInformation() {
		// TODO Auto-generated method stub
		return size() + "nodes";
	}

	@Override
	public IElement transformToUltimateModel(AutomataLibraryServices services)
			throws AutomataOperationCanceledException {
		// TODO Auto-generated method stub
		return null;
	}

	public void addRules(BuchiTreeAutomatonRule<LETTER, STATE>... rules) {
		for (BuchiTreeAutomatonRule<LETTER, STATE> rule : rules) {
			addRule(rule);
		}
	}

	@Override
	public void addRule(BuchiTreeAutomatonRule<LETTER, STATE> rule) {
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
			mChildrenMap.put(dest, new HashMap<LETTER, Collection<BuchiTreeAutomatonRule<LETTER, STATE>>>());
		}
		final Map<LETTER, Collection<BuchiTreeAutomatonRule<LETTER, STATE>>> childLetter = mChildrenMap.get(dest);
		if (!childLetter.containsKey(letter)) {
			childLetter.put(letter, new HashSet<BuchiTreeAutomatonRule<LETTER, STATE>>());
		}
		final HashSet<BuchiTreeAutomatonRule<LETTER, STATE>> children = (HashSet<BuchiTreeAutomatonRule<LETTER, STATE>>) childLetter
				.get(letter);
		children.add(rule);

		// parents
		if (!mParentsMap.containsKey(src)) {
			mParentsMap.put(src, new HashMap<LETTER, Collection<BuchiTreeAutomatonRule<LETTER, STATE>>>());
		}
		final Map<LETTER, Collection<BuchiTreeAutomatonRule<LETTER, STATE>>> parentLetter = mParentsMap.get(src);
		if (!parentLetter.containsKey(letter)) {
			parentLetter.put(letter, new HashSet<BuchiTreeAutomatonRule<LETTER, STATE>>());
		}
		final Set<BuchiTreeAutomatonRule<LETTER, STATE>> parents = (Set<BuchiTreeAutomatonRule<LETTER, STATE>>) parentLetter
				.get(letter);
		parents.add(rule);

		// LettersMap
		if (!mLettersMap.containsKey(letter)) {
			mLettersMap.put(letter, new HashSet<>());
		}
		HashSet<BuchiTreeAutomatonRule<LETTER, STATE>> rulesByLetter = (HashSet<BuchiTreeAutomatonRule<LETTER, STATE>>) mLettersMap
				.get(letter);
		rulesByLetter.add(rule);

		// SourceRules

		if (!mSourceMap.containsKey(src)) {
			mSourceMap.put(src, new HashSet<>());
		}
		final HashSet<BuchiTreeAutomatonRule<LETTER, STATE>> rulesBySource = (HashSet<BuchiTreeAutomatonRule<LETTER, STATE>>) mSourceMap
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

	@Override
	public int getAmountOfRules() {
		return mRules.size();
	}

	@Override
	public Iterable<List<STATE>> getSourceCombinations() {
		// TODO Auto-generated method stub
		// Not implemented yet
		return null;
	}

	@Override
	public void complementFinals() {
		// TODO Auto-generated method stub
		// Not implemented yet
	}

	@Override
	public Set<STATE> getStates() {
		return mStates;
	}

	@Override
	public Iterable<BuchiTreeAutomatonRule<LETTER, STATE>> getSuccessors(List<STATE> states) {
		// TODO Auto-generated method stub
		// Not implemented yet
		return null;
	}

	@Override
	public Iterable<BuchiTreeAutomatonRule<LETTER, STATE>> getSuccessors(LETTER letter) {
		// TODO Auto-generated method stub
		return mLettersMap.get(letter);
	}

	@Override
	public Iterable<STATE> getSuccessors(List<STATE> states, LETTER letter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isFinalState(STATE state) {
		return mFinalStates.contains(state);
	}

	/**
	 * Add a state the set of final states.
	 * 
	 * @param state
	 *            a final state.
	 */
	public void addFinalState(final STATE state) {
		mFinalStates.add(state);
		addState(state);
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
	 * Get the set containing this BuchiTreeAutomaton's final states
	 * @return A set of states.
	 */
	public Set<STATE> getFinalStates() {
		return mFinalStates;
	}

	/**
	 * Get the set of transition rules for this automaton
	 * @return A set of transitions
	 */
	public Set<BuchiTreeAutomatonRule<LETTER, STATE>> getRules() {
		return mRules;
	}

	/**
	 * Get the rank of this tree automaton.
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
	public Collection<BuchiTreeAutomatonRule<LETTER, STATE>> getRulesBySource(final STATE src) {
		return mSourceMap.get(src);
	}

	/**
	 * Get the map that maps automaton states to the collection 
	 * of destination states reachable from that state.
	 * @return A source map
	 */
	public Map<STATE, Collection<BuchiTreeAutomatonRule<LETTER, STATE>>> getSourceMap() {
		return mSourceMap;
	}

	/**
	 * Get the map that maps automaton states to the collection
	 * of source states for a specified state.
	 * @return A child map
	 */
	public Map<STATE, Collection<BuchiTreeAutomatonRule<LETTER, STATE>>> getChildMap() {
		return mChildMap;
	}

	/**
	 * Make every state in this automaton a final state.
	 */
	public void setAllStatesFinal() {
		mFinalStates.addAll(mStates);
	}

	/**
	 * Make a copy of this automaton.
	 * 
	 * @return
	 */
	public BuchiTreeAutomaton<LETTER, STATE> mkcpy() {
		BuchiTreeAutomaton<LETTER, STATE> copy = new BuchiTreeAutomaton<>(rank);
		copy.mChildrenMap.putAll(mChildrenMap);
		copy.mParentsMap.putAll(mParentsMap);
		copy.mAlphabet.addAll(mAlphabet);
		copy.mLettersMap.putAll(mLettersMap);
		for (STATE state : mSourceMap.keySet()) {
			copy.mSourceMap.put(state, new HashSet<>());
			copy.mSourceMap.get(state).addAll(mSourceMap.get(state));
		}
		copy.mRules.addAll(mRules);
		copy.mFinalStates.addAll(mFinalStates);
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
		
		result.append("Final States:\n");
		for (STATE state : mFinalStates) {
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
		for (BuchiTreeAutomatonRule<LETTER, STATE> rule : mRules) {
			result.append(rule.toString());
			result.append("\n");
		}
		result.append("\n");
		
		result.append("\n");
		return result.toString();

	}
}
