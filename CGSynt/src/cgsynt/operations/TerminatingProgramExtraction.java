package cgsynt.operations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cgsynt.interpol.IStatement;
import cgsynt.tree.buchi.BuchiTreeAutomaton;
import cgsynt.tree.buchi.BuchiTreeAutomatonRule;
import cgsynt.tree.buchi.IntersectState;
import cgsynt.tree.buchi.parity.BuchiParityIntersectAutomaton;
import cgsynt.tree.buchi.parity.BuchiParityIntersectRule;
import cgsynt.tree.buchi.parity.BuchiParityIntersectState;
import cgsynt.tree.parity.IParityState;
import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;

/**
 * A slow exponential approach is implemented. A faster approach requires
 * marking good transitions during the emptiness check. Not functioning yet!
 *
 * @param <LETTER>
 */
public class TerminatingProgramExtraction<LETTER extends IRankedLetter, STATE1, STATE2 extends IParityState> {

	private IStatement[] statements;
	private BuchiParityIntersectAutomaton<LETTER, IntersectState<IPredicate, STATE1>, STATE2> tree;
	private Set<BuchiParityIntersectState<IntersectState<IPredicate, STATE1>, STATE2>> visitedStates;
	private Set<BuchiParityIntersectState<IntersectState<IPredicate, STATE1>, STATE2>> repeatedStates;
	private List<String> currentStatements;
	private Map<BuchiParityIntersectState<IntersectState<IPredicate, STATE1>, STATE2>, BuchiParityIntersectRule<LETTER, IntersectState<IPredicate, STATE1>, STATE2>> goodProgram;
	private boolean resultComputed;

	public TerminatingProgramExtraction(
			BuchiParityIntersectAutomaton<LETTER, IntersectState<IPredicate, STATE1>, STATE2> tree,
			IStatement[] statements,
			Map<BuchiParityIntersectState<IntersectState<IPredicate, STATE1>, STATE2>, BuchiParityIntersectRule<LETTER, IntersectState<IPredicate, STATE1>, STATE2>> goodProgram) {
		this.statements = statements;
		this.tree = tree;
		this.visitedStates = new HashSet<>();
		this.currentStatements = new ArrayList<>();
		this.repeatedStates = new HashSet<>();
		this.resultComputed = false;
		this.goodProgram = goodProgram;
	}

	public void computeResult() {
		if (resultComputed)
			return;
		resultComputed = true;
		for (BuchiParityIntersectState<IntersectState<IPredicate, STATE1>, STATE2> initial : tree.getInitStates()) {
			retrieveProgram(initial);
			return;
		}
		return;
	}

	public List<String> getResult() {
		if (resultComputed)
			return currentStatements;
		return null;
	}

	private void retrieveProgram(BuchiParityIntersectState<IntersectState<IPredicate, STATE1>, STATE2> state) {
		if (visitedStates.contains(state)) {
			repeatedStates.add(state);
			return;
		}
		for (BuchiParityIntersectRule<LETTER, IntersectState<IPredicate, STATE1>, STATE2> transition : tree
				.getRulesBySource(state)) {
			if (!goodProgram.get(state).equals(transition))
				continue;
			Object[] retrievalResult = retrieveValidStatements(transition);
			@SuppressWarnings("unchecked")
			List<BuchiParityIntersectState<IntersectState<IPredicate, STATE1>, STATE2>> validProgramStatements = (List<BuchiParityIntersectState<IntersectState<IPredicate, STATE1>, STATE2>>) retrievalResult[0];
			@SuppressWarnings("unchecked")
			List<String> validStatementStrings = (List<String>) retrievalResult[1];
			if (validProgramStatements.size() == 0) {

			} else if (validProgramStatements.size() == 1) {
				currentStatements.add(validStatementStrings.get(0));
				retrieveProgram(validProgramStatements.get(0));
			} else if (validProgramStatements.size() == 2) {
				visitedStates.add(state);
				int statementIndex = currentStatements.size();
				currentStatements.add(validStatementStrings.get(0));
				retrieveProgram(validProgramStatements.get(0));
				if (repeatedStates.contains(state)) {
					currentStatements.set(statementIndex, "while(" + currentStatements.get(statementIndex) + ") {");
					currentStatements.add("}");
					repeatedStates.remove(state);
					retrieveProgram(validProgramStatements.get(1));
				} else {
					currentStatements.set(statementIndex, "if(" + currentStatements.get(statementIndex) + ") {");
					currentStatements.add("}");
					currentStatements.add("else {");
					retrieveProgram(validProgramStatements.get(1));
					currentStatements.add("}");
				}
				visitedStates.remove(state);
			}
			return;
		}
		return;
	}

	public Object[] retrieveValidStatements(
			BuchiParityIntersectRule<LETTER, IntersectState<IPredicate, STATE1>, STATE2> transition) {
		List<BuchiParityIntersectState<IntersectState<IPredicate, STATE1>, STATE2>> validProgramStatements = new ArrayList<>();
		List<String> validStatementStrings = new ArrayList<>();
		validProgramStatements.add(null);
		validProgramStatements.add(null);
		validStatementStrings.add(null);
		validStatementStrings.add(null);
		for (int i = 0; i < transition.getDests().size(); i++) {
			if (transition.getDests().get(i).getState1().toString().equals("bottom")) {
				validProgramStatements.set(0, transition.getDests().get(i));
				validStatementStrings.set(0, statements[i].toString());
			}
			if (transition.getDests().get(i).getState1().toString().equals("left")) {
				validProgramStatements.set(1, transition.getDests().get(i));
				validStatementStrings.set(1, statements[i].toString());

			}
		}
		if (validProgramStatements.get(1) == null) {
			validProgramStatements.remove(1);
			validStatementStrings.remove(1);
		}
		if (validProgramStatements.get(0) == null) {
			validProgramStatements.remove(0);
			validStatementStrings.remove(0);
		}
		return new Object[] { validProgramStatements, validStatementStrings };
	}
}
