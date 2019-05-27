package usra.trace2;

import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Sort;

public class Variable extends Token {

	private final String name;
	private final Sort[] paramSorts;
	private final String resultSort;

	public Variable(String name, Sort[] paramSorts, String resultSort) {
		this.name = name;
		this.paramSorts = paramSorts;
		this.resultSort = resultSort;
	}

	public String getName(int id) {
		return name + "_" + id;
	}

	public void declareVariable(Script script, int id) {
		script.declareFun(getName(id), paramSorts, script.sort(resultSort));
	}

	@Override
	public String getName() {
		return name;
	}

}
