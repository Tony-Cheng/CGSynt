package usra.trace2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Trace implements Iterable<Statement> {

	private final List<Statement> trace;
	private final Set<String> variables;

	public Trace() {
		trace = new ArrayList<Statement>();
		variables = new HashSet<String>();
	}

	public void addStatement(Statement statement) {
		trace.add(statement);
	}

	public void addVariable(String variable) {
		variables.add(variable);
	}

	@Override
	public Iterator<Statement> iterator() {
		return trace.iterator();
	}

}
