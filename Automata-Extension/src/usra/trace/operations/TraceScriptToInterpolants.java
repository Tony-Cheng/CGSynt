package usra.trace.operations;

import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Term;

public class TraceScriptToInterpolants {
	private Script mScript;
	private Term[] mInterpolants;
	private int numberOfStatements;
	
	public TraceScriptToInterpolants(Script script, int numberOfStatements) {
		this.mScript = script;
		
		this.numberOfStatements = numberOfStatements;
		
		this.compute();
	}
	
	public Term[] getResult() {
		return mInterpolants;
	}
	
	private void compute() {
		mScript.checkSat();
		Term[] annotations = new Term[this.numberOfStatements];
		
		for (int i = 0; i < this.numberOfStatements; i++) {
			annotations[i] = mScript.term("s_" + (i + 1));
		}
		
		mInterpolants = mScript.getInterpolants(annotations);
	}
}
