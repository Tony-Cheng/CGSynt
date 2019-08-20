package cgsynt;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Takes a List of objects which have verbose string representations
 * and maps them to a simpler string representation.
 * 
 * Used in toString() method of most custom automaton.
 * */
public class RepCondenser<VERBOSE> {
	private List<VERBOSE> mVerbose;
	private int mLetterNum = 0;
	
	public RepCondenser (List<VERBOSE> verbose) {
		mVerbose = verbose;
	}
	
	/**
	 * Generate a map that will map string representations
	 * to simpler strings.
	 * @return A map from Strings to Strings.
	 */
	public Map<String, String> getMapping(){
		Map<String, String> map = new LinkedHashMap<>();
		
		for (VERBOSE verbose : mVerbose) {
			if (!map.containsKey(verbose.toString())) {
				char letter = (char)((int)'A' + (mLetterNum % 26));
				int num = mLetterNum / 26;
				String identifier = "" + letter + num;
				
				map.put(verbose.toString(), identifier);
				
				mLetterNum++;
			}
		}
		
		return map;
	}
}
