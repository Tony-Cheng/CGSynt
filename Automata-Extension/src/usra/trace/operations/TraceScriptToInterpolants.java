package usra.trace.operations;

import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Term;

public class TraceScriptToInterpolants {
	private Script mScript;
	private Term[] mInterpolants;
	
	public TraceScriptToInterpolants(Script script, int numberOfStatements) {
		this.mScript = script;
		
		this.mInterpolants = new Term[numberOfStatements - 1];
		
		this.compute();
	}
	
	public Term[] getResult() {
		return mInterpolants;
	}
	
	private void compute() {
		Term[] annotations = new Term[mInterpolants.length + 1];
		
		for (int i = 0; i < mInterpolants.length; i++) {
			annotations[i] = mScript.term("s_" + i);
		}
		
		mInterpolants = mScript.getInterpolants(annotations);
	}
}
