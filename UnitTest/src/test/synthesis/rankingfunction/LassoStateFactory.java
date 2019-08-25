package test.synthesis.rankingfunction;

import de.uni_freiburg.informatik.ultimate.automata.statefactory.IEmptyStackStateFactory;

public class LassoStateFactory implements IEmptyStackStateFactory<String> {
	
	public String createEmptyStackState() {
		return "Empty";
	}

}
