package test.synthesis.rankingfunction;

import org.junit.jupiter.api.Test;

import cgsynt.interpol.IStatement;
import cgsynt.interpol.TraceGlobalVariables;
import cgsynt.interpol.VariableFactory;
import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.VpAlphabet;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.boogie.BoogieNonOldVar;

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
		
		VpAlphabet<IStatement> alphabet = new VpAlphabet<>();
		NestedWordAutomaton<String, IStatement> lassoBuchi = new NestedWordAutomaton<>(libraryServices, );
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
