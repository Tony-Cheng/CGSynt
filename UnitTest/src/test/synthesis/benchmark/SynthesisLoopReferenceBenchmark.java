package test.synthesis.benchmark;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import cgsynt.benchmark.SynthesisLoopReference;
import cgsynt.interpol.IStatement;
import cgsynt.interpol.ScriptAssignmentStatement;
import cgsynt.interpol.ScriptAssumptionStatement;
import cgsynt.interpol.ScriptPredicateAssumptionStatement;
import cgsynt.interpol.TraceGlobalVariables;
import cgsynt.interpol.VariableFactory;
import cgsynt.synthesis.SynthesisLoop;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.boogie.BoogieNonOldVar;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.BasicPredicateFactory;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;

public class SynthesisLoopReferenceBenchmark {

	@Test
	void benchmark1() throws Exception {
		// find max in an array
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
		IStatement mleai = new ScriptPredicateAssumptionStatement(
				predicateFactory
						.newPredicate(script.term("<", m.getTerm(), script.term("select", A.getTerm(), i.getTerm()))),
				globalVars.getManagedScript(), globalVars.getPredicateFactory());
		IStatement meai = new ScriptAssignmentStatement(m, script.term("select", A.getTerm(), i.getTerm()),
				globalVars.getManagedScript(), vf.getSymbolTable());
		IStatement ipp = new ScriptAssignmentStatement(i, script.term("+", i.getTerm(), script.numeral("1")),
				globalVars.getManagedScript(), vf.getSymbolTable());
		List<IStatement> transitionAlphabet = new ArrayList<>();
		transitionAlphabet.add(igen);
		transitionAlphabet.add(mleai);
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
		SynthesisLoopReference synthesis = new SynthesisLoopReference(transitionAlphabet, preconditions, postconditions,
				globalVars);
		synthesis.computeMainLoop(4);
	}
}
