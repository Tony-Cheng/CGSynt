package cgsynt.interpol;

import java.util.Map;
import java.util.Set;

import de.uni_freiburg.informatik.ultimate.boogie.type.BoogiePrimitiveType;
import de.uni_freiburg.informatik.ultimate.boogie.type.BoogieType;
import de.uni_freiburg.informatik.ultimate.core.model.models.IBoogieType;
import de.uni_freiburg.informatik.ultimate.logic.ApplicationTerm;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Sort;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.boogie.BoogieNonOldVar;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.boogie.BoogieOldVar;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.DefaultIcfgSymbolTable;

/**
 * A factory for constructing variables used in the trace.
 *
 */
public class VariableFactory {

	private Set<BoogieNonOldVar> variables;
	private Map<String, BoogieNonOldVar> variablesMap;
	private Script script;
	private DefaultIcfgSymbolTable symbolTable;
	private int curID;

	public static final int Int = 1;
	public static final int Bool = 2;

	public VariableFactory(Script script) {
		symbolTable = new DefaultIcfgSymbolTable();
		this.script = script;
		curID = 0;
	}

	/**
	 * 
	 * @param identifier
	 * @return
	 */
	public BoogieNonOldVar getVariable(String identifier) {
		return variablesMap.get(identifier);
	}

	public boolean isVariable(String identifier) {
		return getVariable(identifier) != null;
	}

	private String typeToString(int type) throws Exception {
		switch (type) {
		case Int:
			return "Int";
		case Bool:
			return "Bool";
		default:
			throw new Exception("No such type");
		}
	}

	private BoogiePrimitiveType typeToBoogieType(int type) throws Exception {
		switch (type) {
		case Int:
			return BoogieType.TYPE_INT;
		case Bool:
			return BoogieType.TYPE_BOOL;
		default:
			throw new Exception("No such type");
		}
	}

	public BoogieNonOldVar constructVariable(String identifier, int type) throws Exception {
		if (isVariable(identifier)) {
			throw new Exception("Variable already defined.");
		}
		String identifierPrime = identifier + "'";
		String stringType = typeToString(type);
		IBoogieType boogieType = typeToBoogieType(type);
		script.declareFun(identifier, new Sort[0], script.sort(stringType));
		script.declareFun(identifierPrime, new Sort[0], script.sort(stringType));
		BoogieOldVar var1 = new BoogieOldVar(identifierPrime, boogieType,
				script.variable(identifierPrime, script.sort(stringType)),
				(ApplicationTerm) script.term(identifierPrime), (ApplicationTerm) script.term(identifier));

		BoogieNonOldVar var2 = new BoogieNonOldVar(identifier, boogieType,
				script.variable(identifier, script.sort("Int")), (ApplicationTerm) script.term(identifier),
				(ApplicationTerm) script.term(identifierPrime), var1);
		var1.setNonOldVar(var2);
		symbolTable.add(var2);
		variablesMap.put(identifier, var2);
		variables.add(var2);
		return var2;
	}

	public BoogieNonOldVar constructVariable(int type) throws Exception {
		String var = "var";
		while (isVariable(var + curID)) {
			curID++;
		}
		return constructVariable(var + curID, type);
	}

	public DefaultIcfgSymbolTable getSymbolTable() {
		return symbolTable;
	}

}
