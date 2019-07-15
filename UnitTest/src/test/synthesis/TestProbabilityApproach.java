package test.synthesis;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import cgsynt.interpol.IStatement;
import cgsynt.interpol.ScriptAssignmentStatement;
import cgsynt.interpol.ScriptAssumptionStatement;
import cgsynt.interpol.ScriptPredicateAssumptionStatement;
import cgsynt.interpol.TraceGlobalVariables;
import cgsynt.interpol.TraceToInterpolants;
import cgsynt.interpol.VariableFactory;
import cgsynt.synthesis.SynthesisLoop;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.boogie.BoogieNonOldVar;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.BasicPredicateFactory;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;

public class TestProbabilityApproach {

//	@Test
//	void test1() throws Exception {
//		SynthesisLoop.resetAll();
//		VariableFactory vf = TraceGlobalVariables.getGlobalVariables().getVariableFactory();
//		Script script = TraceGlobalVariables.getGlobalVariables().getManagedScript().getScript();
//		BoogieNonOldVar i = vf.constructVariable("i", VariableFactory.INT);
//		BoogieNonOldVar n = vf.constructVariable("n", VariableFactory.INT);
//
//		BasicPredicateFactory predicateFactory = TraceToInterpolants.getTraceToInterpolants().getPredicateFactory();
//		IStatement ilen = new ScriptAssumptionStatement(i, n.getTerm(), "<");
//		IStatement igen = new ScriptAssumptionStatement(i, n.getTerm(), ">=");
//		IStatement ipp = new ScriptAssignmentStatement(i, script.term("+", i.getTerm(), script.numeral("1")));
//		IStatement imm = new ScriptAssignmentStatement(i, script.term("-", i.getTerm(), script.numeral("1")));
//		IStatement ie0 = new ScriptAssumptionStatement(i, script.numeral("0"), "=");
//		IStatement ie1 = new ScriptAssumptionStatement(i, script.numeral("1"), "=");
//		IStatement ien = new ScriptAssumptionStatement(i, n.getTerm(), "=");
//		IPredicate preconditions = predicateFactory.newPredicate(script.term("=", i.getTerm(), script.numeral("0")));
//		IPredicate postconditions = predicateFactory.newPredicate(script.term("=", i.getTerm(), script.numeral("2")));
//		List<IStatement> transitionAlphabet = new ArrayList<>();
//		transitionAlphabet.add(ipp);
//		transitionAlphabet.add(imm);
//		SynthesisLoop synthesis = new SynthesisLoop(transitionAlphabet, preconditions, postconditions);
//		synthesis.computeMainLoopRandomly(3);;
//		System.out.println("Test 1");
//		System.out.println(synthesis.isCorrect());
//	}
//	
//	@Test
//	void test2() throws Exception {
//		SynthesisLoop.resetAll();
//		VariableFactory vf = TraceGlobalVariables.getGlobalVariables().getVariableFactory();
//		Script script = TraceGlobalVariables.getGlobalVariables().getManagedScript().getScript();
//		BoogieNonOldVar i = vf.constructVariable("i", VariableFactory.INT);
//		BoogieNonOldVar n = vf.constructVariable("n", VariableFactory.INT);
//
//		BasicPredicateFactory predicateFactory = TraceToInterpolants.getTraceToInterpolants().getPredicateFactory();
//		IStatement ilen = new ScriptAssumptionStatement(i, n.getTerm(), "<");
//		IStatement ipp = new ScriptAssignmentStatement(i, script.term("+", i.getTerm(), script.numeral("1")));
//		// IStatement imm = new ScriptAssignmentStatement(i, script.term("-", i.getTerm(), script.numeral("1")));
//		IPredicate preconditions = predicateFactory.newPredicate(script.term("=", i.getTerm(), script.numeral("0")));
//		IPredicate postconditions = predicateFactory.newPredicate(script.term("=", i.getTerm(), n.getTerm()));
//		List<IStatement> transitionAlphabet = new ArrayList<>();
//		transitionAlphabet.add(ipp);
//		transitionAlphabet.add(ilen);
//		// transitionAlphabet.add(imm);
//		SynthesisLoop synthesis = new SynthesisLoop(transitionAlphabet, preconditions, postconditions);
//		synthesis.computeMainLoopRandomly(5);;
//		System.out.println("Test 2");
//		System.out.println(synthesis.isCorrect());
//
//	}
//	
//	@Test void test3() throws Exception {
//		SynthesisLoop.resetAll();
//		VariableFactory vf = TraceGlobalVariables.getGlobalVariables().getVariableFactory();
//		Script script = TraceGlobalVariables.getGlobalVariables().getManagedScript().getScript();
//		BoogieNonOldVar j = vf.constructVariable("j", VariableFactory.INT);
//
//		BoogieNonOldVar i = vf.constructVariable("i", VariableFactory.INT);
//		BoogieNonOldVar n = vf.constructVariable("n", VariableFactory.INT);
//		BoogieNonOldVar m = vf.constructVariable("m", VariableFactory.INT);
//
//		BoogieNonOldVar A = vf.constructVariable("A", VariableFactory.INT_ARR);
//		BasicPredicateFactory predicateFactory = TraceToInterpolants.getTraceToInterpolants().getPredicateFactory();
//		IStatement igen = new ScriptAssumptionStatement(i, n.getTerm(), ">=");
//		IStatement mleai = new ScriptPredicateAssumptionStatement(predicateFactory
//				.newPredicate(script.term("<", m.getTerm(), script.term("select", A.getTerm(), i.getTerm()))));
//		IStatement meai = new ScriptAssignmentStatement(m, script.term("select", A.getTerm(), i.getTerm()));
//		IStatement ipp = new ScriptAssignmentStatement(i, script.term("+", i.getTerm(), script.numeral("1")));
//		List<IStatement> transitionAlphabet = new ArrayList<>();
//		transitionAlphabet.add(igen);
//		transitionAlphabet.add(mleai);
//		transitionAlphabet.add(meai);
//		transitionAlphabet.add(ipp);
//		
//		IPredicate preconditions = predicateFactory.newPredicate(script.term("=", i.getTerm(), script.numeral("0")));
//		preconditions = predicateFactory.and(preconditions,
//				predicateFactory.newPredicate(script.term(">=", n.getTerm(), script.numeral("1"))));
//		preconditions = predicateFactory.and(preconditions, predicateFactory
//				.newPredicate(script.term("=", m.getTerm(), script.term("select", A.getTerm(), script.numeral("0")))));
//		preconditions = predicateFactory.and(preconditions,
//				predicateFactory.newPredicate(script.term(">=", j.getTerm(), script.numeral("0"))));
//		preconditions = predicateFactory.and(preconditions,
//				predicateFactory.newPredicate(script.term("<", j.getTerm(), n.getTerm())));
//		IPredicate postconditions = predicateFactory
//				.newPredicate(script.term(">=", m.getTerm(), script.term("select", A.getTerm(), j.getTerm())));
//		SynthesisLoop synthesis = new SynthesisLoop(transitionAlphabet, preconditions, postconditions);
//		synthesis.computeMainLoopRandomly(5);;
//		System.out.println("Test 3");
//		System.out.println(synthesis.isCorrect());
//
//	}
}
