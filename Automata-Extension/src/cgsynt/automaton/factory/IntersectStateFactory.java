package cgsynt.automaton.factory;

import cgsynt.nfa.operations.NFAIntersectedState;
import de.uni_freiburg.informatik.ultimate.automata.statefactory.IEmptyStackStateFactory;

public class IntersectStateFactory<STATE1, STATE2>
		implements IEmptyStackStateFactory<NFAIntersectedState<STATE1, STATE2>> {

	private IEmptyStackStateFactory<STATE1> factory1;
	private IEmptyStackStateFactory<STATE2> factory2;

	public IntersectStateFactory(IEmptyStackStateFactory<STATE1> factory1, IEmptyStackStateFactory<STATE2> factory2) {
		this.factory1 = factory1;
		this.factory2 = factory2;
	}

	@Override
	public NFAIntersectedState<STATE1, STATE2> createEmptyStackState() {
		return new NFAIntersectedState<>(factory1.createEmptyStackState(), factory2.createEmptyStackState(), 1);
	}

}
