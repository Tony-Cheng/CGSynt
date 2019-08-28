package cgsynt.operations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

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
	private int maxBs;
	private int totalBs;

	public CounterExampleGenerationStatisticalApproach(int k, int bs, int maxBs, double pValue,
			TraceToInterpolants interpolator, List<IStatement> alphabet) {
		this.k = k;
		this.bs = bs;
		this.pValue = pValue;
		this.alphabet = alphabet;
		this.interpolants = new HashSet<>();
		this.numTraceSamples = new HashMap<>();
		this.numInfeasibleTraceSamples = new HashMap<>();
		this.numPiSamples = new HashMap<>();
		this.numInfeasiblePiSamples = new HashMap<>();
		this.interpolator = interpolator;
		this.maxBs = maxBs;
	}

	private void computeTraceStatistics(long root, long size, int num) throws Exception {
		for (int i = 0; i < num; i++) {
			long path = root * size + (long) (Math.random() * size);
			List<IStatement> trace = new ArrayList<>();
			Stack<Integer> pathStack = computePath(path);
			trace.addAll(findTrace(pathStack));
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

	private List<IStatement> findTrace(Stack<Integer> pathStack) {
		List<IStatement> trace = new ArrayList<>();
		if (pathStack.isEmpty()) {
			return trace;
		}
		int next = pathStack.pop();
		trace.add(alphabet.get(next));
		trace.addAll(findTrace(pathStack));
		return trace;

	}

	private void computePiStatistics(long root, long size, int num) {
		for (int i = 0; i < num; i++) {
			long path = root * size + (long) (Math.random() * size);
			Stack<Integer> pathStack = computePath(path);
			boolean isFinal = isFinalState(this.dfa.getInitialStates().iterator().next(), pathStack);
			if (!this.numPiSamples.containsKey(path)) {
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

	private boolean isFinalState(STATE state, Stack<Integer> pathStack) {
		if (pathStack.isEmpty()) {
			return dfa.isFinal(state);
		}
		int next = pathStack.pop();
		for (OutgoingInternalTransition<IStatement, STATE> transition : dfa.internalSuccessors(state,
				alphabet.get(next))) {
			return isFinalState(transition.getSucc(), pathStack);
		}
		return false;
	}

	public Set<IPredicate> computeInterpolants(INestedWordAutomaton<IStatement, STATE> dfa) throws Exception {
		this.dfa = dfa;
		this.totalBs = 0;
		computeNode(0, (long) Math.round((Math.pow(alphabet.size(), k + 1)) - 1));
		return this.interpolants;
	}

	private void computeNode(long currentPath, long size) throws Exception {
		if (size == 0 || totalBs > maxBs) {
			return;
		}
		computeTraceStatistics(currentPath, size, bs);
		computePiStatistics(currentPath, size, bs);
		totalBs += bs;
		for (int i = 0; i < alphabet.size(); i++) {
			long next = alphabet.size() * currentPath + 1 + i;
			if (computeMeanDifference(next) >= 0.05) {
				computeNode(next, size / alphabet.size());
			}
		}
	}

	private double computeMeanDifference(long path) {
		if (!numTraceSamples.containsKey(path) || !numPiSamples.containsKey(path)) {
			return 1;
		}
		double meanTraceSample = 1.0 * numInfeasibleTraceSamples.get(path) / numTraceSamples.get(path);
		double meanPiSample = 1.0 * numInfeasiblePiSamples.get(path) / numPiSamples.get(path);
		// System.out.println(numInfeasibleTraceSamples.get(path) + " " + numInfeasiblePiSamples.get(path));
		return meanTraceSample - meanPiSample;
	}

	private Stack<Integer> computePath(long path) {
		Stack<Integer> pathStack = new Stack<>();
		while (path != 0) {
			path -= 1;
			pathStack.push((int) (path % alphabet.size()));
			path /= alphabet.size();
		}
		return pathStack;
	}
}
