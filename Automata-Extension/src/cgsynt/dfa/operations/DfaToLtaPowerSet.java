package cgsynt.dfa.operations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cgsynt.tree.buchi.BuchiTreeAutomaton;
import cgsynt.tree.buchi.BuchiTreeAutomatonRule;
import cgsynt.tree.buchi.lta.RankedBool;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.INestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.transitions.OutgoingInternalTransition;

public class DfaToLtaPowerSet<LETTER, STATE> {
	private final INestedWordAutomaton<LETTER, STATE> mDfa;
	private final BuchiTreeAutomaton<RankedBool, STATE> mResult;
	private final List<STATE> mAllStateOrdering;

	private final int mArity;
	private final STATE mDeadState;

	/**
	 * Convert A DFA to an LTA that accepts all subsets of the language of the DFA.
	 *
	 * @param dfa
	 *            The DFA to Convert
	 */
	public DfaToLtaPowerSet(final INestedWordAutomaton<LETTER, STATE> dfa, final List<STATE> allStateOrdering, final STATE deadState) {
		this.mDfa = dfa;
		this.mAllStateOrdering = allStateOrdering;
		
		this.mArity = dfa.getAlphabet().size();
		this.mDeadState = deadState;

		this.mResult = new BuchiTreeAutomaton<RankedBool, STATE>(mArity);
		
		// Setup the dead state
		this.mResult.addState(deadState);
		List<STATE> deadStateDestinations = new ArrayList<STATE>();
		for (int i = 0; i < this.mArity; i++)
			deadStateDestinations.add(deadState);
		final BuchiTreeAutomatonRule<RankedBool, STATE> deadRule = new BuchiTreeAutomatonRule<>(RankedBool.FALSE,
				deadState, deadStateDestinations);
		this.mResult.addRule(deadRule);
		
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
			final Iterator<OutgoingInternalTransition<LETTER, STATE>> dests = this.mDfa.internalSuccessors(state)
					.iterator();
			List<STATE> destStates = new ArrayList<>();

			while (dests.hasNext()) {
				destStates.add(dests.next().getSucc());
			}

			destStates = this.orderStates(destStates);
			
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
		final Set<STATE> states = (Set<STATE>) this.mDfa.getFinalStates();

		for (STATE state : states) {
			this.mResult.addFinalState(state);
		}
	}
	
	/**
	 * Order the input states in the same order as the allStateOrdering.
	 * If a state is missing in the states list, then add the dead state in
	 * to fill the missing states spot.
	 */
	private List<STATE> orderStates(List<STATE> states){
		List<STATE> orderedStates = new ArrayList<>();
		
		for (STATE stateToLookFor : this.mAllStateOrdering) {
			for (STATE state : states) {
				if (state.equals(stateToLookFor)) {
					orderedStates.add(state);
					break;
				}
			}
			
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
