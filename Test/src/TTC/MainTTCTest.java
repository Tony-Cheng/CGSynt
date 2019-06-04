package TTC;

import java.io.File;
import java.io.IOException;

import de.uni_freiburg.informatik.ultimate.boogie.ast.Unit;
import de.uni_freiburg.informatik.ultimate.boogie.parser.BoogieParser;
import de.uni_freiburg.informatik.ultimate.core.coreplugin.services.ToolchainStorage;
import de.uni_freiburg.informatik.ultimate.core.model.models.IElement;
import de.uni_freiburg.informatik.ultimate.core.model.preferences.IPreferenceProvider;
import de.uni_freiburg.informatik.ultimate.core.model.services.IBacktranslationService;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.BoogieIcfgLocation;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.CfgBuilder;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IIcfg;

public class MainTTCTest {
	public MainTTCTest() {
		IUltimateServiceProvider generator = new ToolchainStorage();
		
		String[] pluginIDs = new String[]{
				de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.Activator.PLUGIN_ID,
		};
		
		IUltimateServiceProvider service = generator.registerDefaultPreferenceLayer(this.getClass(), pluginIDs);
		IPreferenceProvider pref = service.getPreferenceProvider(pluginIDs[0]);
		pref.put("SMT solver", "Internal_SMTInterpol");
		pref.put("Size of a code block", "SingleStatement"); // Possible options: SingleStatement, SequenceOfStatements, LoopFreeBlock
		
		BoogieParser bp = new BoogieParser();
		bp.setServices(service);
		
		Unit unit = (Unit)parseBoogie(bp, "res/code.bpl");
		
		CfgBuilder cfgBuilder = makeCfgBuilder(unit, service);
		
		IIcfg<BoogieIcfgLocation> icfg = cfgBuilder.createIcfg(unit);
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
