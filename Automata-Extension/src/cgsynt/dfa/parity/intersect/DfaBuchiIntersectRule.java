package cgsynt.dfa.parity.intersect;

import cgsynt.tree.parity.IParityState;

public class DfaBuchiIntersectRule<LETTER, STATE1, STATE2 extends IParityState> {

	public final DfaBuchiIntersectState<STATE1, STATE2> source;
	public final DfaBuchiIntersectState<STATE1, STATE2> dest;
	public final LETTER letter;

	public DfaBuchiIntersectRule(DfaBuchiIntersectState<STATE1, STATE2> source,
			DfaBuchiIntersectState<STATE1, STATE2> dest, LETTER letter) {
		super();
		this.source = source;
		this.dest = dest;
		this.letter = letter;
	}

	public DfaBuchiIntersectState<STATE1, STATE2> getSucc() {
		return dest;
	}

	public LETTER getLetter() {
		return letter;
	}

}
