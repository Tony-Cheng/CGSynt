package cgsynt.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cgsynt.interpol.IStatement;
import cgsynt.interpol.ScriptAssignmentStatement;
import cgsynt.interpol.ScriptAssumptionStatement;
import cgsynt.interpol.ScriptPredicateAssumptionStatement;
import cgsynt.interpol.TraceGlobalVariables;
import cgsynt.interpol.VariableFactory;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Term;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.boogie.BoogieNonOldVar;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;

public class InputFileParser {
	private String[] mVariableTypes = { "int", "bool", "array" };
	private String[] mComparators = { ">", ">=", "<", "<=", "==", "!=" };

	private TraceGlobalVariables mGlobalVars;
	private VariableFactory mVf;
	private Script mScript;
	private Map<String, BoogieNonOldVar> mVariableMap;
	private int mLinePointer;
	private List<IStatement> statements;
	private IPredicate precondition;
	private IPredicate postcondition;

	private void init() throws Exception {
		mGlobalVars = new TraceGlobalVariables();
		mVf = mGlobalVars.getVariableFactory();
		mScript = mGlobalVars.getManagedScript().getScript();

		mVariableMap = new HashMap<>();
		this.statements = new ArrayList<>();
		mLinePointer = 1;
	}

	public Specification parseFile(ArrayList<String> lines) throws Exception {
		init();

		parseVariableDeclarations(lines);
		parseStatements(lines);
		parsePrecondition(lines);
		parsePostcondition(lines);

		// NOTE FOR TONY: linePointer points to the current line in the lines arraylist,
		// so just update the
		// to move down the file. Also I was throwing new ParseErrors for errors so that
		// can be used
		// for throwing new errors.
		// Create Specification file using the parsed data.
		Specification spec = new Specification(statements, precondition, postcondition, this.mGlobalVars);

		return spec;
	}

	private void parseVariableDeclarations(ArrayList<String> lines) throws Exception {
		while (!lines.get(mLinePointer).trim().equals("statements")) {
			String[] tokens = lines.get(mLinePointer).split(" ");

			if (tokens.length < 2)
				throw new ParseException("Line" + mLinePointer + " must declare both variable type and variable name");
			else if (tokens.length > 2)
				throw new ParseException("Line" + mLinePointer + " must only declare variable type and variable name");

			int type = stringToVariableInt(tokens[0]);
			mVariableMap.put(tokens[1], mVf.constructVariable(tokens[1], type));

			mLinePointer++;
		}
	}

	private void parseStatements(ArrayList<String> lines) throws ParseException {
		if (!lines.get(mLinePointer).equals("statements"))
			throw new ParseException("The file must have a \"Statements\" section label");

		mLinePointer++;

		while (!lines.get(mLinePointer).trim().equals("pre")) {
			String[] tokens = lines.get(mLinePointer).split(" ");
			if (tokens[1].equals("=")) {
				String leftVar = tokens[0];
				String rightVar1 = tokens[2];
				String operator = tokens[3];
				String rightVar2 = tokens[4];
				BoogieNonOldVar left = this.mVariableMap.get(leftVar);
				Term right1 = null;
				Term right2 = null;
				if (this.mVariableMap.get(rightVar1) == null)
					right1 = this.mGlobalVars.getManagedScript().getScript().numeral(rightVar1);
				else
					right1 = this.mVariableMap.get(rightVar1).getTerm();
				if (this.mVariableMap.get(rightVar2) == null)
					right2 = this.mGlobalVars.getManagedScript().getScript().numeral(rightVar2);
				else
					right2 = this.mVariableMap.get(rightVar2).getTerm();
				Term right = this.mGlobalVars.getManagedScript().getScript().term(operator, right1, right2);
				IStatement statement = new ScriptAssignmentStatement(left, right, mGlobalVars.getManagedScript(),
						this.mGlobalVars.getVariableFactory().getSymbolTable());
				this.statements.add(statement);

			} else {
				String leftVar = tokens[0];
				String operator = tokens[1];
				String rightVar = tokens[2];
				Term left = mVariableMap.get(leftVar).getTerm();
				Term right = mVariableMap.get(rightVar).getTerm();
				IStatement statement = new ScriptPredicateAssumptionStatement(
						this.mGlobalVars.getPredicateFactory().newPredicate(mScript.term(operator, left, right)),
						this.mGlobalVars.getManagedScript(), this.mGlobalVars.getPredicateFactory());
				statements.add(statement);
			}

			mLinePointer++;
		}

	}

	private void parsePrecondition(ArrayList<String> lines) throws ParseException {
		if (!lines.get(mLinePointer).equals("pre"))
			throw new ParseException("ERROR: PRE");

		mLinePointer++;

		String[] tokens = lines.get(mLinePointer).split(" ");
		String leftVar = tokens[0];
		String operator = tokens[1];
		String rightVar = tokens[2];
		Term left = mVariableMap.get(leftVar).getTerm();
		Term right = null;
		if (mVariableMap.get(rightVar) == null)
			right = mScript.numeral(rightVar);
		else
			right = mVariableMap.get(rightVar).getTerm();
		precondition = this.mGlobalVars.getPredicateFactory().newPredicate(mScript.term(operator, left, right));
		mLinePointer++;
		while (!lines.get(mLinePointer).trim().equals("post")) {
			tokens = lines.get(mLinePointer).split(" ");

			leftVar = tokens[1];
			operator = tokens[2];
			rightVar = tokens[3];
			left = mVariableMap.get(leftVar).getTerm();

			IPredicate nextCondition = this.mGlobalVars.getPredicateFactory()
					.newPredicate(mScript.term(operator, left, right));
			if (tokens[0].equals("and"))
				precondition = this.mGlobalVars.getPredicateFactory().and(precondition, nextCondition);
			else if (tokens[0].equals("or"))
				precondition = this.mGlobalVars.getPredicateFactory().or(false, precondition, nextCondition);
			mLinePointer++;
		}
	}

	private void parsePostcondition(ArrayList<String> lines) throws ParseException {
		if (!lines.get(mLinePointer).equals("post"))
			throw new ParseException("ERROR: Post");

		mLinePointer++;

		String[] tokens = lines.get(mLinePointer).split(" ");
		String leftVar = tokens[0];
		String operator = tokens[1];
		String rightVar = tokens[2];
		Term left = mVariableMap.get(leftVar).getTerm();
		Term right = null;
		if (mVariableMap.get(rightVar) == null)
			right = mScript.numeral(rightVar);
		else
			right = mVariableMap.get(rightVar).getTerm();
		postcondition = this.mGlobalVars.getPredicateFactory().newPredicate(mScript.term(operator, left, right));
		mLinePointer++;
		while (!lines.get(mLinePointer).trim().equals("compute")) {
			tokens = lines.get(mLinePointer).split(" ");

			leftVar = tokens[1];
			operator = tokens[2];
			rightVar = tokens[3];
			left = mVariableMap.get(leftVar).getTerm();
			if (mVariableMap.get(rightVar) == null)
				right = mScript.numeral(rightVar);
			else
				right = mVariableMap.get(rightVar).getTerm();

			IPredicate nextCondition = this.mGlobalVars.getPredicateFactory()
					.newPredicate(mScript.term(operator, left, right));
			if (tokens[0].equals("and"))
				postcondition = this.mGlobalVars.getPredicateFactory().and(precondition, nextCondition);
			else if (tokens[0].equals("or"))
				postcondition = this.mGlobalVars.getPredicateFactory().or(false, precondition, nextCondition);
			mLinePointer++;
		}
	}

	private boolean isVariableDeclarationLine(String line) {
		for (int i = 0; i < mVariableTypes.length; i++) {
			if (line.startsWith(mVariableTypes[i]))
				return true;
		}

		return false;
	}

	private boolean isAssumption(String[] tokens) throws ParseException {
		boolean found = false;
		int tokenNum = -1;
		for (int j = 0; j < tokens.length; j++) {
			for (int i = 0; i < mComparators.length; i++) {
				if (tokens[j].equals(mComparators[i])) {
					found = true;
					tokenNum = j;
					break;
				}
			}

			if (found)
				break;
		}

		if (tokenNum != 1 && tokenNum != -1)
			throw new ParseException("The synthesizer only supports monomial terms on the left hand side");

		if (tokenNum == -1)
			return false;
		else
			return true;
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

class ParseException extends Exception {
	private static final long serialVersionUID = 6420159566994853936L;

	public ParseException(String message) {
		System.err.println("PARSE ERROR: " + message);
		System.exit(1);
	}
}

//// i++; i++
// TraceGlobalVariables globalVars = new TraceGlobalVariables();
// VariableFactory vf = globalVars.getVariableFactory();
// Script script = globalVars.getManagedScript().getScript();
// BoogieNonOldVar i = vf.constructVariable("i", VariableFactory.INT);
//
// BasicPredicateFactory predicateFactory = globalVars.getPredicateFactory();
//
// IStatement ipp = new ScriptAssignmentStatement(i, script.term("+",
//// i.getTerm(), script.numeral("1")),
// globalVars.getManagedScript(), vf.getSymbolTable());
// IStatement imm = new ScriptAssignmentStatement(i, script.term("-",
//// i.getTerm(), script.numeral("1")),
// globalVars.getManagedScript(), vf.getSymbolTable());
//
// IPredicate preconditions = predicateFactory.newPredicate(script.term("=",
//// i.getTerm(), script.numeral("0")));
// IPredicate postconditions = predicateFactory.newPredicate(script.term("=",
//// i.getTerm(), script.numeral("2")));
// List<IStatement> transitionAlphabet = new ArrayList<>();
// transitionAlphabet.add(ipp);
// transitionAlphabet.add(imm);
// SynthesisLoop synthesis = new SynthesisLoop(transitionAlphabet,
//// preconditions, postconditions, globalVars);
// synthesis.computeMainLoop();
// System.out.println("Test 1");
// System.out.println(synthesis.isCorrect());
// synthesis.printProgram();