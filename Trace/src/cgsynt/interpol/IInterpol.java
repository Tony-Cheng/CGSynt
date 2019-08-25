package cgsynt.interpol;

import java.util.List;

import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;

/**
 * An interface for the interpolation class.
 *
 */
public interface IInterpol {

	/**
	 * Return true if the statement satisfies the pre and post conditions and false
	 * otherwise.
	 * 
	 * @param pre
	 * @param statement
	 * @param post
	 * @return
	 */
	public boolean isCorrect(IPredicate pre, IStatement statement, IPredicate post);

	/**
	 * Compute the interpolants from a list of statements that represents a trace.
	 * 
	 * @param statements
	 * @return
	 * @throws Exception
	 */
	public IPredicate[] computeInterpolants(List<IStatement> statements) throws Exception;

	/**
	 * Return the true predicate.
	 * 
	 * @return
	 */
	public IPredicate getTruePredicate();

	/**
	 * Return the false predicate.
	 * 
	 * @return
	 */
	public IPredicate getFalsePredicate();
}
