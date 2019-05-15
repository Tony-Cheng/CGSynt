package tree.buchi.operations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.transitions.OutgoingInternalTransition;
import tree.buchi.BuchiTreeAutomaton;
import tree.buchi.BuchiTreeAutomatonRule;
import tree.buchi.lta.LtaBool;


public class DfaToLta<LETTER, STATE> {
	private final NestedWordAutomaton<LETTER, STATE> mDfa;
	private final BuchiTreeAutomaton<LtaBool, STATE> mResult;
	
	private final int mArity;
	
	/*
	 * Convert A DFA to an LTA that accepts all subsets of the language
	 * of the DFA
	 *
	 * @param dfa
	 * 		The DFA to Convert
	 */
	public DfaToLta(final NestedWordAutomaton<LETTER, STATE> dfa) {
		this.mDfa = dfa;
		this.mArity = dfa.getAlphabet().size();
		
		this.mResult = new BuchiTreeAutomaton<LtaBool, STATE>(mArity);	
		
		this.compute();
	}
	
	private void computeInitialStates() {
		final Set<STATE> intialStates = this.mDfa.getInitialStates();
		
		for (STATE state : intialStates) {
			this.mResult.addInitState(state);
		}
	}
	
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
				final BuchiTreeAutomatonRule<LtaBool, STATE> trueRule = new BuchiTreeAutomatonRule<>(new LtaBool(true), state, destStates);
				
				this.mResult.addRule(trueRule);
			}
			
			final BuchiTreeAutomatonRule<LtaBool, STATE> falseRule = new BuchiTreeAutomatonRule<>(new LtaBool(false), state, destStates);
			this.mResult.addRule(falseRule);
		}
	}
	
	private void computeFinalStates() {
		final Set<STATE> states = this.mDfa.getFinalStates();
		
		for (STATE state : states) {
			this.mResult.addFinalState(state);
		}
	}
	
	private void compute() {
		this.computeInitialStates();
		this.computeTransitions();
		this.computeFinalStates();
	}
	
	public BuchiTreeAutomaton<LtaBool, STATE> getResult(){
		return this.mResult;
	}
}
