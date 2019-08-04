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
	private int root;
	private Map<Integer, Integer> parentMap;
	private Map<Integer, Set<Integer>> childrenMap;
	private Map<Integer, Set<STATE>> labelMap;
	private Map<Integer, Integer> rem;
	private Set<STATE> initialStates;
	private int e;
	private int f;
	private int minGreenNode;
	private int numBuchiStates;
	private int greatestName;

	public SafraTree(Set<STATE> initialStates, int numBuchiStates) {
		this.initialStates = initialStates;
		this.states = new HashSet<>();
		this.nameMap = new HashMap<>();
		this.root = 1;
		this.parentMap = new HashMap<>();
		this.childrenMap = new HashMap<>();
		this.labelMap = new HashMap<>();
		this.rem = new HashMap<>();
		this.greatestName = 0;
		this.e = 2;
		this.f = 1;
		this.numBuchiStates = numBuchiStates;
		this.minGreenNode = numBuchiStates + 1;
		addRoot(initialStates);
	}

	private void addRoot(Set<STATE> initialStates) {
		states.add(root);
		nameMap.put(root, 1);
		childrenMap.put(root, new HashSet<>());
		parentMap.put(root, null);
		labelMap.put(root, initialStates);
		rem.put(root, 0);
		this.greatestName++;
	}

	public Set<Integer> getStates() {
		return this.states;
	}

	public Integer addNode(Integer parent, Set<STATE> label) {
		Integer next = greatestName + 1;
		greatestName += 1;
		states.add(next);
		nameMap.put(next, next);
		parentMap.put(next, parent);
		childrenMap.put(next, new HashSet<>());
		childrenMap.get(parent).add(next);
		labelMap.put(next, label);
		return next;
	}

	public Set<Integer> getSiblings(Integer node) {
		if (parentMap.get(node) != null) {
			Integer parent = parentMap.get(node);
			return childrenMap.get(parent);
		}
		return null;
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

	public int getName(Integer node) {
		return nameMap.get(node);
	}

	public void setGreenNode(Integer node) {
		minGreenNode = Math.min(minGreenNode, node);
		f = minGreenNode;
		for (Integer child : childrenMap.get(node)) {
			removeSubtree(child);
		}
		childrenMap.get(node).clear();
	}
	
	public Set<Integer> getChildren(Integer node) {
		return childrenMap.get(node);
	}

	private void removeSubtree(Integer node) {
		Set<Integer> children = childrenMap.get(node);
		states.remove(node);
		parentMap.remove(node);
		labelMap.remove(node);
		nameMap.remove(node);
		for (Integer child : children) {
			removeSubtree(child);
		}
		children.remove(node);
	}

	public SafraTree<STATE> copy() {
		SafraTree<STATE> tree = new SafraTree<>(initialStates, this.numBuchiStates);
		tree.states.addAll(states);
		tree.nameMap.putAll(nameMap);
		tree.root = root;
		tree.parentMap.putAll(parentMap);
		tree.childrenMap.putAll(childrenMap);
		tree.labelMap.putAll(labelMap);
		tree.rem.putAll(rem);
		tree.e = e;
		tree.f = f;
		tree.minGreenNode = minGreenNode;
		tree.greatestName = greatestName;
		return tree;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((childrenMap == null) ? 0 : childrenMap.hashCode());
		result = prime * result + e;
		result = prime * result + f;
		result = prime * result + ((initialStates == null) ? 0 : initialStates.hashCode());
		result = prime * result + ((labelMap == null) ? 0 : labelMap.hashCode());
		result = prime * result + ((nameMap == null) ? 0 : nameMap.hashCode());
		result = prime * result + ((parentMap == null) ? 0 : parentMap.hashCode());
		result = prime * result + ((rem == null) ? 0 : rem.hashCode());
		result = prime * result + root;
		result = prime * result + ((states == null) ? 0 : states.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SafraTree other = (SafraTree) obj;
		if (childrenMap == null) {
			if (other.childrenMap != null)
				return false;
		} else if (!childrenMap.equals(other.childrenMap))
			return false;
		if (e != other.e)
			return false;
		if (f != other.f)
			return false;
		if (initialStates == null) {
			if (other.initialStates != null)
				return false;
		} else if (!initialStates.equals(other.initialStates))
			return false;
		if (labelMap == null) {
			if (other.labelMap != null)
				return false;
		} else if (!labelMap.equals(other.labelMap))
			return false;
		if (nameMap == null) {
			if (other.nameMap != null)
				return false;
		} else if (!nameMap.equals(other.nameMap))
			return false;
		if (parentMap == null) {
			if (other.parentMap != null)
				return false;
		} else if (!parentMap.equals(other.parentMap))
			return false;
		if (rem == null) {
			if (other.rem != null)
				return false;
		} else if (!rem.equals(other.rem))
			return false;
		if (root != other.root)
			return false;
		if (states == null) {
			if (other.states != null)
				return false;
		} else if (!states.equals(other.states))
			return false;
		return true;
	}

}
