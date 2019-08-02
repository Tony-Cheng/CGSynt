package cgsynt.buchi.determinization;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * An implementation of a compact Safra tree described in the paper below:
 * https://arxiv.org/pdf/0705.2205.pdf
 *
 */
public class SafraTree<STATE> {

	private Set<Integer> states;
	private Map<Integer, Integer> nameMap;
	private final int root;
	private Map<Integer, Integer> parentMap;
	private Map<Integer, Set<Integer>> childrenMap;
	private Map<Integer, Set<STATE>> labelMap;
	private Map<Integer, Integer> rem;
	private int e;
	private int f;

	public SafraTree() {
		this.states = new HashSet<>();
		this.nameMap = new HashMap<>();
		this.root = 1;
		this.parentMap = new HashMap<>();
		this.childrenMap = new HashMap<>();
		this.labelMap = new HashMap<>();
		this.rem = new HashMap<>();
		this.e = 2;
		this.f = 1;
	}

	private void addRoot() {
		states.add(root);
		nameMap.put(root, 1);
		childrenMap.put(root, new HashSet<>());
		childrenMap.get(root).add(root);
		parentMap.put(root, root);
		labelMap.put(root, new HashSet<>());
		rem.put(root, 0);
	}

	public Set<STATE> getLabels(Integer node) {
		return labelMap.get(node);
	}

	public void setLabels(Integer node, Set<STATE> labels) {
		this.labelMap.put(node, labels);
	}

	public void removeLabel(Integer node, STATE label) {
		if (labelMap.get(node).contains(label)) {
			labelMap.get(node).remove(label);
			for (Integer child : childrenMap.get(node)) {
				removeLabel(child, label);
			}
		}
	}

	public boolean checkUnionOfChildren(Integer node) {
		Set<STATE> union = new HashSet<>();
		for (Integer child : childrenMap.get(node)) {
			union.addAll(labelMap.get(child));
		}
		return union.equals(labelMap.get(node));
	}

	public int getRem(Integer node) {
		return rem.get(node);
	}

}
