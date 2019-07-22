package test.verification.loop;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import cgsynt.interpol.IStatement;
import cgsynt.interpol.ScriptAssignmentStatement;
import cgsynt.interpol.TraceGlobalVariables;
import cgsynt.interpol.VariableFactory;
import cgsynt.tree.buchi.BuchiTreeAutomaton;
import cgsynt.tree.buchi.BuchiTreeAutomatonRule;
import cgsynt.tree.buchi.lta.RankedBool;
import cgsynt.verification.MainVerificationLoop;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.boogie.BoogieNonOldVar;

public class TestLoopFromCegarRunner {

	@Test
	void test1() throws Exception {
		// Repeated x++ with no pre nor post conditions.
		TraceGlobalVariables globalVars = new TraceGlobalVariables();
		BuchiTreeAutomaton<RankedBool, String> aut = new BuchiTreeAutomaton<>(1);
		aut.addFinalState("s1");
		aut.addInitState("s1");

		VariableFactory vf = globalVars.getVariableFactory();
		Script script = globalVars.getManagedScript().getScript();
		BoogieNonOldVar x = vf.constructVariable("x", VariableFactory.INT);

		IStatement xpp = new ScriptAssignmentStatement(x, script.term("+", x.getTerm(), script.numeral("1")),
				globalVars.getManagedScript(), vf.getSymbolTable());
		List<IStatement> letters = new ArrayList<>();
		letters.add(xpp);

		List<String> dest = new ArrayList<>();
		dest.add("s1");
		aut.addRule(new BuchiTreeAutomatonRule<>(RankedBool.TRUE, "s1", dest));
		aut.addRule(new BuchiTreeAutomatonRule<>(RankedBool.FALSE, "s1", dest));

		MainVerificationLoop loop = new MainVerificationLoop(aut, letters, null, null, globalVars);
		loop.computeMainLoop();
		assertTrue(loop.isCorrect());
	}
}
