package cgsynt.core;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cgsynt.synthesis.SynthesisLoop;

public class Core {
	public Core(String[] args) throws Exception {

		BufferedReader reader = null;
		reader = new BufferedReader(new InputStreamReader(System.in));

		ArrayList<String> lines = new ArrayList<>();
		String line;
		while (!(line = reader.readLine()).equals("end"))
			lines.add(line);

		InputFileParser parser = new InputFileParser();
		Specification spec = parser.parseFile(lines);
		SynthesisLoop synthesis = new SynthesisLoop(spec);
		synthesis.computeMainLoop();
		System.out.println(synthesis.isCorrect());
		//synthesis.printProgram();

	}

	public Map<String, List<String>> parseArgs(String args[]) {
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
