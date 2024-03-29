package test.synthesis;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import cgsynt.core.GlobalsConfigurer;
import cgsynt.interpol.IAssumption;
import cgsynt.interpol.IStatement;
import cgsynt.interpol.ScriptAssignmentStatement;
import cgsynt.interpol.ScriptAssumptionStatement;
import cgsynt.interpol.TraceGlobalVariables;
import cgsynt.interpol.VariableFactory;
import cgsynt.synthesis.SynthesisLoop;
import cgsynt.synthesis.SynthesisLoopTerminationWithoutGeneralization;
import cgsynt.synthesis.SynthesisLoopWithTermination;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.boogie.BoogieNonOldVar;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.BasicPredicateFactory;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;

public class TestSynthesisTerminationWithoutGeneralization {

	@Test
	void test1() throws Exception {
		// i++; i++
		TraceGlobalVariables globalVars = new TraceGlobalVariables();
		VariableFactory vf = globalVars.getVariableFactory();
		Script script = globalVars.getManagedScript().getScript();
		BoogieNonOldVar i = vf.constructVariable("i", VariableFactory.INT);

		BasicPredicateFactory predicateFactory = globalVars.getPredicateFactory();

		IStatement ipp = new ScriptAssignmentStatement(i, script.term("+", i.getTerm(), script.numeral("1")),
				globalVars.getManagedScript(), vf.getSymbolTable());
		IStatement imm = new ScriptAssignmentStatement(i, script.term("-", i.getTerm(), script.numeral("1")),
				globalVars.getManagedScript(), vf.getSymbolTable());

		IPredicate preconditions = predicateFactory.newPredicate(script.term("=", i.getTerm(), script.numeral("0")));
		IPredicate postconditions = predicateFactory.newPredicate(script.term("=", i.getTerm(), script.numeral("2")));
		List<IStatement> transitionAlphabet = new ArrayList<>();
		transitionAlphabet.add(ipp);
		transitionAlphabet.add(imm);
		SynthesisLoopTerminationWithoutGeneralization synthesis = new SynthesisLoopTerminationWithoutGeneralization(
				transitionAlphabet, preconditions, postconditions, globalVars);
		synthesis.computeMainLoop();
		System.out.println("Test 1");
		System.out.println(synthesis.isCorrect());
		synthesis.printProgram();

	}

	@Test
	void test2() throws Exception {
		// while i < n, i++
		TraceGlobalVariables globalVars = new TraceGlobalVariables();
		VariableFactory vf = globalVars.getVariableFactory();
		Script script = globalVars.getManagedScript().getScript();
		BoogieNonOldVar i = vf.constructVariable("i", VariableFactory.INT);
		BoogieNonOldVar n = vf.constructVariable("n", VariableFactory.INT);

		BasicPredicateFactory predicateFactory = globalVars.getPredicateFactory();
		IAssumption iln = new ScriptAssumptionStatement(i, n.getTerm(), "<", globalVars.getManagedScript(),
				vf.getSymbolTable());
		IStatement ipp = new ScriptAssignmentStatement(i, script.term("+", i.getTerm(), script.numeral("1")),
				globalVars.getManagedScript(), vf.getSymbolTable());

		IPredicate preconditions = predicateFactory.newPredicate(script.term("=", i.getTerm(), script.numeral("0")));
		preconditions = predicateFactory.and(preconditions,
				predicateFactory.newPredicate(script.term(">", n.getTerm(), script.numeral("0"))));
		IPredicate postconditions = predicateFactory.newPredicate(script.term("=", i.getTerm(), n.getTerm()));

		List<IStatement> transitionAlphabet = new ArrayList<>();
		transitionAlphabet.add(ipp);
		transitionAlphabet.add(iln);
		// transitionAlphabet.add(imm);
		SynthesisLoopTerminationWithoutGeneralization synthesis = new SynthesisLoopTerminationWithoutGeneralization(
				transitionAlphabet, preconditions, postconditions, globalVars);
		IAssumption igen = synthesis.getNegation().get(iln);
		synthesis.addState(1, true, false);
		synthesis.addState(2, false, true);
		synthesis.addRule(1, ipp, 2);
		synthesis.addRule(2, iln, 1);
		synthesis.addRule(2, igen, 1);
		synthesis.addRule(2, ipp, 1);
		synthesis.addRule(1, iln, 1);
		synthesis.addRule(1, igen, 1);
		synthesis.addRule(1, ipp, 1);


		// synthesis.addRule(1, igen, 2);

		synthesis.computeMainLoop();
		System.out.println("Test 2");
		System.out.println(synthesis.isCorrect());
		synthesis.printProgram();
	}
}
