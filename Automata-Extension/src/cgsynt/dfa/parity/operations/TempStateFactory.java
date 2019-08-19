package cgsynt.dfa.parity.operations;

import de.uni_freiburg.informatik.ultimate.automata.statefactory.IEmptyStackStateFactory;

public class TempStateFactory<STATE> implements IEmptyStackStateFactory<STATE>{

	private STATE mEmptyState;
	
	public TempStateFactory(STATE emptyState) {
		this.mEmptyState = emptyState;
	}
	
	@Override
	public STATE createEmptyStackState() {
		return this.mEmptyState;
	}

}
