package cgsynt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StateRepCondenser<STATE> {
	private List<STATE> mStates;
	private int mLetterNum = 0;
	
	public StateRepCondenser (List<STATE> states) {
		mStates = states;
	}
	
	public Map<String, String> getMapping(){
		Map<String, String> map = new HashMap<>();
		
		for (STATE state : mStates) {
			if (!map.containsKey(state.toString())) {
				char letter = (char)((int)'A' + (mLetterNum % 26));
				int num = mLetterNum / 26;
				String identifier = "" + letter + num;
				
				map.put(state.toString(), identifier);
				
				mLetterNum++;
			}
		}
		
		return map;
	}
}
