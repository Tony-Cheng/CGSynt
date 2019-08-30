package cgsynt.synthesis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cgsynt.automaton.factory.PDeterminizeStateFactory;
import cgsynt.buchi.determinization.BuchiDeterminization;
import cgsynt.core.service.CustomServiceProvider;
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
import cgsynt.parity.games.ParityGameProgramExtraction;
import cgsynt.parity.games.ParityGameProgramExtractionV2;
import cgsynt.parity.games.QuasiTimeEmptinessCheck;
import cgsynt.parity.games.QuasiTimeEmptinessCheckV2;
import cgsynt.termination.OmegaRefiner;
import cgsynt.tree.buchi.BuchiTreeAutomaton;
import cgsynt.tree.buchi.IntersectState;
import cgsynt.tree.buchi.lta.RankedBool;
import cgsynt.tree.buchi.operations.BuchiIntersection;
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
import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;
import de.uni_freiburg.informatik.ultimate.core.model.models.Payload;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger.LogLevel;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.logic.Term;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IcfgEdgeFactory;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IcfgInternalTransition;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IcfgLocation;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.debugidentifiers.StringDebugIdentifier;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.BasicPredicate;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;
import de.uni_freiburg.informatik.ultimate.plugins.generator.buchiautomizer.BuchiAutomizer;

public class SynthesisLoopTerminationWithoutGeneralization {

	private BuchiTreeAutomaton<RankedBool, IPredicate> mPrograms;
	private INestedWordAutomaton<IStatement, IPredicate> mPI;
	private NestedWordAutomaton<IcfgInternalTransition, IPredicate> mOmega;

	private List<IStatement> mTransitionAlphabet;
	private Map<IStatement, IcfgInternalTransition> mIcfgTransitionMap;
	private Map<IcfgInternalTransition, IStatement> mIStatementMap;

	private IUltimateServiceProvider mService;
	private Set<IPredicate> mAllInterpolants;
	private AutomataLibraryServices mAutService;
	private BuchiParityHybridIntersectAutomaton<RankedBool, IntersectState<IPredicate, IPredicate>, IParityState> mResult;
	private Set<List<IStatement>> mVisitedCounterexamples;

	private boolean mResultComputed;
	private boolean mIsCorrect;
	private boolean mPrintLogs;
	private List<String> mLogs;
	private int mPrintedLogsSize;

	private TraceGlobalVariables mGlobalVars;

	private IParityGameState source;
	private Map<IParityGameState, IParityGameState> nonEmptyTree;
	private ParityGame<RankedBool, BuchiParityIntersectStateV2<IntersectState<IPredicate, IPredicate>, IParityState>> nonEmptyParityGame;
	private IPredicate deadState;
	private Map<IAssumption, IAssumption> negation;

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
		this.mIStatementMap = new HashMap<>();
		this.mIcfgTransitionMap = createIcfgTransitionMap(mTransitionAlphabet);
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

	public Map<IStatement, IcfgInternalTransition> createIcfgTransitionMap(List<IStatement> alphabet) {
		Map<IStatement, IcfgInternalTransition> icfgAlphabetMap = new HashMap<>();

		IcfgEdgeFactory factory = new IcfgEdgeFactory(OmegaRefiner.SERIAL_PROVIDER);
		IcfgLocation location = new IcfgLocation(new StringDebugIdentifier("0"), "p1");

		for (IStatement statement : alphabet) {
			IcfgInternalTransition trans = factory.createInternalTransition(location, location, new Payload(),
					statement.getTransFormula());
			icfgAlphabetMap.put(statement, trans);
			this.mIStatementMap.put(trans, statement);
		}

		return icfgAlphabetMap;
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

	private NestedWordAutomaton<IcfgInternalTransition, IPredicate> createOmega(IPredicate precondition) {
		Set<IcfgInternalTransition> letters = new HashSet<>(this.mIcfgTransitionMap.values());

		VpAlphabet<IcfgInternalTransition> alphabet = new VpAlphabet<>(letters);

		NestedWordAutomaton<IcfgInternalTransition, IPredicate> omega = new NestedWordAutomaton<>(mAutService, alphabet,
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
		BuchiDeterminization<IcfgInternalTransition, IPredicate> determinizeBuchi = new BuchiDeterminization<>(mOmega,
				mAutService, new ParityStateFactory());
		determinizeBuchi.computeResult();

		ParityAutomaton<IcfgInternalTransition, IParityState> parityOmega = determinizeBuchi.getResult();
		ParityAutomatonToTree<IcfgInternalTransition, IParityState> parityOmegaToParityTreeOmega = new ParityAutomatonToTree<>(
				parityOmega, new ArrayList<>(mIcfgTransitionMap.values()), shutdownParityState, offParityState,
				deadParityState);

		ParityTreeAutomaton<RankedBool, IParityState> termTree = parityOmegaToParityTreeOmega.getResult();
		////////////////////////////////////////////////

		// Determinize PI
		Determinize<IStatement, IPredicate> determinize = new Determinize<>(mAutService,
				new PDeterminizeStateFactory(mGlobalVars.getPredicateFactory()), mPI);

		INestedWordAutomaton<IStatement, IPredicate> dfaPI = determinize.getResult();

		// Transform the DFA into an LTA
		DfaToLtaPowerSet<IStatement, IPredicate> dfaToLta = new DfaToLtaPowerSet<>(dfaPI, mTransitionAlphabet,
				deadPredicateState);

		BuchiTreeAutomaton<RankedBool, IPredicate> powerSet = dfaToLta.getResult();

		BuchiIntersection<RankedBool, IPredicate, IPredicate> intersection = new BuchiIntersection<>(mPrograms,
				powerSet);
		BuchiTreeAutomaton<RankedBool, IntersectState<IPredicate, IPredicate>> intersectedAut = intersection
				.computeResult();

		BuchiParityIntersectAutomatonV2<RankedBool, IntersectState<IPredicate, IPredicate>, IParityState> buchiParityIntersectedAut = new BuchiParityIntersectAutomatonV2<>(
				intersectedAut, termTree);

		ParityGame<RankedBool, BuchiParityIntersectStateV2<IntersectState<IPredicate, IPredicate>, IParityState>> parityGame = new ParityGame<>(
				buchiParityIntersectedAut);
		QuasiTimeEmptinessCheckV2<RankedBool, BuchiParityIntersectStateV2<IntersectState<IPredicate, IPredicate>, IParityState>> emptinessCheck = new QuasiTimeEmptinessCheckV2<>(
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
		CounterexamplesGeneration<IStatement, IPredicate> generator = new CounterexamplesGeneration<>(dfaPI,
				k * dfaPI.getStates().size(), mVisitedCounterexamples, bs, this.mTransitionAlphabet);
		generator.computeResult();
		Set<List<IStatement>> counterExamples = generator.getResult();
		CounterExamplesToInterpolants counterExampleToInterpolants = new CounterExamplesToInterpolants(counterExamples,
				mGlobalVars.getTraceInterpolator());
		counterExampleToInterpolants.computeResult();

		OptimizedTraceGeneralization generalization = new OptimizedTraceGeneralization(mAllInterpolants,
				flatten(counterExampleToInterpolants.getInterpolants()), new HashSet<>(mTransitionAlphabet), mPI,
				mGlobalVars.getTraceInterpolator());
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
	public BuchiParityHybridIntersectAutomaton<RankedBool, IntersectState<IPredicate, IPredicate>, IParityState> getResult() {
		return mResult;
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
		ParityGameProgramExtractionV2<RankedBool, BuchiParityIntersectStateV2<IntersectState<IPredicate, IPredicate>, IParityState>> programExtraction = new ParityGameProgramExtractionV2<>(
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
				new BasicPredicate(source, new String[0], trueTerm, new HashSet<>(), trueTerm),
				mIcfgTransitionMap.get(letter),
				new BasicPredicate(dest, new String[0], trueTerm, new HashSet<>(), trueTerm));
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
