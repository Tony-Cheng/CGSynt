package cgsynt.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cgsynt.interpol.IStatement;
import cgsynt.interpol.ScriptAssignmentStatement;
import cgsynt.interpol.TraceGlobalVariables;
import cgsynt.interpol.VariableFactory;
import cgsynt.synthesis.SynthesisLoop;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.boogie.BoogieNonOldVar;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.BasicPredicateFactory;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;

public class Core {
	public Core(String[] args) throws Exception {
		Map<String, List<String>> argsMap = parseArgs(args);

		
//		// i++; i++
//		TraceGlobalVariables globalVars = new TraceGlobalVariables();
//		VariableFactory vf = globalVars.getVariableFactory();
//		Script script = globalVars.getManagedScript().getScript();
//		BoogieNonOldVar i = vf.constructVariable("i", VariableFactory.INT);
//
//		BasicPredicateFactory predicateFactory = globalVars.getPredicateFactory();
//
//		IStatement ipp = new ScriptAssignmentStatement(i, script.term("+", i.getTerm(), script.numeral("1")),
//				globalVars.getManagedScript(), vf.getSymbolTable());
//		IStatement imm = new ScriptAssignmentStatement(i, script.term("-", i.getTerm(), script.numeral("1")),
//				globalVars.getManagedScript(), vf.getSymbolTable());
//
//		IPredicate preconditions = predicateFactory.newPredicate(script.term("=", i.getTerm(), script.numeral("0")));
//		IPredicate postconditions = predicateFactory.newPredicate(script.term("=", i.getTerm(), script.numeral("2")));
//		List<IStatement> transitionAlphabet = new ArrayList<>();
//		transitionAlphabet.add(ipp);
//		transitionAlphabet.add(imm);
//		SynthesisLoop synthesis = new SynthesisLoop(transitionAlphabet, preconditions, postconditions, globalVars);
//		synthesis.computeMainLoop();
//		System.out.println("Test 1");
//		System.out.println(synthesis.isCorrect());
//		synthesis.printProgram();
	}
	
	public Map<String, List<String>> parseArgs(String args[]){
		Map<String, List<String>> argsMap = new HashMap<>();
		
		String flag = null;
		ArrayList<String> params = new ArrayList<>();
		
		for (int i = 0; i < args.length; i++) {
			String token = args[i];
			if (token.startsWith("-"))
				flag = token;
			else
				params.add(token);
			
			if (i + 1 >= args.length || args[i + 1].startsWith("-")) {
				argsMap.put(flag, params);
				params = new ArrayList<>();
			}
		}
		
		return argsMap;
	}
	
	public static void main(String[] args) throws Exception {
		new Core(args);
	}
}
