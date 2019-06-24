package cgsynt.interpol;

import java.util.HashMap;
import java.util.HashSet;
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
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.variables.ProgramVarUtils;

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

	public static final int INT = 1;
	public static final int BOOL = 2;
	// public static final int INT_ARR = 3;

	public VariableFactory(Script script) {
		symbolTable = new DefaultIcfgSymbolTable();
		variablesMap = new HashMap<>();
		variables = new HashSet<>();
		this.script = script;
		curID = 0;
	}

	/**
	 * 
	 * @param identifier
	 * @return
	 */
	public BoogieNonOldVar getVariable(String identifier) {
		if (variablesMap.containsKey(identifier))
			return variablesMap.get(identifier);
		else
			return null;
	}

	public boolean isVariable(String identifier) {
		return getVariable(identifier) != null;
	}

	private Sort typeToSort(int type) throws Exception {
		switch (type) {
		case INT:
			return script.sort("Int");
		case BOOL:
			return script.sort("Bool");
		default:
			throw new Exception("No such type");
		}
	}

	public BoogieNonOldVar constructVariable(String identifier, int type) throws Exception {
		if (isVariable(identifier)) {
			throw new Exception("Variable already defined.");
		}
		Sort sort = typeToSort(type);
		BoogieNonOldVar var = ProgramVarUtils.constructGlobalProgramVarPair(identifier, sort,
				TraceGlobalVariables.getGlobalVariables().getManagedScript(), null);
		symbolTable.add(var);
		variablesMap.put(identifier, var);
		variables.add(var);
		return var;
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
