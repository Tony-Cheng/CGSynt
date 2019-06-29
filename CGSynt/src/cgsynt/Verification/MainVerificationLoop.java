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
import cgsynt.interpol.ScriptPredicateAssumptionStatement;
import cgsynt.interpol.TraceGlobalVariables;
import cgsynt.interpol.TraceToInterpolants;
import cgsynt.interpol.VariableFactory;
import cgsynt.nfa.GeneralizeStateFactory;
import cgsynt.nfa.MultiThreadGeneralization;
import cgsynt.nfa.OptimizedTraceGeneralization;
import cgsynt.tree.buchi.BuchiTreeAutomaton;
import cgsynt.tree.buchi.lta.LTAIntersectState;
import cgsynt.tree.buchi.lta.RankedBool;
import cgsynt.tree.buchi.operations.ConvertToStringState;
import cgsynt.tree.buchi.operations.LTAEmptinessCheck;
import cgsynt.tree.buchi.operations.LTAIntersection;
import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.AutomataOperationCanceledException;
import de.uni_freiburg.informatik.ultimate.automata.LibraryIdentifiers;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.INestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.VpAlphabet;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.operations.Determinize;
import de.uni_freiburg.informatik.ultimate.automata.statefactory.StringFactory;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger.LogLevel;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.boogie.BoogieNonOldVar;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.PredicateUnifier;

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
			IPredicate preconditions, IPredicate postconditions) throws Exception {
		RankedBool.setRank(transitionAlphabet.size());
		TraceToInterpolants.getTraceToInterpolants().setPreconditions(preconditions);
		TraceToInterpolants.getTraceToInterpolants().setPostconditions(postconditions);
		preconditions = TraceToInterpolants.getTraceToInterpolants().getPreconditions();
		postconditions = TraceToInterpolants.getTraceToInterpolants().getPostconditions();
		this.mService = TraceGlobalVariables.getGlobalVariables().getService();
		this.mAutService = new AutomataLibraryServices(mService);
		this.mPrograms = programs;
		this.mResultComputed = false;
		this.mTransitionAlphabet = transitionAlphabet;
		this.mAllInterpolants = new HashSet<>();
		this.mAutService.getLoggingService().getLogger(LibraryIdentifiers.PLUGIN_ID).setLevel(LogLevel.OFF);
		this.mAllInterpolants.add(preconditions);
		this.mAllInterpolants.add(postconditions);
		this.mPI = createPI(preconditions, postconditions);

	}

	private NestedWordAutomaton<IStatement, IPredicate> createPI(IPredicate prePred, IPredicate postPred)
			throws Exception {
		Set<IStatement> letters = new HashSet<>(mTransitionAlphabet);
		VpAlphabet<IStatement> alpha = new VpAlphabet<>(letters);
		NestedWordAutomaton<IStatement, IPredicate> pi = new NestedWordAutomaton<>(mAutService, alpha,
				new GeneralizeStateFactory());
		if (!prePred.equals(postPred)) {
			pi.addState(true, false, prePred);
			pi.addState(false, true, postPred);
		} else {
			pi.addState(true, true, prePred);
		}
		OptimizedTraceGeneralization generalization = new OptimizedTraceGeneralization(new HashSet<>(),
				mAllInterpolants, new HashSet<>(mTransitionAlphabet), pi);
		pi = generalization.getResult();
		return pi;
	}

	private IPredicate createDeadState() throws Exception {
		VariableFactory vf = TraceGlobalVariables.getGlobalVariables().getVariableFactory();
		Script script = TraceGlobalVariables.getGlobalVariables().getManagedScript().getScript();
		BoogieNonOldVar x = vf.constructVariable(VariableFactory.INT);
		return TraceToInterpolants.getTraceToInterpolants().getPredicateFactory()
				.newPredicate(script.term("=", x.getTerm(), script.numeral("1")));

	}

	private void computeOneIteration() throws Exception {
		// Turn PI into a NFA that has String states.
		long time = System.nanoTime();
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
		System.out.println("Start to emptiness check: " + (System.nanoTime() - time)/1000000);
		time = System.nanoTime();
		Set<List<IStatement>> counterExamples = emptinessCheck.findCounterExamples(mTransitionAlphabet);
		System.out.println("Find counter examples: " + (System.nanoTime() - time)/1000000);
		time = System.nanoTime();
		CounterExamplesToInterpolants counterExampleToInterpolants = new CounterExamplesToInterpolants(counterExamples);
		counterExampleToInterpolants.computeResult();
		System.out.println("Find interpolants: " + (System.nanoTime() - time)/1000000);
		time = System.nanoTime();
		if (counterExampleToInterpolants.getIncorrectTrace().size() > 0) {
			mIsCorrect = false;
			mResultComputed = true;
			return;
		}

		OptimizedTraceGeneralization generalization = new OptimizedTraceGeneralization(mAllInterpolants,
				flatten(counterExampleToInterpolants.getInterpolants()), new HashSet<>(mTransitionAlphabet), mPI);
		mPI = generalization.getResult();
		System.out.println("Generalization: " + (System.nanoTime() - time)/1000000);
		time = System.nanoTime();

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

	public void computeMainLoop() throws Exception {
		int i = 0;
		while (!mResultComputed) {
			System.out.println("Iteration:" + i);
			System.out.println("Number of interpolants: " + this.mAllInterpolants.size());
			computeOneIteration();
			System.out.println("Interpolants:");
			i++;
		}

		System.err.println("The process took " + i + " iterations.");
	}
	
	private void printAllInterpolants() {
		for (IPredicate interpol : this.mAllInterpolants) {
			System.out.println(interpol);
		}
	}
	
	public static void resetAll() throws Exception {
		TraceGlobalVariables.reset();
		TraceToInterpolants.reset();
	}

}
