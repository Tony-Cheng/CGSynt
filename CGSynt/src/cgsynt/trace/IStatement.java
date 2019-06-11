package cgsynt.trace;

import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWord;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IAction;

/**
 * A programming statement in a trace.
 *
 */
public interface IStatement {

	/**
	 * Return a trace that only contains this statement.
	 * 
	 * @return
	 */
	public NestedWord<IAction> getTrace();

	/**
	 * Return a formula that represents this statement.
	 * 
	 * @param prevProcedure
	 * @param nextProcedure
	 * @return
	 */
	public IAction getFormula();
}
