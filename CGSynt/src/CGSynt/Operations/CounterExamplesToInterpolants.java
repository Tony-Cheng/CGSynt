package CGSynt.Operations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cgsynt.interpol.IStatement;
import cgsynt.interpol.TraceToInterpolants;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;

public class CounterExamplesToInterpolants {

	private List<List<IStatement>> counterExamples;
	private boolean resultComputed;
	private List<Set<IPredicate>> interpolants;
	private List<Set<IStatement>> correctTraces;
	private List<Set<IStatement>> incorrectTraces;

	public CounterExamplesToInterpolants(List<List<IStatement>> counterExamples) {
		this.counterExamples = counterExamples;
		this.resultComputed = false;
		interpolants = new ArrayList<>();
		correctTraces = new ArrayList<>();
		incorrectTraces = new ArrayList<>();
	}

	public boolean getResultComputed() {
		return resultComputed;
	}

	public void computeResult() {
		for (List<IStatement> statements : counterExamples) {
			IPredicate[] trace_interpolants = TraceToInterpolants.getTraceToInterpolants()
					.computeInterpolants(statements);
			if (trace_interpolants != null) {
				interpolants.add(new HashSet<>(Arrays.asList(trace_interpolants)));
				correctTraces.add(new HashSet<>(statements));
			} else {
				incorrectTraces.add(new HashSet<>(statements));
			}
		}
		resultComputed = true;
	}

	public List<Set<IPredicate>> getInterpolants() {
		if (resultComputed)
			return interpolants;
		return null;
	}

	public List<Set<IStatement>> getTraces() {
		if (resultComputed)
			return correctTraces;
		return null;
	}

	public List<Set<IStatement>> getIncorrectTrace() {
		if (resultComputed)
			return incorrectTraces;
		return null;
	}

}
