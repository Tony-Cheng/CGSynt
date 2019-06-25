package cgsynt.tree.buchi.operations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cgsynt.interpol.IAssumption;
import cgsynt.interpol.IStatement;
import cgsynt.tree.buchi.BuchiTreeAutomaton;
import cgsynt.tree.buchi.BuchiTreeAutomatonRule;
import cgsynt.tree.buchi.lta.RankedBool;
import javafx.util.Pair;

public class ProgramAutomatonConstructon {
	private Set<IStatement> statements;
	private String stateLeft;
	private String stateRight;
	private String stateBottom;
	private boolean resultComputed;
	private List<IStatement> assignments;
	private List<Pair<IAssumption, IAssumption>> assumptions;
	private List<IStatement> alphabet;
	private Map<IStatement, Integer> assignmentsMap;
	private Map<Pair<IAssumption, IAssumption>, Integer> assumptionsMap;
	private BuchiTreeAutomaton<RankedBool, String> result;

	public ProgramAutomatonConstructon(Set<IStatement> statements) {
		this.statements = statements;
		this.resultComputed = false;
	}

	public void computeResult() {
		if (resultComputed)
			return;
		computeAssignmentsAndAssumptions();
		this.result = new BuchiTreeAutomaton<>(assignments.size() + 2 * assumptions.size());
		RankedBool.setRank(assignments.size() + 2 * assumptions.size());
		computeAlphabet();
		computeStates();
		computeRightStateEdges();
		computeBottomStateEdges();
	}

	private void computeAssignmentsAndAssumptions() {
		for (IStatement statement : statements) {
			if (statement instanceof IAssumption) {
				IAssumption copy = ((IAssumption) statement).copy();
				copy.negate();
				assumptions.add(new Pair<>((IAssumption) statement, copy));
			} else {
				assignments.add(statement);
			}
		}
	}

	private void computeAlphabet() {
		alphabet = new ArrayList<>();
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

	private void computeRightStateEdges() {
		List<String> dest = new ArrayList<>();
		for (int i = 0; i < alphabet.size(); i++) {
			dest.add(stateRight);
		}
		BuchiTreeAutomatonRule<RankedBool, String> rule = new BuchiTreeAutomatonRule<>(RankedBool.FALSE, stateRight,
				dest);
		result.addRule(rule);
	}

	private void computeBottomStateEdges() {
		for (IStatement statement : assignments) {
			List<String> dest = new ArrayList<>();
			int index = assignmentsMap.get(statement);
			for (int i = 0; i < alphabet.size(); i++) {
				if (i == index) {
					dest.add(stateLeft);
				} else {
					dest.add(stateRight);
				}
			}
			BuchiTreeAutomatonRule<RankedBool, String> rule = new BuchiTreeAutomatonRule<>(RankedBool.FALSE,
					stateBottom, dest);
			result.addRule(rule);
		}
	}

	private void computeLeftStateEdges() {
		for (IStatement statement : assignments) {
			List<String> dest = new ArrayList<>();
			int index = assignmentsMap.get(statement);
			for (int i = 0; i < alphabet.size(); i++) {
				if (i == index) {
					dest.add(stateLeft);
				} else {
					dest.add(stateRight);
				}
			}
			BuchiTreeAutomatonRule<RankedBool, String> rule = new BuchiTreeAutomatonRule<>(RankedBool.FALSE, stateLeft,
					dest);
			result.addRule(rule);
		}
		for (Pair<IAssumption, IAssumption> statement : assumptions) {
			List<String> dest1 = new ArrayList<>();
			List<String> dest2 = new ArrayList<>();
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
			BuchiTreeAutomatonRule<RankedBool, String> rule1 = new BuchiTreeAutomatonRule<>(RankedBool.FALSE, stateLeft,
					dest1);
			BuchiTreeAutomatonRule<RankedBool, String> rule2 = new BuchiTreeAutomatonRule<>(RankedBool.FALSE, stateLeft,
					dest1);
			result.addRule(rule1);
			result.addRule(rule2);
		}

		List<String> dest = new ArrayList<>();
		for (int i = 0; i < alphabet.size(); i++) {
			dest.add(stateRight);
		}
		BuchiTreeAutomatonRule<RankedBool, String> rule = new BuchiTreeAutomatonRule<>(RankedBool.TRUE, stateLeft,
				dest);
		result.addRule(rule);
	}

	private void computeStates() {
		this.stateLeft = "left";
		this.stateBottom = "bottom";
		this.stateRight = "right";
		result.addInitState(stateLeft);
		result.addFinalState(stateRight);
		result.addFinalState(stateBottom);
	}

	public BuchiTreeAutomaton<RankedBool, String> getResult() {
		if (!resultComputed)
			return null;
		return result;
	}

	public List<IStatement> getAlphabet() {
		if (!resultComputed)
			return null;
		return alphabet;
	}

}
