package cgsynt.loop;

import java.util.ArrayList;
import java.util.List;

import cgsynt.Verification.MainVerificationLoop;
import cgsynt.interpol.IStatement;
import cgsynt.interpol.ScriptAssignmentStatement;
import cgsynt.interpol.TraceGlobalVariables;
import cgsynt.interpol.VariableFactory;
import cgsynt.tree.buchi.BuchiTreeAutomaton;
import cgsynt.tree.buchi.BuchiTreeAutomatonRule;
import cgsynt.tree.buchi.lta.RankedBool;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.boogie.BoogieNonOldVar;

public class CegarRunner {
	CegarRunner() throws Exception{
		BuchiTreeAutomaton<RankedBool, String> aut = new BuchiTreeAutomaton<>(1);
		aut.addFinalState("s1");
		
		VariableFactory vf = TraceGlobalVariables.getGlobalVariables().getVariableFactory();
		Script script = TraceGlobalVariables.getGlobalVariables().getManagedScript().getScript();
		BoogieNonOldVar x = vf.constructVariable("x", vf.INT);
		
		IStatement xpp = new ScriptAssignmentStatement(x, script.term("+", x.getTerm(), script.numeral("1")));
		List<IStatement> letters = new ArrayList<>();
		letters.add(xpp);
		
		List<String> dest = new ArrayList<>();
		dest.add("s1");
		aut.addRule(new BuchiTreeAutomatonRule<>(RankedBool.TRUE, "s1", dest));
		
		MainVerificationLoop loop = new MainVerificationLoop(aut, letters, new ArrayList<>(), new ArrayList<>());
	}

	public static void main(String args[]) throws Exception {
		new CegarRunner();
	}
}
