package cgsynt.tree.parity;

import java.util.ArrayList;
import java.util.List;

import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;

public class ParityIntersectRule<LETTER extends IRankedLetter, STATE1 extends IParityState, STATE2 extends IParityState, STATE3 extends IParityState> {
	public final ParityTreeAutomatonRule<LETTER, STATE1> rule1;
	public final ParityTreeAutomatonRule<LETTER, STATE2> rule2;
	public final ParityTreeAutomatonRule<LETTER, STATE3> rule3;

	public ParityIntersectRule(ParityTreeAutomatonRule<LETTER, STATE1> rule1,
			ParityTreeAutomatonRule<LETTER, STATE2> rule2, ParityTreeAutomatonRule<LETTER, STATE3> rule3) {
		this.rule1 = rule1;
		this.rule2 = rule2;
		this.rule3 = rule3;
	}

	public List<ParityIntersectState<STATE1, STATE2, STATE3>> getDests() {
		List<ParityIntersectState<STATE1, STATE2, STATE3>> dests = new ArrayList<>();
		for (int i = 0; i < rule1.getDest().size(); i++) {
			dests.add(
					new ParityIntersectState<>(rule1.getDest().get(i), rule2.getDest().get(i), rule3.getDest().get(i)));
		}
		return dests;
	}

	public ParityIntersectState<STATE1, STATE2, STATE3> getSource() {
		return new ParityIntersectState<>(rule1.getSource(), rule2.getSource(), rule3.getSource());

	}
}
