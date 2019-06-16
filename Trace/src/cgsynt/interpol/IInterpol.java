package cgsynt.interpol;

import java.util.List;

import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;

public interface IInterpol {

	public boolean isCorrect(IPredicate pre, IStatement statement, IPredicate post);

	public boolean isCorrect(List<IStatement> statements);

	public IPredicate[] computeInterpolants(List<IStatement> statements);

	public IPredicate getTruePredicate();

	public IPredicate getFalsePredicate();
}
