package cgsynt.dfa.operations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cgsynt.interpol.IStatement;
import cgsynt.tree.buchi.BuchiTreeAutomaton;
import cgsynt.tree.buchi.BuchiTreeAutomatonRule;
import cgsynt.tree.buchi.lta.RankedBool;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.INestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.transitions.OutgoingInternalTransition;

public class DfaToLtaPowerSet<LETTER, STATE> {
	private final INestedWordAutomaton<LETTER, STATE> mDfa;
	private final BuchiTreeAutomaton<RankedBool, STATE> mResult;
	private final List<IStatement> mAllStateOrdering;

	private final int mArity;
	private final STATE mDeadState;

	/**
	 * Convert A DFA to an LTA that accepts all subsets of the language of the DFA.
	 *
	 * @param dfa
	 *            The DFA to Convert
	 */
	public DfaToLtaPowerSet(final INestedWordAutomaton<LETTER, STATE> dfa, final List<IStatement> allStateOrdering,
			final STATE deadState) {
		this.mDfa = dfa;
		this.mAllStateOrdering = allStateOrdering;

		this.mArity = allStateOrdering.size();
		this.mDeadState = deadState;

		this.mResult = new BuchiTreeAutomaton<RankedBool, STATE>(mArity);

		// Setup the dead state
		this.mResult.addState(deadState);

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
	 * the DFA, add a transition rule with the letter false, and the letter true,
	 * otherwise only add a transition rule with the letter false.
	 */
	private void computeTransitions() {
		final Set<STATE> states = this.mDfa.getStates();

		for (STATE state : states) {
			List<STATE> destStates = this.orderStates(state);

			assert destStates.size() == this.mArity;

			if (this.mDfa.isFinal(state)) {
				final BuchiTreeAutomatonRule<RankedBool, STATE> trueRule = new BuchiTreeAutomatonRule<>(RankedBool.TRUE,
						state, destStates);

				this.mResult.addRule(trueRule);
			}

			final BuchiTreeAutomatonRule<RankedBool, STATE> falseRule = new BuchiTreeAutomatonRule<>(RankedBool.FALSE,
					state, destStates);
			this.mResult.addRule(falseRule);
		}
	}

	/**
	 * Set the set of final states in the LTA to be the set of final states in the
	 * DFA.
	 */
	private void computeFinalStates() {
		final Set<STATE> states = (Set<STATE>) this.mDfa.getStates();

		for (STATE state : states) {
			this.mResult.addFinalState(state);
		}
	}

	/*
	 * Order the input states in the same order as the allStateOrdering. If a state
	 * is missing in the states list, then add the dead state in to fill the missing
	 * states spot.
	 */
	private List<STATE> orderStates(STATE state) {
		List<STATE> orderedStates = new ArrayList<>();

		for (IStatement statementToLookFor : this.mAllStateOrdering) {
			Iterator<OutgoingInternalTransition<LETTER, STATE>> transitions = this.mDfa.internalSuccessors(state)
					.iterator();

			boolean found = false;

			while (transitions.hasNext()) {
				OutgoingInternalTransition<LETTER, STATE> transition = transitions.next();
				if (transition.getLetter().equals(statementToLookFor)) {
					orderedStates.add(transition.getSucc());
					found = true;
					break;
				}
			}

			if (!found)
				orderedStates.add(this.mDeadState);
		}

		return orderedStates;
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
