package cgsynt.Operations;

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
import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;

/**
 * A slow exponential approach is implemented. A faster approach requires
 * marking good transitions during the emptiness check. Not functioning yet!
 *
 * @param <LETTER>
 */
public class ProgramRetrieval<LETTER extends IRankedLetter> {

	private IStatement[] statements;
	private BuchiTreeAutomaton<LETTER, IntersectState<String, String>> tree;
	private Set<IntersectState<String, String>> visitedStates;
	private Set<IntersectState<String, String>> repeatedStates;
	private List<String> currentStatements;
	private Map<IntersectState<String, String>, BuchiTreeAutomatonRule<LETTER, IntersectState<String, String>>> goodProgram;
	private boolean resultComputed;

	public ProgramRetrieval(BuchiTreeAutomaton<LETTER, IntersectState<String, String>> tree, IStatement[] statements,
			Map<IntersectState<String, String>, BuchiTreeAutomatonRule<LETTER, IntersectState<String, String>>> goodProgram) {
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
		for (IntersectState<String, String> initial : tree.getInitStates()) {
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

	private void retrieveProgram(IntersectState<String, String> state) {
		if (visitedStates.contains(state)) {
			repeatedStates.add(state);
			return;
		}
		for (BuchiTreeAutomatonRule<LETTER, IntersectState<String, String>> transition : tree.getRulesBySource(state)) {
			if (!goodProgram.get(state).equals(transition))
				continue;
			Object[] retrievalResult = retrieveValidStatements(transition);
			@SuppressWarnings("unchecked")
			List<IntersectState<String, String>> validProgramStatements = (List<IntersectState<String, String>>) retrievalResult[0];
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

	public Object[] retrieveValidStatements(BuchiTreeAutomatonRule<LETTER, IntersectState<String, String>> transition) {
		List<IntersectState<String, String>> validProgramStatements = new ArrayList<>();
		List<String> validStatementStrings = new ArrayList<>();
		validProgramStatements.add(null);
		validProgramStatements.add(null);
		validStatementStrings.add(null);
		validStatementStrings.add(null);
		for (int i = 0; i < transition.getDest().size(); i++) {
			if (transition.getDest().get(i).getState1().equals("bottom")) {
				validProgramStatements.set(0, transition.getDest().get(i));
				validStatementStrings.set(0, statements[i].toString());
			}
			if (transition.getDest().get(i).getState1().equals("left")) {
				validProgramStatements.set(1, transition.getDest().get(i));
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
