package cgsynt.synthesis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cgsynt.dfa.operations.CounterexamplesGeneration;
import cgsynt.dfa.operations.DfaToLtaPowerSet;
import cgsynt.interpol.IStatement;
import cgsynt.interpol.TraceGlobalVariables;
import cgsynt.interpol.TraceToInterpolants;
import cgsynt.nfa.GeneralizeStateFactory;
import cgsynt.nfa.OptimizedTraceGeneralization;
import cgsynt.operations.CounterExamplesToInterpolants;
import cgsynt.probability.ConfidenceIntervalCalculator;
import cgsynt.tree.buchi.BuchiTreeAutomaton;
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

public class SynthesisLoopExperimental {

	private BuchiTreeAutomaton<RankedBool, String> mPrograms;
	private NestedWordAutomaton<IStatement, IPredicate> mPI;
	private List<IStatement> mTransitionAlphabet;
	private IUltimateServiceProvider mService;
	private Set<IPredicate> mAllInterpolants;
	private AutomataLibraryServices mAutService;
	private BuchiTreeAutomaton<RankedBool, IntersectState<String, String>> result;
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

	public SynthesisLoopExperimental(List<IStatement> transitionAlphabet, IPredicate preconditions,
			IPredicate postconditions, TraceGlobalVariables globalVars) throws Exception {
		this.globalVars = globalVars;
		RankedBool.setRank(transitionAlphabet.size());
		globalVars.getTraceInterpolator().setPreconditions(preconditions);
		globalVars.getTraceInterpolator().setPostconditions(postconditions);
		preconditions = globalVars.getTraceInterpolator().getPreconditions();
		postconditions = globalVars.getTraceInterpolator().getPostconditions();
		this.mService = globalVars.getService();
		this.mAutService = new AutomataLibraryServices(mService);
		ProgramAutomatonConstruction construction = new ProgramAutomatonConstruction(new HashSet<>(transitionAlphabet));
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

	private void computeOneIterationExponential(int k) throws Exception {
		// Turn PI into a NFA that has String states.
		ConvertToStringState<IStatement, IPredicate> automataConverter = new ConvertToStringState<>(this.mPI);
		NestedWordAutomaton<IStatement, String> stringNFAPI = automataConverter.convert(mAutService);

		// Determinize the String state version of PI.
		Determinize<IStatement, String> determinize = new Determinize<>(mAutService, new StringFactory(), stringNFAPI);

		INestedWordAutomaton<IStatement, String> stringDFAPI = determinize.getResult();
		this.dfa = stringDFAPI;

		// Dead State
		String deadState = "DeadState";

		// Transform the DFA into an LTA
		DfaToLtaPowerSet<IStatement, String> dfaToLta = new DfaToLtaPowerSet<IStatement, String>(stringDFAPI,
				mTransitionAlphabet, deadState);

		BuchiTreeAutomaton<RankedBool, String> powerSet = dfaToLta.getResult();

		BuchiIntersection<RankedBool, String, String> intersection = new BuchiIntersection<>(mPrograms, powerSet);
		BuchiTreeAutomaton<RankedBool, IntersectState<String, String>> intersectedAut = intersection.computeResult();
		EmptinessCheck<RankedBool, IntersectState<String, String>> emptinessCheck = new EmptinessCheck<>(
				intersectedAut);
		emptinessCheck.computeResult();
		if (!emptinessCheck.getResult()) {
			mIsCorrect = true;
			mResultComputed = true;
			result = intersectedAut;
		}
		prevSize = k * stringDFAPI.getStates().size();
		CounterexamplesGeneration<IStatement, String> generator = new CounterexamplesGeneration<>(stringDFAPI, k,
				visitedCounterexamples, CounterexamplesGeneration.NO_BATCH, this.mTransitionAlphabet);
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

	private void computeOneIterationRandom(int k, int bs) throws Exception {
		// Turn PI into a NFA that has String states.
		ConvertToStringState<IStatement, IPredicate> automataConverter = new ConvertToStringState<>(this.mPI);
		NestedWordAutomaton<IStatement, String> stringNFAPI = automataConverter.convert(mAutService);

		// Determinize the String state version of PI.
		Determinize<IStatement, String> determinize = new Determinize<>(mAutService, new StringFactory(), stringNFAPI);

		INestedWordAutomaton<IStatement, String> stringDFAPI = determinize.getResult();
		this.dfa = stringDFAPI;

		// Dead State
		String deadState = "DeadState";

		// Transform the DFA into an LTA
		DfaToLtaPowerSet<IStatement, String> dfaToLta = new DfaToLtaPowerSet<IStatement, String>(stringDFAPI,
				mTransitionAlphabet, deadState);

		BuchiTreeAutomaton<RankedBool, String> powerSet = dfaToLta.getResult();

		BuchiIntersection<RankedBool, String, String> intersection = new BuchiIntersection<>(mPrograms, powerSet);
		BuchiTreeAutomaton<RankedBool, IntersectState<String, String>> intersectedAut = intersection.computeResult();
		EmptinessCheck<RankedBool, IntersectState<String, String>> emptinessCheck = new EmptinessCheck<>(
				intersectedAut);
		emptinessCheck.computeResult();
		if (!emptinessCheck.getResult()) {
			mIsCorrect = true;
			mResultComputed = true;
			result = intersectedAut;
		}
		CounterexamplesGeneration<IStatement, String> generator = new CounterexamplesGeneration<>(stringDFAPI, k,
				visitedCounterexamples, bs, this.mTransitionAlphabet);
		generator.computeResult();
		;
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

	public BuchiTreeAutomaton<RankedBool, IntersectState<String, String>> getResult() {
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

	public void computeMainLoopRandomly(int len) throws Exception {
		double[] traceInterval = new double[] { 0.0, 0.0 };
		double[] piInterval = new double[] { 0.0, 0.0 };
		double traceProb = (traceInterval[1] - traceInterval[0]) / 2;
		double piProb = (piInterval[1] - piInterval[0]) / 2;
		double[] traceBottomInterval = new double[] { 0.0, 0.0 };
		double[] piBottomInterval = new double[] { 0.0, 0.0 };
		for (int i = 0; i < len; i++) {
			if (i > 0) {
				ConfidenceIntervalCalculator calc = new ConfidenceIntervalCalculator(this.dfa, i, 500,
						this.mTransitionAlphabet, globalVars.getTraceInterpolator());
				traceInterval = calc.calculate95TraceConfIntervals();
				piInterval = calc.calculate95PiConfIntervals();
				traceProb = (traceInterval[1] + traceInterval[0]) / 2;
				piProb = (piInterval[1] + piInterval[0]) / 2;
			} else {
				computeOneIterationRandom(i, 100);
			}
			while (!(Math.abs(traceProb - piProb) <= 0.01)
					|| !(traceInterval[0] <= piProb && piProb <= traceInterval[1])
					|| !(piInterval[0] <= traceProb && traceProb <= piInterval[1])) {
				logs.add(!(Math.abs(traceProb - piProb) <= 0.05) + " "
						+ !(traceInterval[0] <= piProb && piProb <= traceInterval[1]) + " "
						+ !(piInterval[0] <= traceProb && traceProb <= piInterval[1]));
				computeOneIterationRandom(i, 100);
				ConfidenceIntervalCalculator calc = new ConfidenceIntervalCalculator(this.dfa, i, 500,
						this.mTransitionAlphabet, globalVars.getTraceInterpolator());
				traceInterval = calc.calculate95TraceConfIntervals();
				piInterval = calc.calculate95PiConfIntervals();
				traceProb = (traceInterval[1] + traceInterval[0]) / 2;
				piProb = (piInterval[1] + piInterval[0]) / 2;
				logs.add("Size: " + i);
				logs.add("Trace conf interval: (" + traceInterval[0] + ", " + traceInterval[1] + ")");
				logs.add("PI conf interval: (" + piInterval[0] + ", " + piInterval[1] + ")");
				logs.add(
						"Trace bottom conf interval: (" + traceBottomInterval[0] + ", " + traceBottomInterval[1] + ")");
				logs.add("PI bottom conf interval: (" + piBottomInterval[0] + ", " + piBottomInterval[1] + ")");
				logs.add("Number of interpolants: " + this.mAllInterpolants.size());
			}
		}
	}

	public void computeMainLoopExponential(int len) throws Exception {
		for (int i = 0; i < len; i++) {
			System.out.println("Iteration: " + i);
			this.computeOneIterationExponential(i);
			ConfidenceIntervalCalculator calc = new ConfidenceIntervalCalculator(this.dfa, i, 500,
					this.mTransitionAlphabet, globalVars.getTraceInterpolator());
			double[] traceInterval = calc.calculate95TraceConfIntervals();
			double[] piInterval = calc.calculate95PiConfIntervals();
			double[] traceBottomInterval = calc.calculate95TraceConfIntervalsBottom();
			double[] piBottomInterval = calc.calculate95PiConfIntervalsBottom();
			logs.add("Size: " + i);
			logs.add("Trace conf interval: (" + traceInterval[0] + ", " + traceInterval[1] + ")");
			logs.add("PI conf interval: (" + piInterval[0] + ", " + piInterval[1] + ")");
			logs.add("Trace bottom conf interval: (" + traceBottomInterval[0] + ", " + traceBottomInterval[1] + ")");
			logs.add("PI bottom conf interval: (" + piBottomInterval[0] + ", " + piBottomInterval[1] + ")");
			logs.add("Number of interpolants: " + this.mAllInterpolants.size());

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
}
