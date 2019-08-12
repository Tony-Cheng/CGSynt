package cgsynt.dfa.parity.intersect;

public class DfaBuchiIntersectRule<LETTER, STATE1, STATE2> {

	public final STATE1 state1;
	public final STATE2 state2;
	public final LETTER letter;

	public DfaBuchiIntersectRule(STATE1 state1, STATE2 state2, LETTER letter) {
		super();
		this.state1 = state1;
		this.state2 = state2;
		this.letter = letter;
	}

}
