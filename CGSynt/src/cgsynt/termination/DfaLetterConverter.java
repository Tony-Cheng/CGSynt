package cgsynt.termination;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import cgsynt.interpol.IStatement;
import cgsynt.nfa.GeneralizeStateFactory;
import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
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

public class DfaLetterConverter<LETTER extends IStatement> {
	private NestedWordAutomaton<LETTER, IPredicate> mInAut;

	private NestedWordAutomaton<IcfgInternalTransition, IPredicate> mOutAut;
	private IcfgEdgeFactory mFactory;
	
	private List<IcfgInternalTransition> mIcfgTransitionLetters;
 	
	public DfaLetterConverter(NestedWordAutomaton<LETTER, IPredicate> inAut, AutomataLibraryServices autServices, BasicPredicateFactory bpf) {
		this.mInAut = inAut;
		this.mFactory = new IcfgEdgeFactory(OmegaRefiner.SERIAL_PROVIDER);
		
		mIcfgTransitionLetters = createIcfgAlphabetList(new ArrayList<>(this.mInAut.getAlphabet()));
		VpAlphabet<IcfgInternalTransition> alphabet = new VpAlphabet<>(new HashSet<>(mIcfgTransitionLetters));
		
		this.mOutAut = new NestedWordAutomaton<>(autServices, alphabet, 
				new GeneralizeStateFactory(bpf));
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
		Map<IStatement, IcfgInternalTransition> mapping = 
				createLetterMapping(new ArrayList<>(this.mInAut.getAlphabet()), mIcfgTransitionLetters);
		
		for (IPredicate source : mInAut.getStates()) {
			Iterable<OutgoingInternalTransition<LETTER, IPredicate>> transitions = mInAut.internalSuccessors(source);
			
			for (OutgoingInternalTransition<LETTER, IPredicate> transition : transitions)
				this.mOutAut.addInternalTransition(source, mapping.get(transition.getLetter()), transition.getSucc());
		}
	}
	
	public NestedWordAutomaton<IcfgInternalTransition, IPredicate> getResult(){
		return mOutAut;
	}
	
	public List<IcfgInternalTransition> createIcfgAlphabetList(List<IStatement> oldAlphabetList){
		List<IcfgInternalTransition> transitionList = new ArrayList<>();
		
		IcfgLocation location = new IcfgLocation(new StringDebugIdentifier("0"), "p1");

		for (IStatement statement : oldAlphabetList) {
			IcfgInternalTransition trans = mFactory.createInternalTransition(location, location, new Payload(),
					statement.getTransFormula());
			transitionList.add(trans);
		}
		
		return transitionList;
	}
	
	public Map<IStatement, IcfgInternalTransition> createLetterMapping(List<IStatement> oldLetters, List<IcfgInternalTransition> newLetters){
		Map<IStatement, IcfgInternalTransition> mapping = new HashMap<>();
		
		int i = 0;
		for (IStatement statement : oldLetters) {
			mapping.put(statement, newLetters.get(i));
			i++;
		}
		
		return mapping;
	}
}
