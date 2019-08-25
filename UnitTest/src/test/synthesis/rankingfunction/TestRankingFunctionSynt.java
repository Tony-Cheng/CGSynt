package test.synthesis.rankingfunction;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import cgsynt.interpol.IStatement;
import cgsynt.interpol.ScriptAssignmentStatement;
import cgsynt.interpol.ScriptAssumptionStatement;
import cgsynt.interpol.TraceGlobalVariables;
import cgsynt.interpol.TraceToInterpolants;
import cgsynt.interpol.VariableFactory;
import cgsynt.nfa.GeneralizeStateFactory;
import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.INwaOutgoingLetterAndTransitionProvider;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.VpAlphabet;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.buchi.BuchiIsEmpty;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.buchi.NestedLassoRun;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.buchi.NestedLassoWord;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.lassoranker.LassoAnalysis;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.boogie.BoogieNonOldVar;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.SmtUtils.SimplificationTechnique;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.SmtUtils.XnfConversionTechnique;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.lassoranker.LassoAutomatonBuilder;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.predicates.PredicateFactory;

public class TestRankingFunctionSynt {

	@Test
	public void test() throws Exception{
		TraceGlobalVariables globalVars = new TraceGlobalVariables();
		
		IUltimateServiceProvider serviceProvider = globalVars.getService();
		AutomataLibraryServices libraryServices = new AutomataLibraryServices(serviceProvider);
		
		// Setup letter factories
		VariableFactory vf = globalVars.getVariableFactory();
		Script script = globalVars.getManagedScript().getScript();
		
		// Build automaton letters
		BoogieNonOldVar i = vf.constructVariable("i", VariableFactory.INT);
		BoogieNonOldVar j = vf.constructVariable("j", VariableFactory.INT);
		IStatement igz = new ScriptAssumptionStatement(i, script.numeral("0"), ">", globalVars.getManagedScript(), vf.getSymbolTable());
		IStatement jeo = new ScriptAssignmentStatement(j, script.numeral("1"), globalVars.getManagedScript(), vf.getSymbolTable());
		IStatement jli = new ScriptAssumptionStatement(j, i.getTerm(), "<", globalVars.getManagedScript(), vf.getSymbolTable());
		IStatement jpp = new ScriptAssignmentStatement(j, script.term("+", script.numeral("1"), j.getTerm()), globalVars.getManagedScript(), vf.getSymbolTable());
		
		Set<IStatement> letters = new HashSet<>(Arrays.asList(igz, jeo, jli, jpp));
		VpAlphabet<IStatement> alphabet = new VpAlphabet<>(letters);
	
		// Setup buchi automaton from "Termination Analysis by Learning Terminating Programs" Paper -> Page 8
		NestedWordAutomaton<IStatement, String> lassoBuchi = new NestedWordAutomaton<>(libraryServices, alphabet,
				new LassoStateFactory());
		
		// Add states
		lassoBuchi.addState(true, false, "l0");
		lassoBuchi.addState(false, false, "l1");
		lassoBuchi.addState(false, true, "l2");
		lassoBuchi.addState(false, false, "l3");
		
		// Add transitions
		lassoBuchi.addInternalTransition("l0", igz, "l1");
		lassoBuchi.addInternalTransition("l1", jeo, "l2");
		lassoBuchi.addInternalTransition("l2", jli, "l3");
		lassoBuchi.addInternalTransition("l3", jpp, "l2");
		
		// Make Lasso Run
		NestedLassoRun<IStatement, String> run =
				new BuchiIsEmpty<>(libraryServices, lassoBuchi).getAcceptingNestedLassoRun();
		
		// Make Lasso Word
		NestedLassoWord<IStatement> lw = run.getNestedLassoWord();
		
//		PredicateFactory pf = new PredicateFactory(serviceProvider, globalVars.getManagedScript(), vf.getSymbolTable(), 
//				SimplificationTechnique.SIMPLIFY_BDD_FIRST_ORDER, XnfConversionTechnique.BDD_BASED);
//		INwaOutgoingLetterAndTransitionProvider<IStatement, IPredicate> lassoAutomaton;
//		lassoAutomaton = new LassoAutomatonBuilder<>(alphabet, new GeneralizeStateFactory(globalVars.getPredicateFactory()),
//				pf, lw.getStem(), lw.getLoop(), serviceProvider).getResult();
		
		TraceToInterpolants tti = new TraceToInterpolants(globalVars.getManagedScript(), serviceProvider, vf.getSymbolTable());
		//LassoAnalysis la = new LassoAnalysis(tti.getCfgSmtToolkit(),);
	}
}
//TraceGlobalVariables globalVars = new TraceGlobalVariables();
//BuchiTreeAutomaton<RankedBool, String> program = new BuchiTreeAutomaton<>(3);
//RankedBool.setRank(3);
//program.addInitState("s1");
//program.addState("s2");
//program.addState("s3");
//program.addState("s4");
//
//program.setAllStatesFinal();
//VariableFactory vf = globalVars.getVariableFactory();
//Script script = globalVars.getManagedScript().getScript();
//
//BoogieNonOldVar i = vf.constructVariable("i", VariableFactory.INT);
//BoogieNonOldVar n = vf.constructVariable("n", VariableFactory.INT);
//
//IStatement ilen = new ScriptAssumptionStatement(i, n.getTerm(), "<", globalVars.getManagedScript(),
//		vf.getSymbolTable());
//IStatement ipp = new ScriptAssignmentStatement(i, script.term("+", i.getTerm(), script.numeral("1")),
//		globalVars.getManagedScript(), vf.getSymbolTable());
//IStatement igen = new ScriptAssumptionStatement(i, n.getTerm(), ">=", globalVars.getManagedScript(),
//		vf.getSymbolTable());
//
//List<IStatement> letters = new ArrayList<IStatement>();
//letters.add(ilen);
//letters.add(ipp);
//letters.add(igen);
//
//List<String> dest1 = Arrays.asList("s4", "s3", "s2");
//program.addRule(new BuchiTreeAutomatonRule<>(RankedBool.FALSE, "s1", dest1));
//
//List<String> dest2true = Arrays.asList("s3", "s3", "s3");
//program.addRule(new BuchiTreeAutomatonRule<>(RankedBool.TRUE, "s2", dest2true));
//
//List<String> dest3 = Arrays.asList("s3", "s3", "s3");
//program.addRule(new BuchiTreeAutomatonRule<>(RankedBool.FALSE, "s3", dest3));
//
//List<String> dest4 = Arrays.asList("s3", "s1", "s3");
//program.addRule(new BuchiTreeAutomatonRule<>(RankedBool.FALSE, "s4", dest4));
//
//BasicPredicateFactory predicateFactory = globalVars.getPredicateFactory();
//IPredicate pre = predicateFactory.newPredicate(script.term("=", i.getTerm(), script.numeral("0")));
//pre = predicateFactory.and(pre,
//		predicateFactory.newPredicate(script.term(">=", n.getTerm(), script.numeral("0"))));
//
//IPredicate post = predicateFactory.newPredicate(script.term("=", i.getTerm(), n.getTerm()));
//
//AlternateVerification loop = new AlternateVerification(program, letters, pre, post, globalVars);
//loop.computeMainLoop();
//assertTrue(loop.isCorrect());
