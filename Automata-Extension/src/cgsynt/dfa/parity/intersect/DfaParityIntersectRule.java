package cgsynt.dfa.parity.intersect;

import cgsynt.tree.parity.IParityState;

public class DfaParityIntersectRule<LETTER, STATE1, STATE2 extends IParityState> {

	public final DfaParityIntersectState<STATE1, STATE2> source;
	public final DfaParityIntersectState<STATE1, STATE2> dest;
	public final LETTER letter;

	public DfaParityIntersectRule(DfaParityIntersectState<STATE1, STATE2> source,
			DfaParityIntersectState<STATE1, STATE2> dest, LETTER letter) {
		super();
		this.source = source;
		this.dest = dest;
		this.letter = letter;
	}

	public DfaParityIntersectState<STATE1, STATE2> getSucc() {
		return dest;
	}

	public LETTER getLetter() {
		return letter;
	}

}
