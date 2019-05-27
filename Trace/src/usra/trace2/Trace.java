package usra.trace2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.uni_freiburg.informatik.ultimate.logic.Script;

public class Trace implements Iterable<Statement> {

	private final List<Statement> trace;
	private final Set<Variable> variables;

	public Trace() {
		trace = new ArrayList<Statement>();
		variables = new HashSet<Variable>();
	}

	/**
	 * Add a statement to this trace.
	 * 
	 * @param statement
	 */
	public void addStatement(Statement statement) {
		trace.add(statement);
	}

	/**
	 * Add a variable to this term.
	 * 
	 * @param variable
	 */
	public void addVariable(Variable variable) {
		variables.add(variable);
	}

	/**
	 * Return all of the variables used in the trace.
	 * 
	 * @return
	 */
	public Set<Variable> getVariables() {
		return variables;
	}

	@Override
	public Iterator<Statement> iterator() {
		return trace.iterator();
	}

}
