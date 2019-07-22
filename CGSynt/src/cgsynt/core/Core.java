package cgsynt.core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Core {
	public Core(String[] args) throws Exception {
		Map<String, List<String>> argsMap = parseArgs(args);
		
		if (argsMap.containsKey("-f")){
			List<String> paths = argsMap.get("-f");
			
			for (String path : paths) {
				BufferedReader reader = null;
				try {
					reader = new BufferedReader(new FileReader(path));
				} catch(IOException ioe) {
					System.out.println("ERROR: Couldn't read file " + path);
					System.exit(1);
				}
				
				ArrayList<String> lines = new ArrayList<>();
				String line;
				while ((line = reader.readLine()) != null) {
					lines.add(line);
				}
				
				InputFileParser parser = new InputFileParser();
				Specification spec = parser.parseFile(lines);
				//SynthesisLoop synthesis = new SynthesisLoop(spec);
				//synthesis.computeMainLoop();
				//System.out.println(synthesis.isCorrect());
				//synthesis.printProgram();
			}
		}
		else {
			System.err.println("ERROR: Please specify the input files with the -f flag");
		}
	}
	
	public Map<String, List<String>> parseArgs(String args[]){
		Map<String, List<String>> argsMap = new HashMap<>();
		
		String flag = null;
		ArrayList<String> params = new ArrayList<>();
		
		for (int i = 0; i < args.length; i++) {
			String token = args[i];
			if (token.startsWith("-"))
				flag = token;
			else
				params.add(token);
			
			if (i + 1 >= args.length || args[i + 1].startsWith("-")) {
				argsMap.put(flag, params);
				params = new ArrayList<>();
			}
		}
		
		return argsMap;
	}
	
	public static void main(String[] args) throws Exception {
		new Core(args);
	}
}
