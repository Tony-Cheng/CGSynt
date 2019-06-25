package test.verification.loop;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
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
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.BasicPredicateFactory;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.PredicateUnifier;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.predicates.PredicateFactory;

public class TestMainVerificationLoop1 {

//	@Test
//	void test1() throws Exception {
//		// No pre and post conditions.
//		// Only a single statement (x++).
//		TraceGlobalVariables.reset();
//		TraceToInterpolants.reset();
//		BuchiTreeAutomaton<RankedBool, String> aut = new BuchiTreeAutomaton<>(1);
//		aut.addInitState("s1");
//		aut.addState("s1");
//		aut.addState("s2");
//		aut.addState("s3");
//		aut.setAllStatesFinal();
//		VariableFactory vf = TraceGlobalVariables.getGlobalVariables().getVariableFactory();
//		BoogieNonOldVar x = vf.constructVariable("x", VariableFactory.INT);
//		Script script = TraceGlobalVariables.getGlobalVariables().getManagedScript().getScript();
//
//		IStatement xpp = new ScriptAssignmentStatement(x, script.term("+", x.getTerm(), script.numeral("1")));
//		List<IStatement> letters = new ArrayList<>();
//		letters.add(xpp);
//
//		List<String> dest1 = new ArrayList<>();
//		dest1.add("s2");
//		aut.addRule(new BuchiTreeAutomatonRule<>(RankedBool.FALSE, "s1", dest1));
//
//		List<String> dest2 = new ArrayList<>();
//		dest2.add("s3");
//		aut.addRule(new BuchiTreeAutomatonRule<>(RankedBool.TRUE, "s2", dest2));
//
//		List<String> dest3 = new ArrayList<>();
//		dest3.add("s3");
//		aut.addRule(new BuchiTreeAutomatonRule<>(RankedBool.FALSE, "s3", dest3));
//
//		MainVerificationLoop loop = new MainVerificationLoop(aut, letters, new ArrayList<>(), new ArrayList<>());
//		loop.computeMainLoop();
//		assertTrue(loop.isCorrect());
//	}
//
//	@Test
//	void test2() throws Exception {
//		// {x==0} x++ {x>=1}
//		TraceGlobalVariables.reset();
//		TraceToInterpolants.reset();
//		BuchiTreeAutomaton<RankedBool, String> aut = new BuchiTreeAutomaton<>(1);
//		aut.addInitState("s1");
//		aut.addState("s1");
//		aut.addState("s2");
//		aut.addState("s3");
//		aut.setAllStatesFinal();
//		VariableFactory vf = TraceGlobalVariables.getGlobalVariables().getVariableFactory();
//		BoogieNonOldVar x = vf.constructVariable("x", VariableFactory.INT);
//		Script script = TraceGlobalVariables.getGlobalVariables().getManagedScript().getScript();
//
//		IStatement xpp = new ScriptAssignmentStatement(x, script.term("+", x.getTerm(), script.numeral("1")));
//		List<IStatement> letters = new ArrayList<>();
//		letters.add(xpp);
//
//		List<String> dest1 = new ArrayList<>();
//		dest1.add("s2");
//		aut.addRule(new BuchiTreeAutomatonRule<>(RankedBool.FALSE, "s1", dest1));
//
//		List<String> dest2 = new ArrayList<>();
//		dest2.add("s3");
//		aut.addRule(new BuchiTreeAutomatonRule<>(RankedBool.TRUE, "s2", dest2));
//
//		List<String> dest3 = new ArrayList<>();
//		dest3.add("s3");
//		aut.addRule(new BuchiTreeAutomatonRule<>(RankedBool.FALSE, "s3", dest3));
//
//		List<IAssumption> preconditions = new ArrayList<>();
//		List<IAssumption> postconditions = new ArrayList<>();
//
//		IAssumption xe0 = new ScriptAssumptionStatement(x, script.numeral("0"), "=");
//		IAssumption xl1 = new ScriptAssumptionStatement(x, script.numeral("1"), ">=");
//
//		preconditions.add(xe0);
//		postconditions.add(xl1);
//
//		MainVerificationLoop loop = new MainVerificationLoop(aut, letters, preconditions, postconditions);
//		loop.computeMainLoop();
//		assertTrue(loop.isCorrect());
//	}
//
//	@Test
//	void test3() throws Exception {
//		// {x==0} x++ {x<1}
//		TraceGlobalVariables.reset();
//		TraceToInterpolants.reset();
//		BuchiTreeAutomaton<RankedBool, String> aut = new BuchiTreeAutomaton<>(1);
//		aut.addInitState("s1");
//		aut.addState("s1");
//		aut.addState("s2");
//		aut.addState("s3");
//		aut.setAllStatesFinal();
//		VariableFactory vf = TraceGlobalVariables.getGlobalVariables().getVariableFactory();
//		BoogieNonOldVar x = vf.constructVariable("x", VariableFactory.INT);
//		Script script = TraceGlobalVariables.getGlobalVariables().getManagedScript().getScript();
//
//		IStatement xpp = new ScriptAssignmentStatement(x, script.term("+", x.getTerm(), script.numeral("1")));
//		List<IStatement> letters = new ArrayList<>();
//		letters.add(xpp);
//
//		List<String> dest1 = new ArrayList<>();
//		dest1.add("s2");
//		aut.addRule(new BuchiTreeAutomatonRule<>(RankedBool.FALSE, "s1", dest1));
//
//		List<String> dest2 = new ArrayList<>();
//		dest2.add("s3");
//		aut.addRule(new BuchiTreeAutomatonRule<>(RankedBool.TRUE, "s2", dest2));
//
//		List<String> dest3 = new ArrayList<>();
//		dest3.add("s3");
//		aut.addRule(new BuchiTreeAutomatonRule<>(RankedBool.FALSE, "s3", dest3));
//
//		List<IAssumption> preconditions = new ArrayList<>();
//		List<IAssumption> postconditions = new ArrayList<>();
//
//		IAssumption xe0 = new ScriptAssumptionStatement(x, script.numeral("0"), "=");
//		IAssumption xl1 = new ScriptAssumptionStatement(x, script.numeral("1"), "<");
//
//		preconditions.add(xe0);
//		postconditions.add(xl1);
//
//		MainVerificationLoop loop = new MainVerificationLoop(aut, letters, preconditions, postconditions);
//		loop.computeMainLoop();
//		assertFalse(loop.isCorrect());
//	}

	@Test
	void tes4() throws Exception {
		// {x==0} x++ {x>=1}
		MainVerificationLoop.resetAll();
		BuchiTreeAutomaton<RankedBool, String> aut = new BuchiTreeAutomaton<>(1);
		aut.addInitState("s1");
		aut.addState("s1");
		aut.addState("s2");
		aut.addState("s3");
		aut.setAllStatesFinal();
		VariableFactory vf = TraceGlobalVariables.getGlobalVariables().getVariableFactory();
		BoogieNonOldVar x = vf.constructVariable("x", VariableFactory.INT);
		Script script = TraceGlobalVariables.getGlobalVariables().getManagedScript().getScript();

		IStatement xpp = new ScriptAssignmentStatement(x, script.term("+", x.getTerm(), script.numeral("1")));
		List<IStatement> letters = new ArrayList<>();
		letters.add(xpp);

		List<String> dest1 = new ArrayList<>();
		dest1.add("s2");
		aut.addRule(new BuchiTreeAutomatonRule<>(RankedBool.FALSE, "s1", dest1));

		List<String> dest2 = new ArrayList<>();
		dest2.add("s3");
		aut.addRule(new BuchiTreeAutomatonRule<>(RankedBool.TRUE, "s2", dest2));

		List<String> dest3 = new ArrayList<>();
		dest3.add("s3");
		aut.addRule(new BuchiTreeAutomatonRule<>(RankedBool.FALSE, "s3", dest3));

		BasicPredicateFactory predicateFactory = TraceToInterpolants.getTraceToInterpolants().getPredicateFactory();
		IPredicate preconditions = predicateFactory.newPredicate(script.term("=", x.getTerm(), script.numeral("0")));
		IPredicate postconditions = predicateFactory.newPredicate(script.term(">=", x.getTerm(), script.numeral("1")));

		MainVerificationLoop loop = new MainVerificationLoop(aut, letters, preconditions, postconditions);
		loop.computeMainLoop();
		assertTrue(loop.isCorrect());
	}

	@Test
	void test5() throws Exception {
		// {x==0} x++ {x<1}
		MainVerificationLoop.resetAll();
		BuchiTreeAutomaton<RankedBool, String> aut = new BuchiTreeAutomaton<>(1);
		aut.addInitState("s1");
		aut.addState("s1");
		aut.addState("s2");
		aut.addState("s3");
		aut.setAllStatesFinal();
		VariableFactory vf = TraceGlobalVariables.getGlobalVariables().getVariableFactory();
		BoogieNonOldVar x = vf.constructVariable("x", VariableFactory.INT);
		Script script = TraceGlobalVariables.getGlobalVariables().getManagedScript().getScript();

		IStatement xpp = new ScriptAssignmentStatement(x, script.term("+", x.getTerm(), script.numeral("1")));
		List<IStatement> letters = new ArrayList<>();
		letters.add(xpp);

		List<String> dest1 = new ArrayList<>();
		dest1.add("s2");
		aut.addRule(new BuchiTreeAutomatonRule<>(RankedBool.FALSE, "s1", dest1));

		List<String> dest2 = new ArrayList<>();
		dest2.add("s3");
		aut.addRule(new BuchiTreeAutomatonRule<>(RankedBool.TRUE, "s2", dest2));

		List<String> dest3 = new ArrayList<>();
		dest3.add("s3");
		aut.addRule(new BuchiTreeAutomatonRule<>(RankedBool.FALSE, "s3", dest3));

		BasicPredicateFactory predicateFactory = TraceToInterpolants.getTraceToInterpolants().getPredicateFactory();
		IPredicate preconditions = predicateFactory.newPredicate(script.term("=", x.getTerm(), script.numeral("0")));
		IPredicate postconditions = predicateFactory.newPredicate(script.term("<", x.getTerm(), script.numeral("1")));

		MainVerificationLoop loop = new MainVerificationLoop(aut, letters, preconditions, postconditions);
		loop.computeMainLoop();
		assertFalse(loop.isCorrect());
	}
}
