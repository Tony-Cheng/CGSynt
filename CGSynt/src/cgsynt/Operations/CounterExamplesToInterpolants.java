package cgsynt.Operations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cgsynt.interpol.IStatement;
import cgsynt.interpol.TraceToInterpolants;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.transitions.ITransitionlet;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.transitions.IncomingInternalTransition;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.transitions.OutgoingInternalTransition;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;

public class CounterExamplesToInterpolants {

	private Set<List<IStatement>> counterExamples;
	private boolean resultComputed;
	private List<Set<IPredicate>> interpolants;
	private List<Set<IStatement>> correctTraces;
	private List<Set<IStatement>> incorrectTraces;
	private List<IPredicate[]> nonSetInterpolants;

	public CounterExamplesToInterpolants(Set<List<IStatement>> counterExamples) {
		this.counterExamples = counterExamples;
		this.resultComputed = false;
		interpolants = new ArrayList<>();
		correctTraces = new ArrayList<>();
		incorrectTraces = new ArrayList<>();
		this.nonSetInterpolants = new ArrayList<>();
	}

	public boolean getResultComputed() {
		return resultComputed;
	}

	public void computeResult() {
		for (List<IStatement> statements : counterExamples) {
			IPredicate[] trace_interpolants = TraceToInterpolants.getTraceToInterpolants()
					.computeInterpolants(statements);
			nonSetInterpolants.add(trace_interpolants);
			if (trace_interpolants != null) {
				interpolants.add(new HashSet<>(Arrays.asList(trace_interpolants)));
				correctTraces.add(new HashSet<>(statements));
			} else {
				incorrectTraces.add(new HashSet<>(statements));
			}
		}
		resultComputed = true;
	}

	public List<IPredicate[]> getNonSetInterpolants() {
		return nonSetInterpolants;
	}

	public List<Set<IPredicate>> getInterpolants() {
		if (resultComputed)
			return interpolants;
		return null;
	}

	public List<Set<IStatement>> getCorrectTraces() {
		if (resultComputed)
			return correctTraces;
		return null;
	}

	public List<Set<IStatement>> getIncorrectTrace() {
		if (resultComputed)
			return incorrectTraces;
		return null;
	}

	public void setPreAndPostStatesFinal(NestedWordAutomaton<IStatement, IPredicate> pi,
			List<IPredicate[]> nonSetInterpolants, Set<IPredicate> preconditionInterpolants,
			Set<IPredicate> postconditionInterpolants) {
		int preSize = TraceToInterpolants.getTraceToInterpolants().getPreconditionsSize();
		int negPostSize = TraceToInterpolants.getTraceToInterpolants().getNegatedPostconditionsSize();
		for (IPredicate[] interpolants : nonSetInterpolants) {
			for (int i = 0; i < preSize; i++) {
				if (!preconditionInterpolants.contains(interpolants[i])) {
					Set<IncomingInternalTransition<IStatement, IPredicate>> removedIncomingTransitions = new HashSet<>();
					Set<OutgoingInternalTransition<IStatement, IPredicate>> removedOutgoingTransitions = new HashSet<>();
					if (pi.contains(interpolants[i])) {
						addAllRemovedTransitions(pi, removedIncomingTransitions, removedOutgoingTransitions,
								interpolants[i]);
						pi.removeState(interpolants[i]);
					}
					if (postconditionInterpolants.contains(interpolants[i])) {
						pi.addState(true, true, interpolants[i]);
					} else {
						pi.addState(true, false, interpolants[i]);
					}
					addRemovedTransitionsToPi(pi, removedIncomingTransitions, removedOutgoingTransitions,
							interpolants[i]);
					preconditionInterpolants.add(interpolants[i]);
				}
			}
			for (int i = interpolants.length - 1; i >= interpolants.length - negPostSize; i--) {
				if (!postconditionInterpolants.contains(interpolants[i])) {
					Set<IncomingInternalTransition<IStatement, IPredicate>> removedIncomingTransitions = new HashSet<>();
					Set<OutgoingInternalTransition<IStatement, IPredicate>> removedOutgoingTransitions = new HashSet<>();
					if (pi.contains(interpolants[i])) {
						addAllRemovedTransitions(pi, removedIncomingTransitions, removedOutgoingTransitions,
								interpolants[i]);
						pi.removeState(interpolants[i]);
					}
					if (preconditionInterpolants.contains(interpolants[i])) {
						pi.addState(true, true, interpolants[i]);
					} else {
						pi.addState(false, true, interpolants[i]);
					}
					addRemovedTransitionsToPi(pi, removedIncomingTransitions, removedOutgoingTransitions,
							interpolants[i]);
					postconditionInterpolants.add(interpolants[i]);
				}
			}
		}
	}

	private void addRemovedTransitionsToPi(NestedWordAutomaton<IStatement, IPredicate> pi,
			Set<IncomingInternalTransition<IStatement, IPredicate>> removedIncomingTransitions,
			Set<OutgoingInternalTransition<IStatement, IPredicate>> removedOutgoingTransitions, IPredicate state) {
		for (IncomingInternalTransition<IStatement, IPredicate> transition : removedIncomingTransitions) {
			pi.addInternalTransition(transition.getPred(), transition.getLetter(), state);
		}
		for (OutgoingInternalTransition<IStatement, IPredicate> transition : removedOutgoingTransitions) {
			pi.addInternalTransition(state, transition.getLetter(), transition.getSucc());
		}

	}

	private void addAllRemovedTransitions(NestedWordAutomaton<IStatement, IPredicate> pi,
			Set<IncomingInternalTransition<IStatement, IPredicate>> removedIncomingTransitions,
			Set<OutgoingInternalTransition<IStatement, IPredicate>> removedOutgoingTransitions, IPredicate state) {
		for (IncomingInternalTransition<IStatement, IPredicate> transition : pi.internalPredecessors(state)) {
			removedIncomingTransitions.add(transition);
		}
		for (OutgoingInternalTransition<IStatement, IPredicate> transition : pi.internalSuccessors(state)) {
			removedOutgoingTransitions.add(transition);
		}
	}
}
