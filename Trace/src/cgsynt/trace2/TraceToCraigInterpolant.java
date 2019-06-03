package cgsynt.trace2;

import de.uni_freiburg.informatik.ultimate.logic.Annotation;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Term;

/**
 * Compute the interpolants of a trace.
 */
public class TraceToCraigInterpolant {

	private final Trace trace;
	private final Script script;

	public TraceToCraigInterpolant(Trace trace, Script script) {
		this.trace = trace;
		this.script = script;
	}

	/**
	 * Return the interpolants of a trace.
	 * 
	 * @return
	 */
	public CraigInterpolant computeResult() {
		int id = 0;
		int nStatements = 0;
		declareVariables(id);
		for (Statement statement : trace) {
			Term combinedTerm = null;
			if (statement.isAssignment()) {
				id++;
				declareVariables(id);
				combinedTerm = updateVariables(((Assignment) statement).getVariable(), id);
			}
			if (combinedTerm == null) {
				combinedTerm = statement.getTerm(script, id);
			} else {
				combinedTerm = script.term("and", combinedTerm, statement.getTerm(script, id));
			}
			script.assertTerm(script.annotate(combinedTerm, new Annotation(":named", "s" + nStatements)));
			nStatements++;
		}
		script.checkSat();
		Term[] namedTerms = generateNamedTerms(nStatements);
		return new CraigInterpolant(script.getInterpolants(namedTerms));

	}

	/**
	 * Declare all the variables of a particular id.
	 * 
	 * @param id
	 */
	private void declareVariables(int id) {
		for (Variable var : trace.getVariables()) {
			var.declareVariable(script, id);
		}
	}

	/**
	 * Return all the named equation in the trace.
	 * 
	 * @param nStatements
	 * @return
	 */
	private Term[] generateNamedTerms(int nStatements) {
		Term[] terms = new Term[nStatements];
		for (int i = 0; i < nStatements; i++) {
			terms[i] = script.term("s" + i);
		}
		return terms;
	}

	/**
	 * Return a term that updates all names of the variables.
	 * 
	 * @param assignmentVariable
	 * @param id
	 * @return
	 */
	private Term updateVariables(Variable assignmentVariable, int id) {
		String name = assignmentVariable.getName();
		Term combinedTerm = null;
		for (Variable var : trace.getVariables()) {
			if (!var.getName().equals(name)) {
				Term next = script.term("=", var.getTerm(script, id), var.getTerm(script, id - 1));
				if (combinedTerm == null) {
					combinedTerm = next;
				} else {
					combinedTerm = script.term("and", combinedTerm, next);
				}
			}
		}
		return combinedTerm;
	}
}
