package cgsynt.synthesis;

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
import cgsynt.interpol.TraceToInterpolants;
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
import cgsynt.tree.buchi.operations.ConvertToStringState;
import cgsynt.tree.buchi.operations.EmptinessCheck;
import cgsynt.tree.buchi.operations.ProgramAutomatonConstruction;
import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.LibraryIdentifiers;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.INestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.VpAlphabet;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.operations.Determinize;
import de.uni_freiburg.informatik.ultimate.automata.statefactory.StringFactory;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger.LogLevel;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;

public class SynthesisLoop {

	private BuchiTreeAutomaton<RankedBool, IPredicate> mPrograms;
	private NestedWordAutomaton<IStatement, IPredicate> mPI;
	private List<IStatement> mTransitionAlphabet;
	private IUltimateServiceProvider mService;
	private Set<IPredicate> mAllInterpolants;
	private AutomataLibraryServices mAutService;
	private BuchiTreeAutomaton<RankedBool, IntersectState<IPredicate, IPredicate>> result;
	private Set<List<IStatement>> visitedCounterexamples;
	private int prevSize;

	// For probability testing
	private INestedWordAutomaton<IStatement, String> dfa;

	private boolean mResultComputed;
	private boolean mIsCorrect;
	private List<String> logs;
	private boolean printLogs;
	private int printedLogsSize;
	private TraceGlobalVariables globalVars;
	private Map<IntersectState<IPredicate, IPredicate>, BuchiTreeAutomatonRule<RankedBool, IntersectState<IPredicate, IPredicate>>> goodProgram;

	public SynthesisLoop(List<IStatement> transitionAlphabet, IPredicate preconditions, IPredicate postconditions,
			TraceGlobalVariables globalVars) throws Exception {
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
		this.mResultComputed = false;
		this.mTransitionAlphabet = construction.getAlphabet();
		this.mAllInterpolants = new HashSet<>();
		this.mAutService.getLoggingService().getLogger(LibraryIdentifiers.PLUGIN_ID).setLevel(LogLevel.OFF);
		this.mAllInterpolants.add(preconditions);
		this.mAllInterpolants.add(postconditions);
		this.mPI = createPI(preconditions, postconditions);
		this.visitedCounterexamples = new HashSet<>();
		this.logs = new ArrayList<>();
		this.printLogs = false;
		this.printedLogsSize = 0;
	}

	public SynthesisLoop(Specification spec) throws Exception {
		List<IStatement> transitionAlphabet = spec.getTransitionAlphabet();
		IPredicate preconditions = spec.getPreconditions();
		IPredicate postconditions = spec.getPostconditions();
		TraceGlobalVariables globalVars = spec.getGlobalVars();

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
		this.mResultComputed = false;
		this.mTransitionAlphabet = construction.getAlphabet();
		this.mAllInterpolants = new HashSet<>();
		this.mAutService.getLoggingService().getLogger(LibraryIdentifiers.PLUGIN_ID).setLevel(LogLevel.OFF);
		this.mAllInterpolants.add(preconditions);
		this.mAllInterpolants.add(postconditions);
		this.mPI = createPI(preconditions, postconditions);
		this.visitedCounterexamples = new HashSet<>();
		this.logs = new ArrayList<>();
		this.printLogs = false;
		this.printedLogsSize = 0;
	}

	public void setPrintLogs(boolean printLogs) {
		this.printLogs = printLogs;
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

		// Dead State
		IPredicate deadState = globalVars.getPredicateFactory().newDebugPredicate("deadState");

		// Transform the DFA into an LTA
		DfaToLtaPowerSet<IStatement, IPredicate> dfaToLta = new DfaToLtaPowerSet<>(dfaPI, mTransitionAlphabet,
				deadState);

		BuchiTreeAutomaton<RankedBool, IPredicate> powerSet = dfaToLta.getResult();

		BuchiIntersection<RankedBool, IPredicate, IPredicate> intersection = new BuchiIntersection<>(mPrograms,
				powerSet);
		BuchiTreeAutomaton<RankedBool, IntersectState<IPredicate, IPredicate>> intersectedAut = intersection
				.computeResult();
		EmptinessCheck<RankedBool, IntersectState<IPredicate, IPredicate>> emptinessCheck = new EmptinessCheck<>(
				intersectedAut);
		emptinessCheck.computeResult();
		if (!emptinessCheck.getResult()) {
			mIsCorrect = true;
			mResultComputed = true;
			result = emptinessCheck.getGoodAutomaton();
			this.goodProgram = emptinessCheck.getGoodProgram();
			return;
		}
		prevSize = k * dfaPI.getStates().size();
		CounterexamplesGeneration<IStatement, IPredicate> generator = new CounterexamplesGeneration<>(dfaPI,
				k * dfaPI.getStates().size(), visitedCounterexamples, bs, this.mTransitionAlphabet);
		generator.computeResult();
		Set<List<IStatement>> counterExamples = generator.getResult();
		CounterExamplesToInterpolants counterExampleToInterpolants = new CounterExamplesToInterpolants(counterExamples,
				globalVars.getTraceInterpolator());
		counterExampleToInterpolants.computeResult();

		OptimizedTraceGeneralization generalization = new OptimizedTraceGeneralization(mAllInterpolants,
				flatten(counterExampleToInterpolants.getInterpolants()), new HashSet<>(mTransitionAlphabet), mPI,
				globalVars.getTraceInterpolator());
		mPI = generalization.getResult();

		// Change the set of interpolants after the old and new ones have been used to
		// calculate the new triplets.
		this.mAllInterpolants.addAll(flatten(counterExampleToInterpolants.getInterpolants()));
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
	public void computeMainLoop() throws Exception {
		int i = 0;
		while (!mResultComputed) {
			logs.add("Iteration: " + i);
			computeOneIteration(i + 1, -1);
			logs.add("Number of interpolants: " + this.mAllInterpolants.size());
			i++;
			if (this.printLogs)
				printLogsIteration();
		}
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

	public void printLogs() {
		for (String log : logs)
			System.out.println(log);
	}

	private void printLogsIteration() {
		for (int i = this.printedLogsSize; i < logs.size(); i++) {
			System.out.println(logs.get(i));
		}
		this.printedLogsSize = logs.size();
	}

	public void printAllInterpolants() {
		for (IPredicate interpol : this.mAllInterpolants) {
			System.out.println(interpol);
		}
	}

	public void printProgram() {
		IStatement[] statements = new IStatement[mTransitionAlphabet.size()];
		for (int i = 0; i < statements.length; i++) {
			statements[i] = mTransitionAlphabet.get(i);
		}
		ProgramRetrieval<RankedBool> retrieve = new ProgramRetrieval<>(result, statements, goodProgram);
		retrieve.computeResult();
		for (String statement : retrieve.getResult()) {
			System.out.println(statement);
		}
	}
}
