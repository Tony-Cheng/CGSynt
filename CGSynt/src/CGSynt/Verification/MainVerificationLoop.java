package CGSynt.Verification;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import CGSynt.Operations.CounterExamplesToInterpolants;
import cgsynt.interpol.IStatement;
import cgsynt.interpol.TraceGlobalVariables;
import cgsynt.interpol.TraceToInterpolants;
import cgsynt.tree.buchi.BuchiTreeAutomaton;
import cgsynt.tree.buchi.lta.LTAIntersectState;
import cgsynt.tree.buchi.lta.RankedBool;
import cgsynt.tree.buchi.operations.LTAEmptinessCheck;
import cgsynt.tree.buchi.operations.LTAIntersection;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;

public class MainVerificationLoop {

	private BuchiTreeAutomaton<RankedBool, String> programs;
	private BuchiTreeAutomaton<RankedBool, IPredicate> powerSet;
	private NestedWordAutomaton<IStatement, IPredicate> pi;
	private List<IStatement> transitionAlphabet;
	private boolean isCorrect;

	private boolean resultComputed;

	public MainVerificationLoop(BuchiTreeAutomaton<RankedBool, String> programs, List<IStatement> transitionAlphabet) {
		this.programs = programs;
		this.resultComputed = false;
		this.transitionAlphabet = new ArrayList<>();
		this.powerSet = constructInitPowerSet(transitionAlphabet.size());
	}

	private BuchiTreeAutomaton<RankedBool, IPredicate> constructInitPowerSet(int n) {
		BuchiTreeAutomaton<RankedBool, IPredicate> aut = new BuchiTreeAutomaton<>(n);
		aut.addInitState(TraceToInterpolants.getTraceToInterpolants().getTruePredicate());
		return powerSet;

	}

	private void computeOneIteration() {
		LTAIntersection<RankedBool, String, IPredicate> intersection = new LTAIntersection<>(programs, powerSet);
		BuchiTreeAutomaton<RankedBool, LTAIntersectState<String, IPredicate>> intersectedAut = intersection
				.computeResult();
		LTAEmptinessCheck<RankedBool, LTAIntersectState<String, IPredicate>> emptinessCheck = new LTAEmptinessCheck<>(
				intersectedAut);
		emptinessCheck.computeResult();
		if (!emptinessCheck.getResult()) {
			isCorrect = true;
			resultComputed = true;
			return;
		}
		Set<List<IStatement>> counterExamples = emptinessCheck.findCounterExamples(transitionAlphabet);
		CounterExamplesToInterpolants counterExampleToInterpolants = new CounterExamplesToInterpolants(counterExamples);
		counterExampleToInterpolants.computeResult();
		if (counterExampleToInterpolants.getIncorrectTrace().size() > 0) {
			isCorrect = false;
			resultComputed = true;
			return;
		}
		List<Set<IPredicate>> interpolants = counterExampleToInterpolants.getInterpolants();
		List<Set<IStatement>> correctTrace = counterExampleToInterpolants.getCorrectTraces();

	}

	public boolean isCorrect() {
		return isCorrect;
	}

	public void computeMainLoop() {
		while (!resultComputed) {
			computeOneIteration();
		}
	}

}
