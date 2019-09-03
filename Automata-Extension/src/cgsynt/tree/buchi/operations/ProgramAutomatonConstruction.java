package cgsynt.tree.buchi.operations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cgsynt.interpol.IAssumption;
import cgsynt.interpol.IStatement;
import cgsynt.tree.buchi.BuchiTreeAutomaton;
import cgsynt.tree.buchi.BuchiTreeAutomatonRule;
import cgsynt.tree.buchi.lta.RankedBool;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.BasicPredicateFactory;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;

/**
 * Construct a buchi tree automaton that represents a set of well-formed
 * programs.
 *
 */
public class ProgramAutomatonConstruction {
	private Set<IStatement> statements;
	private IPredicate stateLeft;
	private IPredicate stateRight;
	private IPredicate stateBottom;
	private boolean resultComputed;
	private List<IStatement> assignments;
	private List<Pair<IAssumption, IAssumption>> assumptions;
	private List<IStatement> alphabet;
	private Map<IStatement, Integer> assignmentsMap;
	private Map<Pair<IAssumption, IAssumption>, Integer> assumptionsMap;
	private BuchiTreeAutomaton<RankedBool, IPredicate> result;
	private BasicPredicateFactory mPredicateFactory;
	private Map<IAssumption, IAssumption> negation;

	public ProgramAutomatonConstruction(Set<IStatement> statements, BasicPredicateFactory predicateFactory) {
		this.statements = statements;
		this.resultComputed = false;
		this.mPredicateFactory = predicateFactory;
	}

	/**
	 * Compute the buchi automaton that represents the program.
	 */
	public void computeResult() {
		if (resultComputed)
			return;
		this.negation = new HashMap<>();
		computeAssignmentsAndAssumptions();
		this.result = new BuchiTreeAutomaton<>(assignments.size() + 2 * assumptions.size());
		RankedBool.setRank(assignments.size() + 2 * assumptions.size());
		computeAlphabet();
		computeStates();
		computeRightStateEdges();
		computeBottomStateEdges();
		computeLeftStateEdges();
		resultComputed = true;
	}

	/**
	 * Separate the list of statements in the assignments and assumptions.
	 */
	private void computeAssignmentsAndAssumptions() {
		assumptions = new ArrayList<>();
		assignments = new ArrayList<>();
		for (IStatement statement : statements) {
			if (statement instanceof IAssumption) {
				IAssumption copy = ((IAssumption) statement).copy();
				copy.negate();
				negation.put((IAssumption) statement, copy);
				negation.put(copy, (IAssumption) statement);
				assumptions.add(new Pair<>((IAssumption) statement, copy));
			} else {
				assignments.add(statement);
			}
		}
	}

	public Map<IAssumption, IAssumption> getNegation() {
		return negation;
	}

	/**
	 * Compute the alphabet for the automaton.
	 */
	private void computeAlphabet() {
		alphabet = new ArrayList<>();
		assignmentsMap = new HashMap<>();
		assumptionsMap = new HashMap<>();
		for (int i = 0; i < assignments.size(); i++) {
			alphabet.add(assignments.get(i));
			assignmentsMap.put(assignments.get(i), i);
		}
		for (int i = 0; i < assumptions.size(); i++) {
			alphabet.add(assumptions.get(i).getKey());
			alphabet.add(assumptions.get(i).getValue());
			assumptionsMap.put(assumptions.get(i), 2 * i + assignments.size());
		}
	}

	/**
	 * Compute all the transitions coming out of the right state.
	 */
	private void computeRightStateEdges() {
		List<IPredicate> dest = new ArrayList<>();
		for (int i = 0; i < alphabet.size(); i++) {
			dest.add(stateRight);
		}
		BuchiTreeAutomatonRule<RankedBool, IPredicate> rule = new BuchiTreeAutomatonRule<>(RankedBool.FALSE, stateRight,
				dest);
		result.addRule(rule);
	}

	/**
	 * Compute all the transitions coming out of the bottom state.
	 */
	private void computeBottomStateEdges() {
		for (IStatement statement : assignments) {
			List<IPredicate> dest = new ArrayList<>();
			int index = assignmentsMap.get(statement);
			for (int i = 0; i < alphabet.size(); i++) {
				if (i == index) {
					dest.add(stateLeft);
				} else {
					dest.add(stateRight);
				}
			}
			BuchiTreeAutomatonRule<RankedBool, IPredicate> rule = new BuchiTreeAutomatonRule<>(RankedBool.FALSE,
					stateBottom, dest);
			result.addRule(rule);
		}
		for (Pair<IAssumption, IAssumption> statement : assumptions) {
			List<IPredicate> dest1 = new ArrayList<>();
			List<IPredicate> dest2 = new ArrayList<>();
			int index = assumptionsMap.get(statement);
			for (int i = 0; i < alphabet.size(); i++) {
				if (i == index) {
					dest1.add(stateLeft);
					dest2.add(stateBottom);
				} else if (i == index + 1) {
					dest1.add(stateBottom);
					dest2.add(stateLeft);
				} else {
					dest1.add(stateRight);
					dest2.add(stateRight);
				}
			}
			BuchiTreeAutomatonRule<RankedBool, IPredicate> rule1 = new BuchiTreeAutomatonRule<>(RankedBool.FALSE,
					stateBottom, dest1);
			BuchiTreeAutomatonRule<RankedBool, IPredicate> rule2 = new BuchiTreeAutomatonRule<>(RankedBool.FALSE,
					stateBottom, dest2);
			result.addRule(rule1);
			result.addRule(rule2);
		}

		List<IPredicate> dest = new ArrayList<>();
		for (int i = 0; i < alphabet.size(); i++) {
			dest.add(stateRight);
		}
		BuchiTreeAutomatonRule<RankedBool, IPredicate> rule = new BuchiTreeAutomatonRule<>(RankedBool.TRUE, stateBottom,
				dest);
		result.addRule(rule);
	}

	public IPredicate getDeadState() {
		return stateRight;
	}

	/**
	 * Compute all the transitions coming out of the left state.
	 */
	private void computeLeftStateEdges() {
		for (IStatement statement : assignments) {
			List<IPredicate> dest = new ArrayList<>();
			int index = assignmentsMap.get(statement);
			for (int i = 0; i < alphabet.size(); i++) {
				if (i == index) {
					dest.add(stateLeft);
				} else {
					dest.add(stateRight);
				}
			}
			BuchiTreeAutomatonRule<RankedBool, IPredicate> rule = new BuchiTreeAutomatonRule<>(RankedBool.FALSE,
					stateLeft, dest);
			result.addRule(rule);
		}
		for (Pair<IAssumption, IAssumption> statement : assumptions) {
			List<IPredicate> dest1 = new ArrayList<>();
			List<IPredicate> dest2 = new ArrayList<>();
			int index = assumptionsMap.get(statement);
			for (int i = 0; i < alphabet.size(); i++) {
				if (i == index) {
					dest1.add(stateLeft);
					dest2.add(stateBottom);
				} else if (i == index + 1) {
					dest1.add(stateBottom);
					dest2.add(stateLeft);
				} else {
					dest1.add(stateRight);
					dest2.add(stateRight);
				}
			}
			BuchiTreeAutomatonRule<RankedBool, IPredicate> rule1 = new BuchiTreeAutomatonRule<>(RankedBool.FALSE,
					stateLeft, dest1);
			BuchiTreeAutomatonRule<RankedBool, IPredicate> rule2 = new BuchiTreeAutomatonRule<>(RankedBool.FALSE,
					stateLeft, dest2);
			result.addRule(rule1);
			result.addRule(rule2);
		}

		List<IPredicate> dest = new ArrayList<>();
		for (int i = 0; i < alphabet.size(); i++) {
			dest.add(stateRight);
		}
		BuchiTreeAutomatonRule<RankedBool, IPredicate> rule = new BuchiTreeAutomatonRule<>(RankedBool.TRUE, stateLeft,
				dest);
		result.addRule(rule);
	}

	/**
	 * Compute all the states.
	 */
	private void computeStates() {
		this.stateLeft = mPredicateFactory.newDebugPredicate("left");
		this.stateBottom = mPredicateFactory.newDebugPredicate("bottom");
		this.stateRight = mPredicateFactory.newDebugPredicate("right");
		result.addInitState(stateLeft);
		result.addFinalState(stateRight);
		result.addFinalState(stateBottom);
	}

	/**
	 * Return the buchi tree automaton that represents a program.
	 * 
	 * @return
	 */
	public BuchiTreeAutomaton<RankedBool, IPredicate> getResult() {
		if (!resultComputed)
			return null;
		return result;
	}

	/**
	 * Return the destination alphabet.
	 * 
	 * @return
	 */
	public List<IStatement> getAlphabet() {
		if (!resultComputed)
			return null;
		return alphabet;
	}

}

class Pair<K, V> {
	public Pair(K key, V value) {
		super();
		this.key = key;
		this.value = value;
	}

	@Override
	public String toString() {
		return "Pair [key=" + key + ", value=" + value + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pair other = (Pair) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	public K getKey() {
		return key;
	}

	public void setKey(K key) {
		this.key = key;
	}

	public V getValue() {
		return value;
	}

	public void setValue(V value) {
		this.value = value;
	}

	private K key;
	private V value;

}
