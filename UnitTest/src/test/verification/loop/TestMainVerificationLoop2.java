package test.verification.loop;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

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

public class TestMainVerificationLoop2 {
	
	/**
	 * [i == 0 & n >= 0]
	 * while i < n:
	 * 		i++
	 * [i==n]
	 */
	@Test
	void test1() throws Exception {
		TraceGlobalVariables.reset();
		TraceToInterpolants.reset();
		BuchiTreeAutomaton<RankedBool, String> program = new BuchiTreeAutomaton<>(3);
		RankedBool.setRank(3);
		program.addInitState("s1");
		program.addState("s2");
		program.addState("s3");
		program.addState("s4");
		
		program.setAllStatesFinal();
		VariableFactory vf = TraceGlobalVariables.getGlobalVariables().getVariableFactory();
		Script script = TraceGlobalVariables.getGlobalVariables().getManagedScript().getScript();
		
		BoogieNonOldVar i = vf.constructVariable("i", VariableFactory.INT);
		BoogieNonOldVar n = vf.constructVariable("n", VariableFactory.INT);
		
		IAssumption pre1 = new ScriptAssumptionStatement(i, script.numeral("0"), "=");
		IAssumption pre2 = new ScriptAssumptionStatement(n, script.numeral("0"), ">=");
		
		IStatement ilen = new ScriptAssumptionStatement(i, n.getTerm(), "<");
		IStatement ipp = new ScriptAssignmentStatement(i, script.term("+", i.getTerm(), script.numeral("1")));
		IStatement igen = new ScriptAssumptionStatement(i, n.getTerm(), ">=");
		
		IAssumption post = new ScriptAssumptionStatement(i, n.getTerm(), ">="); // originally type="="
		
		List<IStatement> letters = new ArrayList<IStatement>();
		letters.add(ilen);
		letters.add(ipp);
		letters.add(igen);
		
		List<String> dest1 = Arrays.asList("s4", "s3", "s2");
		program.addRule(new BuchiTreeAutomatonRule<>(RankedBool.FALSE, "s1", dest1));

		//List<String> dest2false = Arrays.asList("s3", "s3", "s3");
		//program.addRule(new BuchiTreeAutomatonRule<>(RankedBool.FALSE, "s2", dest2false));
		
		List<String> dest2true = Arrays.asList("s3", "s3", "s3");
		program.addRule(new BuchiTreeAutomatonRule<>(RankedBool.TRUE, "s2", dest2true));
		
		List<String> dest3 = Arrays.asList("s3", "s3", "s3");
		program.addRule(new BuchiTreeAutomatonRule<>(RankedBool.FALSE, "s3", dest3));
		
		List<String> dest4 = Arrays.asList("s3", "s1", "s3");
		program.addRule(new BuchiTreeAutomatonRule<>(RankedBool.FALSE, "s4", dest4));
	
		List<IAssumption> preconditions = Arrays.asList(pre1, pre2);
		List<IAssumption> postconditions = Arrays.asList(post);
		
		MainVerificationLoop loop = new MainVerificationLoop(program, letters, preconditions, postconditions);
		loop.computeMainLoop();
		assertTrue(loop.isCorrect());
	}
	
	@Test
	void mult() throws Exception {
		/**
		 * [i = 0 & s = 0]
		 * while i < y:
		 * 		s += x
		 * [s = x * y & i = y]		 
		 */
		
		TraceGlobalVariables.reset();
		TraceToInterpolants.reset();
		BuchiTreeAutomaton<RankedBool, String> program = new BuchiTreeAutomaton<>(3);
		RankedBool.setRank(3);
		program.addInitState("s1");
		program.addState("s2");
		program.addState("s3");
		program.addState("s4");
		
		program.setAllStatesFinal();
		VariableFactory vf = TraceGlobalVariables.getGlobalVariables().getVariableFactory();
		Script script = TraceGlobalVariables.getGlobalVariables().getManagedScript().getScript();
		
		BoogieNonOldVar i = vf.constructVariable("i", VariableFactory.INT);
		BoogieNonOldVar s = vf.constructVariable("s", VariableFactory.INT);
		BoogieNonOldVar x = vf.constructVariable("x", VariableFactory.INT);
		BoogieNonOldVar y = vf.constructVariable("y", VariableFactory.INT);
		
		IStatement pre1 = new ScriptAssumptionStatement(i, script.numeral("0"), "=");
		IStatement pre2 = new ScriptAssumptionStatement(s, script.numeral("0"), "=");
		
		IStatement iley = new ScriptAssumptionStatement(i, y.getTerm(), "<");
		IStatement spex = new ScriptAssignmentStatement(s, script.term("+", s.getTerm(), x.getTerm()));
		IStatement igey = new ScriptAssumptionStatement(i, y.getTerm(), ">=");
		
		IAssumption post1 = new ScriptAssumptionStatement(s, script.term("*", x.getTerm(), y.getTerm()), "=");
		IAssumption post2 = new ScriptAssumptionStatement(i, y.getTerm(), "=");
		
		List<IStatement> letters = new ArrayList<IStatement>();
		letters.add(iley);
		letters.add(spex);
		letters.add(igey);
		
		List<String> dest1 = Arrays.asList("s4", "s3", "s2");
		program.addRule(new BuchiTreeAutomatonRule<>(RankedBool.FALSE, "s1", dest1));
		
		List<String> dest2 = Arrays.asList("s3", "s3", "s3");
		program.addRule(new BuchiTreeAutomatonRule<>(RankedBool.TRUE, "s2", dest2));
		
		List<String> dest3 = Arrays.asList("s3", "s3", "s3");
		program.addRule(new BuchiTreeAutomatonRule<>(RankedBool.FALSE, "s3", dest3));
		
		List<String> dest4 = Arrays.asList("s3", "s1", "s3");
		program.addRule(new BuchiTreeAutomatonRule<>(RankedBool.FALSE, "s4", dest4));
	
		List<IStatement> preconditions = Arrays.asList(pre1, pre2);
		List<IAssumption> postconditions = Arrays.asList(post1, post2);
		
		MainVerificationLoop loop = new MainVerificationLoop(program, letters, preconditions, postconditions);
		loop.computeMainLoop();
		assertTrue(loop.isCorrect());
	}
}