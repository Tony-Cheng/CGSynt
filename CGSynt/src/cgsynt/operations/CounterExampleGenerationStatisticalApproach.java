package cgsynt.operations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cgsynt.interpol.IStatement;
import cgsynt.interpol.TraceToInterpolants;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.INestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.transitions.OutgoingInternalTransition;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;

public class CounterExampleGenerationStatisticalApproach<STATE> {

	private INestedWordAutomaton<IStatement, STATE> dfa;
	private int k;
	private int bs;
	private double pValue;
	private Map<Long, Long> numTraceSamples;
	private Map<Long, Long> numInfeasibleTraceSamples;
	private Map<Long, Long> numPiSamples;
	private Map<Long, Long> numInfeasiblePiSamples;
	private TraceToInterpolants interpolator;
	private List<IStatement> alphabet;
	private Set<IPredicate> interpolants;

	public CounterExampleGenerationStatisticalApproach(INestedWordAutomaton<IStatement, STATE> dfa, int k, int bs,
			double pValue, TraceToInterpolants interpolator, List<IStatement> alphabet) {
		this.dfa = dfa;
		this.k = k;
		this.bs = bs;
		this.pValue = pValue;
		this.alphabet = alphabet;
		this.numTraceSamples = new HashMap<>();
		this.numInfeasibleTraceSamples = new HashMap<>();
		this.numPiSamples = new HashMap<>();
		this.numInfeasiblePiSamples = new HashMap<>();
		this.interpolator = interpolator;
	}

	private void computeTraceStatistics(long root, long size, int num) throws Exception {
		for (int i = 0; i < num; i++) {
			long path = root * size + (long) (Math.random() * size) + 1;
			List<IStatement> trace = new ArrayList<>();
			trace.addAll(findTrace(path));
			IPredicate[] interpolants = interpolator.computeInterpolants(trace);
			if (!this.numTraceSamples.containsKey(path)) {
				this.numTraceSamples.put(path, (long) 0);
				this.numInfeasibleTraceSamples.put(path, (long) 0);
			}
			if (interpolants == null) {
				this.numTraceSamples.put(path, numTraceSamples.get(path) + 1);
			} else {
				this.interpolants.addAll(Arrays.asList(interpolants));
				this.numTraceSamples.put(path, numTraceSamples.get(path) + 1);
				this.numInfeasibleTraceSamples.put(path, numInfeasibleTraceSamples.get(path) + 1);
			}
		}
	}

	private List<IStatement> findTrace(long path) {
		List<IStatement> trace = new ArrayList<>();
		if (path == 0) {
			return trace;
		}
		path -= 1;
		trace.add(alphabet.get((int) (path % alphabet.size())));
		trace.addAll(findTrace(path / alphabet.size()));
		return trace;

	}

	private void computePiStatistics(long root, long size, int num) {
		for (int i = 0; i < num; i++) {
			long path = root * size + (long) (Math.random() * size) + 1;
			boolean isFinal = isFinalState(this.dfa.getInitialStates().iterator().next(), path);
			if (this.numPiSamples.containsKey(path)) {
				this.numPiSamples.put(path, (long) 0);
				this.numInfeasiblePiSamples.put(path, (long) 0);
			}
			if (isFinal) {
				this.numPiSamples.put(path, this.numPiSamples.get(path) + 1);
				this.numInfeasiblePiSamples.put(path, this.numInfeasiblePiSamples.get(path) + 1);
			} else {
				this.numPiSamples.put(path, this.numPiSamples.get(path) + 1);
			}
		}
	}

	private boolean isFinalState(STATE state, long path) {
		if (path == 0) {
			return dfa.isFinal(state);
		}
		path -= 1;
		for (OutgoingInternalTransition<IStatement, STATE> transition : dfa.internalSuccessors(state,
				alphabet.get((int) (path % alphabet.size())))) {
			return isFinalState(transition.getSucc(), path / alphabet.size());
		}
		return false;
	}

	public Set<IPredicate> computeInterpolants(INestedWordAutomaton<IStatement, STATE> dfa) throws Exception {
		this.dfa = dfa;
		computeNode(0, (long) Math.round((Math.pow(alphabet.size(), k)) - 1));
		return this.interpolants;
	}

	private void computeNode(long currentPath, long size) throws Exception {
		if (size == 0) {
			return;
		}
		computeTraceStatistics(currentPath, size, bs);
		computePiStatistics(currentPath, size, bs);
		for (int i = 0; i < alphabet.size(); i++) {
			long next = alphabet.size() * currentPath + (i + 1);
			if (computeMeanDifference(next) >= 0.05) {
				computeNode(next, size / alphabet.size());
			}
		}
	}

	private double computeMeanDifference(long path) {
		if (!numTraceSamples.containsKey(path) || !numPiSamples.containsKey(path)) {
			return 1;
		}
		double meanTraceSample = numInfeasibleTraceSamples.get(path) / numTraceSamples.get(path);
		double meanPiSample = numInfeasiblePiSamples.get(path) / numPiSamples.get(path);
		return Math.abs(meanTraceSample - meanPiSample);
	}

}
