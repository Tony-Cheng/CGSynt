package cgsynt.operations;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cgsynt.interpol.IStatement;
import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.INestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.VpAlphabet;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.transitions.OutgoingInternalTransition;
import de.uni_freiburg.informatik.ultimate.automata.statefactory.StringFactory;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IcfgInternalTransition;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;

public class NestedWordAutomatonConvsersion {

	private INestedWordAutomaton<IcfgInternalTransition, IPredicate> aut;
	private boolean resultComputed;
	private NestedWordAutomaton<IStatement, String> result;
	private Map<IcfgInternalTransition, IStatement> statementMap;
	private AutomataLibraryServices services;

	public NestedWordAutomatonConvsersion(INestedWordAutomaton<IcfgInternalTransition, IPredicate> aut,
			Map<IcfgInternalTransition, IStatement> statementMap, AutomataLibraryServices services) {
		this.aut = aut;
		this.resultComputed = false;
		this.statementMap = statementMap;
		this.services = services;
	}

	public void computeResult() {
		if (resultComputed)
			return;
		Set<IStatement> internalAlphabet = new HashSet<>();
		for (IcfgInternalTransition transition : aut.getAlphabet()) {
			internalAlphabet.add(statementMap.get(transition));
		}
		VpAlphabet<IStatement> vpAlphabet = new VpAlphabet<>(internalAlphabet, new HashSet<>(), new HashSet<>());
		result = new NestedWordAutomaton<>(services, vpAlphabet, new StringFactory());
		for (IPredicate state : aut.getStates()) {
			boolean isInitial = false;
			boolean isFinal = false;
			if (aut.isFinal(state)) {
				isFinal = true;
			}
			if (aut.isInitial(state)) {
				isInitial = true;
			}
			result.addState(isInitial, isFinal, state.toString());
		}
		for (IPredicate state : aut.getStates()) {
			for (OutgoingInternalTransition<IcfgInternalTransition, IPredicate> transition : aut
					.internalSuccessors(state)) {
				result.addInternalTransition(state.toString(), statementMap.get(transition.getLetter()),
						transition.getSucc().toString());
			}
		}
	}

	public NestedWordAutomaton<IStatement, String> getResult() {
		return result;
	}
}
