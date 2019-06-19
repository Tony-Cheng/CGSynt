package cgsynt.Verification;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import cgsynt.Operations.CounterExamplesToInterpolants;
import cgsynt.dfa.operations.DfaToLtaPowerSet;
import cgsynt.interpol.IAssumption;
import cgsynt.interpol.IStatement;
import cgsynt.interpol.TraceGlobalVariables;
import cgsynt.interpol.TraceToInterpolants;
import cgsynt.nfa.GeneralizeStateFactory;
import cgsynt.nfa.OptimizedTraceGeneralization;
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
import de.uni_freiburg.informatik.ultimate.automata.statefactory.StringFactory;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;

public class MainVerificationLoop {

	private BuchiTreeAutomaton<RankedBool, String> mPrograms;
	private NestedWordAutomaton<IStatement, IPredicate> mPI;
	private List<IStatement> mTransitionAlphabet;
	private IUltimateServiceProvider mService;
	private Set<IPredicate> mAllInterpolants;
	private AutomataLibraryServices mAutService;

	private boolean mResultComputed;
	private boolean mIsCorrect;

	public MainVerificationLoop(BuchiTreeAutomaton<RankedBool, String> programs, List<IStatement> transitionAlphabet,
			List<IStatement> preconditions, List<IAssumption> postconditions) throws Exception {
		RankedBool.setRank(transitionAlphabet.size());
		this.mService = TraceGlobalVariables.getGlobalVariables().getService();
		this.mAutService = new AutomataLibraryServices(mService);
		this.mPrograms = programs;
		this.mResultComputed = false;
		this.mTransitionAlphabet = transitionAlphabet;
		this.mPI = createPI();
		this.mAllInterpolants = new HashSet<>();

		// Add the True and False Predicates
		this.mAllInterpolants.add(TraceToInterpolants.getTraceToInterpolants().getTruePredicate());
		this.mAllInterpolants.add(TraceToInterpolants.getTraceToInterpolants().getFalsePredicate());

		TraceToInterpolants.getTraceToInterpolants().setPreconditions(preconditions);
		TraceToInterpolants.getTraceToInterpolants().setNegatedPostconditions(postconditions);
	}

	private NestedWordAutomaton<IStatement, IPredicate> createPI() {
		Set<IStatement> letters = new HashSet<>(mTransitionAlphabet);
		VpAlphabet<IStatement> alpha = new VpAlphabet<>(letters);
		NestedWordAutomaton<IStatement, IPredicate> pi = new NestedWordAutomaton<>(mAutService, alpha,
				new GeneralizeStateFactory<>());
		pi.addState(true, false, TraceToInterpolants.getTraceToInterpolants().getTruePredicate());
		pi.addState(false, true, TraceToInterpolants.getTraceToInterpolants().getFalsePredicate());
		IPredicate falsePred = TraceToInterpolants.getTraceToInterpolants().getFalsePredicate();
		IPredicate truePred = TraceToInterpolants.getTraceToInterpolants().getTruePredicate();

		for (IStatement statement : mTransitionAlphabet) {
			pi.addInternalTransition(falsePred, statement, falsePred);
			pi.addInternalTransition(truePred, statement, truePred);
		}
		return pi;

	}

	private void computeOneIteration() throws AutomataOperationCanceledException {

		// Turn PI into a NFA that has String states.
		ConvertToStringState<IStatement, IPredicate> automataConverter = new ConvertToStringState<>(this.mPI);
		NestedWordAutomaton<IStatement, String> stringNFAPI = automataConverter.convert(mAutService);

		// Determinize the String state version of PI.
		Determinize<IStatement, String> determinize = new Determinize<>(mAutService, new StringFactory(), stringNFAPI);

		INestedWordAutomaton<IStatement, String> stringDFAPI = determinize.getResult();

		// Dead State
		String deadState = "DeadState";

		// Transform the DFA into an LTA
		DfaToLtaPowerSet<IStatement, String> dfaToLta = new DfaToLtaPowerSet<IStatement, String>(stringDFAPI,
				mTransitionAlphabet, deadState);

		BuchiTreeAutomaton<RankedBool, String> powerSet = dfaToLta.getResult();

		LTAIntersection<RankedBool, String, String> intersection = new LTAIntersection<>(mPrograms, powerSet);
		BuchiTreeAutomaton<RankedBool, LTAIntersectState<String, String>> intersectedAut = intersection.computeResult();
		LTAEmptinessCheck<RankedBool, LTAIntersectState<String, String>> emptinessCheck = new LTAEmptinessCheck<>(
				intersectedAut);
		emptinessCheck.computeResult();
		if (!emptinessCheck.getResult()) {
			mIsCorrect = true;
			mResultComputed = true;
			return;
		}
		Set<List<IStatement>> counterExamples = emptinessCheck.findCounterExamples(mTransitionAlphabet);
		CounterExamplesToInterpolants counterExampleToInterpolants = new CounterExamplesToInterpolants(counterExamples);
		counterExampleToInterpolants.computeResult();
		if (counterExampleToInterpolants.getIncorrectTrace().size() > 0) {
			mIsCorrect = false;
			mResultComputed = true;
			return;
		}
		List<IPredicate[]> nonSetInterpolants = counterExampleToInterpolants.getNonSetInterpolants();
		counterExampleToInterpolants.setPreAndPostStatesFinal(mPI, nonSetInterpolants);

		OptimizedTraceGeneralization generalization = new OptimizedTraceGeneralization(mAllInterpolants,
				flatten(counterExampleToInterpolants.getInterpolants()), new HashSet<>(mTransitionAlphabet), mPI);
		mPI = generalization.getResult();

		// Change the set of interpolants after the old and new ones have been used to
		// calculate the new triplets.
		this.mAllInterpolants.addAll(flatten(counterExampleToInterpolants.getInterpolants()));
	}

	private Set<IPredicate> flatten(List<Set<IPredicate>> interpolants) {
		Set<IPredicate> flattenedInterpolants = new HashSet<>();

		for (Set<IPredicate> interpolantsSet : interpolants) {
			flattenedInterpolants.addAll(interpolantsSet);
		}

		return flattenedInterpolants;
	}

	public boolean isCorrect() {
		return mIsCorrect;
	}

	public void computeMainLoop() throws AutomataOperationCanceledException {
		int i = 0;
		while (!mResultComputed) {
			System.out.println("Iteration: " + i);
			computeOneIteration();
			i++;
		}
	}

}
