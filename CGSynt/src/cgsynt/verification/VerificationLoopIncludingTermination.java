package cgsynt.verification;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import cgsynt.buchi.determinization.BuchiDeterminization;
import cgsynt.dfa.operations.CounterexamplesGeneration;
import cgsynt.dfa.operations.DfaToLtaPowerSet;
import cgsynt.dfa.parity.ParityAutomaton;
import cgsynt.dfa.parity.operations.ParityAutomatonToTree;
import cgsynt.interpol.IAssumption;
import cgsynt.interpol.IStatement;
import cgsynt.interpol.ScriptPredicateAssumptionStatement;
import cgsynt.interpol.TraceGlobalVariables;
import cgsynt.interpol.TraceToInterpolants;
import cgsynt.interpol.VariableFactory;
import cgsynt.nfa.GeneralizeStateFactory;
import cgsynt.nfa.MultiThreadGeneralization;
import cgsynt.nfa.OptimizedTraceGeneralization;
import cgsynt.operations.CounterExamplesToInterpolants;
import cgsynt.parity.games.ParityGame;
import cgsynt.parity.games.QuasiTimeEmptinessCheckV2;
import cgsynt.tree.buchi.BuchiTreeAutomaton;
import cgsynt.tree.buchi.IntersectState;
import cgsynt.tree.buchi.lta.LTAIntersectState;
import cgsynt.tree.buchi.lta.RankedBool;
import cgsynt.tree.buchi.operations.BuchiIntersection;
import cgsynt.tree.buchi.operations.ConvertToStringState;
import cgsynt.tree.buchi.operations.LTAEmptinessCheck;
import cgsynt.tree.buchi.operations.LTAIntersection;
import cgsynt.tree.buchi.parity.BuchiParityIntersectAutomatonV2;
import cgsynt.tree.buchi.parity.BuchiParityIntersectStateV2;
import cgsynt.tree.parity.IParityState;
import cgsynt.tree.parity.ParityState;
import cgsynt.tree.parity.ParityStateFactory;
import cgsynt.tree.parity.ParityTreeAutomaton;
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
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IcfgInternalTransition;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.PredicateUnifier;

public class VerificationLoopIncludingTermination {

	private BuchiTreeAutomaton<RankedBool, String> mPrograms;
	private NestedWordAutomaton<IStatement, IPredicate> mPI;
	private List<IStatement> mTransitionAlphabet;
	private IUltimateServiceProvider mService;
	private Set<IPredicate> mAllInterpolants;
	private AutomataLibraryServices mAutService;

	private boolean mResultComputed;
	private boolean mIsCorrect;
	private List<String> logs;
	private boolean printLogs;
	private int prevLogLen;
	private TraceGlobalVariables globalVars;
	private NestedWordAutomaton<IStatement, String> omega;

	public VerificationLoopIncludingTermination(BuchiTreeAutomaton<RankedBool, String> programs,
			List<IStatement> transitionAlphabet, IPredicate preconditions, IPredicate postconditions,
			TraceGlobalVariables globalVars, NestedWordAutomaton<IStatement, String> omega) throws Exception {
		RankedBool.setRank(transitionAlphabet.size());
		this.globalVars = globalVars;
		globalVars.getTraceInterpolator().setPreconditions(preconditions);
		globalVars.getTraceInterpolator().setPostconditions(postconditions);
		preconditions = globalVars.getTraceInterpolator().getPreconditions();
		postconditions = globalVars.getTraceInterpolator().getPostconditions();
		this.mService = globalVars.getService();
		this.mAutService = new AutomataLibraryServices(mService);
		this.mPrograms = programs;
		this.mResultComputed = false;
		this.mTransitionAlphabet = transitionAlphabet;
		this.mAllInterpolants = new HashSet<>();
		this.mAutService.getLoggingService().getLogger(LibraryIdentifiers.PLUGIN_ID).setLevel(LogLevel.OFF);
		this.mAllInterpolants.add(preconditions);
		this.mAllInterpolants.add(postconditions);
		this.mPI = createPI(preconditions, postconditions);
		this.logs = new ArrayList<>();
		this.printLogs = false;
		this.prevLogLen = 0;
		this.omega = omega;
	}

	public void setPrintLogs(boolean printLogs) {
		this.printLogs = printLogs;
	}

	/**
	 * Create an empty program proof.
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
	 * @throws Exception
	 */
	private void computeOneIteration(int k) throws Exception {
		// Dead states
		IParityState deadParityState = new ParityState<>("dead state", 1);

		// Shutdown State
		IParityState shutdownParityState = new ParityState<>("shutdown state", 1);

		// Off State
		IParityState offParityState = new ParityState<>("off state", 2);

		// Compute the termination proof
		BuchiDeterminization<IStatement, String> determinizeBuchi = new BuchiDeterminization<>(omega, mAutService,
				new ParityStateFactory());
		determinizeBuchi.computeResult();

		ParityAutomaton<IStatement, IParityState> parityOmega = determinizeBuchi.getResult();

		ParityAutomatonToTree<IStatement, IParityState> parityOmegaToParityTreeOmega = new ParityAutomatonToTree<>(
				parityOmega, mTransitionAlphabet, shutdownParityState, offParityState, deadParityState);

		ParityTreeAutomaton<RankedBool, IParityState> termTree = parityOmegaToParityTreeOmega.getResult();

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

		BuchiIntersection<RankedBool, String, String> intersection = new BuchiIntersection<>(mPrograms, powerSet);
		BuchiTreeAutomaton<RankedBool, IntersectState<String, String>> intersectedAut = intersection.computeResult();

		BuchiParityIntersectAutomatonV2<RankedBool, IntersectState<String, String>, IParityState> buchiParityIntersectedAut = new BuchiParityIntersectAutomatonV2<>(
				intersectedAut, termTree);

		ParityGame<RankedBool, BuchiParityIntersectStateV2<IntersectState<String, String>, IParityState>> parityGame = new ParityGame<>(
				buchiParityIntersectedAut);
		QuasiTimeEmptinessCheckV2<RankedBool, BuchiParityIntersectStateV2<IntersectState<String, String>, IParityState>> emptinessCheck = new QuasiTimeEmptinessCheckV2<>(
				parityGame);
		emptinessCheck.computeResult();
		if (!emptinessCheck.getResult()) {
			mIsCorrect = true;
			mResultComputed = true;
			return;
		}
		CounterexamplesGeneration<IStatement, String> generator = new CounterexamplesGeneration<>(stringDFAPI,
				k * stringDFAPI.getStates().size(), this.mTransitionAlphabet);
		generator.computeResult();
		Set<List<IStatement>> counterExamples = generator.getResult();
		CounterExamplesToInterpolants counterExampleToInterpolants = new CounterExamplesToInterpolants(counterExamples,
				globalVars.getTraceInterpolator());
		counterExampleToInterpolants.computeResult();

		OptimizedTraceGeneralization generalization = new OptimizedTraceGeneralization(mAllInterpolants,
				flatten(counterExampleToInterpolants.getInterpolants()), new HashSet<>(mTransitionAlphabet), mPI,
				globalVars.getTraceInterpolator());
		mPI = generalization.getResult();
		logs.add("Generalization: " + (System.nanoTime() - time) / 1000000);
		time = System.nanoTime();

		// Change the set of interpolants after the old and new ones have been used to
		// calculate the new triplets.
		this.mAllInterpolants.addAll(flatten(counterExampleToInterpolants.getInterpolants()));
	}

	public void printLogs() {
		for (String log : logs) {
			System.out.println(log);
		}
	}

	public List<String> getLogs() {
		return logs;
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
	 * Compute the main loop until the program is verified.
	 * 
	 * @throws Exception
	 */
	public void computeMainLoop() throws Exception {
		int i = 0;
		while (!mResultComputed) {
			logs.add("Iteration:" + i);
			computeOneIteration();
			logs.add("Number of interpolants: " + this.mAllInterpolants.size());
			i++;
			if (printLogs)
				printLogsOnIteration();
		}
		logs.add("The process took " + i + " iterations.");
		if (printLogs)
			printLogsOnIteration();

	}

	private void printLogsOnIteration() {
		for (int i = this.prevLogLen; i < this.logs.size(); i++) {
			System.out.println(logs.get(i));
		}
		this.prevLogLen = this.logs.size();
	}

	private void printAllInterpolants() {
		for (IPredicate interpol : this.mAllInterpolants) {
			System.out.println(interpol);
		}
	}

}
