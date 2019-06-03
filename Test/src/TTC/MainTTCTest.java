package TTC;

import java.io.File;
import java.io.IOException;

import de.uni_freiburg.informatik.ultimate.boogie.ast.Unit;
import de.uni_freiburg.informatik.ultimate.boogie.parser.BoogieParser;
import de.uni_freiburg.informatik.ultimate.core.model.models.IElement;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.CfgBuilder;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger.LogLevel;
import de.uni_freiburg.informatik.ultimate.test.mocks.UltimateMocks;

public class MainTTCTest {
	public MainTTCTest() {
		IUltimateServiceProvider mock = UltimateMocks.createUltimateServiceProviderMock(LogLevel.DEBUG);
		BoogieParser bp = new BoogieParser();
		bp.setServices(mock);
		
		Unit unit = (Unit)parseBoogie(bp, "res/code.bpl");
		
		CfgBuilder cfgBuilder = makeCfgBuilder(unit, mock);
	}
	
	private CfgBuilder makeCfgBuilder(Unit unit, IUltimateServiceProvider service) {
		try {
			return new CfgBuilder(unit, service);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private IElement parseBoogie(BoogieParser parser, String path) {
		File file = new File(path);
		File[] files = new File[]{file};
		
		IElement ast = null;
		
		try {
			ast = parser.parseAST(files);
		} catch(IOException ioe) {
			ioe.printStackTrace();
			System.exit(1);
		}
		
		return ast;
	}
	
	public static void main(String[] args) {
		new MainTTCTest();
	}
}
