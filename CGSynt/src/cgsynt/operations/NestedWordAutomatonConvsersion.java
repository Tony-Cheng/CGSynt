package cgsynt.operations;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cgsynt.automaton.factory.PDeterminizeStateFactory;
import cgsynt.interpol.IStatement;
import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.INestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.VpAlphabet;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.transitions.OutgoingInternalTransition;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IcfgInternalTransition;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.BasicPredicateFactory;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;

public class NestedWordAutomatonConvsersion {

	private INestedWordAutomaton<IcfgInternalTransition, IPredicate> aut;
	private boolean resultComputed;
	private NestedWordAutomaton<IStatement, IPredicate> result;
	private Map<IcfgInternalTransition, IStatement> statementMap;
	private AutomataLibraryServices services;
	private BasicPredicateFactory predFactory;

	public NestedWordAutomatonConvsersion(INestedWordAutomaton<IcfgInternalTransition, IPredicate> aut,
			Map<IcfgInternalTransition, IStatement> statementMap, AutomataLibraryServices services,
			BasicPredicateFactory predFactory) {
		this.aut = aut;
		this.resultComputed = false;
		this.statementMap = statementMap;
		this.services = services;
		this.predFactory = predFactory;
	}

	public void computeResult() {
		if (resultComputed)
			return;
		Set<IStatement> internalAlphabet = new HashSet<>();
		for (IcfgInternalTransition transition : aut.getAlphabet()) {
			internalAlphabet.add(statementMap.get(transition));
		}
		VpAlphabet<IStatement> vpAlphabet = new VpAlphabet<>(internalAlphabet, new HashSet<>(), new HashSet<>());
		result = new NestedWordAutomaton<>(services, vpAlphabet, new PDeterminizeStateFactory(predFactory));
		for (IPredicate state : aut.getStates()) {
			boolean isInitial = false;
			boolean isFinal = false;
			if (aut.isFinal(state)) {
				isFinal = true;
			}
			if (aut.isInitial(state)) {
				isInitial = true;
			}
			result.addState(isInitial, isFinal, state);
		}
		for (IPredicate state : aut.getStates()) {
			for (OutgoingInternalTransition<IcfgInternalTransition, IPredicate> transition : aut
					.internalSuccessors(state)) {
				result.addInternalTransition(state, statementMap.get(transition.getLetter()),
						transition.getSucc());
			}
		}
		resultComputed = true;
	}

	public NestedWordAutomaton<IStatement, IPredicate> getResult() {
		if (!resultComputed)
			return null;
		return result;
	}
}
