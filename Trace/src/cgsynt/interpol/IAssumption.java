package cgsynt.interpol;

import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.BasicPredicateFactory;

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
}
