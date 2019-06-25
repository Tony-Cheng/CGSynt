package cgsynt.interpol;

/**
 * The interface for an assumption statement.
 *
 */
public interface IAssumption extends IStatement {
	/**
	 * Negate the assumption.
	 */
	public void negate();
	
	/**
	 * Return whether the statement is negated or not.
	 * @return
	 */
	public boolean isNegated();
	
	public IAssumption copy();
}
