package test.interpolants;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import cgsynt.interpol.IStatement;
import cgsynt.interpol.ScriptAssignmentStatement;
import cgsynt.interpol.ScriptAssumptionStatement;
import cgsynt.interpol.TraceGlobalVariables;
import cgsynt.interpol.TraceToInterpolants;
import cgsynt.interpol.VariableFactory;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.boogie.BoogieNonOldVar;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;

public class TestTraceToInterpolants {

	@Test
	void Test1() throws Exception {
		Script script = TraceGlobalVariables.getGlobalVariables().getManagedScript().getScript();
		VariableFactory varFactory = TraceGlobalVariables.getGlobalVariables().getVariableFactory();
		BoogieNonOldVar x = varFactory.constructVariable("x", VariableFactory.INT);
		BoogieNonOldVar y = varFactory.constructVariable("y", VariableFactory.INT);
		IStatement statement1 = new ScriptAssignmentStatement(x, script.numeral("0"));
		IStatement statement2 = new ScriptAssignmentStatement(y, script.numeral("0"));
		IStatement statement3 = new ScriptAssignmentStatement(x, script.term("+", x.getTerm(), script.numeral("1")));
		IStatement statement4 = new ScriptAssumptionStatement(x, script.numeral("0"), "=");
		List<IStatement> statements = new ArrayList<>();
		statements.add(statement1);
		statements.add(statement2);
		statements.add(statement3);
		statements.add(statement4);
		IPredicate[] interpolants = TraceToInterpolants.getTraceToInterpolants().computeInterpolants(statements);
		for (IPredicate predicate : interpolants) {
			System.out.println(predicate);
		}
	}
}
