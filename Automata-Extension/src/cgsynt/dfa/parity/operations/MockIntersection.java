package cgsynt.dfa.parity.operations;

import java.util.Set;

import cgsynt.dfa.parity.ParityAutomaton;
import cgsynt.dfa.parity.intersect.DfaParityIntersectAutomaton;
import cgsynt.tree.parity.IParityState;
import cgsynt.tree.parity.ParityStateFactory;
import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.VpAlphabet;

public class MockIntersection<LETTER, STATE1, STATE2 extends IParityState> {
	private ParityAutomaton<LETTER, STATE2> mInAut;
	private DfaParityIntersectAutomaton<LETTER, STATE1, STATE2> mOutAut;
	
	private STATE1 mDummyState, mEmptyState;
	private AutomataLibraryServices mAutServices;
	private Set<LETTER> mLetters;
	
	public MockIntersection(ParityAutomaton<LETTER, STATE2> inAut, STATE1 dummyState, STATE1 emptyState, AutomataLibraryServices autServices,
			Set<LETTER> letters) {
		this.mInAut = inAut;
		this.mDummyState = dummyState;
		this.mEmptyState = emptyState;
		this.mAutServices = autServices;
		this.mLetters = letters;
		
		computeResult();
	}
	
	private void computeResult() {
		VpAlphabet<LETTER> alphabet = new VpAlphabet<>(this.mLetters);
		
		NestedWordAutomaton<LETTER, STATE1> dummyAut = new NestedWordAutomaton<>(this.mAutServices, 
				alphabet, new TempStateFactory(this.mEmptyState));
		dummyAut.addState(true, true, mDummyState);
		
		for (LETTER letter : this.mLetters) {
			dummyAut.addInternalTransition(mDummyState, letter, mDummyState);
		}
		
		this.mOutAut = new DfaParityIntersectAutomaton<>(dummyAut, this.mInAut);
	}
	
	public DfaParityIntersectAutomaton<LETTER, STATE1, STATE2> getResult() {
		return this.mOutAut;
	}
}
