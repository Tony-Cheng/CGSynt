package cgsynt.interpol;

import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.transitions.UnmodifiableTransFormula;

/**
 * The interface for an assumption statement.
 *
 */
public interface IAssumption extends IStatement {

	/**
	 * Return whether the statement is negated or not.
	 * 
	 * @return
	 */
	public boolean isNegated();

	/**
	 * Return a copy of this assumption statement.
	 * 
	 * @return
	 */
	public IAssumption copy();

	/**
	 * Negate the assumption.
	 */
	void negate();

	/**
	 * Return the formula of this statement.
	 * 
	 * @param negated
	 * @return
	 */
	public UnmodifiableTransFormula getTransFormula(boolean negated);
}
