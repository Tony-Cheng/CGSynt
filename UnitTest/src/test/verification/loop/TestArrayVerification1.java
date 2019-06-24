package test.verification.loop;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import cgsynt.Verification.MainVerificationLoop;
import cgsynt.interpol.IStatement;
import cgsynt.interpol.ScriptAssignmentStatement;
import cgsynt.interpol.ScriptPredicateAssumptionStatement;
import cgsynt.interpol.TraceGlobalVariables;
import cgsynt.interpol.TraceToInterpolants;
import cgsynt.interpol.VariableFactory;
import cgsynt.tree.buchi.BuchiTreeAutomaton;
import cgsynt.tree.buchi.BuchiTreeAutomatonRule;
import cgsynt.tree.buchi.lta.RankedBool;
import de.uni_freiburg.informatik.ultimate.logic.Logics;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Sort;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.boogie.BoogieNonOldVar;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.variables.IProgramVar;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.BasicPredicateFactory;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;

public class TestArrayVerification1 {

	@Test
	void test1() throws Exception {
		// Pre : None
		// Post: (A[3] = A[1] or A[3] = A[2]) and (A[3] > A[2] and A[3] >= A[2])
		MainVerificationLoop.resetAll();
		Script script = TraceGlobalVariables.getGlobalVariables().getManagedScript().getScript();
		script.declareFun("arr", new Sort[0], script.sort("Array", script.sort("Int"), script.sort("Int")));
		VariableFactory factory = TraceGlobalVariables.getGlobalVariables().getVariableFactory();
		BasicPredicateFactory predicateFactory = TraceToInterpolants.getTraceToInterpolants().getPredicateFactory();
		BoogieNonOldVar A = factory.constructVariable(VariableFactory.INT_ARR);
		IPredicate preconditions = TraceToInterpolants.getTraceToInterpolants().getTruePredicate();
		IPredicate postconditions = predicateFactory
				.newPredicate(script.term(">=", script.term("select", A.getTerm(), script.numeral("3")),
						script.term("select", A.getTerm(), script.numeral("1"))));
		postconditions = predicateFactory.and(postconditions,
				predicateFactory.newPredicate(script.term(">=", script.term("select", A.getTerm(), script.numeral("3")),
						script.term("select", A.getTerm(), script.numeral("2")))));

		BuchiTreeAutomaton<RankedBool, String> program = new BuchiTreeAutomaton<>(4);
		RankedBool.setRank(4);
		program.addInitState("s1");
		program.addState("s2");
		program.addState("s3");
		program.addState("s4");
		program.addState("s5");
		program.setAllStatesFinal();
		IStatement statement1 = new ScriptPredicateAssumptionStatement(
				predicateFactory.newPredicate(script.term("<", script.term("select", A.getTerm(), script.numeral("1")),
						script.term("select", A.getTerm(), script.numeral("2")))));
		IStatement statement2 = new ScriptPredicateAssumptionStatement(
				predicateFactory.newPredicate(script.term(">=", script.term("select", A.getTerm(), script.numeral("1")),
						script.term("select", A.getTerm(), script.numeral("2")))));
		IStatement statement3 = new ScriptAssignmentStatement(A, script.term("store", A.getTerm(), script.numeral("3"),
				script.term("select", A.getTerm(), script.numeral("2"))));
		IStatement statement4 = new ScriptAssignmentStatement(A, script.term("store", A.getTerm(), script.numeral("3"),
				script.term("select", A.getTerm(), script.numeral("1"))));

		List<IStatement> letters = new ArrayList<>(Arrays.asList(statement1, statement2, statement3, statement4));

		List<String> dest1 = new ArrayList<>(Arrays.asList("s2", "s3", "s5", "s5"));
		List<String> dest2 = new ArrayList<>(Arrays.asList("s5", "s5", "s4", "s5"));
		List<String> dest3 = new ArrayList<>(Arrays.asList("s5", "s5", "s5", "s4"));
		List<String> dest4 = new ArrayList<>(Arrays.asList("s5", "s5", "s5", "s5"));

		program.addRule(new BuchiTreeAutomatonRule<>(RankedBool.FALSE, "s1", dest1));
		program.addRule(new BuchiTreeAutomatonRule<>(RankedBool.FALSE, "s2", dest2));
		program.addRule(new BuchiTreeAutomatonRule<>(RankedBool.FALSE, "s3", dest3));
		program.addRule(new BuchiTreeAutomatonRule<>(RankedBool.TRUE, "s4", dest4));
		program.addRule(new BuchiTreeAutomatonRule<>(RankedBool.FALSE, "s5", dest4));

		MainVerificationLoop loop = new MainVerificationLoop(program, letters, preconditions, postconditions);
		loop.computeMainLoop();
		assertTrue(loop.isCorrect());
	}
	
	@Test
	void test2() throws Exception {
		// Pre : None
		// Post: (A[3] = A[1] or A[3] = A[2]) and (A[3] > A[2] and A[3] >= A[2])
		MainVerificationLoop.resetAll();
		Script script = TraceGlobalVariables.getGlobalVariables().getManagedScript().getScript();
		script.declareFun("arr", new Sort[0], script.sort("Array", script.sort("Int"), script.sort("Int")));
		VariableFactory factory = TraceGlobalVariables.getGlobalVariables().getVariableFactory();
		BasicPredicateFactory predicateFactory = TraceToInterpolants.getTraceToInterpolants().getPredicateFactory();
		BoogieNonOldVar A = factory.constructVariable(VariableFactory.INT_ARR);
		IPredicate preconditions = TraceToInterpolants.getTraceToInterpolants().getTruePredicate();
		IPredicate postconditions = predicateFactory
				.newPredicate(script.term("<", script.term("select", A.getTerm(), script.numeral("3")),
						script.term("select", A.getTerm(), script.numeral("1"))));
		postconditions = predicateFactory.and(postconditions,
				predicateFactory.newPredicate(script.term(">=", script.term("select", A.getTerm(), script.numeral("3")),
						script.term("select", A.getTerm(), script.numeral("2")))));

		BuchiTreeAutomaton<RankedBool, String> program = new BuchiTreeAutomaton<>(4);
		RankedBool.setRank(4);
		program.addInitState("s1");
		program.addState("s2");
		program.addState("s3");
		program.addState("s4");
		program.addState("s5");
		program.setAllStatesFinal();
		IStatement statement1 = new ScriptPredicateAssumptionStatement(
				predicateFactory.newPredicate(script.term("<", script.term("select", A.getTerm(), script.numeral("1")),
						script.term("select", A.getTerm(), script.numeral("2")))));
		IStatement statement2 = new ScriptPredicateAssumptionStatement(
				predicateFactory.newPredicate(script.term(">=", script.term("select", A.getTerm(), script.numeral("1")),
						script.term("select", A.getTerm(), script.numeral("2")))));
		IStatement statement3 = new ScriptAssignmentStatement(A, script.term("store", A.getTerm(), script.numeral("3"),
				script.term("select", A.getTerm(), script.numeral("2"))));
		IStatement statement4 = new ScriptAssignmentStatement(A, script.term("store", A.getTerm(), script.numeral("3"),
				script.term("select", A.getTerm(), script.numeral("1"))));

		List<IStatement> letters = new ArrayList<>(Arrays.asList(statement1, statement2, statement3, statement4));

		List<String> dest1 = new ArrayList<>(Arrays.asList("s2", "s3", "s5", "s5"));
		List<String> dest2 = new ArrayList<>(Arrays.asList("s5", "s5", "s4", "s5"));
		List<String> dest3 = new ArrayList<>(Arrays.asList("s5", "s5", "s5", "s4"));
		List<String> dest4 = new ArrayList<>(Arrays.asList("s5", "s5", "s5", "s5"));

		program.addRule(new BuchiTreeAutomatonRule<>(RankedBool.FALSE, "s1", dest1));
		program.addRule(new BuchiTreeAutomatonRule<>(RankedBool.FALSE, "s2", dest2));
		program.addRule(new BuchiTreeAutomatonRule<>(RankedBool.FALSE, "s3", dest3));
		program.addRule(new BuchiTreeAutomatonRule<>(RankedBool.TRUE, "s4", dest4));
		program.addRule(new BuchiTreeAutomatonRule<>(RankedBool.FALSE, "s5", dest4));

		MainVerificationLoop loop = new MainVerificationLoop(program, letters, preconditions, postconditions);
		loop.computeMainLoop();
		assertFalse(loop.isCorrect());
	}

}
