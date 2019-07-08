package cgsynt.probability;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cgsynt.Operations.CounterExamplesToInterpolants;
import cgsynt.interpol.IStatement;
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

	private void generateSingleSample(long len, List<IStatement> trace) {
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
		generateSingleSample(len - 1, trace);
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

	private List<List<IStatement>> generateTraceSamples() {
		List<List<IStatement>> samples = new ArrayList<>();
		for (int i = 0; i < sampleSize; i++) {
			List<IStatement> trace = new ArrayList<>();
			generateSingleSample(k, trace);
			samples.add(trace);
		}
		return samples;
	}

	private int generatePiSample() {
		int count = 0;
		for (int i = 0; i < sampleSize; i++) {
			for (String initial : aut.getInitialStates()) {
				if (generateSingleSample(initial, k))
					count++;
			}
		}
		return count;
	}

	public double[] calculate95PiConfIntervals() throws Exception {
		int samples = generatePiSample();
		return calculateInterval(samples);
	}

	public double[] calculate95TraceConfIntervals() throws Exception {
		List<List<IStatement>> traces = generateTraceSamples();
		for (int i = 0; i < traces.size(); i++) {
			if (traces.get(i).equals(new ArrayList<>())) {
				traces.remove(i);
				i--;
			}
		}
		int count = 0;
		for (int i = 0; i < traces.size(); i++) {
			Set<List<IStatement>> singleTraceSet = new HashSet<>();
			singleTraceSet.add(traces.get(i));
			CounterExamplesToInterpolants counterExampleToInterpolants = new CounterExamplesToInterpolants(
					singleTraceSet);
			counterExampleToInterpolants.computeResult();
			count += counterExampleToInterpolants.getCorrectTraces().size();
		}
		return calculateInterval(count);
	}

	private double[] calculateInterval(int count) {
		double[] interval = new double[2];
		double x_bar = 1.0 * count / sampleSize;
		// double var = (count * Math.pow(1 - x_bar, 2) + (sampleSize - count) *
		// Math.pow(0 - x_bar, 2)) / (sampleSize - 1);
		double var = x_bar * (1 - x_bar);
		interval[0] = x_bar - z975 * Math.sqrt(var / sampleSize);
		interval[1] = x_bar + z975 * Math.sqrt(var / sampleSize);
		return interval;
	}

}
