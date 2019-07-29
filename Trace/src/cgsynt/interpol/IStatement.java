package cgsynt.interpol;

import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWord;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IAction;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.transitions.UnmodifiableTransFormula;

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
	 * @return
	 */
	public IAction getFormula();
	
	/**
	 * Return the string representation of an IStatement.
	 */
	public String toString();
	
	public boolean isAssumption();
	
	public UnmodifiableTransFormula getTransFormula();
}
