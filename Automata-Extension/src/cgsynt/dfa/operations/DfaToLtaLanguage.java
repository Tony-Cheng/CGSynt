package cgsynt.dfa.operations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cgsynt.tree.buchi.BuchiTreeAutomaton;
import cgsynt.tree.buchi.BuchiTreeAutomatonRule;
import cgsynt.tree.buchi.lta.RankedBool;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.transitions.OutgoingInternalTransition;

public class DfaToLtaLanguage<LETTER, STATE> {
	private final NestedWordAutomaton<LETTER, STATE> mDfa;
	private final BuchiTreeAutomaton<RankedBool, STATE> mResult;

	private final int mArity;

	/**
	 * Convert A DFA to an LTA that the language of the DFA of the DFA.
	 *
	 * @param dfa
	 *            The DFA to Convert
	 */
	public DfaToLtaLanguage(final NestedWordAutomaton<LETTER, STATE> dfa) {
		this.mDfa = dfa;
		this.mArity = dfa.getAlphabet().size();

		this.mResult = new BuchiTreeAutomaton<RankedBool, STATE>(mArity);

		this.compute();
	}

	/**
	 * Set the set of initial states in the LTA to be the set of initial states in
	 * the DFA.
	 */
	private void computeInitialStates() {
		final Set<STATE> intialStates = this.mDfa.getInitialStates();

		for (STATE state : intialStates) {
			this.mResult.addInitState(state);
		}
	}

	/**
	 * Add the new transition rules. For a given state, if it was a final state in
	 * the DFA, add a transition rule with the letter true, otherwise only add a
	 * transition rule with the letter false.
	 */
	private void computeTransitions() {
		final Set<STATE> states = this.mDfa.getStates();

		for (STATE state : states) {
			final Iterator<OutgoingInternalTransition<LETTER, STATE>> dests = this.mDfa.internalSuccessors(state)
					.iterator();
			final List<STATE> destStates = new ArrayList<>();

			while (dests.hasNext()) {
				destStates.add(dests.next().getSucc());
			}

			assert destStates.size() == this.mArity;

			boolean truth = this.mDfa.isFinal(state);
			RankedBool bool;
			if (truth)
				bool = RankedBool.TRUE;
			else
				bool = RankedBool.FALSE;
			final BuchiTreeAutomatonRule<RankedBool, STATE> rule = new BuchiTreeAutomatonRule<>(bool, state,
					destStates);
			this.mResult.addRule(rule);
		}
	}

	/**
	 * Set the set of final states in the LTA to be the set of final states in the
	 * DFA.
	 */
	private void computeFinalStates() {
		final Set<STATE> states = this.mDfa.getFinalStates();

		for (STATE state : states) {
			this.mResult.addFinalState(state);
		}
	}

	/**
	 * Compute the result of this operation.
	 */
	private void compute() {
		this.computeInitialStates();
		this.computeTransitions();
		this.computeFinalStates();
	}

	/**
	 * Gets the result of the operation.
	 * 
	 * @return The result of the operation.
	 */
	public BuchiTreeAutomaton<RankedBool, STATE> getResult() {
		return this.mResult;
	}
}