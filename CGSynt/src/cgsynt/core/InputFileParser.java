package cgsynt.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cgsynt.interpol.TraceGlobalVariables;
import cgsynt.interpol.VariableFactory;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.boogie.BoogieNonOldVar;

public class InputFileParser {	
	private String[] mVariableTypes = {"int", "bool", "array"};
	
	public Specification parseFile(ArrayList<String> lines) throws Exception {
		TraceGlobalVariables globalVars = new TraceGlobalVariables();
		VariableFactory vf = globalVars.getVariableFactory();
		Script script = globalVars.getManagedScript().getScript();
		
		ArrayList<BoogieNonOldVar> variables = new ArrayList<BoogieNonOldVar>();
		Map<String, Integer> mVariableMap = new HashMap<>();
		
		int linePointer = 1;
		
		// Check that the file starts with Vars marker
		if (!lines.get(0).equals("Vars"))
			throw new ParseException("The specification file must begin with \"Vars\""); 
		
		while (variableDeclarationLine(lines.get(linePointer))) {
			String[] tokens = lines.get(linePointer).split(" ");
			if (tokens.length < 2)
				throw new ParseException("Line" + linePointer + " must declare both variable type and variable name"); 
			else if (tokens.length > 2)
				throw new ParseException("Line" + linePointer + " must only declare variable type and variable name"); 
			
			int type = stringToVariableInt(tokens[0]);
			variables.add(vf.constructVariable(tokens[1], type));
			mVariableMap.put(tokens[1], linePointer - 1);
			
			linePointer++;
		}
		
		// NOTE FOR TONY: linePointer points to the current line in the lines arraylist, so juzt update the 
		//				  to move down the file. Also I was throwing new ParseErrors for errors so that can be used
		//				  for throwing new errors.
		// Create Specification file using the parsed data.
		Specification spec = new Specification(null, null, null, null);
		
		return spec;
	}
	
	private boolean variableDeclarationLine(String line) {
		for (int i = 0; i < mVariableTypes.length; i++) {
			if (line.startsWith(mVariableTypes[i]))
				return true;
		}
		
		return false;
	}
	
	private int stringToVariableInt(String type) {
		if (type.equals("int"))
			return VariableFactory.INT;
		else if (type.equals("bool"))
			return VariableFactory.BOOL;
		else if (type.equals("array"))
			return VariableFactory.INT_ARR;
		return -1;
	}
}

class ParseException extends Exception{
	private static final long serialVersionUID = 6420159566994853936L;

	public ParseException(String message) {
		System.err.println("PARSE ERROR: " + message);
		System.exit(1);
	}
}

//// i++; i++
//TraceGlobalVariables globalVars = new TraceGlobalVariables();
//VariableFactory vf = globalVars.getVariableFactory();
//Script script = globalVars.getManagedScript().getScript();
//BoogieNonOldVar i = vf.constructVariable("i", VariableFactory.INT);
//
//BasicPredicateFactory predicateFactory = globalVars.getPredicateFactory();
//
//IStatement ipp = new ScriptAssignmentStatement(i, script.term("+", i.getTerm(), script.numeral("1")),
//		globalVars.getManagedScript(), vf.getSymbolTable());
//IStatement imm = new ScriptAssignmentStatement(i, script.term("-", i.getTerm(), script.numeral("1")),
//		globalVars.getManagedScript(), vf.getSymbolTable());
//
//IPredicate preconditions = predicateFactory.newPredicate(script.term("=", i.getTerm(), script.numeral("0")));
//IPredicate postconditions = predicateFactory.newPredicate(script.term("=", i.getTerm(), script.numeral("2")));
//List<IStatement> transitionAlphabet = new ArrayList<>();
//transitionAlphabet.add(ipp);
//transitionAlphabet.add(imm);
//SynthesisLoop synthesis = new SynthesisLoop(transitionAlphabet, preconditions, postconditions, globalVars);
//synthesis.computeMainLoop();
//System.out.println("Test 1");
//System.out.println(synthesis.isCorrect());
//synthesis.printProgram();