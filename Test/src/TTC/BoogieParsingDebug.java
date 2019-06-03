package TTC;

import java.io.File;

public class BoogieParsingDebug {
	public BoogieParsingDebug() {
		File[] files = new File[1];
		files[0] = new File("/code.bpl"); 
	}
	
	public static void main(String args[]) {
		new BoogieParsingDebug();
	}
}
