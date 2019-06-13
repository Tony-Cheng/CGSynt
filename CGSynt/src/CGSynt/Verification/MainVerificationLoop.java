package CGSynt.Verification;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import CGSynt.Operations.CounterExamplesToInterpolants;
import cgsynt.dfa.operations.DfaToLtaPowerSet;
import cgsynt.interpol.IStatement;
import cgsynt.interpol.TraceGlobalVariables;
import cgsynt.interpol.TraceToInterpolants;
import cgsynt.nfa.GeneralizeStateFactory;
import cgsynt.nfa.TraceGeneralization;
import cgsynt.tree.buchi.BuchiTreeAutomaton;
import cgsynt.tree.buchi.lta.LTAIntersectState;
import cgsynt.tree.buchi.lta.RankedBool;
import cgsynt.tree.buchi.operations.ConvertToStringState;
import cgsynt.tree.buchi.operations.LTAEmptinessCheck;
import cgsynt.tree.buchi.operations.LTAIntersection;
import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.AutomataOperationCanceledException;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.INestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.VpAlphabet;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.operations.Determinize;
import de.uni_freiburg.informatik.ultimate.automata.statefactory.IDeterminizeStateFactory;
import de.uni_freiburg.informatik.ultimate.automata.statefactory.StringFactory;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;
import de.uni_freiburg.informatik.ultimate.test.mocks.UltimateMocks;

public class MainVerificationLoop {

	private BuchiTreeAutomaton<RankedBool, String> programs;
	private NestedWordAutomaton<IStatement, IPredicate> pi;
	private List<IStatement> transitionAlphabet;
	private boolean isCorrect;
	private IUltimateServiceProvider service;
	private Set<IPredicate> allInterpolants;
	private AutomataLibraryServices autService;

	private boolean resultComputed;

	public MainVerificationLoop(BuchiTreeAutomaton<RankedBool, String> programs, List<IStatement> transitionAlphabet) {
		this.service = UltimateMocks.createUltimateServiceProviderMock();
		this.autService = new AutomataLibraryServices(service);
		TraceGlobalVariables.init(service);
		this.programs = programs;
		this.resultComputed = false;
		this.transitionAlphabet = new ArrayList<>();
		this.pi = createPI();
		this.allInterpolants = new TreeSet<>();
	}

	private NestedWordAutomaton<IStatement, IPredicate> createPI() {
		Set<IStatement> letters = new HashSet<>(transitionAlphabet);
		VpAlphabet<IStatement> alpha = new VpAlphabet<>(letters);
		NestedWordAutomaton<IStatement, IPredicate> pi = new NestedWordAutomaton<>(autService, alpha,
				new GeneralizeStateFactory<>());
		pi.addState(true, false, TraceToInterpolants.getTraceToInterpolants().getTruePredicate());
		pi.addState(false, true, TraceToInterpolants.getTraceToInterpolants().getTruePredicate());
		return pi;

	}

	private void computeOneIteration() throws AutomataOperationCanceledException {
		
		// Turn PI into a NFA that has String states.
		ConvertToStringState<IStatement, IPredicate> automataConverter = new ConvertToStringState<>(this.pi);
		NestedWordAutomaton<IStatement, String> stringNFAPI = automataConverter.convert(autService);
		
		// Determinize the String state version of PI.
		Determinize<IStatement, String> determinize = new Determinize<>(autService,
				new StringFactory(), stringNFAPI);
		
		INestedWordAutomaton<IStatement, String> stringDFAPI =  determinize.getResult();
		
		// Dead State
		String deadState = "hi Im dead!";
		
		// Transform the DFA into an LTA
		DfaToLtaPowerSet<IStatement, String> dfaToLta = new DfaToLtaPowerSet<IStatement, String>(stringDFAPI, transitionAlphabet, deadState);
		
		BuchiTreeAutomaton<RankedBool, String> powerSet = dfaToLta.getResult();
		
		
		LTAIntersection<RankedBool, String, String> intersection = new LTAIntersection<>(programs, powerSet);
		BuchiTreeAutomaton<RankedBool, LTAIntersectState<String, String>> intersectedAut = intersection
				.computeResult();
		LTAEmptinessCheck<RankedBool, LTAIntersectState<String, String>> emptinessCheck = new LTAEmptinessCheck<>(
				intersectedAut);
		emptinessCheck.computeResult();
		if (!emptinessCheck.getResult()) {
			isCorrect = true;
			resultComputed = true;
			return;
		}
		Set<List<IStatement>> counterExamples = emptinessCheck.findCounterExamples(transitionAlphabet);
		CounterExamplesToInterpolants counterExampleToInterpolants = new CounterExamplesToInterpolants(counterExamples);
		counterExampleToInterpolants.computeResult();
		if (counterExampleToInterpolants.getIncorrectTrace().size() > 0) {
			isCorrect = false;
			resultComputed = true;
			return;
		}
		addToAllInterpolants(counterExampleToInterpolants.getInterpolants());

		TraceGeneralization generalization = new TraceGeneralization(allInterpolants,
				new HashSet<>(transitionAlphabet));
		pi = generalization.getResult();
	}

	private void addToAllInterpolants(List<Set<IPredicate>> interpolants) {
		for (Set<IPredicate> interpolantsSet : interpolants) {
			allInterpolants.addAll(interpolantsSet);
		}
	}

	public boolean isCorrect() {
		return isCorrect;
	}

	public void computeMainLoop() throws AutomataOperationCanceledException {
		while (!resultComputed) {
			computeOneIteration();
		}
	}
	

}
