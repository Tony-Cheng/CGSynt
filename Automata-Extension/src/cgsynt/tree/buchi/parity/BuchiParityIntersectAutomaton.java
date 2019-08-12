package cgsynt.tree.buchi.parity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;

import cgsynt.RepCondenser;
import cgsynt.tree.buchi.BuchiTreeAutomaton;
import cgsynt.tree.buchi.BuchiTreeAutomatonRule;
import cgsynt.tree.parity.IParityState;
import cgsynt.tree.parity.ParityIntersectRule;
import cgsynt.tree.parity.ParityIntersectState;
import cgsynt.tree.parity.ParityTreeAutomaton;
import cgsynt.tree.parity.ParityTreeAutomatonRule;
import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;

/**
 * An automaton that represents the intersection between a buchi tree and a
 * parity tree.
 *
 * @param <LETTER>
 * @param <STATE1>
 * @param <STATE2>
 */
public class BuchiParityIntersectAutomaton<LETTER extends IRankedLetter, STATE1, STATE2 extends IParityState> {

	private final Map<BuchiParityIntersectState<STATE1, STATE2>, Collection<BuchiParityIntersectRule<LETTER, STATE1, STATE2>>> mSourceMap;
	private final Set<BuchiParityIntersectRule<LETTER, STATE1, STATE2>> mRules;
	private final Map<BuchiParityIntersectState<STATE1, STATE2>, Collection<BuchiParityIntersectRule<LETTER, STATE1, STATE2>>> mChildMap;
	private final Set<BuchiParityIntersectState<STATE1, STATE2>> mStates;
	private final Set<BuchiParityIntersectState<STATE1, STATE2>> mAltStates;
	private final Set<BuchiParityIntersectState<STATE1, STATE2>> mInitStates;
	private final Set<STATE1> mFinalStates;
	private Set<LETTER> mAlphabet;

	public BuchiParityIntersectAutomaton(BuchiTreeAutomaton<LETTER, STATE1> tree1,
			ParityTreeAutomaton<LETTER, STATE2> tree2) {
		this.mSourceMap = new HashMap<>();
		this.mRules = new HashSet<>();
		this.mChildMap = new HashMap<>();
		this.mStates = new HashSet<>();
		this.mInitStates = new HashSet<>();
		this.mAltStates = new HashSet<>();
		this.mFinalStates = tree1.getFinalStates();
		this.mAlphabet = tree1.getAlphabet();
		initializeStates(tree1.getStates(), tree2.getStates());
		initializeInitStates(tree1.getInitStates(), tree2.getInitStates());
		initializeRules(tree1, tree2);
	}

	/**
	 * Add an intersected state to the automaton.
	 * 
	 * @param state
	 */
	private void addState(BuchiParityIntersectState<STATE1, STATE2> state) {
		if (mStates.contains(state))
			return;
		mStates.add(state);
		mAltStates.add(state);
		mSourceMap.put(state, new HashSet<>());
		mChildMap.put(state, new HashSet<>());
	}

	/**
	 * Intialize all possible combination of states.
	 * 
	 * @param states1
	 * @param states2
	 */
	private void initializeStates(Set<STATE1> states1, Set<STATE2> states2) {
		for (STATE1 state1 : states1) {
			for (STATE2 state2 : states2) {
				BuchiParityIntersectState<STATE1, STATE2> intersectState = new BuchiParityIntersectState<>(state1,
						state2);
				addState(intersectState);
				addAltState(intersectState.copy(false, false));
				addAltState(intersectState.copy(false, true));
				addAltState(intersectState.copy(true, false));
				addAltState(intersectState.copy(true, true));
			}
		}
	}

	/**
	 * Add an intersected state that is a good buchi and parity state.
	 * 
	 * @param state
	 */
	private void addAltState(BuchiParityIntersectState<STATE1, STATE2> state) {
		if (mAltStates.contains(state))
			return;
		mAltStates.add(state);
		mSourceMap.put(state, new HashSet<>());
		mChildMap.put(state, new HashSet<>());
	}

	/**
	 * Initialize all the initial states.
	 * 
	 * @param init1
	 * @param init2
	 */
	private void initializeInitStates(Set<STATE1> init1, Set<STATE2> init2) {
		for (STATE1 state1 : init1) {
			for (STATE2 state2 : init2) {
				BuchiParityIntersectState<STATE1, STATE2> intersectState = new BuchiParityIntersectState<>(state1,
						state2);
				mInitStates.add(intersectState);
			}
		}
	}

	/**
	 * Initialize all the rules.
	 * 
	 * @param tree1
	 * @param tree2
	 */
	private void initializeRules(BuchiTreeAutomaton<LETTER, STATE1> tree1, ParityTreeAutomaton<LETTER, STATE2> tree2) {
		for (LETTER letter : tree1.getAlphabet()) {
			if (tree1.getSuccessors(letter) == null || tree2.getSuccessors(letter) == null) {
				continue;
			}

			for (BuchiTreeAutomatonRule<LETTER, STATE1> rule1 : tree1.getSuccessors(letter)) {
				for (ParityTreeAutomatonRule<LETTER, STATE2> rule2 : tree2.getSuccessors(letter)) {
					addRule(rule1, rule2);
				}
			}
		}
	}

	/**
	 * Add a rule to this automaton.
	 * 
	 * @param rule1
	 * @param rule2
	 */
	private void addRule(BuchiTreeAutomatonRule<LETTER, STATE1> rule1, ParityTreeAutomatonRule<LETTER, STATE2> rule2) {
		STATE1 src1 = rule1.getSource();
		STATE2 src2 = rule2.getSource();
		BuchiParityIntersectState<STATE1, STATE2> intersectSrc = new BuchiParityIntersectState<>(src1, src2);
		BuchiParityIntersectRule<LETTER, STATE1, STATE2> intersectRule = new BuchiParityIntersectRule<>(rule1, rule2);
		mSourceMap.get(intersectSrc).add(intersectRule);
		mRules.add(intersectRule);
		for (int i = 0; i < rule1.getDest().size(); i++) {
			BuchiParityIntersectState<STATE1, STATE2> intersectDest = new BuchiParityIntersectState<>(
					rule1.getDest().get(i), rule2.getDest().get(i));
			mChildMap.get(intersectDest).add(intersectRule);

		}

	}

	public Collection<BuchiParityIntersectRule<LETTER, STATE1, STATE2>> getForSourceMap(
			BuchiParityIntersectState<STATE1, STATE2> intersectState) {
		return mSourceMap.get(intersectState.getGoodIntersectState());
	}
	
	public Collection<BuchiParityIntersectRule<LETTER, STATE1, STATE2>> getRulesBySource(
			BuchiParityIntersectState<STATE1, STATE2> intersectState) {
		return getForSourceMap(intersectState);
	}

	public Collection<BuchiParityIntersectRule<LETTER, STATE1, STATE2>> getForChildMap(
			BuchiParityIntersectState<STATE1, STATE2> intersectState) {
		return mChildMap.get(intersectState.getGoodIntersectState());
	}

	public Set<BuchiParityIntersectState<STATE1, STATE2>> getStates() {
		return mStates;
	}

	public Set<BuchiParityIntersectState<STATE1, STATE2>> getAltStates() {
		return mAltStates;
	}

	public Set<BuchiParityIntersectRule<LETTER, STATE1, STATE2>> getRules() {
		return mRules;
	}

	public boolean isFinal(BuchiParityIntersectState<STATE1, STATE2> state) {
		return mFinalStates.contains(state.getState1());
	}

	public boolean isEven(BuchiParityIntersectState<STATE1, STATE2> state) {
		return state.getState2().getRank() % 2 == 0;
	}

	public Set<BuchiParityIntersectState<STATE1, STATE2>> getInitStates() {
		return mInitStates;
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		RepCondenser<BuchiParityIntersectState<STATE1, STATE2>> condenser = new RepCondenser<>(new ArrayList<>(this.mStates));
		Map<String, String> map = condenser.getMapping();
		
		result.append("States:\n");
		for (BuchiParityIntersectState<STATE1, STATE2> state : mStates) {
			result.append(map.get(state.toString()));
			result.append("\n");
		}
		result.append("\n");

		result.append("Initial States:\n");
		for (BuchiParityIntersectState<STATE1, STATE2> state : mInitStates) {
			result.append(map.get(state.toString()));
			result.append("\n");
		}
		result.append("\n");

		result.append("Alphabet:\n");
		for (LETTER letter : mAlphabet) {
			result.append(letter.toString());
			result.append("\n");
		}
		result.append("\n");

		result.append("Transitions:\n");
		for (BuchiParityIntersectRule<LETTER, STATE1, STATE2> rule : mRules) {
			String dests = "";
			for (int i = 0; i < rule.getDests().size(); i++) {
				dests += map.get(rule.getDests().get(i).toString()) + " ";
			}
		
			result.append("(" + map.get(rule.getSource().toString()) + " | " + rule.getLetter().toString() + " | " + dests + ")");
			result.append("\n");
		}
		result.append("\n");

		List<String> values = new ArrayList<>(map.values());
		Collections.sort(values);
		result.append("Mappings:\n");
		for (String value : values) {
			String matchingKey = "";
		    for (Entry<String, String> entry : map.entrySet()) {
		        if (Objects.equals(value, entry.getValue())) {
		        	matchingKey = entry.getKey();
		        }
		    }
		    
		    result.append(value + " = " + matchingKey + "\n");
		}
		
		result.append("\n");
		
		return result.toString();
	}

}
