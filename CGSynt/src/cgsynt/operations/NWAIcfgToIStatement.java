package cgsynt.operations;

import java.util.Map;

import cgsynt.interpol.IStatement;
import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.INestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.VpAlphabet;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.transitions.OutgoingInternalTransition;
import de.uni_freiburg.informatik.ultimate.automata.statefactory.IEmptyStackStateFactory;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IcfgInternalTransition;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;

public class NWAIcfgToIStatement {

	private INestedWordAutomaton<IcfgInternalTransition, IPredicate> original;
	private NestedWordAutomaton<IStatement, IPredicate> result;
	private AutomataLibraryServices services;
	private VpAlphabet<IStatement> vpAlphabet;
	private IEmptyStackStateFactory<IPredicate> emptyStateFactory;
	private Map<IcfgInternalTransition, IStatement> statementMap;

	public NWAIcfgToIStatement(INestedWordAutomaton<IcfgInternalTransition, IPredicate> original,
			AutomataLibraryServices services, final VpAlphabet<IStatement> vpAlphabet,
			final IEmptyStackStateFactory<IPredicate> emptyStateFactory,
			Map<IcfgInternalTransition, IStatement> statementMap) {
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
			for (OutgoingInternalTransition<IcfgInternalTransition, IPredicate> transition : original
					.internalSuccessors(state)) {
				result.addInternalTransition(state, statementMap.get(transition.getLetter()), transition.getSucc());
			}
		}
	}
	
	public NestedWordAutomaton<IStatement, IPredicate> getResult() {
		return result;
	}
}
