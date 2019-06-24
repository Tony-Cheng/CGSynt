package cgsynt.array;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cgsynt.Verification.MainVerificationLoop;
import cgsynt.interpol.IAssumption;
import cgsynt.interpol.IStatement;
import cgsynt.interpol.ScriptAssignmentStatement;
import cgsynt.interpol.ScriptAssumptionStatement;
import cgsynt.interpol.TraceGlobalVariables;
import cgsynt.interpol.TraceToInterpolants;
import cgsynt.interpol.VariableFactory;
import cgsynt.tree.buchi.BuchiTreeAutomaton;
import cgsynt.tree.buchi.BuchiTreeAutomatonRule;
import cgsynt.tree.buchi.lta.RankedBool;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.boogie.BoogieNonOldVar;

public class TestArrays2 {
	
	/**
	 * [i = 0, n >= 0, m = A[0], j >=0, j < n]
	 * while i < n:
	 * 		if m < A[i]:
	 * 			m = A[i]
	 * 		i++
	 * [m >= A[j]]
	 */
	public static String arrayMaxTest() throws Exception {
		TraceGlobalVariables.reset();
		TraceToInterpolants.reset();
		BuchiTreeAutomaton<RankedBool, String> program = new BuchiTreeAutomaton<>(6);
		
		RankedBool.setRank(6);
		program.addInitState("1");
		program.addState("2");
		program.addState("3");
		program.addState("4");
		program.addState("5");
		program.addState("I");
		
		program.setAllStatesFinal();
		
		VariableFactory vf = TraceGlobalVariables.getGlobalVariables().getVariableFactory();
		Script script = TraceGlobalVariables.getGlobalVariables().getManagedScript().getScript();
		
		// Ghost Var
		BoogieNonOldVar j = vf.constructVariable("j", VariableFactory.INT);
		
		BoogieNonOldVar i = vf.constructVariable("i", VariableFactory.INT);
		BoogieNonOldVar n = vf.constructVariable("n", VariableFactory.INT);
		BoogieNonOldVar m = vf.constructVariable("m", VariableFactory.INT);
		
		BoogieNonOldVar A = vf.constructVariable("A", VariableFactory.INT_ARR);
		
		// Define Preconditions
		IAssumption pre1 = new ScriptAssumptionStatement(i, script.numeral("0"), "=");
		IAssumption pre2 = new ScriptAssumptionStatement(n, script.numeral("0"), ">=");
		IAssumption pre3 = new ScriptAssumptionStatement(m, script.term("select", A.getTerm(), script.numeral("0")), "=");
		IAssumption pre4 = new ScriptAssumptionStatement(j, script.numeral("0"), ">=");
		IAssumption pre5 = new ScriptAssumptionStatement(j, n.getTerm(), "<");
		
		// Define Program Statements
		IStatement ilen = new ScriptAssumptionStatement(i, n.getTerm(), "<");
		IStatement igen = new ScriptAssumptionStatement(i, n.getTerm(), ">=");
		IStatement mleai = new ScriptAssumptionStatement(m, script.term("select", A.getTerm(), i.getTerm()), "<");
		IStatement mgeai = new ScriptAssumptionStatement(m, script.term("select", A.getTerm(), i.getTerm()), ">=");
		
		IStatement meai = new ScriptAssignmentStatement(m, script.term("select", A.getTerm(), i.getTerm()));
		IStatement ipp = new ScriptAssignmentStatement(i, script.term("+", i.getTerm(), script.numeral("1")));
		
		// Define Postconditions
		IAssumption post = new ScriptAssumptionStatement(m, script.term("select", A.getTerm(), i.getTerm()), ">=");
		
		// Create Meta Alphabet for LTA
		List<IStatement> letters = Arrays.asList(ilen, igen, mleai, mgeai, meai, ipp);
		
		List<String> dest1 = 	   Arrays.asList("3",  "2",  "I",   "I",   "I",  "I");
		List<String> dest2 = 	   Arrays.asList("I",  "I",  "I",   "I",   "I",  "I");
		List<String> dest3 = 	   Arrays.asList("I",  "I",  "4",   "5",   "I",  "I");
		List<String> dest4 = 	   Arrays.asList("I",  "I",  "I",   "I",   "I",  "1");
		List<String> dest5 = 	   Arrays.asList("I",  "I",  "I",   "I",   "4",  "I");
		List<String> destI = 	   Arrays.asList("I",  "I",  "I",   "I",   "I",  "I");
		
		program.addRule(new BuchiTreeAutomatonRule<>(RankedBool.FALSE, "1", dest1));
		program.addRule(new BuchiTreeAutomatonRule<>(RankedBool.TRUE, "2", dest2));
		program.addRule(new BuchiTreeAutomatonRule<>(RankedBool.FALSE, "3", dest3));
		program.addRule(new BuchiTreeAutomatonRule<>(RankedBool.FALSE, "4", dest4));
		program.addRule(new BuchiTreeAutomatonRule<>(RankedBool.FALSE, "5", dest5));
		program.addRule(new BuchiTreeAutomatonRule<>(RankedBool.FALSE, "I", destI));
		
		List<IAssumption> preconditions = Arrays.asList(pre1, pre2, pre3, pre4, pre5);
		List<IAssumption> postconditions = Arrays.asList(post);
		
		MainVerificationLoop loop = new MainVerificationLoop(program, letters, preconditions, postconditions);
		loop.computeMainLoop();
		
		String out = "ArrayMAX: " + ((loop.isCorrect()) ? "Correct" : "Incorrect / unknown");
		assert loop.isCorrect() == true;
		
		return out;
	}
	
	/**
	 * [i = 0, t = A[0], t = 0]
	 * t = A[i] + 1
	 * A[i] = t
	 * [i < A[i]]
	 */
	public static String simpleTest() throws Exception {
		TraceGlobalVariables.reset();
		TraceToInterpolants.reset();
		BuchiTreeAutomaton<RankedBool, String> program = new BuchiTreeAutomaton<>(2);
		
		RankedBool.setRank(2);
		program.addInitState("1");
		program.addState("2");
		program.addState("3");
		program.setAllStatesFinal();
		
		VariableFactory vf = TraceGlobalVariables.getGlobalVariables().getVariableFactory();
		Script script = TraceGlobalVariables.getGlobalVariables().getManagedScript().getScript();
		
		BoogieNonOldVar i = vf.constructVariable("i", VariableFactory.INT);
		BoogieNonOldVar t = vf.constructVariable("t", VariableFactory.INT);
		BoogieNonOldVar A = vf.constructVariable("A", VariableFactory.INT_ARR);
		
		// Define Preconditions
		IAssumption pre1 = new ScriptAssumptionStatement(i, script.numeral("0"), "=");
		IAssumption pre2 = new ScriptAssumptionStatement(t, script.term("select", A.getTerm(), script.numeral("0")), "=");
		IAssumption pre3 = new ScriptAssumptionStatement(t, script.numeral("0"), "=");
		
		// Define Program Statements
		IStatement teapo = new ScriptAssignmentStatement(t, script.term("+", script.term("select", A.getTerm(), i.getTerm()), script.numeral("1")));
		IStatement aiet = new ScriptAssignmentStatement(A, script.term("store", A.getTerm(), i.getTerm(), t.getTerm()));
		
		// Define Postconditions
		IAssumption post = new ScriptAssumptionStatement(i, script.term("select", A.getTerm(), i.getTerm()), "<");
		
		// Create Meta Alphabet for LTA
		List<IStatement> letters = Arrays.asList(teapo, aiet);
		
		List<String> dest1 = 	   Arrays.asList("2",  "I");
		List<String> dest2 = 	   Arrays.asList("I",  "3");
		List<String> dest3 = 	   Arrays.asList("I",  "I");
		List<String> destI = 	   Arrays.asList("I",  "I");
		
		program.addRule(new BuchiTreeAutomatonRule<>(RankedBool.FALSE, "1", dest1));
		program.addRule(new BuchiTreeAutomatonRule<>(RankedBool.FALSE, "2", dest2));
		program.addRule(new BuchiTreeAutomatonRule<>(RankedBool.TRUE, "3", dest3));
		program.addRule(new BuchiTreeAutomatonRule<>(RankedBool.FALSE, "I", destI));
		
		List<IAssumption> preconditions = Arrays.asList(pre1, pre2, pre3);
		List<IAssumption> postconditions = Arrays.asList(post);
		
		MainVerificationLoop loop = new MainVerificationLoop(program, letters, preconditions, postconditions);
		loop.computeMainLoop();
		
		String out = "SimpleProg: " + ((loop.isCorrect()) ? "Correct" : "Incorrect / unknown");
		assert loop.isCorrect() == true;
		
		return out;
	}
	
	public static void main(String args[]) throws Exception {
		List<String> testResults = Arrays.asList(arrayMaxTest()); //simpleTest());
		
		for (String test : testResults) {
			System.err.println(test);
		}
	}
}
