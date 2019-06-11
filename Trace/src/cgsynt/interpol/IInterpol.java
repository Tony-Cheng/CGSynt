package cgsynt.interpol;

import java.util.List;

import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;

public interface IInterpol {

	public boolean checkSat();
	
	public IPredicate[] computeInterpolants(List<IStatement> statements);
}
