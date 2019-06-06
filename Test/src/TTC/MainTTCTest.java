package TTC;

import java.io.File;
import java.io.IOException;
import java.util.List;

import de.uni_freiburg.informatik.ultimate.boogie.ast.Unit;
import de.uni_freiburg.informatik.ultimate.boogie.parser.BoogieParser;
import de.uni_freiburg.informatik.ultimate.core.coreplugin.services.ToolchainStorage;
import de.uni_freiburg.informatik.ultimate.core.lib.models.WrapperNode;
import de.uni_freiburg.informatik.ultimate.core.model.models.IElement;
import de.uni_freiburg.informatik.ultimate.core.model.preferences.IPreferenceProvider;
import de.uni_freiburg.informatik.ultimate.core.model.services.IBacktranslationService;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.BoogieIcfgContainer;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.BoogieIcfgLocation;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.CfgBuilder;
import de.uni_freiburg.informatik.ultimate.smtinterpol.DefaultLogger;
import de.uni_freiburg.informatik.ultimate.smtinterpol.smtlib2.SMTInterpol;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.boogie.Boogie2SMT;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.boogie.BoogieDeclarations;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IIcfg;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.managedscript.ManagedScript;

public class MainTTCTest {
	public MainTTCTest() {
		IUltimateServiceProvider generator = new ToolchainStorage();
		
		String[] pluginIDs = new String[]{
				de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.Activator.PLUGIN_ID,
		};
		
		IUltimateServiceProvider service = generator.registerDefaultPreferenceLayer(this.getClass(), pluginIDs);
		IPreferenceProvider pref = service.getPreferenceProvider(pluginIDs[0]);
		pref.put("SMT solver", "Internal_SMTInterpol");
		pref.put("Size of a code block", "SingleStatement"); // Possible options: , SequenceOfStatements, LoopFreeBlock
		
		BoogieParser bp = new BoogieParser();
		bp.setServices(service);
		
		String[] files = {"res/t6.bpl"};
		
		Unit unit = (Unit)parseBoogie(bp, files);
		
		BoogieDeclarations decs = new BoogieDeclarations(unit, service.getLoggingService().getControllerLogger());
		Script script = new SMTInterpol(new DefaultLogger());
		ManagedScript mScript = new ManagedScript(service, script);
		Boogie2SMT b2smt = new Boogie2SMT(mScript, decs, false, service, false);
		BoogieIcfgContainer icfg = new BoogieIcfgContainer(service, decs, b2smt, null);
		
		
	}
	
	private IElement parseBoogie(BoogieParser parser, String... path) {
		File[] files = new File[path.length];
		
		int i = 0;
		for (String pathName : path) {
			files[i] = new File(pathName);
			i++;
		}
		
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
