package cgsynt.tree.buchi.parity;

import java.util.List;

import cgsynt.tree.parity.IParityState;
import cgsynt.tree.parity.ParityTreeAutomatonRule;
import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;

public class BuchiParityIntersectRule<LETTER extends IRankedLetter, STATE1, STATE2 extends IParityState>
		extends ParityTreeAutomatonRule<LETTER, BuchiParityIntersectState<STATE1, STATE2>> {
	private final BuchiParityIntersectState<STATE1, STATE2> source;
	private final List<BuchiParityIntersectState<STATE1, STATE2>> dests;
	private final LETTER letter;

	public BuchiParityIntersectState<STATE1, STATE2> getSource() {
		return source;
	}

	public List<BuchiParityIntersectState<STATE1, STATE2>> getDests() {
		return dests;
	}

	public LETTER getLetter() {
		return letter;
	}

	public BuchiParityIntersectRule(BuchiParityIntersectState<STATE1, STATE2> source,
			List<BuchiParityIntersectState<STATE1, STATE2>> dests, LETTER letter) {
		super(letter, source, dests);
		this.source = source;
		this.dests = dests;
		this.letter = letter;
	}

}
