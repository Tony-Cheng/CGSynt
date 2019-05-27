package usra.trace2;

import de.uni_freiburg.informatik.ultimate.logic.Annotation;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Term;

public class TraceToCraigInterpolant {

	private final Trace trace;
	private final Script script;

	public TraceToCraigInterpolant(Trace trace, Script script) {
		this.trace = trace;
		this.script = script;
	}

	public CraigInterpolant computeResult() {
		int id = 0;
		int nStatements = 0;
		declareVariables(id);
		for (Statement statement : trace) {
			if (statement.isAssignment()) {
				id++;
				declareVariables(id);
				updateVariables(((Assignment) statement).getVariable(), id);
			}
			script.assertTerm(
					script.annotate(statement.getTerm(script, id), new Annotation(":named", "s" + nStatements)));
			nStatements++;
		}
		script.checkSat();
		Term[] namedTerms = generateNamedTerms(nStatements);
		return new CraigInterpolant(script.getInterpolants(namedTerms));

	}

	private void declareVariables(int id) {
		for (Variable var : trace.getVariables()) {
			var.declareVariable(script, id);
		}
	}

	private Term[] generateNamedTerms(int nStatements) {
		Term[] terms = new Term[nStatements];
		for (int i = 0; i < nStatements; i++) {
			terms[i] = script.term("s" + i);
		}
		return terms;
	}

	private void updateVariables(Variable assignmentVariable, int id) {
		String name = assignmentVariable.getName();
		for (Variable var : trace.getVariables()) {
			if (!var.getName().equals(name)) {
				script.assertTerm(script.term("=", var.getTerm(script, id), var.getTerm(script, id - 1)));
			}
		}
	}
}
