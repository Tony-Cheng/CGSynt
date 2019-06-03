package TTC;

import java.io.File;
import java.io.IOException;

import de.uni_freiburg.informatik.ultimate.boogie.parser.BoogieParser;
import de.uni_freiburg.informatik.ultimate.core.model.models.IElement;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger.LogLevel;
import de.uni_freiburg.informatik.ultimate.test.mocks.UltimateMocks;
import de.uni_freiburg.informatik.ultimate.test.mocks.UltimateServiceProviderMock;

public class BoogieParsingDebug {
	public BoogieParsingDebug() throws IOException {
		File[] files = new File[1];
		files[0] = new File("res/code.bpl"); 
		// The parser requires an absolute path so you need to change this
		
		UltimateServiceProviderMock mock = (UltimateServiceProviderMock) UltimateMocks.createUltimateServiceProviderMock(LogLevel.DEBUG);
		
		BoogieParser bp = new BoogieParser();
		bp.setServices(mock);
		
		IElement ast = bp.parseAST(files);
		System.out.println("Parsed!");
	}
	
	public static void main(String args[]) throws IOException {
		new BoogieParsingDebug();
	}
}
