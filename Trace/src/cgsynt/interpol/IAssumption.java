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

	public IAssumption copy();

	/**
	 * Negate the assumption.
	 */
	void negate();
	
	public UnmodifiableTransFormula getTransFormula(boolean negated);
}
