package test.verification.loop;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import cgsynt.Verification.AlternateVerification;
import cgsynt.Verification.MainVerificationLoop;
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

public class TestMainVerificationLoop2 {

	/**
	 * [i == 0 & n >= 0] while i < n: i++ [i==n]
	 */
	@Test
	void test1() throws Exception {
		AlternateVerification.resetAll();
		BuchiTreeAutomaton<RankedBool, String> program = new BuchiTreeAutomaton<>(3);
		RankedBool.setRank(3);
		program.addInitState("s1");
		program.addState("s2");
		program.addState("s3");
		program.addState("s4");

		program.setAllStatesFinal();
		VariableFactory vf = TraceGlobalVariables.getGlobalVariables().getVariableFactory();
		Script script = TraceGlobalVariables.getGlobalVariables().getManagedScript().getScript();

		BoogieNonOldVar i = vf.constructVariable("i", VariableFactory.INT);
		BoogieNonOldVar n = vf.constructVariable("n", VariableFactory.INT);

		IStatement ilen = new ScriptAssumptionStatement(i, n.getTerm(), "<");
		IStatement ipp = new ScriptAssignmentStatement(i, script.term("+", i.getTerm(), script.numeral("1")));
		IStatement igen = new ScriptAssumptionStatement(i, n.getTerm(), ">=");

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

		BasicPredicateFactory predicateFactory = TraceToInterpolants.getTraceToInterpolants().getPredicateFactory();
		IPredicate pre = predicateFactory.newPredicate(script.term("=", i.getTerm(), script.numeral("0")));
		pre = predicateFactory.and(pre,
				predicateFactory.newPredicate(script.term(">=",	n.getTerm(), script.numeral("0"))));
		
		IPredicate post = predicateFactory.newPredicate(script.term("=", i.getTerm(), n.getTerm()));

		AlternateVerification loop = new AlternateVerification(program, letters, pre, post);
		loop.computeMainLoop();
		assertTrue(loop.isCorrect());
	}

	/**
	 * [i == 0 & n >= 0] while i < n: i++ [i>n]
	 */
	@Test
	void test2() throws Exception {
		AlternateVerification.resetAll();
		BuchiTreeAutomaton<RankedBool, String> program = new BuchiTreeAutomaton<>(3);
		RankedBool.setRank(3);
		program.addInitState("s1");
		program.addState("s2");
		program.addState("s3");
		program.addState("s4");

		program.setAllStatesFinal();
		VariableFactory vf = TraceGlobalVariables.getGlobalVariables().getVariableFactory();
		Script script = TraceGlobalVariables.getGlobalVariables().getManagedScript().getScript();

		BoogieNonOldVar i = vf.constructVariable("i", VariableFactory.INT);
		BoogieNonOldVar n = vf.constructVariable("n", VariableFactory.INT);

		IStatement ilen = new ScriptAssumptionStatement(i, n.getTerm(), "<");
		IStatement ipp = new ScriptAssignmentStatement(i, script.term("+", i.getTerm(), script.numeral("1")));
		IStatement igen = new ScriptAssumptionStatement(i, n.getTerm(), ">=");

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

		BasicPredicateFactory predicateFactory = TraceToInterpolants.getTraceToInterpolants().getPredicateFactory();
		IPredicate pre = predicateFactory.newPredicate(script.term("=", i.getTerm(), script.numeral("0")));
		pre = predicateFactory.and(pre,
				predicateFactory.newPredicate(script.term(">=",	n.getTerm(), script.numeral("0"))));
		
		IPredicate post = predicateFactory.newPredicate(script.term(">", i.getTerm(), n.getTerm()));

		AlternateVerification loop = new AlternateVerification(program, letters, pre, post);
		loop.computeMainLoop();
		assertFalse(loop.isCorrect());
	}

	/**
	 * [i == 0 & n >= 0] while i < n: i++ [i>=n]
	 */
	@Test
	void test3() throws Exception {
		AlternateVerification.resetAll();
		BuchiTreeAutomaton<RankedBool, String> program = new BuchiTreeAutomaton<>(3);
		RankedBool.setRank(3);
		program.addInitState("s1");
		program.addState("s2");
		program.addState("s3");
		program.addState("s4");

		program.setAllStatesFinal();
		VariableFactory vf = TraceGlobalVariables.getGlobalVariables().getVariableFactory();
		Script script = TraceGlobalVariables.getGlobalVariables().getManagedScript().getScript();

		BoogieNonOldVar i = vf.constructVariable("i", VariableFactory.INT);
		BoogieNonOldVar n = vf.constructVariable("n", VariableFactory.INT);

		IStatement ilen = new ScriptAssumptionStatement(i, n.getTerm(), "<");
		IStatement ipp = new ScriptAssignmentStatement(i, script.term("+", i.getTerm(), script.numeral("1")));
		IStatement igen = new ScriptAssumptionStatement(i, n.getTerm(), ">=");

		List<IStatement> letters = new ArrayList<IStatement>();
		letters.add(ilen);
		letters.add(ipp);
		letters.add(igen);

		List<String> dest1 = Arrays.asList("s4", "s3", "s2");
		program.addRule(new BuchiTreeAutomatonRule<>(RankedBool.FALSE, "s1", dest1));

		// List<String> dest2false = Arrays.asList("s3", "s3", "s3");
		// program.addRule(new BuchiTreeAutomatonRule<>(RankedBool.FALSE, "s2",
		// dest2false));

		List<String> dest2true = Arrays.asList("s3", "s3", "s3");
		program.addRule(new BuchiTreeAutomatonRule<>(RankedBool.TRUE, "s2", dest2true));

		List<String> dest3 = Arrays.asList("s3", "s3", "s3");
		program.addRule(new BuchiTreeAutomatonRule<>(RankedBool.FALSE, "s3", dest3));

		List<String> dest4 = Arrays.asList("s3", "s1", "s3");
		program.addRule(new BuchiTreeAutomatonRule<>(RankedBool.FALSE, "s4", dest4));

		// Pre and post condition
		BasicPredicateFactory predicateFactory = TraceToInterpolants.getTraceToInterpolants().getPredicateFactory();
		IPredicate pre = predicateFactory.newPredicate(script.term("=", i.getTerm(), script.numeral("0")));
		pre = predicateFactory.and(pre,
				predicateFactory.newPredicate(script.term(">=",	n.getTerm(), script.numeral("0"))));
		
		IPredicate post = predicateFactory.newPredicate(script.term(">=", i.getTerm(), n.getTerm()));

		AlternateVerification loop = new AlternateVerification(program, letters, pre, post);
		loop.computeMainLoop();
		assertTrue(loop.isCorrect());
	}

	/**
	 * [i == 0 & n >= 0] while i < n: i++ [i<n]
	 */
	@Test
	void test4() throws Exception {
		AlternateVerification.resetAll();
		BuchiTreeAutomaton<RankedBool, String> program = new BuchiTreeAutomaton<>(3);
		RankedBool.setRank(3);
		program.addInitState("s1");
		program.addState("s2");
		program.addState("s3");
		program.addState("s4");

		program.setAllStatesFinal();
		VariableFactory vf = TraceGlobalVariables.getGlobalVariables().getVariableFactory();
		Script script = TraceGlobalVariables.getGlobalVariables().getManagedScript().getScript();

		BoogieNonOldVar i = vf.constructVariable("i", VariableFactory.INT);
		BoogieNonOldVar n = vf.constructVariable("n", VariableFactory.INT);

		IStatement ilen = new ScriptAssumptionStatement(i, n.getTerm(), "<");
		IStatement ipp = new ScriptAssignmentStatement(i, script.term("+", i.getTerm(), script.numeral("1")));
		IStatement igen = new ScriptAssumptionStatement(i, n.getTerm(), ">=");

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

		BasicPredicateFactory predicateFactory = TraceToInterpolants.getTraceToInterpolants().getPredicateFactory();
		IPredicate pre = predicateFactory.newPredicate(script.term("=", i.getTerm(), script.numeral("0")));
		pre = predicateFactory.and(pre,
				predicateFactory.newPredicate(script.term(">=",	n.getTerm(), script.numeral("0"))));
		
		IPredicate post = predicateFactory.newPredicate(script.term("<", i.getTerm(), n.getTerm()));
		
		AlternateVerification loop = new AlternateVerification(program, letters, pre, post);
		loop.computeMainLoop();
		assertFalse(loop.isCorrect());
	}

	/**
	 * [i == 0 & n >= 0] while i < n: i++ [i<=n]
	 */
	@Test
	void test5() throws Exception {
		AlternateVerification.resetAll();
		BuchiTreeAutomaton<RankedBool, String> program = new BuchiTreeAutomaton<>(3);
		RankedBool.setRank(3);
		program.addInitState("s1");
		program.addState("s2");
		program.addState("s3");
		program.addState("s4");

		program.setAllStatesFinal();
		VariableFactory vf = TraceGlobalVariables.getGlobalVariables().getVariableFactory();
		Script script = TraceGlobalVariables.getGlobalVariables().getManagedScript().getScript();

		BoogieNonOldVar i = vf.constructVariable("i", VariableFactory.INT);
		BoogieNonOldVar n = vf.constructVariable("n", VariableFactory.INT);

		IStatement ilen = new ScriptAssumptionStatement(i, n.getTerm(), "<");
		IStatement ipp = new ScriptAssignmentStatement(i, script.term("+", i.getTerm(), script.numeral("1")));
		IStatement igen = new ScriptAssumptionStatement(i, n.getTerm(), "<=");

		List<IStatement> letters = new ArrayList<IStatement>();
		letters.add(ilen);
		letters.add(ipp);
		letters.add(igen);

		List<String> dest1 = Arrays.asList("s4", "s3", "s2");
		program.addRule(new BuchiTreeAutomatonRule<>(RankedBool.FALSE, "s1", dest1));

		// List<String> dest2false = Arrays.asList("s3", "s3", "s3");
		// program.addRule(new BuchiTreeAutomatonRule<>(RankedBool.FALSE, "s2",
		// dest2false));

		List<String> dest2true = Arrays.asList("s3", "s3", "s3");
		program.addRule(new BuchiTreeAutomatonRule<>(RankedBool.TRUE, "s2", dest2true));

		List<String> dest3 = Arrays.asList("s3", "s3", "s3");
		program.addRule(new BuchiTreeAutomatonRule<>(RankedBool.FALSE, "s3", dest3));

		List<String> dest4 = Arrays.asList("s3", "s1", "s3");
		program.addRule(new BuchiTreeAutomatonRule<>(RankedBool.FALSE, "s4", dest4));

		BasicPredicateFactory predicateFactory = TraceToInterpolants.getTraceToInterpolants().getPredicateFactory();
		IPredicate pre = predicateFactory.newPredicate(script.term("=", i.getTerm(), script.numeral("0")));
		pre = predicateFactory.and(pre,
				predicateFactory.newPredicate(script.term(">=",	n.getTerm(), script.numeral("0"))));
		
		IPredicate post = predicateFactory.newPredicate(script.term("<=", i.getTerm(), n.getTerm()));

		AlternateVerification loop = new AlternateVerification(program, letters, pre, post);
		loop.computeMainLoop();
		assertTrue(loop.isCorrect());
	}
	// @Test
	// void mult() throws Exception {
	// /**
	// * [i = 0 & s = 0]
	// * while i < y:
	// * s += x
	// * [s = x * y & i = y]
	// */
	//
	// TraceGlobalVariables.reset();
	// TraceToInterpolants.reset();
	// BuchiTreeAutomaton<RankedBool, String> program = new BuchiTreeAutomaton<>(3);
	// RankedBool.setRank(3);
	// program.addInitState("s1");
	// program.addState("s2");
	// program.addState("s3");
	// program.addState("s4");
	//
	// program.setAllStatesFinal();
	// VariableFactory vf =
	// TraceGlobalVariables.getGlobalVariables().getVariableFactory();
	// Script script =
	// TraceGlobalVariables.getGlobalVariables().getManagedScript().getScript();
	//
	// BoogieNonOldVar i = vf.constructVariable("i", VariableFactory.INT);
	// BoogieNonOldVar s = vf.constructVariable("s", VariableFactory.INT);
	// BoogieNonOldVar x = vf.constructVariable("x", VariableFactory.INT);
	// BoogieNonOldVar y = vf.constructVariable("y", VariableFactory.INT);
	//
	// IAssumption pre1 = new ScriptAssumptionStatement(i, script.numeral("0"),
	// "=");
	// IAssumption pre2 = new ScriptAssumptionStatement(s, script.numeral("0"),
	// "=");
	//
	// IStatement iley = new ScriptAssumptionStatement(i, y.getTerm(), "<");
	// IStatement spex = new ScriptAssignmentStatement(s, script.term("+",
	// s.getTerm(), x.getTerm()));
	// IStatement igey = new ScriptAssumptionStatement(i, y.getTerm(), ">=");
	//
	// IAssumption post1 = new ScriptAssumptionStatement(s, script.term("*",
	// x.getTerm(), y.getTerm()), "=");
	// IAssumption post2 = new ScriptAssumptionStatement(i, y.getTerm(), "=");
	//
	// List<IStatement> letters = new ArrayList<IStatement>();
	// letters.add(iley);
	// letters.add(spex);
	// letters.add(igey);
	//
	// List<String> dest1 = Arrays.asList("s4", "s3", "s2");
	// program.addRule(new BuchiTreeAutomatonRule<>(RankedBool.FALSE, "s1", dest1));
	//
	// List<String> dest2 = Arrays.asList("s3", "s3", "s3");
	// program.addRule(new BuchiTreeAutomatonRule<>(RankedBool.TRUE, "s2", dest2));
	//
	// List<String> dest3 = Arrays.asList("s3", "s3", "s3");
	// program.addRule(new BuchiTreeAutomatonRule<>(RankedBool.FALSE, "s3", dest3));
	//
	// List<String> dest4 = Arrays.asList("s3", "s1", "s3");
	// program.addRule(new BuchiTreeAutomatonRule<>(RankedBool.FALSE, "s4", dest4));
	//
	// List<IAssumption> preconditions = Arrays.asList(pre1, pre2);
	// List<IAssumption> postconditions = Arrays.asList(post1, post2);
	//
	// MainVerificationLoop loop = new MainVerificationLoop(program, letters,
	// preconditions, postconditions);
	// loop.computeMainLoop();
	// assertTrue(loop.isCorrect());
	// }

	// @Test
	// void arrayTest() throws Exception {
	// IUltimateServiceProvider service =
	// UltimateMocks.createUltimateServiceProviderMock();
	// ILogger logger = new ConsoleLogger();
	// ManagedScript managedScript = new ManagedScript(service, new SMTInterpol(new
	// DefaultLogger()));
	// NoopScript script = (NoopScript) managedScript.getScript();
	// script.setOption(":produce-proofs", true);
	// script.setLogic(Logics.QF_AUFLIA);
	//
	// DefaultIcfgSymbolTable symbolTable = new DefaultIcfgSymbolTable();
	//
	// // For the array
	// Sort arraySort = script.sort("Array", script.sort("Int"),
	// script.sort("Int"));
	// BoogieType arrayType = BoogieType.createArrayType(0, new BoogieType[0],
	// BoogieType.TYPE_INT);
	//
	// BoogieOldVar arrayOld = new BoogieOldVar("A'", arrayType,
	// script.variable("A'", arraySort), (ApplicationTerm) script.term("A'"),
	// (ApplicationTerm) script.term("A"));
	// BoogieNonOldVar arrayNew = new BoogieNonOldVar("A", arrayType,
	// script.variable("A", arraySort), (ApplicationTerm) script.term("A"),
	// (ApplicationTerm) script.term("A'"), arrayOld);
	// arrayOld.setNonOldVar(arrayNew);
	//
	// // For the indexing variable of that array
	// BoogieOldVar indOld = new BoogieOldVar("i'", BoogieType.TYPE_INT,
	// script.variable("i'", script.sort("Int")),
	// (ApplicationTerm)script.term("i'"),
	// (ApplicationTerm)script.term("i"));
	// BoogieNonOldVar indNew = new BoogieNonOldVar("i", BoogieType.TYPE_INT,
	// script.variable("i", script.sort("Int")), (ApplicationTerm)script.term("i"),
	// (ApplicationTerm)script.term("i'"), indOld);
	// indOld.setNonOldVar(indNew);
	//
	// // Prepare hybrid variable
	// Theory t = script.getTheory();
	// BoogieOldVar hybridOld = new BoogieOldVar("A[i]'", BoogieType.TYPE_INT,
	// script.variable("A[i]'", script.sort("Int")), );
	//
	//
	// symbolTable.add(arrayNew);
	// symbolTable.add(indNew);
	//
	// String p0 = "p0";
	// HashRelation<String, IProgramNonOldVar> mProc2Globals = new HashRelation<>();
	// mProc2Globals.addPair(p0, arrayNew);
	// ModifiableGlobalsTable modifiableGlobalsTable = new
	// ModifiableGlobalsTable(mProc2Globals);
	// Set<String> procedures = new HashSet<>();
	// procedures.add(p0);
	//
	// Map<String, List<ILocalProgramVar>> inParams = new HashMap<>();
	// Map<String, List<ILocalProgramVar>> outParams = new HashMap<>();
	// IcfgEdgeFactory icfgEdgeFactory = new IcfgEdgeFactory(new SerialProvider());
	// Map<IIcfgForkTransitionThreadCurrent<IcfgLocation>, ThreadInstance>
	// threadInstanceMap = new HashMap<>();
	// Collection<IIcfgJoinTransitionThreadCurrent<IcfgLocation>> joinTransitions =
	// new ArrayList<>();
	// ConcurrencyInformation concurInfo = new
	// ConcurrencyInformation(threadInstanceMap, joinTransitions);
	// SmtSymbols smtSymbols = new SmtSymbols(managedScript.getScript());
	// CfgSmtToolkit toolkit = new CfgSmtToolkit(modifiableGlobalsTable,
	// managedScript, symbolTable, procedures,
	// inParams, outParams, icfgEdgeFactory, concurInfo, smtSymbols);
	// SortedMap<Integer, IPredicate> pendingContexts = new TreeMap<>();
	//
	// List<IProgramVar> lhs0 = new ArrayList<>();
	// List<Term> rhs0 = new ArrayList<>();
	//
	// lhs0.add(arrayNew);
	// rhs0.add(script.numeral("0"));
	//
	// UnmodifiableTransFormula formula0 =
	// ExtendedTransFormulaBuilder.constructAssumption(lhs0, rhs0, symbolTable,
	// managedScript, ">=", false);
	// UnmodifiableTransFormula formula1 =
	// ExtendedTransFormulaBuilder.constructAssignment(lhs1, rhs1, symbolTable,
	// managedScript);
	// UnmodifiableTransFormula formula2 =
	// ExtendedTransFormulaBuilder.constructAssignment(lhs2, rhs2, symbolTable,
	// managedScript);
	// UnmodifiableTransFormula formula3 =
	// ExtendedTransFormulaBuilder.constructAssignment(lhs3, rhs3, symbolTable,
	// managedScript);
	// UnmodifiableTransFormula formula4 =
	// ExtendedTransFormulaBuilder.constructAssumption(lhs4, rhs4, symbolTable,
	// managedScript, ">", false);
	//
	// BasicInternalAction basicAction0 = new BasicInternalAction(p0, p1, formula0);
	// BasicInternalAction basicAction1 = new BasicInternalAction(p1, p2, formula1);
	// BasicInternalAction basicAction2 = new BasicInternalAction(p2, p3, formula2);
	// BasicInternalAction basicAction3 = new BasicInternalAction(p3, p4, formula3);
	// BasicInternalAction basicAction4 = new BasicInternalAction(p4, null,
	// formula4);
	//
	// IAction[] word = new IAction[5];
	// int[] nestingRelation = new int[5];
	// word[0] = basicAction0;
	// word[1] = basicAction1;
	// word[2] = basicAction2;
	// word[3] = basicAction3;
	// word[4] = basicAction4;
	//
	// nestingRelation[0] = NestedWord.INTERNAL_POSITION;
	// nestingRelation[1] = NestedWord.INTERNAL_POSITION;
	// nestingRelation[2] = NestedWord.INTERNAL_POSITION;
	// nestingRelation[3] = NestedWord.INTERNAL_POSITION;
	// nestingRelation[4] = NestedWord.INTERNAL_POSITION;
	//
	// NestedWord<IAction> trace = new NestedWord<>(word, nestingRelation);
	//
	// BasicPredicateFactory predicateFactory = new BasicPredicateFactory(service,
	// managedScript, symbolTable,
	// SimplificationTechnique.NONE, XnfConversionTechnique.BDD_BASED);
	// PredicateUnifier pUnifer = new PredicateUnifier(logger, service,
	// managedScript, predicateFactory, symbolTable,
	// SimplificationTechnique.NONE, XnfConversionTechnique.BDD_BASED);
	//
	// List<Object> controlLocationSequence = new ArrayList<>();
	// InterpolatingTraceCheckCraig<IAction> interpolate = new
	// InterpolatingTraceCheckCraig<>(pUnifer.getTruePredicate(),
	// pUnifer.getFalsePredicate(), pendingContexts, trace, controlLocationSequence,
	// service, toolkit,
	// managedScript, null, pUnifer, AssertCodeBlockOrder.NOT_INCREMENTALLY, false,
	// true,
	// InterpolationTechnique.Craig_NestedInterpolation, false,
	// XnfConversionTechnique.BDD_BASED,
	// SimplificationTechnique.NONE, false);
	// IPredicate[] preds = interpolate.getInterpolants();
	// }
}