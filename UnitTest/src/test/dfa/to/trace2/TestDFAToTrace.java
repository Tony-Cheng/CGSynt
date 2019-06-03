package test.dfa.to.trace2;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import cgsynt.trace2.Assignment;
import cgsynt.trace2.Assumption;
import cgsynt.trace2.CraigInterpolant;
import cgsynt.trace2.Formula;
import cgsynt.trace2.Numerical;
import cgsynt.trace2.StandardFormula;
import cgsynt.trace2.Statement;
import cgsynt.trace2.Trace;
import cgsynt.trace2.TraceToCraigInterpolant;
import cgsynt.trace2.Variable;
import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.logic.Logics;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Sort;
import de.uni_freiburg.informatik.ultimate.logic.Term;
import de.uni_freiburg.informatik.ultimate.smtinterpol.DefaultLogger;
import de.uni_freiburg.informatik.ultimate.smtinterpol.smtlib2.SMTInterpol;
import de.uni_freiburg.informatik.ultimate.test.mocks.UltimateMocks;

public class TestDFAToTrace {

	private static AutomataLibraryServices service;

	@BeforeAll
	static void init() {
		IUltimateServiceProvider mock = UltimateMocks.createUltimateServiceProviderMock();
		service = new AutomataLibraryServices(mock);
	}

	@Test
	void test1() {
		// x:=0 y:=0 x++ x==-1
		final Script s = new SMTInterpol(new DefaultLogger());
		s.setOption(":produce-proofs", true);
		s.setLogic(Logics.QF_LIA);
		Trace trace = new Trace();
		Variable x = new Variable("x", new Sort[0], "Int");
		Variable y = new Variable("y", new Sort[0], "Int");
		trace.addVariable(x);
		trace.addVariable(y);
		NestedWordAutomaton<Statement, String> dfa = ConstructDFA.dfa1(service, x, y);
		trace = DFAToTrace2BFS.bfs(dfa, trace, 4);

		TraceToCraigInterpolant ttc = new TraceToCraigInterpolant(trace, s);

		CraigInterpolant interpolants = ttc.computeResult();
		
		for (Statement statement : trace) {
			System.out.println(statement);
		}
		
		System.out.println();
		for (Term term : interpolants.getTerms()) {
			System.out.println(term);
		}
		
		System.out.println("\n");

	}
	
	@Test
	void test2() {
		
		final Script s = new SMTInterpol(new DefaultLogger());
		s.setOption(":produce-proofs", true);
		s.setLogic(Logics.QF_LIA);
		Trace trace = new Trace();
		Variable x = new Variable("x", new Sort[0], "Int");
		trace.addVariable(x);
		NestedWordAutomaton<Statement, String> dfa = ConstructDFA.dfa2(service, x);
		trace = DFAToTrace2BFS.bfs(dfa, trace, 3);

		TraceToCraigInterpolant ttc = new TraceToCraigInterpolant(trace, s);

		CraigInterpolant interpolants = ttc.computeResult();

		for(Statement statement : trace) {
			System.out.println(statement);
		}
		
		System.out.println();
		
		for (Term term : interpolants.getTerms()) {
			System.out.println(term);
		}
		
		System.out.println();
		System.out.println();

	}
}
