package cgsynt.tree.buchi.parity;

import java.util.List;

import cgsynt.tree.parity.IParityState;
import cgsynt.tree.parity.ParityTreeAutomatonRule;
import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;

/**
 * A transition in the intersection between a buchi and a parity automaton.
 *
 * @param <LETTER>
 * @param <STATE1>
 * @param <STATE2>
 */
public class BuchiParityIntersectRuleV2<LETTER extends IRankedLetter, STATE1, STATE2 extends IParityState>
		extends ParityTreeAutomatonRule<LETTER, BuchiParityIntersectStateV2<STATE1, STATE2>> {
	private final BuchiParityIntersectStateV2<STATE1, STATE2> source;
	private final List<BuchiParityIntersectStateV2<STATE1, STATE2>> dests;
	private final LETTER letter;

	public BuchiParityIntersectStateV2<STATE1, STATE2> getSource() {
		return source;
	}

	public List<BuchiParityIntersectStateV2<STATE1, STATE2>> getDests() {
		return dests;
	}

	public LETTER getLetter() {
		return letter;
	}

	public BuchiParityIntersectRuleV2(BuchiParityIntersectStateV2<STATE1, STATE2> source,
			List<BuchiParityIntersectStateV2<STATE1, STATE2>> dests, LETTER letter) {
		super(letter, source, dests);
		this.source = source;
		this.dests = dests;
		this.letter = letter;
	}

}
