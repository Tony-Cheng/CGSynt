package cgsynt.operations;

import java.util.Map;
import java.util.Set;

import cgsynt.interpol.IStatement;
import cgsynt.nfa.GeneralizeStateFactory;
import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.INestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.VpAlphabet;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.transitions.OutgoingInternalTransition;
import de.uni_freiburg.informatik.ultimate.automata.statefactory.IEmptyStackStateFactory;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IcfgInternalTransition;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.predicates.PredicateFactory;

public class NWAIStatementToIcfg {

	private INestedWordAutomaton<IStatement, IPredicate> original;
	private NestedWordAutomaton<IcfgInternalTransition, IPredicate> result;
	private AutomataLibraryServices services;
	private VpAlphabet<IcfgInternalTransition> vpAlphabet;
	private GeneralizeStateFactory emptyStateFactory;
	private Map<IStatement, IcfgInternalTransition> statementMap;

	public NWAIStatementToIcfg(INestedWordAutomaton<IStatement, IPredicate> original,
			AutomataLibraryServices services, final VpAlphabet<IcfgInternalTransition> vpAlphabet,
			final GeneralizeStateFactory emptyStateFactory,
			Map<IStatement, IcfgInternalTransition> statementMap) {
		this.original = original;
		this.services = services;
		this.vpAlphabet = vpAlphabet;
		this.emptyStateFactory = emptyStateFactory;
		this.statementMap = statementMap;
	}

	public void computeResult() {
		this.result = new NestedWordAutomaton<>(services, vpAlphabet, emptyStateFactory);
		for (IPredicate state : original.getStates()) {
			result.addState(original.isInitial(state), original.isFinal(state), state);
		}
		for (IPredicate state : original.getStates()) {
			for (OutgoingInternalTransition<IStatement, IPredicate> transition : original
					.internalSuccessors(state)) {
				result.addInternalTransition(state, statementMap.get(transition.getLetter()), transition.getSucc());
			}
		}
	}
	
	public NestedWordAutomaton<IcfgInternalTransition, IPredicate> getResult() {
		return result;
	}
}
