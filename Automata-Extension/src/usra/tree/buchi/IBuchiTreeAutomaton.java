package usra.tree.buchi;

import java.util.List;
import java.util.Set;

import de.uni_freiburg.informatik.ultimate.automata.IAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;

/**
 * @author weiszben
 *
 * @param <LETTER>
 * 			is the type of alphabet.
 * @param <STATE>
 * 			is the type of states.
 */
public interface IBuchiTreeAutomaton<LETTER extends IRankedLetter, STATE> extends IAutomaton<LETTER, STATE>{
	/***
	 * Add a new rule to the automaton.
	 * 
	 * @param rule
	 */
	void addRule(final BuchiTreeAutomatonRule<LETTER, STATE> rule);

	/**
	 * Gets the amount of rules contained in this automaton. This operation operates
	 * in O(1), i.e. it is fast.
	 * 
	 * @return The amount of rules contained in this automaton
	 */
	int getAmountOfRules();

	/**
	 * 
	 * @return iterable of all source lists occuring in some rules.
	 */
	Iterable<List<STATE>> getSourceCombinations();

	/***
	 * Complement the set of final states
	 */
	void complementFinals();
	
	/**
	 * @return a set of all the states in the automaton.
	 */
	Set<STATE> getStates();

	/**
	 * @param states
	 * @return a list of all successor states for given states.
	 */
	Iterable<BuchiTreeAutomatonRule<LETTER, STATE>> getSuccessors(final List<STATE> states);
	
	/***
	 * @param letter
	 * @return
	 */
	Iterable<BuchiTreeAutomatonRule<LETTER, STATE>> getSuccessors(final LETTER letter);

	/**
	 * @param states
	 * @param letter
	 * @return a list of all successors for given states and given letter.
	 */
	Iterable<STATE> getSuccessors(final List<STATE> states, final LETTER letter);

	/**
	 * @param state
	 * @return true, if given state is final.
	 */
	boolean isFinalState(final STATE state);
	
}
