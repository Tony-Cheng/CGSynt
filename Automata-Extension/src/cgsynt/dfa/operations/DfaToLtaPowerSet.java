package cgsynt.dfa.operations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cgsynt.tree.buchi.BuchiTreeAutomaton;
import cgsynt.tree.buchi.BuchiTreeAutomatonRule;
import cgsynt.tree.buchi.lta.LtaBool;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.transitions.OutgoingInternalTransition;

public class DfaToLtaPowerSet<LETTER, STATE> {
	private final NestedWordAutomaton<LETTER, STATE> mDfa;
	private final BuchiTreeAutomaton<LtaBool, STATE> mResult;
	
	private final int mArity;
	
	/**
	 * Convert A DFA to an LTA that accepts all subsets of the language
	 * of the DFA.
	 *
	 * @param dfa
	 * 		The DFA to Convert
	 */
	public DfaToLtaPowerSet(final NestedWordAutomaton<LETTER, STATE> dfa) {
		this.mDfa = dfa;
		this.mArity = dfa.getAlphabet().size();
		
		this.mResult = new BuchiTreeAutomaton<LtaBool, STATE>(mArity);	
		
		this.compute();
	}
	
	/**
	 * Set the set of initial states in the LTA to be the set of initial
	 * states in the DFA.
	 */
	private void computeInitialStates() {
		final Set<STATE> intialStates = this.mDfa.getInitialStates();
		
		for (STATE state : intialStates) {
			this.mResult.addInitState(state);
		}
	}
	
	/**
	 * Add the new transition rules. For a given state, if it was a final state
	 * in the DFA, add a transition rule with the letter false, and the letter true,
	 * otherwise only add a transition rule with the letter false.
	 */
	private void computeTransitions() {
		final Set<STATE> states = this.mDfa.getStates();
		
		for (STATE state : states) {
			final Iterator<OutgoingInternalTransition<LETTER, STATE>> dests = this.mDfa.internalSuccessors(state).iterator();
			final List<STATE> destStates = new ArrayList<>();
			
			while (dests.hasNext()) {
				destStates.add(dests.next().getSucc());
			}
			
			assert destStates.size() == this.mArity;
			
			if (this.mDfa.isFinal(state)) {
				final BuchiTreeAutomatonRule<LtaBool, STATE> trueRule = new BuchiTreeAutomatonRule<>(new LtaBool(true, this.mArity), state, destStates);
				
				this.mResult.addRule(trueRule);
			}
			
			final BuchiTreeAutomatonRule<LtaBool, STATE> falseRule = new BuchiTreeAutomatonRule<>(new LtaBool(false, this.mArity), state, destStates);
			this.mResult.addRule(falseRule);
		}
	}
	
	/**
	 * Set the set of final states in the LTA to be the set of final
	 * states in the DFA.
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
	public BuchiTreeAutomaton<LtaBool, STATE> getResult(){
		return this.mResult;
	}
}
