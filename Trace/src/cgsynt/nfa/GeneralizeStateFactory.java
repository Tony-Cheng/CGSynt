package cgsynt.nfa;

import de.uni_freiburg.informatik.ultimate.automata.statefactory.IEmptyStackStateFactory;

public class GeneralizeStateFactory<STATE> implements IEmptyStackStateFactory<STATE>{

	@Override
	public STATE createEmptyStackState() {
		return null;
	}

}
