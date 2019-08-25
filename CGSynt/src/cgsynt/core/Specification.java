package cgsynt.core;

import java.util.List;

import cgsynt.interpol.IStatement;
import cgsynt.interpol.TraceGlobalVariables;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;

public class Specification {
	private List<IStatement> mTransitionAlphabet;
	private IPredicate mPreconditions, mPostconditions;
	private TraceGlobalVariables mGlobalVars;
		
	public Specification(List<IStatement> transitionAlphabet, IPredicate preconditions
			, IPredicate postconditions, TraceGlobalVariables globalVars) {
		mTransitionAlphabet = transitionAlphabet;
		mPreconditions = preconditions;
		mPostconditions = postconditions;
		mGlobalVars = globalVars;
	}

	public List<IStatement> getTransitionAlphabet() {
		return mTransitionAlphabet;
	}

	public IPredicate getPreconditions() {
		return mPreconditions;
	}

	public IPredicate getPostconditions() {
		return mPostconditions;
	}

	public TraceGlobalVariables getGlobalVars() {
		return mGlobalVars;
	}
}
