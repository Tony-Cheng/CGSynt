package cgsynt.synthesis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cgsynt.automaton.factory.PDeterminizeStateFactory;
import cgsynt.buchi.determinization.BuchiDeterminization;
import cgsynt.core.Specification;
import cgsynt.core.service.CustomServiceProvider;
import cgsynt.dfa.operations.CounterexamplesGeneration;
import cgsynt.dfa.operations.DfaToLtaPowerSet;
import cgsynt.dfa.operations.FiniteTracesAcceptanceConversion;
import cgsynt.dfa.parity.ParityAutomaton;
import cgsynt.dfa.parity.operations.ParityAutomatonToTree;
import cgsynt.dfa.parity.operations.ParityComplementAndCounterexampleGeneration;
import cgsynt.dfa.parity.operations.ParityCounterexample;
import cgsynt.interpol.IStatement;
import cgsynt.interpol.TraceGlobalVariables;
import cgsynt.nfa.GeneralizeStateFactory;
import cgsynt.nfa.OptimizedTraceGeneralization;
import cgsynt.operations.CounterExamplesToInterpolants;
import cgsynt.operations.ProgramRetrieval;
import cgsynt.operations.TerminatingProgramExtraction;
import cgsynt.probability.ConfidenceIntervalCalculator;
import cgsynt.termination.OmegaRefiner;
import cgsynt.tree.buchi.BuchiTreeAutomaton;
import cgsynt.tree.buchi.BuchiTreeAutomatonRule;
import cgsynt.tree.buchi.IntersectState;
import cgsynt.tree.buchi.lta.RankedBool;
import cgsynt.tree.buchi.operations.BuchiIntersection;
import cgsynt.tree.buchi.operations.ProgramAutomatonConstruction;
import cgsynt.tree.buchi.parity.BuchiParityIntersectAutomaton;
import cgsynt.tree.buchi.parity.BuchiParityIntersectRule;
import cgsynt.tree.buchi.parity.BuchiParityIntersectState;
import cgsynt.tree.buchi.parity.operations.BuchiParityEmptinessCheck;
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
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.core.model.models.Payload;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger.LogLevel;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IcfgEdgeFactory;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IcfgInternalTransition;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IcfgLocation;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.debugidentifiers.StringDebugIdentifier;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;

public class SynthesisLoopWithTermination {

	private BuchiTreeAutomaton<RankedBool, IPredicate> mPrograms;
	private NestedWordAutomaton<IStatement, IPredicate> mPI;
	private NestedWordAutomaton<IcfgInternalTransition, IPredicate> mOmega;

	private List<IStatement> mTransitionAlphabet;
	private List<IcfgInternalTransition> mIcfgTransitionAlphabet;
	private IUltimateServiceProvider mService;
	private Set<IPredicate> mAllInterpolants;
	private AutomataLibraryServices mAutService;
	private BuchiParityIntersectAutomaton<RankedBool, IntersectState<IPredicate, IPredicate>, IParityState> result;
	private Set<List<IStatement>> visitedCounterexamples;

	private boolean mResultComputed;
	private boolean mIsCorrect;
	private List<String> logs;
	private boolean printLogs;
	private int printedLogsSize;
	private TraceGlobalVariables globalVars;
	private OmegaRefiner mOmegaRefiner;

	public SynthesisLoopWithTermination(List<IStatement> transitionAlphabet, IPredicate preconditions,
			IPredicate postconditions, TraceGlobalVariables globalVars) throws Exception {
		this.globalVars = globalVars;
		assert globalVars.getService() instanceof CustomServiceProvider;

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
		this.mIcfgTransitionAlphabet = createIcfgTransitionAlphabet(mTransitionAlphabet);
		this.mAllInterpolants = new HashSet<>();
		this.mAutService.getLoggingService().getLogger(LibraryIdentifiers.PLUGIN_ID).setLevel(LogLevel.OFF);
		this.mAllInterpolants.add(preconditions);
		this.mAllInterpolants.add(postconditions);
		this.mPI = createPI(preconditions, postconditions);
		this.mOmega = createOmega(preconditions);
		this.visitedCounterexamples = new HashSet<>();
		this.logs = new ArrayList<>();
		this.printLogs = false;
		this.printedLogsSize = 0;
		this.mOmegaRefiner = new OmegaRefiner(this.globalVars, this.mOmega);
	}

	public SynthesisLoopWithTermination(Specification spec) throws Exception {
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
		this.mIcfgTransitionAlphabet = createIcfgTransitionAlphabet(mTransitionAlphabet);
		this.mAllInterpolants = new HashSet<>();
		this.mAutService.getLoggingService().getLogger(LibraryIdentifiers.PLUGIN_ID).setLevel(LogLevel.OFF);
		this.mAllInterpolants.add(preconditions);
		this.mAllInterpolants.add(postconditions);
		this.mPI = createPI(preconditions, postconditions);
		this.mOmega = createOmega(preconditions);
		this.visitedCounterexamples = new HashSet<>();
		this.logs = new ArrayList<>();
		this.printLogs = false;
		this.printedLogsSize = 0;
		this.mOmegaRefiner = new OmegaRefiner(this.globalVars, this.mOmega);
	}

	public void setPrintLogs(boolean printLogs) {
		this.printLogs = printLogs;
	}

	public List<IcfgInternalTransition> createIcfgTransitionAlphabet(List<IStatement> alphabet) {
		List<IcfgInternalTransition> icfgAlphabet = new ArrayList<>();

		IcfgEdgeFactory factory = new IcfgEdgeFactory(OmegaRefiner.SERIAL_PROVIDER);
		IcfgLocation location = new IcfgLocation(new StringDebugIdentifier("0"), "p1");

		for (IStatement statement : alphabet) {
			IcfgInternalTransition trans = factory.createInternalTransition(location, location, new Payload(),
					statement.getTransFormula());
			icfgAlphabet.add(trans);
		}

		return icfgAlphabet;
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

	private NestedWordAutomaton<IcfgInternalTransition, IPredicate> createOmega(IPredicate precondition) {
		Set<IcfgInternalTransition> letters = new HashSet<>(this.mIcfgTransitionAlphabet);

		VpAlphabet<IcfgInternalTransition> alphabet = new VpAlphabet<>(letters);

		NestedWordAutomaton<IcfgInternalTransition, IPredicate> omega = new NestedWordAutomaton<>(mAutService, alphabet,
				new GeneralizeStateFactory(globalVars.getPredicateFactory()));

		omega.addState(true, false, precondition);

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
		IPredicate deadPredicateState = globalVars.getPredicateFactory().newDebugPredicate("deadState");
		IParityState deadParityState = new ParityState<>(deadPredicateState, 1);
		
		// Shutdown State
		IPredicate shutdownPredicateState = globalVars.getPredicateFactory().newDebugPredicate("shutdownState");
		IParityState shutdownParityState = new ParityState<>(shutdownPredicateState, 1);

		// Off State
		IPredicate offPredicateState = globalVars.getPredicateFactory().newDebugPredicate("offState");
		IParityState offParityState = new ParityState<>(offPredicateState, 2);
		
		////////////////////////////////////////////////
		// Building PTA Omega from Buchi Omega
		BuchiDeterminization<IcfgInternalTransition, IPredicate> determinizeBuchi = new BuchiDeterminization<>(mOmega,
				mAutService, new ParityStateFactory());
		determinizeBuchi.computeResult();

		ParityAutomaton<IcfgInternalTransition, IParityState> parityOmega = determinizeBuchi.getResult();

		ParityAutomatonToTree<IcfgInternalTransition, IParityState> parityOmegaToParityTreeOmega = new ParityAutomatonToTree<>(
				parityOmega, mIcfgTransitionAlphabet, shutdownParityState, offParityState, deadParityState);

		ParityTreeAutomaton<RankedBool, IParityState> termTree = parityOmegaToParityTreeOmega.getResult();
		////////////////////////////////////////////////

		// Determinize the String state version of PI.
		Determinize<IStatement, IPredicate> determinize = new Determinize<>(mAutService,
				new PDeterminizeStateFactory(globalVars.getPredicateFactory()), mPI);

		INestedWordAutomaton<IStatement, IPredicate> dfaPI = determinize.getResult();

		// Transform the DFA into an LTA
		DfaToLtaPowerSet<IStatement, IPredicate> dfaToLta = new DfaToLtaPowerSet<>(dfaPI, mTransitionAlphabet,
				deadPredicateState);

		BuchiTreeAutomaton<RankedBool, IPredicate> powerSet = dfaToLta.getResult();

		BuchiIntersection<RankedBool, IPredicate, IPredicate> intersection = new BuchiIntersection<>(mPrograms,
				powerSet);
		BuchiTreeAutomaton<RankedBool, IntersectState<IPredicate, IPredicate>> intersectedAut = intersection
				.computeResult();

		
		
		/*
		BuchiTreeAutomaton<RankedBool, String> aut1 = new BuchiTreeAutomaton<>(2);
		String bs1 = "buchi state 1";
		aut1.addInitState(bs1);
		aut1.addFinalState(bs1);
		List<String> blist1 = new ArrayList<>();
		blist1.add(bs1);
		blist1.add(bs1);
		BuchiTreeAutomatonRule<RankedBool, String> brule1 = new BuchiTreeAutomatonRule<>(RankedBool.FALSE, bs1, blist1);
		aut1.addRule(brule1);
		*/
		
		
		BuchiParityIntersectAutomaton<RankedBool, IntersectState<IPredicate, IPredicate>, IParityState> buchiParityIntersectedAut = new BuchiParityIntersectAutomaton<>(
				intersectedAut, termTree);

		BuchiParityEmptinessCheck<RankedBool, IntersectState<IPredicate, IPredicate>, IParityState> emptinessCheck = new BuchiParityEmptinessCheck<>(
				buchiParityIntersectedAut);
		emptinessCheck.computeResult();
		
		
		
		// BuchiParityIntersectAutomaton<RankedBool, IntersectState<IPredicate,
		// IPredicate>, IParityState> buchiParityIntersectedAut = new
		// BuchiParityIntersectAutomaton<>(
		// intersectedAut, termTree);
		//
		// BuchiParityEmptinessCheck<RankedBool, IntersectState<IPredicate, IPredicate>,
		// IParityState> emptinessCheck = new BuchiParityEmptinessCheck<>(
		// buchiParityIntersectedAut);
		// emptinessCheck.computeResult();
		if (!emptinessCheck.getResult()) {
			mIsCorrect = true;
			mResultComputed = true;
			// result = buchiParityIntersectedAut;
			
			System.out.println("Probably Wrong");
			return;
		}
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

		///////////////////////////////////////////////////////////////////////////////////////////////
		// Omega Refinement Process
		int minOmegaLen = Math.min(mOmega.size(), parityOmega.size());
		ParityComplementAndCounterexampleGeneration<IcfgInternalTransition> omegaCounterexampleGenerator = new ParityComplementAndCounterexampleGeneration<>(
				parityOmega, k * minOmegaLen);
		omegaCounterexampleGenerator.computeResult();

		List<ParityCounterexample<IcfgInternalTransition, IParityState>> omegaCounterexamples = omegaCounterexampleGenerator
				.getResult();

		// Refinement
		for (int i = 0; i < omegaCounterexamples.size(); i++) {
			mOmegaRefiner.certifyCE(omegaCounterexamples.get(i));
		}

		// System.out.println(termTree);
	}

	/**
	 * Return an automaton that contains a correct program.
	 * 
	 * @return
	 */
	public BuchiParityIntersectAutomaton<RankedBool, IntersectState<IPredicate, IPredicate>, IParityState> getResult() {
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

	// public void printProgram() {
	// IStatement[] statements = new IStatement[mTransitionAlphabet.size()];
	// for (int i = 0; i < statements.length; i++) {
	// statements[i] = mTransitionAlphabet.get(i);
	// }
	// TerminatingProgramExtraction<RankedBool, IParityState> retrieve = new
	// TerminatingProgramExtraction<>(result,
	// statements, goodProgram);
	// retrieve.computeResult();
	// for (String statement : retrieve.getResult()) {
	// System.out.println(statement);
	// }
	// }
}
