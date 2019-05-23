package usra.trace.operations;

import de.uni_freiburg.informatik.ultimate.logic.Annotation;
import de.uni_freiburg.informatik.ultimate.logic.Logics;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Term;
import de.uni_freiburg.informatik.ultimate.smtinterpol.DefaultLogger;
import de.uni_freiburg.informatik.ultimate.smtinterpol.smtlib2.SMTInterpol;
import usra.trace.FormulaInterface;
import usra.trace.TraceInterface;

public class TraceToSMT {
	private TraceInterface mTrace;
	private Script mScript;
	
	public TraceToSMT(TraceInterface trace) {
		this.mTrace = trace;
		
		this.mScript = this.compute();
	}
	
	public Script getResult() {
		return this.mScript;
	}
	
	private Script compute() {
		Script script = new SMTInterpol(new DefaultLogger());
		script.setOption(":produce-proofs", true);
		script.setLogic(Logics.QF_LIA);
		
		int stNum = 0;
		for (FormulaInterface form : this.mTrace.getFormulas()) {
			Term stTerm = this.processFormula(form, stNum);
			Term stAnnotated = script.annotate(stTerm, new Annotation(":named", "s_" + stNum));
			
			script.assertTerm(stAnnotated);
			
			stNum++;
		}
		
		return script;
	}
	
	private Term processFormula(FormulaInterface form, int stNum) {
		
	}
}