package test.synthesis;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import cgsynt.interpol.IStatement;
import cgsynt.interpol.ScriptAssignmentStatement;
import cgsynt.interpol.ScriptAssumptionStatement;
import cgsynt.interpol.TraceGlobalVariables;
import cgsynt.interpol.TraceToInterpolants;
import cgsynt.interpol.VariableFactory;
import cgsynt.synthesis.SynthesisLoop;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.boogie.BoogieNonOldVar;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.BasicPredicateFactory;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;

public class TestSynthesisLoop1 {

	@Test
	void test1() throws Exception {
		SynthesisLoop.resetAll();
		VariableFactory vf = TraceGlobalVariables.getGlobalVariables().getVariableFactory();
		Script script = TraceGlobalVariables.getGlobalVariables().getManagedScript().getScript();
		BoogieNonOldVar i = vf.constructVariable("i", VariableFactory.INT);
		BoogieNonOldVar n = vf.constructVariable("n", VariableFactory.INT);

		BasicPredicateFactory predicateFactory = TraceToInterpolants.getTraceToInterpolants().getPredicateFactory();
		IStatement ilen = new ScriptAssumptionStatement(i, n.getTerm(), "<");
		IStatement igen = new ScriptAssumptionStatement(i, n.getTerm(), ">=");
		IStatement ipp = new ScriptAssignmentStatement(i, script.term("+", i.getTerm(), script.numeral("1")));
		IStatement ie0 = new ScriptAssumptionStatement(i, script.numeral("0"), "=");
		IStatement ie1 = new ScriptAssumptionStatement(i, script.numeral("1"), "=");
		IStatement ien = new ScriptAssumptionStatement(i, n.getTerm(), "=");
		IPredicate preconditions = predicateFactory.newPredicate(script.term("=", i.getTerm(), script.numeral("0")));
		IPredicate postconditions = predicateFactory.newPredicate(script.term("=", i.getTerm(), script.numeral("1")));
		List<IStatement> transitionAlphabet = new ArrayList<>();
		transitionAlphabet.add(ipp);
		SynthesisLoop synthesis = new SynthesisLoop(transitionAlphabet, preconditions, postconditions);
		synthesis.computeMainLoop();
		System.out.println("Test 1");
		System.out.println(synthesis.isCorrect());
	}

	@Test
	void test2() throws Exception {
		SynthesisLoop.resetAll();
		VariableFactory vf = TraceGlobalVariables.getGlobalVariables().getVariableFactory();
		Script script = TraceGlobalVariables.getGlobalVariables().getManagedScript().getScript();
		BoogieNonOldVar i = vf.constructVariable("i", VariableFactory.INT);
		BoogieNonOldVar n = vf.constructVariable("n", VariableFactory.INT);

		BasicPredicateFactory predicateFactory = TraceToInterpolants.getTraceToInterpolants().getPredicateFactory();
		IStatement ilen = new ScriptAssumptionStatement(i, n.getTerm(), "<");
		IStatement ipp = new ScriptAssignmentStatement(i, script.term("+", i.getTerm(), script.numeral("1")));
		// IStatement imm = new ScriptAssignmentStatement(i, script.term("-", i.getTerm(), script.numeral("1")));
		IPredicate preconditions = predicateFactory.newPredicate(script.term("=", i.getTerm(), script.numeral("0")));
		IPredicate postconditions = predicateFactory.newPredicate(script.term("=", i.getTerm(), n.getTerm()));
		List<IStatement> transitionAlphabet = new ArrayList<>();
		transitionAlphabet.add(ipp);
		transitionAlphabet.add(ilen);
		// transitionAlphabet.add(imm);
		SynthesisLoop synthesis = new SynthesisLoop(transitionAlphabet, preconditions, postconditions);
		synthesis.computeMainLoop();
		System.out.println("Test 2");
		System.out.println(synthesis.isCorrect());

	}
}
