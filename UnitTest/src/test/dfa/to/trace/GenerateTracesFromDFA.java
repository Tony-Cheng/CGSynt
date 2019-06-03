package test.dfa.to.trace;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import cgsynt.Formula;
import cgsynt.Token;
import cgsynt.TokenFactory;
import cgsynt.Trace;
import cgsynt.trace.operations.TraceScriptToInterpolants;
import cgsynt.trace.operations.TraceToSMTFormula;
import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Term;
import de.uni_freiburg.informatik.ultimate.test.mocks.UltimateMocks;

public class GenerateTracesFromDFA {

	@BeforeAll
	static void init() {
	}

	@Test
	void testDFAToTraces() {
		IUltimateServiceProvider mock = UltimateMocks.createUltimateServiceProviderMock();
		AutomataLibraryServices service = new AutomataLibraryServices(mock);
		TokenFactory factory = new TokenFactory();
		Token x = factory.createVariable();
		Token y = factory.createVariable();
		NestedWordAutomaton<Formula, String> nwa = ConstructDFA.dfa1(service, x, y);
		Set<Trace> traces = DFAToTraceBFS.bfs(nwa, 5);
		assertEquals(traces.size(), 2);
		for (Trace trace : traces) {
			TraceToSMTFormula op = new TraceToSMTFormula(trace);
			Script s = op.getResult();

			Term[] terms = s.getAssertions();

			System.out.println(trace);

			for (Term term : terms) {
				System.out.println(term);
			}
			System.out.println();

			TraceScriptToInterpolants inter = new TraceScriptToInterpolants(s, trace.size());
			Term[] interpolants = inter.getResult();
			for (Term term : interpolants) {
				System.out.println(term);
			}
			System.out.println();
		}

	}

}
