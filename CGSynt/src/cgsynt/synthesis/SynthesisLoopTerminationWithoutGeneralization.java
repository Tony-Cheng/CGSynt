package cgsynt.synthesis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cgsynt.buchi.determinization.BuchiDeterminization;
import cgsynt.dfa.operations.CounterexamplesGeneration;
import cgsynt.dfa.operations.DfaToLtaPowerSet;
import cgsynt.dfa.parity.ParityAutomaton;
import cgsynt.dfa.parity.operations.ParityAutomatonToTree;
import cgsynt.interpol.IAssumption;
import cgsynt.interpol.IStatement;
import cgsynt.interpol.TraceGlobalVariables;
import cgsynt.nfa.GeneralizeStateFactory;
import cgsynt.nfa.OptimizedTraceGeneralization;
import cgsynt.operations.CounterExamplesToInterpolants;
import cgsynt.parity.games.EvaState;
import cgsynt.parity.games.IParityGameState;
import cgsynt.parity.games.ParityGame;
import cgsynt.parity.games.ParityGameProgramExtractionV2;
import cgsynt.parity.games.QuasiTimeEmptinessCheckV2;
import cgsynt.tree.buchi.BuchiTreeAutomaton;
import cgsynt.tree.buchi.IntersectState;
import cgsynt.tree.buchi.lta.RankedBool;
import cgsynt.tree.buchi.operations.BuchiIntersection;
import cgsynt.tree.buchi.operations.ConvertToStringState;
import cgsynt.tree.buchi.operations.ProgramAutomatonConstruction;
import cgsynt.tree.buchi.parity.BuchiParityHybridIntersectAutomaton;
import cgsynt.tree.buchi.parity.BuchiParityIntersectAutomatonV2;
import cgsynt.tree.buchi.parity.BuchiParityIntersectStateV2;
import cgsynt.tree.parity.IParityState;
import cgsynt.tree.parity.ParityState;
import cgsynt.tree.parity.ParityStateFactory;
import cgsynt.tree.parity.ParityTreeAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.LibraryIdentifiers;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.INestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.VpAlphabet;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.operations.Determinize;
import de.uni_freiburg.informatik.ultimate.automata.statefactory.StringFactory;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger.LogLevel;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.logic.Term;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.BasicPredicate;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;
import de.uni_freiburg.informatik.ultimate.plugins.generator.buchiautomizer.BuchiAutomizer;

/**
 * Synthesize a correct and terminating program. The proof of terminating
 * programs must be precomputed, and omega must be set to the precomputed proof.
 *
 */
public class SynthesisLoopTerminationWithoutGeneralization {

	/**
	 * The program automaton.
	 */
	private BuchiTreeAutomaton<RankedBool, IPredicate> mPrograms;

	/**
	 * The NFA representing a set of infeasible traces.
	 */
	private INestedWordAutomaton<IStatement, IPredicate> mPI;

	/**
	 * A non-deterministic buchi automaton representing a set of eventually
	 * terminating traces.
	 */
	private NestedWordAutomaton<IStatement, IPredicate> mOmega;

	/**
	 * A list of program statements.
	 */
	private List<IStatement> mTransitionAlphabet;

	private IUltimateServiceProvider mService;

	/**
	 * The interpolants generated for pi.
	 */
	private Set<IPredicate> mAllInterpolants;
	private AutomataLibraryServices mAutService;

	/**
	 * A set of counterexamples that have already been checked.
	 */
	private Set<List<IStatement>> mVisitedCounterexamples;

	private boolean mResultComputed;
	private boolean mIsCorrect;
	private boolean mPrintLogs;
	private List<String> mLogs;
	private int mPrintedLogsSize;

	private TraceGlobalVariables mGlobalVars;

	/**
	 * The initial state of a non-empty tree.
	 */
	private IParityGameState source;
	
	/**
	 * A winning path in the parity game.
	 */
	private Map<IParityGameState, IParityGameState> nonEmptyTree;
	
	/**
	 * A parity game containing a winning path.
	 */
	private ParityGame<RankedBool, BuchiParityIntersectStateV2<IntersectState<IPredicate, String>, IParityState>> nonEmptyParityGame;
	private IPredicate deadState;
	
	/**
	 * Maps an assumption statement to its negation.
	 */
	private Map<IAssumption, IAssumption> negation;

	/**
	 * Initialize the synthesis loop.
	 * @param transitionAlphabet A list of program statements.
	 * @param preconditions
	 * @param postconditions
	 * @param globalVars
	 * @throws Exception
	 */
	public SynthesisLoopTerminationWithoutGeneralization(List<IStatement> transitionAlphabet, IPredicate preconditions,
			IPredicate postconditions, TraceGlobalVariables globalVars) throws Exception {
		this.init(transitionAlphabet, preconditions, postconditions, globalVars);
	}

	private void init(List<IStatement> transitionAlphabet, IPredicate preconditions, IPredicate postconditions,
			TraceGlobalVariables globalVars) throws Exception {
		this.mGlobalVars = globalVars;

		RankedBool.setRank(transitionAlphabet.size());
		this.mGlobalVars.getTraceInterpolator().setPreconditions(preconditions);
		this.mGlobalVars.getTraceInterpolator().setPostconditions(postconditions);
		preconditions = this.mGlobalVars.getTraceInterpolator().getPreconditions();
		postconditions = this.mGlobalVars.getTraceInterpolator().getPostconditions();
		this.mService = this.mGlobalVars.getService();
		this.mAutService = new AutomataLibraryServices(mService);
		ProgramAutomatonConstruction construction = new ProgramAutomatonConstruction(new HashSet<>(transitionAlphabet),
				this.mGlobalVars.getPredicateFactory());
		construction.computeResult();
		this.negation = construction.getNegation();
		this.deadState = construction.getDeadState();
		this.mPrograms = construction.getResult();
		this.mResultComputed = false;
		this.mTransitionAlphabet = construction.getAlphabet();
		this.mAllInterpolants = new HashSet<>();
		this.mAutService.getLoggingService().getLogger(LibraryIdentifiers.PLUGIN_ID).setLevel(LogLevel.OFF);
		this.mAllInterpolants.add(preconditions);
		this.mAllInterpolants.add(postconditions);
		this.mPI = createPI(preconditions, postconditions);
		this.mOmega = createOmega(preconditions);
		this.mVisitedCounterexamples = new HashSet<>();
		this.mLogs = new ArrayList<>();
		this.mPrintLogs = false;
		this.mPrintedLogsSize = 0;
		this.mService.getLoggingService().getLogger(BuchiAutomizer.class.getPackage().getName()).setLevel(LogLevel.OFF);
	}

	public void setPrintLogs(boolean printLogs) {
		this.mPrintLogs = printLogs;
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
				new GeneralizeStateFactory(mGlobalVars.getPredicateFactory()));
		if (!prePred.equals(postPred)) {
			pi.addState(true, false, prePred);
			pi.addState(false, true, postPred);
		} else {
			pi.addState(true, true, prePred);
		}
		OptimizedTraceGeneralization generalization = new OptimizedTraceGeneralization(new HashSet<>(),
				mAllInterpolants, new HashSet<>(mTransitionAlphabet), pi, mGlobalVars.getTraceInterpolator());
		pi = generalization.getResult();
		return pi;
	}

	private NestedWordAutomaton<IStatement, IPredicate> createOmega(IPredicate precondition) {
		Set<IStatement> letters = new HashSet<>(this.mTransitionAlphabet);

		VpAlphabet<IStatement> alphabet = new VpAlphabet<>(letters);

		NestedWordAutomaton<IStatement, IPredicate> omega = new NestedWordAutomaton<>(mAutService, alphabet,
				new GeneralizeStateFactory(mGlobalVars.getPredicateFactory()));

		return omega;
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
		// Dead states
		String deadState = "deadstate";
		IPredicate deadPredicateState = mGlobalVars.getPredicateFactory().newDebugPredicate("deadState");

		IParityState deadParityState = new ParityState<>(deadPredicateState, 1);

		// Shutdown State
		IPredicate shutdownPredicateState = mGlobalVars.getPredicateFactory().newDebugPredicate("shutdownState");
		IParityState shutdownParityState = new ParityState<>(shutdownPredicateState, 1);

		// Off State
		IPredicate offPredicateState = mGlobalVars.getPredicateFactory().newDebugPredicate("offState");
		IParityState offParityState = new ParityState<>(offPredicateState, 2);

		////////////////////////////////////////////////
		// Building PTA Omega from Buchi Omega
		BuchiDeterminization<IStatement, IPredicate> determinizeBuchi = new BuchiDeterminization<>(mOmega, mAutService,
				new ParityStateFactory());
		determinizeBuchi.computeResult();

		ParityAutomaton<IStatement, IParityState> parityOmega = determinizeBuchi.getResult();
		ParityAutomatonToTree<IStatement, IParityState> parityOmegaToParityTreeOmega = new ParityAutomatonToTree<>(
				parityOmega, this.mTransitionAlphabet, shutdownParityState, offParityState, deadParityState);

		ParityTreeAutomaton<RankedBool, IParityState> termTree = parityOmegaToParityTreeOmega.getResult();
		////////////////////////////////////////////////
		// Building the powerset of pi
		ConvertToStringState<IStatement, IPredicate> automataConverter = new ConvertToStringState<>(this.mPI);
		NestedWordAutomaton<IStatement, String> stringNFAPI = automataConverter.convert(mAutService);

		Determinize<IStatement, String> determinize = new Determinize<>(mAutService, new StringFactory(), stringNFAPI);

		INestedWordAutomaton<IStatement, String> dfaPI = determinize.getResult();

		DfaToLtaPowerSet<IStatement, String> dfaToLta = new DfaToLtaPowerSet<>(dfaPI, mTransitionAlphabet, deadState);

		BuchiTreeAutomaton<RankedBool, String> powerSet = dfaToLta.getResult();

		// Intersect all the automatons
		BuchiIntersection<RankedBool, IPredicate, String> intersection = new BuchiIntersection<>(mPrograms, powerSet);
		BuchiTreeAutomaton<RankedBool, IntersectState<IPredicate, String>> intersectedAut = intersection
				.computeResult();

		BuchiParityIntersectAutomatonV2<RankedBool, IntersectState<IPredicate, String>, IParityState> buchiParityIntersectedAut = new BuchiParityIntersectAutomatonV2<>(
				intersectedAut, termTree);

		// Emptiness check
		ParityGame<RankedBool, BuchiParityIntersectStateV2<IntersectState<IPredicate, String>, IParityState>> parityGame = new ParityGame<>(
				buchiParityIntersectedAut);
		QuasiTimeEmptinessCheckV2<RankedBool, BuchiParityIntersectStateV2<IntersectState<IPredicate, String>, IParityState>> emptinessCheck = new QuasiTimeEmptinessCheckV2<>(
				parityGame);
		emptinessCheck.computeResult();

		if (!emptinessCheck.getResult()) {
			mIsCorrect = true;
			mResultComputed = true;
			this.nonEmptyTree = emptinessCheck.getNonEmptyProof();
			this.source = emptinessCheck.getNonEmptyProofSource();
			this.nonEmptyParityGame = emptinessCheck.getNonEmptyParityGame();
			return;
		}

		// Generate counterexamples for safety
		CounterexamplesGeneration<IStatement, String> generator = new CounterexamplesGeneration<>(dfaPI,
				k * dfaPI.getStates().size(), mVisitedCounterexamples, bs, this.mTransitionAlphabet);
		generator.computeResult();
		Set<List<IStatement>> counterExamples = generator.getResult();

		// Compute the interpolants from the counterexamples
		CounterExamplesToInterpolants counterExampleToInterpolants = new CounterExamplesToInterpolants(counterExamples,
				mGlobalVars.getTraceInterpolator());
		counterExampleToInterpolants.computeResult();

		// Generalize PI from the interpolants
		OptimizedTraceGeneralization generalization = new OptimizedTraceGeneralization(mAllInterpolants,
				flatten(counterExampleToInterpolants.getInterpolants()), new HashSet<>(mTransitionAlphabet), mPI,
				mGlobalVars.getTraceInterpolator());
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

	/**
	 * Compute the main loop until a correct program is found.
	 * 
	 * @throws Exception
	 */
	public void computeMainLoop() throws Exception {
		int i = 0;
		while (!mResultComputed) {
			mLogs.add("Iteration: " + i);
			// Compute one iteration of the loop
			computeOneIteration(i + 1, -1);
			mLogs.add("Number of interpolants: " + this.mAllInterpolants.size());
			i++;
			if (this.mPrintLogs)
				printLogsIteration();
		}
	}

	public void printLogs() {
		for (String log : mLogs)
			System.out.println(log);
	}

	private void printLogsIteration() {
		for (int i = this.mPrintedLogsSize; i < mLogs.size(); i++) {
			System.out.println(mLogs.get(i));
		}
		this.mPrintedLogsSize = mLogs.size();
	}

	public void printAllInterpolants() {
		for (IPredicate interpol : this.mAllInterpolants) {
			System.out.println(interpol);
		}
	}

	private List<String> transAlphabetToString() {
		List<String> stringTransitionAlphabet = new ArrayList<>();
		for (int i = 0; i < this.mTransitionAlphabet.size(); i++) {
			stringTransitionAlphabet.add(this.mTransitionAlphabet.get(i).toString());
		}
		return stringTransitionAlphabet;
	}

	public void printProgram() {
		List<String> stringTransitionAlphabet = transAlphabetToString();
		Set<IParityGameState> deadStates = findAllDeadStates(nonEmptyParityGame.getStates());
		ParityGameProgramExtractionV2<RankedBool, BuchiParityIntersectStateV2<IntersectState<IPredicate, String>, IParityState>> programExtraction = new ParityGameProgramExtractionV2<>(
				this.source, this.nonEmptyTree, stringTransitionAlphabet, nonEmptyParityGame, deadStates);
		programExtraction.printProgram();
	}

	public void addState(int state, boolean isInitial, boolean isFinal) {
		Term trueTerm = mGlobalVars.getTraceInterpolator().getPUnifier().getTruePredicate().getFormula();
		this.mOmega.addState(isInitial, isFinal,
				new BasicPredicate(state, new String[0], trueTerm, new HashSet<>(), trueTerm));
	}

	public void addRule(int source, IStatement letter, int dest) {
		Term trueTerm = mGlobalVars.getTraceInterpolator().getPUnifier().getTruePredicate().getFormula();
		this.mOmega.addInternalTransition(
				new BasicPredicate(source, new String[0], trueTerm, new HashSet<>(), trueTerm), letter,
				new BasicPredicate(dest, new String[0], trueTerm, new HashSet<>(), trueTerm));
	}

	public NestedWordAutomaton<IStatement, IPredicate> getOmega() {
		return mOmega;
	}

	public void setOmega(NestedWordAutomaton<IStatement, IPredicate> omega) {
		this.mOmega = omega;
	}

	public Set<IParityGameState> findAllDeadStates(Set<IParityGameState> states) {
		Set<IParityGameState> deadStates = new HashSet<>();
		for (IParityGameState state : states) {
			if (state instanceof EvaState) {
				EvaState<RankedBool, BuchiParityIntersectStateV2<IntersectState<IPredicate, IPredicate>, IParityState>> evaState = (EvaState<RankedBool, BuchiParityIntersectStateV2<IntersectState<IPredicate, IPredicate>, IParityState>>) state;
				if (evaState.getRule().getSource().getState().getState1().getState1().equals(this.deadState)) {
					deadStates.add(state);
				}
			}
		}
		return deadStates;
	}

	public Map<IAssumption, IAssumption> getNegation() {
		return negation;
	}
}
