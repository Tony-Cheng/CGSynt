package cgsynt.benchmark;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cgsynt.automaton.factory.PDeterminizeStateFactory;
import cgsynt.core.Specification;
import cgsynt.dfa.operations.CounterexamplesGeneration;
import cgsynt.dfa.operations.DfaToLtaPowerSet;
import cgsynt.interpol.IStatement;
import cgsynt.interpol.TraceGlobalVariables;
import cgsynt.nfa.GeneralizeStateFactory;
import cgsynt.nfa.OptimizedTraceGeneralization;
import cgsynt.operations.CounterExamplesToInterpolants;
import cgsynt.operations.ProgramRetrieval;
import cgsynt.probability.ConfidenceIntervalCalculator;
import cgsynt.tree.buchi.BuchiTreeAutomaton;
import cgsynt.tree.buchi.BuchiTreeAutomatonRule;
import cgsynt.tree.buchi.IntersectState;
import cgsynt.tree.buchi.lta.RankedBool;
import cgsynt.tree.buchi.operations.BuchiIntersection;
import cgsynt.tree.buchi.operations.EmptinessCheck;
import cgsynt.tree.buchi.operations.ProgramAutomatonConstruction;
import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.LibraryIdentifiers;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.INestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.VpAlphabet;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.operations.Determinize;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger.LogLevel;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;

public class SynthesisLoopReference {

	private BuchiTreeAutomaton<RankedBool, IPredicate> mPrograms;
	private NestedWordAutomaton<IStatement, IPredicate> mPI;
	private NestedWordAutomaton<IStatement, IPredicate> dummyPi;

	private List<IStatement> mTransitionAlphabet;
	private IUltimateServiceProvider mService;
	private Set<IPredicate> mAllInterpolants;
	private AutomataLibraryServices mAutService;
	private BuchiTreeAutomaton<RankedBool, IntersectState<IPredicate, IPredicate>> result;
	private Set<List<IStatement>> visitedCounterexamples;
	private int prevSize;

	// For probability testing
	private INestedWordAutomaton<IStatement, String> dfa;

	private boolean mIsCorrect;
	private TraceGlobalVariables globalVars;
	private Map<IntersectState<IPredicate, IPredicate>, BuchiTreeAutomatonRule<RankedBool, IntersectState<IPredicate, IPredicate>>> goodProgram;

	public SynthesisLoopReference(List<IStatement> transitionAlphabet, IPredicate preconditions,
			IPredicate postconditions, TraceGlobalVariables globalVars) throws Exception {
		this.globalVars = globalVars;
		RankedBool.setRank(transitionAlphabet.size());
		globalVars.getTraceInterpolator().setPreconditions(preconditions);
		globalVars.getTraceInterpolator().setPostconditions(postconditions);
		preconditions = globalVars.getTraceInterpolator().getPreconditions();
		postconditions = globalVars.getTraceInterpolator().getPostconditions();
		this.mService = globalVars.getService();
		this.mAutService = new AutomataLibraryServices(mService);
		ProgramAutomatonConstruction construction = new ProgramAutomatonConstruction(new HashSet<>(transitionAlphabet),
				globalVars.getPredicateFactory());
		construction.computeResult();
		RankedBool.setRank(construction.getAlphabet().size());
		this.mPrograms = construction.getResult();
		this.mTransitionAlphabet = construction.getAlphabet();
		this.mAllInterpolants = new HashSet<>();
		this.mAutService.getLoggingService().getLogger(LibraryIdentifiers.PLUGIN_ID).setLevel(LogLevel.OFF);
		this.mAllInterpolants.add(preconditions);
		this.mAllInterpolants.add(postconditions);
		this.mPI = createPI(preconditions, postconditions);
		this.dummyPi = createPI(preconditions, postconditions);
		this.visitedCounterexamples = new HashSet<>();
	}

	/**
	 * Create an empty proof.
	 * 
	 * @param prePred
	 * @param postPred
	 * @return
	 * @throws Exception
	 */
	private NestedWordAutomaton<IStatement, IPredicate> createPI(IPredicate prePred, IPredicate postPred)
			throws Exception {
		Set<IStatement> letters = new HashSet<>(mTransitionAlphabet);
		VpAlphabet<IStatement> alpha = new VpAlphabet<>(letters);
		NestedWordAutomaton<IStatement, IPredicate> pi = new NestedWordAutomaton<>(mAutService, alpha,
				new GeneralizeStateFactory(globalVars.getPredicateFactory()));
		if (!prePred.equals(postPred)) {
			pi.addState(true, false, prePred);
			pi.addState(false, true, postPred);
		} else {
			pi.addState(true, true, prePred);
		}
		OptimizedTraceGeneralization generalization = new OptimizedTraceGeneralization(new HashSet<>(),
				mAllInterpolants, new HashSet<>(mTransitionAlphabet), pi, globalVars.getTraceInterpolator());
		pi = generalization.getResult();
		return pi;
	}

	/**
	 * Compute one iteration of the loop.
	 * 
	 * @param k
	 *            the number of iterations that have been completed.
	 * @param bs
	 *            the batch size of the number of traces to check.
	 * @throws Exception
	 */
	private void computeOneIteration(int k, int bs) throws Exception {
		// Determinize the String state version of PI.
		Determinize<IStatement, IPredicate> determinize = new Determinize<>(mAutService,
				new PDeterminizeStateFactory(globalVars.getPredicateFactory()), mPI);

		INestedWordAutomaton<IStatement, IPredicate> dfaPI = determinize.getResult();// addDeadStates((NestedWordAutomaton<IStatement,
																						// String>)determinize.getResult());

		// Transform the DFA into an LTA
		CounterexamplesGeneration<IStatement, IPredicate> generator = new CounterexamplesGeneration<>(dfaPI, k,
				visitedCounterexamples, bs, this.mTransitionAlphabet);
		generator.computeResult();
		Set<List<IStatement>> counterExamples = generator.getResult();
		CounterExamplesToInterpolants counterExampleToInterpolants = new CounterExamplesToInterpolants(counterExamples,
				globalVars.getTraceInterpolator());
		counterExampleToInterpolants.computeResult();

		Set<IPredicate> interpolants = flatten(counterExampleToInterpolants.getInterpolants());

		OptimizedTraceGeneralization generalization = new OptimizedTraceGeneralization(mAllInterpolants, interpolants,
				new HashSet<>(mTransitionAlphabet), dummyPi, globalVars.getTraceInterpolator());
		dummyPi = generalization.getResult();

		// Change the set of interpolants after the old and new ones have been used to
		// calculate the new triplets.
		this.mAllInterpolants.addAll(interpolants);
	}

	/**
	 * Return an automaton that contains a correct program.
	 * 
	 * @return
	 */
	public BuchiTreeAutomaton<RankedBool, IntersectState<IPredicate, IPredicate>> getResult() {
		return result;
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

	/**
	 * Compute the main loop until a correct program is found.
	 * 
	 * @throws Exception
	 */
	public void computeMainLoop(int k) throws Exception {
		long time = System.currentTimeMillis();
		computeOneIteration(k, -1);
		System.out.println("Number of interpolants: " + this.mAllInterpolants.size());
		System.out.println("Time: " + (System.currentTimeMillis() - time));

	}

	public void printRootConfidenceInterval() throws Exception {
		ConfidenceIntervalCalculator calc = new ConfidenceIntervalCalculator(this.dfa, this.prevSize, 3000,
				this.mTransitionAlphabet, globalVars.getTraceInterpolator());
		double[] traceInterval = calc.calculate95TraceConfIntervals();
		double[] piInterval = calc.calculate95PiConfIntervals();
		System.out.println("Size: " + prevSize);
		System.out.println("Trace conf interval: (" + traceInterval[0] + ", " + traceInterval[1] + ")");
		System.out.println("PI conf interval: (" + piInterval[0] + ", " + piInterval[1] + ")");

	}
}
