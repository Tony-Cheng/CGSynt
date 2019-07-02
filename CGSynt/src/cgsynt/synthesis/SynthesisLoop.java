package cgsynt.synthesis;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cgsynt.Operations.CounterExamplesToInterpolants;
import cgsynt.dfa.operations.CounterexamplesGeneration;
import cgsynt.dfa.operations.DfaToLtaPowerSet;
import cgsynt.interpol.IStatement;
import cgsynt.interpol.TraceGlobalVariables;
import cgsynt.interpol.TraceToInterpolants;
import cgsynt.interpol.VariableFactory;
import cgsynt.nfa.GeneralizeStateFactory;
import cgsynt.nfa.OptimizedTraceGeneralization;
import cgsynt.tree.buchi.BuchiTreeAutomaton;
import cgsynt.tree.buchi.IntersectState;
import cgsynt.tree.buchi.lta.LTAIntersectState;
import cgsynt.tree.buchi.lta.RankedBool;
import cgsynt.tree.buchi.operations.BuchiIntersection;
import cgsynt.tree.buchi.operations.ConvertToStringState;
import cgsynt.tree.buchi.operations.EmptinessCheck;
import cgsynt.tree.buchi.operations.LTAEmptinessCheck;
import cgsynt.tree.buchi.operations.LTAIntersection;
import cgsynt.tree.buchi.operations.ProgramAutomatonConstruction;
import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.LibraryIdentifiers;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.INestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.VpAlphabet;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.operations.Determinize;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.transitions.OutgoingInternalTransition;
import de.uni_freiburg.informatik.ultimate.automata.statefactory.StringFactory;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger.LogLevel;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.boogie.BoogieNonOldVar;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;

public class SynthesisLoop {

	private BuchiTreeAutomaton<RankedBool, String> mPrograms;
	private NestedWordAutomaton<IStatement, IPredicate> mPI;
	private List<IStatement> mTransitionAlphabet;
	private IUltimateServiceProvider mService;
	private Set<IPredicate> mAllInterpolants;
	private AutomataLibraryServices mAutService;
	private BuchiTreeAutomaton<RankedBool, IntersectState<String, String>> result;
	private Set<List<IStatement>> visitedCounterexamples;
	
	private boolean mResultComputed;
	private boolean mIsCorrect;

	public SynthesisLoop(List<IStatement> transitionAlphabet, IPredicate preconditions, IPredicate postconditions)
			throws Exception {
		RankedBool.setRank(transitionAlphabet.size());
		TraceToInterpolants.getTraceToInterpolants().setPreconditions(preconditions);
		TraceToInterpolants.getTraceToInterpolants().setPostconditions(postconditions);
		preconditions = TraceToInterpolants.getTraceToInterpolants().getPreconditions();
		postconditions = TraceToInterpolants.getTraceToInterpolants().getPostconditions();
		this.mService = TraceGlobalVariables.getGlobalVariables().getService();
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
		IPredicate deadState = createDeadState();
		pi.addState(false, false, deadState);
		for (IStatement statement : mTransitionAlphabet) {
			pi.addInternalTransition(prePred, statement, deadState);
			pi.addInternalTransition(postPred, statement, deadState);
			pi.addInternalTransition(deadState, statement, deadState);
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

	private void computeOneIteration(int k) throws Exception {
		// Turn PI into a NFA that has String states.
		ConvertToStringState<IStatement, IPredicate> automataConverter = new ConvertToStringState<>(this.mPI);
		NestedWordAutomaton<IStatement, String> stringNFAPI = automataConverter.convert(mAutService);

		// Determinize the String state version of PI.
		Determinize<IStatement, String> determinize = new Determinize<>(mAutService, new StringFactory(), stringNFAPI);

		INestedWordAutomaton<IStatement, String> stringDFAPI = determinize.getResult();//addDeadStates((NestedWordAutomaton<IStatement, String>)determinize.getResult());
		
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
			return;
		}
		CounterexamplesGeneration<IStatement, String> generator = new CounterexamplesGeneration<>(stringDFAPI,
				k *stringDFAPI.getStates().size(), visitedCounterexamples);
		generator.computeResult();
		Set<List<IStatement>> counterExamples = generator.getResult();
		CounterExamplesToInterpolants counterExampleToInterpolants = new CounterExamplesToInterpolants(counterExamples);
		counterExampleToInterpolants.computeResult();

		OptimizedTraceGeneralization generalization = new OptimizedTraceGeneralization(mAllInterpolants,
				flatten(counterExampleToInterpolants.getInterpolants()), new HashSet<>(mTransitionAlphabet), mPI);
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

	public void computeMainLoop() throws Exception {
		int i = 0;
		while (!mResultComputed) {
			System.out.println("Iteration: " + i);
			System.out.println("Number of interpolants: " + this.mAllInterpolants.size());
			computeOneIteration(i + 1);
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
	
//	public NestedWordAutomaton<IStatement, String> addDeadStates(NestedWordAutomaton<IStatement, String> aut){
//		NestedWordAutomaton<IStatement, String> newDFA = aut;
//		
//		newDFA.addState(false, false, "DeadState");
//		
//		for (String state : aut.getStates()) {
//			@SuppressWarnings("unchecked")
//			Iterator<OutgoingInternalTransition<IStatement,String>> trans = (Iterator<OutgoingInternalTransition<IStatement, String>>) aut.internalSuccessors(state);
//			
//			Set<IStatement> lettersUsed = new HashSet<>();
//			while (trans.hasNext())
//				lettersUsed.add(trans.next().getLetter());
//			
//			for (IStatement letter : this.mTransitionAlphabet) {
//				if (!lettersUsed.contains(letter)) {
//					newDFA.addInternalTransition(state, letter, "DeadState");
//				}
//			}
//		}
//		
//		return newDFA;
//	}
}
