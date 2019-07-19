package cgsynt.tree.buchi.parity;

import java.util.ArrayList;
import java.util.List;

import cgsynt.tree.buchi.BuchiTreeAutomatonRule;
import cgsynt.tree.parity.IParityState;
import cgsynt.tree.parity.ParityTreeAutomatonRule;
import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;

public class BuchiParityIntersectRule<LETTER extends IRankedLetter, STATE1, STATE2 extends IParityState> {
	private BuchiParityIntersectState<STATE1, STATE2> source;
	private List<BuchiParityIntersectState<STATE1, STATE2>> dests;

	public BuchiParityIntersectRule(BuchiTreeAutomatonRule<LETTER, STATE1> rule1,
			ParityTreeAutomatonRule<LETTER, STATE2> rule2) {
		this.source = new BuchiParityIntersectState<>(rule1.getSource(), rule2.getSource());
		this.dests = new ArrayList<>();
		for (int i = 0; i < rule1.getDest().size(); i++) {
			dests.add(new BuchiParityIntersectState<>(rule1.getDest().get(i), rule2.getDest().get(i)));
		}
	}

	public BuchiParityIntersectState<STATE1, STATE2> getSource() {
		return source;
	}

	public List<BuchiParityIntersectState<STATE1, STATE2>> getDests() {
		return dests;
	}

}
