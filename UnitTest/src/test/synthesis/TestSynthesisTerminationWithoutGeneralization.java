package test.synthesis;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import cgsynt.core.GlobalsConfigurer;
import cgsynt.interpol.IStatement;
import cgsynt.interpol.ScriptAssignmentStatement;
import cgsynt.interpol.ScriptAssumptionStatement;
import cgsynt.interpol.TraceGlobalVariables;
import cgsynt.interpol.VariableFactory;
import cgsynt.synthesis.SynthesisLoopTerminationWithoutGeneralization;
import cgsynt.synthesis.SynthesisLoopWithTermination;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.boogie.BoogieNonOldVar;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.BasicPredicateFactory;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;

public class TestSynthesisTerminationWithoutGeneralization {

	@Test
	void test2() throws Exception {
		// while i < n, i++
		TraceGlobalVariables globalVars = GlobalsConfigurer.configureForTermination();
		VariableFactory vf = globalVars.getVariableFactory();
		Script script = globalVars.getManagedScript().getScript();
		BoogieNonOldVar i = vf.constructVariable("i", VariableFactory.INT);
		BoogieNonOldVar n = vf.constructVariable("n", VariableFactory.INT);

		BasicPredicateFactory predicateFactory = globalVars.getPredicateFactory();
		IStatement ilen = new ScriptAssumptionStatement(i, n.getTerm(), "<", globalVars.getManagedScript(),
				vf.getSymbolTable());
		IStatement ipp = new ScriptAssignmentStatement(i, script.term("+", i.getTerm(), script.numeral("1")),
				globalVars.getManagedScript(), vf.getSymbolTable());

		IPredicate preconditions = predicateFactory.newPredicate(script.term("=", i.getTerm(), script.numeral("0")));
		IPredicate postconditions = predicateFactory.newPredicate(script.term(">=", i.getTerm(), n.getTerm()));

		List<IStatement> transitionAlphabet = new ArrayList<>();
		transitionAlphabet.add(ipp);
		transitionAlphabet.add(ilen);
		// transitionAlphabet.add(imm);
		SynthesisLoopTerminationWithoutGeneralization synthesis = new SynthesisLoopTerminationWithoutGeneralization(
				transitionAlphabet, preconditions, postconditions, globalVars);
		synthesis.addState(1, true, false);
		synthesis.addState(2, false, false);
		synthesis.addRule(1, ilen, 2);
		// synthesis.computeMainLoop();
		// System.out.println("Test 2");
		// System.out.println(synthesis.isCorrect());
		// synthesis.printProgram();
	}
}