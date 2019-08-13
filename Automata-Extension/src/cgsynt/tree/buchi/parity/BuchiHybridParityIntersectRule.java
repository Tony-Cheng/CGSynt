package cgsynt.tree.buchi.parity;

import java.util.ArrayList;
import java.util.List;

import cgsynt.tree.buchi.BuchiTreeAutomatonRule;
import cgsynt.tree.parity.IParityState;
import cgsynt.tree.parity.ParityTreeAutomatonRule;
import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;

/**
 * A rule that represented the intersect of a buchi tree rule and a parity tree
 * rule.
 *
 * @param <LETTER>
 * @param <STATE1>
 * @param <STATE2>
 */
public class BuchiHybridParityIntersectRule<LETTER extends IRankedLetter, STATE1, STATE2 extends IParityState> {
	@Override
	public String toString() {
		return "BuchiParityIntersectRule [source=" + source + ", dests=" + dests + "]";
	}

	/**
	 * The source state of this rule.
	 */
	private BuchiParityHybridIntersectState<STATE1, STATE2> source;

	/**
	 * The destination of this rule.
	 */
	private List<BuchiParityHybridIntersectState<STATE1, STATE2>> dests;
	
	private LETTER mLetter;

	public BuchiHybridParityIntersectRule(BuchiTreeAutomatonRule<LETTER, STATE1> rule1,
			ParityTreeAutomatonRule<LETTER, STATE2> rule2) {
		this.source = new BuchiParityHybridIntersectState<>(rule1.getSource(), rule2.getSource());
		this.dests = new ArrayList<>();
		for (int i = 0; i < rule1.getDest().size(); i++) {
			dests.add(new BuchiParityHybridIntersectState<>(rule1.getDest().get(i), rule2.getDest().get(i)));
		}
		
		this.mLetter = rule1.getLetter();
	}
 
	public LETTER getLetter() {
		return mLetter;
	}
	
	/**
	 * Return the source state of this rule.
	 * 
	 * @return
	 */
	public BuchiParityHybridIntersectState<STATE1, STATE2> getSource() {
		return source;
	}

	/**
	 * Return the destination of this rule.
	 * 
	 * @return
	 */
	public List<BuchiParityHybridIntersectState<STATE1, STATE2>> getDests() {
		return dests;
	}

}
