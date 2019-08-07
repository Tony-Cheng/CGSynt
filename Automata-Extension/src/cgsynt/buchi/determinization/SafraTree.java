package cgsynt.buchi.determinization;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cgsynt.tree.parity.IParityState;

/**
 * An implementation of a compact Safra tree described in the paper below:
 * https://arxiv.org/pdf/0705.2205.pdf
 *
 */
public class SafraTree<STATE> implements IParityState {

	private Set<Integer> states;
	private Map<Integer, Integer> nameMap;
	private int root;
	private Map<Integer, Integer> parentMap;
	private Map<Integer, Set<Integer>> childrenMap;
	private Map<Integer, Set<STATE>> labelMap;
	private Set<Integer> rem;
	private Set<STATE> initialStates;
	private int e;
	private int f;
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
		this.rem = new HashSet<>();
		this.greatestName = 0;
		this.e = 2;
		this.f = 1;
		this.numBuchiStates = numBuchiStates;
		addRoot(initialStates);
	}

	private void addRoot(Set<STATE> initialStates) {
		states.add(root);
		nameMap.put(root, 1);
		childrenMap.put(root, new HashSet<>());
		parentMap.put(root, null);
		labelMap.put(root, initialStates);
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

	public int getName(Integer node) {
		return nameMap.get(node);
	}

	public void setGreenNode(Integer node) {
		if (!states.contains(node)) 
			return;
		f = Math.min(f, node);
		for (Integer child : childrenMap.get(node)) {
			removeSubtree(child);
		}
		childrenMap.get(node).clear();
	}

	public Set<Integer> getChildren(Integer node) {
		return childrenMap.get(node);
	}

	public void removeNode(Integer node) {
		e = Math.min(e, node);
		states.remove(node);
		nameMap.remove(node);
		if (parentMap.get(node) != null) {
			childrenMap.get(parentMap.get(node)).remove(node);
		}
		parentMap.remove(node);
		labelMap.remove(node);
		rem.add(node);
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
		rem.add(node);
	}

	public SafraTree<STATE> copy() {
		SafraTree<STATE> tree = new SafraTree<>(initialStates, this.numBuchiStates);
		tree.states.addAll(states);
		tree.nameMap.putAll(nameMap);
		tree.root = root;
		tree.parentMap.putAll(parentMap);
		tree.childrenMap.putAll(childrenMap);
		tree.labelMap.putAll(labelMap);
		tree.rem.addAll(rem);
		tree.e = e;
		tree.f = f;
		return tree;
	}

	public void compressTree() {
		int sum = 0;
		for (int i = 1; i < this.greatestName; i++) {
			if (rem.contains(i)) {
				sum++;
			} else {
				states.add(i - sum);
				states.remove(i);
				nameMap.put(i - sum, nameMap.get(i));
				nameMap.remove(i);
				for (Integer child : childrenMap.get(i)) {
					parentMap.put(child, i - sum);
				}
				childrenMap.remove(i);
				parentMap.put(i - sum, parentMap.get(i));
				parentMap.remove(i);
				labelMap.put(i - sum, labelMap.get(i));
				labelMap.remove(i - sum);
			}
		}
		greatestName = greatestName - sum;
		e = this.numBuchiStates + 1;
		f = this.numBuchiStates + 1;
		rem.clear();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((childrenMap == null) ? 0 : childrenMap.hashCode());
		result = prime * result + e;
		result = prime * result + f;
		result = prime * result + greatestName;
		result = prime * result + ((initialStates == null) ? 0 : initialStates.hashCode());
		result = prime * result + ((labelMap == null) ? 0 : labelMap.hashCode());
		result = prime * result + ((nameMap == null) ? 0 : nameMap.hashCode());
		result = prime * result + numBuchiStates;
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
		if (greatestName != other.greatestName)
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
		if (numBuchiStates != other.numBuchiStates)
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

	@Override
	public int getRank() {
		int invertedPriority;
		if (e == 1) {
			invertedPriority = 2 * this.numBuchiStates - 1;
		} else if (f == 1 && e > 1) {
			invertedPriority = 0;
		} else if (f >= e) {
			int i = e - 2;
			invertedPriority = 2 * i + 1;
		} else {
			int i = f - 2;
			invertedPriority = 2 * i + 2;
		}
		return this.numBuchiStates * 2 - invertedPriority;
	}

}
