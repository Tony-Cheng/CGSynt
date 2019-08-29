package test.verification.loop;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.Test;

import cgsynt.interpol.IStatement;
import cgsynt.interpol.ScriptAssignmentStatement;
import cgsynt.interpol.ScriptAssumptionStatement;
import cgsynt.interpol.TraceGlobalVariables;
import cgsynt.interpol.VariableFactory;
import cgsynt.tree.buchi.BuchiTreeAutomaton;
import cgsynt.tree.buchi.BuchiTreeAutomatonRule;
import cgsynt.tree.buchi.lta.RankedBool;
import cgsynt.verification.AlternateVerification;
import cgsynt.verification.MainVerificationLoop;
import cgsynt.verification.VerificationLoopIncludingTermination;
import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.VpAlphabet;
import de.uni_freiburg.informatik.ultimate.automata.statefactory.StringFactory;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.boogie.BoogieNonOldVar;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IcfgInternalTransition;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.BasicPredicateFactory;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;

public class TestVerificationWithTermination {
	@Test
	void test1() throws Exception {
		// No pre and post conditions.
		// Only a single statement (x++).
		TraceGlobalVariables globalVars = new TraceGlobalVariables();
		BuchiTreeAutomaton<RankedBool, String> aut = new BuchiTreeAutomaton<>(1);
		aut.addInitState("s1");
		aut.addState("s1");
		aut.addState("s2");
		aut.addState("s3");
		aut.setAllStatesFinal();
		VariableFactory vf = globalVars.getVariableFactory();
		BoogieNonOldVar x = vf.constructVariable("x", VariableFactory.INT);
		Script script = globalVars.getManagedScript().getScript();

		IStatement xpp = new ScriptAssignmentStatement(x, script.term("+", x.getTerm(), script.numeral("1")),
				globalVars.getManagedScript(), vf.getSymbolTable());
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

		AutomataLibraryServices autService = new AutomataLibraryServices(globalVars.getService());

		VpAlphabet<IStatement> alphabet = new VpAlphabet<>(new HashSet<>(letters));

		NestedWordAutomaton<IStatement, String> omega = new NestedWordAutomaton<>(autService, alphabet,
				new StringFactory());

		VerificationLoopIncludingTermination loop = new VerificationLoopIncludingTermination(aut, letters, null, null,
				globalVars, omega);
		loop.computeMainLoop();
		assertTrue(loop.isCorrect());
	}

	/**
	 * [i == 0 & n >= 0] while i < n: i++ [i==n]
	 */
	@Test
	void test2() throws Exception {
		TraceGlobalVariables globalVars = new TraceGlobalVariables();
		BuchiTreeAutomaton<RankedBool, String> program = new BuchiTreeAutomaton<>(3);
		RankedBool.setRank(3);
		program.addInitState("s1");
		program.addState("s2");
		program.addState("s3");
		program.addState("s4");

		program.setAllStatesFinal();
		VariableFactory vf = globalVars.getVariableFactory();
		Script script = globalVars.getManagedScript().getScript();

		BoogieNonOldVar i = vf.constructVariable("i", VariableFactory.INT);
		BoogieNonOldVar n = vf.constructVariable("n", VariableFactory.INT);

		IStatement ilen = new ScriptAssumptionStatement(i, n.getTerm(), "<", globalVars.getManagedScript(),
				vf.getSymbolTable());
		IStatement ipp = new ScriptAssignmentStatement(i, script.term("+", i.getTerm(), script.numeral("1")),
				globalVars.getManagedScript(), vf.getSymbolTable());
		IStatement igen = new ScriptAssumptionStatement(i, n.getTerm(), ">=", globalVars.getManagedScript(),
				vf.getSymbolTable());

		List<IStatement> letters = new ArrayList<IStatement>();
		letters.add(ilen);
		letters.add(ipp);
		letters.add(igen);

		List<String> dest1 = Arrays.asList("s4", "s3", "s2");
		program.addRule(new BuchiTreeAutomatonRule<>(RankedBool.FALSE, "s1", dest1));

		List<String> dest2true = Arrays.asList("s3", "s3", "s3");
		program.addRule(new BuchiTreeAutomatonRule<>(RankedBool.TRUE, "s2", dest2true));

		List<String> dest3 = Arrays.asList("s3", "s3", "s3");
		program.addRule(new BuchiTreeAutomatonRule<>(RankedBool.FALSE, "s3", dest3));

		List<String> dest4 = Arrays.asList("s3", "s1", "s3");
		program.addRule(new BuchiTreeAutomatonRule<>(RankedBool.FALSE, "s4", dest4));

		BasicPredicateFactory predicateFactory = globalVars.getPredicateFactory();
		IPredicate pre = predicateFactory.newPredicate(script.term("=", i.getTerm(), script.numeral("0")));
		pre = predicateFactory.and(pre,
				predicateFactory.newPredicate(script.term(">=", n.getTerm(), script.numeral("0"))));

		IPredicate post = predicateFactory.newPredicate(script.term("=", i.getTerm(), n.getTerm()));

		AutomataLibraryServices autService = new AutomataLibraryServices(globalVars.getService());

		VpAlphabet<IStatement> alphabet = new VpAlphabet<>(new HashSet<>(letters));

		NestedWordAutomaton<IStatement, String> omega = new NestedWordAutomaton<>(autService, alphabet,
				new StringFactory());

		VerificationLoopIncludingTermination loop = new VerificationLoopIncludingTermination(program, letters, pre,
				post, globalVars, omega);
		loop.computeMainLoop();
		assertTrue(loop.isCorrect());
	}
}
