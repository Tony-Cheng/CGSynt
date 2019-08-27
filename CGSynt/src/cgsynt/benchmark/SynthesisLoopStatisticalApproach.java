package cgsynt.benchmark;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cgsynt.automaton.factory.PDeterminizeStateFactory;
import cgsynt.dfa.operations.CounterexamplesGeneration;
import cgsynt.interpol.IStatement;
import cgsynt.interpol.TraceGlobalVariables;
import cgsynt.nfa.GeneralizeStateFactory;
import cgsynt.nfa.OptimizedTraceGeneralization;
import cgsynt.operations.CounterExampleGenerationStatisticalApproach;
import cgsynt.operations.CounterExamplesToInterpolants;
import cgsynt.tree.buchi.BuchiTreeAutomaton;
import cgsynt.tree.buchi.BuchiTreeAutomatonRule;
import cgsynt.tree.buchi.IntersectState;
import cgsynt.tree.buchi.lta.RankedBool;
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

public class SynthesisLoopStatisticalApproach {

	private BuchiTreeAutomaton<RankedBool, IPredicate> mPrograms;
	private NestedWordAutomaton<IStatement, IPredicate> mPI;
	private List<IStatement> mTransitionAlphabet;
	private IUltimateServiceProvider mService;
	private Set<IPredicate> mAllInterpolants;
	private AutomataLibraryServices mAutService;
	private CounterExampleGenerationStatisticalApproach<IPredicate> generator;
	private TraceGlobalVariables globalVars;

	public SynthesisLoopStatisticalApproach(List<IStatement> transitionAlphabet, IPredicate preconditions,
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
		// IPredicate deadState =
		// globalVars.getPredicateFactory().newDebugPredicate("deadState");

		// Transform the DFA into an LTA
		// DfaToLtaPowerSet<IStatement, IPredicate> dfaToLta = new
		// DfaToLtaPowerSet<>(dfaPI, mTransitionAlphabet,
		// deadState);

		// BuchiTreeAutomaton<RankedBool, IPredicate> powerSet = dfaToLta.getResult();

		// BuchiIntersection<RankedBool, IPredicate, IPredicate> intersection = new
		// BuchiIntersection<>(mPrograms,
		// powerSet);
		// BuchiTreeAutomaton<RankedBool, IntersectState<IPredicate, IPredicate>>
		// intersectedAut = intersection
		// .computeResult();
		// EmptinessCheck<RankedBool, IntersectState<IPredicate, IPredicate>>
		// emptinessCheck = new EmptinessCheck<>(
		// intersectedAut);
		// emptinessCheck.computeResult();
		// if (!emptinessCheck.getResult()) {
		// mIsCorrect = true;
		// mResultComputed = true;
		// result = emptinessCheck.getGoodAutomaton();
		// this.goodProgram = emptinessCheck.getGoodProgram();
		// return;
		// }

		// Change the set of interpolants after the old and new ones have been used to
		// calculate the new triplets.
		Set<IPredicate> interpolants = generator.computeInterpolants(dfaPI);
		OptimizedTraceGeneralization generalization = new OptimizedTraceGeneralization(mAllInterpolants, interpolants,
				new HashSet<>(mTransitionAlphabet), mPI, globalVars.getTraceInterpolator());
		mPI = generalization.getResult();
		this.mAllInterpolants.addAll(interpolants);
	}

	private Set<IPredicate> flatten(List<Set<IPredicate>> interpolants) {
		Set<IPredicate> flattenedInterpolants = new HashSet<>();

		for (Set<IPredicate> interpolantsSet : interpolants) {
			flattenedInterpolants.addAll(interpolantsSet);
		}

		return flattenedInterpolants;
	}

	/**
	 * Compute the main loop until a correct program is found.
	 * 
	 * @throws Exception
	 */
	public void computeMainLoop(int k) throws Exception {
		long time = System.currentTimeMillis();
		int prevSize = this.mAllInterpolants.size();
		generator = new CounterExampleGenerationStatisticalApproach<>(k, 10, 100, 1.0,
				this.globalVars.getTraceInterpolator(), this.mTransitionAlphabet);
		while (true) {
			computeOneIteration(k, 1);
			if (this.mAllInterpolants.size() > prevSize) {
				prevSize = this.mAllInterpolants.size();
				System.out.println(this.mAllInterpolants.size());
				System.out.println("Time: " + (System.currentTimeMillis() - time));
			}
		}
	}
}
