package tree.buchi;

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
 * @author
 *
 * @param <LETTER>
 * @param <STATE>
 */
public class BuchiTreeAutomaton<LETTER extends IRankedLetter, STATE> implements IBuchiTreeAutomaton<LETTER, STATE> {
	private final Set<LETTER> mAlphabet;
	private final Map<List<STATE>, Map<LETTER, Collection<BuchiTreeAutomatonRule<LETTER, STATE>>>> mChildrenMap;
	private final Set<STATE> mFinalStates;
	private final Map<LETTER, Collection<BuchiTreeAutomatonRule<LETTER, STATE>>> mLettersMap;
	private final Map<STATE, Map<LETTER, Collection<BuchiTreeAutomatonRule<LETTER, STATE>>>> mParentsMap;
	private final Set<BuchiTreeAutomatonRule<LETTER, STATE>> mRules;
	private final Map<STATE, Collection<BuchiTreeAutomatonRule<LETTER, STATE>>> mSourceMap;
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
		for (final STATE st : dest) {
			if (!mSourceMap.containsKey(st)) {
				mSourceMap.put(st, new HashSet<>());
			}
			final HashSet<BuchiTreeAutomatonRule<LETTER, STATE>> rulesBySource = (HashSet<BuchiTreeAutomatonRule<LETTER, STATE>>) mSourceMap
					.get(st);
			rulesBySource.add(rule);
		}
	}

	/**
	 * Add a state to this automaton.
	 * 
	 * @param state
	 *            a state in this automaton
	 */
	public void addState(STATE state) {
		// TODO Auto-generated method stub
		mStates.add(state);
	}

	/**
	 * Add a letter to the alphabet.
	 * 
	 * @param letter
	 *            a letter in the alphabet.
	 */
	public void addLetter(LETTER letter) {
		// TODO Auto-generated method stub
		mAlphabet.add(letter);
	}

	@Override
	public int getAmountOfRules() {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
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
	}

	/**
	 * Return the initial states.
	 * 
	 * @return the initial states
	 */
	public Set<STATE> getInitStates() {
		return mInitStates;
	}

	public Set<STATE> getFinalStates() {
		return mFinalStates;
	}

	public Set<BuchiTreeAutomatonRule<LETTER, STATE>> getRules() {
		return mRules;
	}

	public int getRank() {
		return rank;
	}
}
