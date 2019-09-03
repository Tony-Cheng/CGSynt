package test.synthesis.benchmark;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import cgsynt.interpol.IAssumption;
import cgsynt.interpol.IStatement;
import cgsynt.interpol.ScriptAssignmentStatement;
import cgsynt.interpol.ScriptAssumptionStatement;
import cgsynt.interpol.ScriptPredicateAssumptionStatement;
import cgsynt.interpol.TraceGlobalVariables;
import cgsynt.interpol.VariableFactory;
import cgsynt.synthesis.SynthesisLoopTerminationWithoutGeneralization;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.boogie.BoogieNonOldVar;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.BasicPredicateFactory;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;

public class SynthesisLoopArrayMax {

	@Test
	void test1() throws Exception {
		// i++; i++
		TraceGlobalVariables globalVars = new TraceGlobalVariables();
		VariableFactory vf = globalVars.getVariableFactory();
		Script script = globalVars.getManagedScript().getScript();
		BoogieNonOldVar j = vf.constructVariable("j", VariableFactory.INT);

		BoogieNonOldVar i = vf.constructVariable("i", VariableFactory.INT);
		BoogieNonOldVar n = vf.constructVariable("n", VariableFactory.INT);
		BoogieNonOldVar m = vf.constructVariable("m", VariableFactory.INT);

		BoogieNonOldVar A = vf.constructVariable("A", VariableFactory.INT_ARR);
		BasicPredicateFactory predicateFactory = globalVars.getPredicateFactory();
		IStatement igen = new ScriptAssumptionStatement(i, n.getTerm(), ">=", globalVars.getManagedScript(),
				vf.getSymbolTable());
		IStatement mlai = new ScriptPredicateAssumptionStatement(
				predicateFactory
						.newPredicate(script.term("<", m.getTerm(), script.term("select", A.getTerm(), i.getTerm()))),
				globalVars.getManagedScript(), globalVars.getPredicateFactory());
		IStatement meai = new ScriptAssignmentStatement(m, script.term("select", A.getTerm(), i.getTerm()),
				globalVars.getManagedScript(), vf.getSymbolTable());
		IStatement ipp = new ScriptAssignmentStatement(i, script.term("+", i.getTerm(), script.numeral("1")),
				globalVars.getManagedScript(), vf.getSymbolTable());
		List<IStatement> transitionAlphabet = new ArrayList<>();
		transitionAlphabet.add(igen);
		transitionAlphabet.add(mlai);
		transitionAlphabet.add(meai);
		transitionAlphabet.add(ipp);

		IPredicate preconditions = predicateFactory.newPredicate(script.term("=", i.getTerm(), script.numeral("0")));
		preconditions = predicateFactory.and(preconditions,
				predicateFactory.newPredicate(script.term(">=", n.getTerm(), script.numeral("1"))));
		preconditions = predicateFactory.and(preconditions, predicateFactory
				.newPredicate(script.term("=", m.getTerm(), script.term("select", A.getTerm(), script.numeral("0")))));
		preconditions = predicateFactory.and(preconditions,
				predicateFactory.newPredicate(script.term(">=", j.getTerm(), script.numeral("0"))));
		preconditions = predicateFactory.and(preconditions,
				predicateFactory.newPredicate(script.term("<", j.getTerm(), n.getTerm())));
		IPredicate postconditions = predicateFactory
				.newPredicate(script.term(">=", m.getTerm(), script.term("select", A.getTerm(), j.getTerm())));
		postconditions = predicateFactory.and(postconditions,
				predicateFactory.newPredicate(script.term("=", i.getTerm(), n.getTerm())));
		SynthesisLoopTerminationWithoutGeneralization synthesis = new SynthesisLoopTerminationWithoutGeneralization(
				transitionAlphabet, preconditions, postconditions, globalVars);
		IAssumption iln = synthesis.getNegation().get(igen);
		IAssumption mgeai = synthesis.getNegation().get(mlai);
		synthesis.addState(1, true, false);
		synthesis.addState(2, false, false);
		synthesis.addState(3, false, true);
		synthesis.addRule(1, iln, 1);
		synthesis.addRule(1, igen, 1);
		synthesis.addRule(1, ipp, 1);
		synthesis.addRule(1, meai, 1);
		synthesis.addRule(1, mlai, 1);
		synthesis.addRule(1, mgeai, 1);
		
		synthesis.addRule(1, iln, 2);
		
		synthesis.addRule(2, iln, 2);
		synthesis.addRule(2, igen, 2);
		synthesis.addRule(2, ipp, 2);
		synthesis.addRule(2, meai, 2);
		synthesis.addRule(2, mlai, 2);
		synthesis.addRule(2, mgeai, 2);
		
		synthesis.addRule(2, ipp, 3);

		synthesis.addRule(3, iln, 1);
		synthesis.addRule(3, igen, 1);
		synthesis.addRule(3, ipp, 1);
		synthesis.addRule(3, meai, 1);
		synthesis.addRule(3, mlai, 1);
		synthesis.addRule(3, mgeai, 1);

		synthesis.computeMainLoop();
		System.out.println("Test 1");
		System.out.println(synthesis.isCorrect());
		synthesis.printProgram();

	}

}
