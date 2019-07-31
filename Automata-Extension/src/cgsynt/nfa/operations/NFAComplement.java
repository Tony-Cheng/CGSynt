package cgsynt.nfa.operations;

import java.util.HashSet;
import java.util.Set;

import cgsynt.automaton.factory.PDeterminizeStateFactory;
import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.VpAlphabet;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.transitions.OutgoingInternalTransition;
import de.uni_freiburg.informatik.ultimate.automata.statefactory.StringFactory;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.BasicPredicateFactory;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;

public class NFAComplement<LETTER> {

	private NestedWordAutomaton<LETTER, IPredicate> aut;
	private boolean resultComputed;
	private NestedWordAutomaton<LETTER, IPredicate> result;
	private AutomataLibraryServices services;
	private BasicPredicateFactory predFactory;

	public NFAComplement(NestedWordAutomaton<LETTER, IPredicate> aut, AutomataLibraryServices services,
			BasicPredicateFactory predFactory) {
		this.aut = aut;
		this.resultComputed = false;
		this.services = services;
		this.predFactory = predFactory;
	}

	public void computeResult() {
		if (resultComputed)
			return;
		Set<LETTER> internalAlphabet = new HashSet<>();
		for (LETTER transition : aut.getAlphabet()) {
			internalAlphabet.add(transition);
		}
		VpAlphabet<LETTER> vpAlphabet = new VpAlphabet<>(internalAlphabet, new HashSet<>(), new HashSet<>());
		result = new NestedWordAutomaton<>(services, vpAlphabet, new PDeterminizeStateFactory(predFactory));
		for (IPredicate state : aut.getStates()) {
			boolean isInitial = false;
			boolean isFinal = true;
			if (aut.isFinal(state)) {
				isFinal = false;
			}
			if (aut.isInitial(state)) {
				isInitial = true;
			}
			result.addState(isInitial, isFinal, state);
		}
		for (IPredicate state : aut.getStates()) {
			for (OutgoingInternalTransition<LETTER, IPredicate> transition : aut.internalSuccessors(state)) {
				result.addInternalTransition(state, transition.getLetter(), transition.getSucc());
			}
		}
		resultComputed = true;
	}

	public NestedWordAutomaton<LETTER, IPredicate> getResult() {
		if (!resultComputed)
			return null;
		return result;
	}
}
