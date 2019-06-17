package cgsynt.interpol;

import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWord;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IAction;

/**
 * A programming statement in a trace.
 *
 */
public interface IStatement {

	/**
	 * Return a trace that only contains this statement.
	 * @param negated True if the negation of
	 * 		  this IStatement is to be returned.
	 * 
	 * @return
	 */
	public NestedWord<IAction> getTrace(boolean negated);

	/**
	 * Return a formula that represents this statement.
	 * 
	 * @param negated True if the negation of 
	 * 		  this IStatement is to be returned.
	 * @return
	 */
	public IAction getFormula(boolean negated);
	
	/**
	 * Return the string representation of an IStatement.
	 */
	public String toString();
}
