package usra.tree.buchi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;

public class BüchiTreeAutomatonRule<LETTER extends IRankedLetter, STATE, ALPHA>
		extends BuchiTreeAutomatonRule<LETTER, STATE> {

	private List<ALPHA> alpha;
	private Map<ALPHA, STATE> transitions;

	public BüchiTreeAutomatonRule(LETTER letter, STATE src, List<STATE> dest, List<ALPHA> alpha) {
		super(letter, src, dest);
		this.alpha = alpha;
		this.transitions = new HashMap<>();
		for (int i = 0; i < letter.getRank(); i++) {
			transitions.put(alpha.get(i), dest.get(i));
		}
	}

	public STATE getState(ALPHA alpha) {
		return transitions.get(alpha);
	}
	
	public List<ALPHA> getAlphabet() {
		return alpha;
	}
}
