package cgsynt.tree.parity;

import de.uni_freiburg.informatik.ultimate.automata.statefactory.IEmptyStackStateFactory;

public class ParityStateFactory implements IEmptyStackStateFactory<ParityState<String>>{

	@Override
	public ParityState<String> createEmptyStackState() {
		System.out.println("ParitStateFactory: EmptyState used!");
		return new ParityState<>("EmptyState", 1);
	}

}
