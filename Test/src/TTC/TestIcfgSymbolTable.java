package TTC;

import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.DefaultIcfgSymbolTable;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.IIcfgSymbolTable;

public class TestIcfgSymbolTable {
	public TestIcfgSymbolTable() {
		IIcfgSymbolTable symbolTable = new DefaultIcfgSymbolTable(); 
	}
	
	public static void main(String args[]) {
		new TestIcfgSymbolTable();
	}
}
