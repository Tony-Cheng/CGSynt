package cgsynt.trace;

import java.util.List;

import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;

public interface IInterpol {

	public boolean checkSat(IPredicate pre, IStatement statement, IPredicate post);
	
	public IPredicate[] computeInterpolants(List<IStatement> statements);
	
	public IPredicate getTruePredicate();
	public IPredicate getFalsePredicate();
}
