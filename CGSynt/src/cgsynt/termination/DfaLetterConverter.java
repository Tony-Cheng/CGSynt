package cgsynt.termination;

import java.util.HashSet;
import java.util.Map;

import cgsynt.interpol.IStatement;
import cgsynt.nfa.GeneralizeStateFactory;
import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.INestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.VpAlphabet;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.transitions.OutgoingInternalTransition;
import de.uni_freiburg.informatik.ultimate.core.model.models.Payload;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IcfgEdgeFactory;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IcfgInternalTransition;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IcfgLocation;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.debugidentifiers.StringDebugIdentifier;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.BasicPredicateFactory;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;

public class DfaLetterConverter {
	private INestedWordAutomaton<IStatement, IPredicate> mInAut;

	private NestedWordAutomaton<IcfgInternalTransition, IPredicate> mOutAut;
	
	private Map<IStatement, IcfgInternalTransition> mIcfgTransitionMap;
 	
	public DfaLetterConverter(INestedWordAutomaton<IStatement, IPredicate> inAut, AutomataLibraryServices autServices, 
			BasicPredicateFactory bpf, Map<IStatement, IcfgInternalTransition> icfgTransitionMap) {
		this.mInAut = inAut;
		this.mIcfgTransitionMap = icfgTransitionMap;
		
		VpAlphabet<IcfgInternalTransition> alphabet = new VpAlphabet<>(new HashSet<>(mIcfgTransitionMap.values()));
		
		this.mOutAut = new NestedWordAutomaton<>(autServices, alphabet, 
				new GeneralizeStateFactory(bpf));
		
		this.computeResult();
	}
	
	private void computeResult() {
		computeStates();
		computeTransitions();
	}
	
	private void computeStates() {
		for (IPredicate state : mInAut.getStates())
			mOutAut.addState(this.mInAut.isInitial(state), this.mInAut.isFinal(state), state);
	}
	
	private void computeTransitions() {		
		for (IPredicate source : mInAut.getStates()) {
			Iterable<OutgoingInternalTransition<IStatement, IPredicate>> transitions = mInAut.internalSuccessors(source);
			
			for (OutgoingInternalTransition<IStatement, IPredicate> transition : transitions)
				this.mOutAut.addInternalTransition(source, this.mIcfgTransitionMap.get(transition.getLetter()), transition.getSucc());
		}
	}
	
	public NestedWordAutomaton<IcfgInternalTransition, IPredicate> getResult(){
		return mOutAut;
	}
}
