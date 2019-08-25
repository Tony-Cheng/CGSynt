package cgsynt.dfa.parity.operations;

import java.util.HashMap;
import java.util.Map;

import cgsynt.dfa.parity.ParityAutomaton;
import cgsynt.tree.parity.IExtendedParityState;
import cgsynt.tree.parity.IParityState;
import cgsynt.tree.parity.ParityStateFactory;
import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.VpAlphabet;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.transitions.OutgoingInternalTransition;

public class ParityComplement<LETTER, STATE extends IParityState> {
	private ParityAutomaton<LETTER, STATE> mInAut, mOutAut;
	
	public ParityComplement(ParityAutomaton<LETTER, STATE> inAut, AutomataLibraryServices autServices,
			VpAlphabet<LETTER> alphabet, ParityStateFactory stateFactory) {
		this.mInAut = inAut;
		this.mOutAut = new ParityAutomaton<>(autServices, alphabet, stateFactory);
		
		this.computeResult();
	}
	
	private void computeResult() {
		Map<STATE, STATE> mapping = new HashMap<>();
		for (STATE state : this.mInAut.getStates()) {
			IExtendedParityState cpy = (IExtendedParityState) state.getSimpleRepresentation();
			cpy.setRank(cpy.getRank() + 1);
			
			mapping.put(state, (STATE)cpy);
		
			this.mOutAut.addState(this.mInAut.isInitial(state), false, mapping.get(state));
		}
		
		for (STATE state : this.mInAut.getStates()) {
			for (OutgoingInternalTransition<LETTER, STATE> transition : this.mInAut.internalSuccessors(state))
				this.mOutAut.addInternalTransition(mapping.get(state), transition.getLetter(), mapping.get(transition.getSucc()));
		}
	}
	
	public ParityAutomaton<LETTER, STATE> getResult(){
		return this.mOutAut;
	}
}
