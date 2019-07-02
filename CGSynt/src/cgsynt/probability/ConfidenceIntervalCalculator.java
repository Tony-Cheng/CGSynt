package cgsynt.probability;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cgsynt.Operations.CounterExamplesToInterpolants;
import cgsynt.interpol.IStatement;
import cgsynt.interpol.TraceToInterpolants;
import cgsynt.tree.buchi.BuchiTreeAutomaton;
import cgsynt.tree.buchi.IntersectState;
import cgsynt.tree.buchi.lta.RankedBool;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.INestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.transitions.OutgoingInternalTransition;

public class ConfidenceIntervalCalculator {

	private int k;
	private INestedWordAutomaton<IStatement, String> aut;
	private int sampleSize;
	private static double z975 = 1.96;
	private List<IStatement> transitionAlphabet;

	public ConfidenceIntervalCalculator(INestedWordAutomaton<IStatement, String> aut, int k, int sampleSize,
			List<IStatement> transitionAlphabet) {
		this.aut = aut;
		this.k = k;
		this.sampleSize = sampleSize;
		this.transitionAlphabet = transitionAlphabet;
	}

	private void generateSingleSample(String state, long len, List<IStatement> trace) {
		if (len == 0)
			return;
		long size = 1;
		size = (long) ((Math.pow(transitionAlphabet.size(), len + 1) - 1) / (transitionAlphabet.size() - 1));
		long randNum = (long) (Math.random() * size);
		if (randNum == size - 1) {
			return;
		}
		int index = (int) (randNum % transitionAlphabet.size());
		trace.add(transitionAlphabet.get(index));
		for (OutgoingInternalTransition<IStatement, String> transition : aut.internalSuccessors(state,
				transitionAlphabet.get(index))) {
			generateSingleSample(transition.getSucc(), len - 1, trace);
		}
	}

	private boolean generateSingleSample(String state, long len) {
		if (len == 0)
			return aut.isFinal(state);
		long size = 1;
		size = (long) ((Math.pow(transitionAlphabet.size(), len + 1) - 1) / (transitionAlphabet.size() - 1));
		long randNum = (long) (Math.random() * size);
		if (randNum == size - 1) {
			return aut.isFinal(state);
		}
		int index = (int) (randNum % transitionAlphabet.size());
		for (OutgoingInternalTransition<IStatement, String> transition : aut.internalSuccessors(state,
				transitionAlphabet.get(index))) {
			return generateSingleSample(transition.getSucc(), len - 1);
		}
		return false;
	}

	private Set<List<IStatement>> generateTraceSamples() {
		Set<List<IStatement>> samples = new HashSet<>();
		for (int i = 0; i < sampleSize; i++) {
			for (String initial : aut.getInitialStates()) {
				List<IStatement> trace = new ArrayList<>();
				generateSingleSample(initial, k, trace);
				samples.add(trace);
			}
		}
		return samples;
	}

	public double[] calculate95TraceConfIntervals() throws Exception {
		Set<List<IStatement>> traces = generateTraceSamples();
		if (traces.contains(new ArrayList<>())) {
			traces.remove(new ArrayList<>());
		}
		CounterExamplesToInterpolants counterExampleToInterpolants = new CounterExamplesToInterpolants(traces);
		counterExampleToInterpolants.computeResult();
		int count = counterExampleToInterpolants.getCorrectTraces().size();
		return calculateInterval(count);
	}

	private double[] calculateInterval(int count) {
		double[] interval = new double[2];
		double x_bar = 1.0 * count / sampleSize;
		double s2 = (count * Math.pow(1 - x_bar, 2) + (sampleSize - count) * Math.pow(0 - x_bar, 2)) / (sampleSize - 1);
		interval[0] = x_bar - z975 * Math.sqrt(s2 / sampleSize);
		interval[1] = x_bar + z975 * Math.sqrt(s2 / sampleSize);
		return interval;
	}

}
