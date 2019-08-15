package cgsynt.buchi.determinization;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import cgsynt.tree.parity.IParityState;
import cgsynt.tree.parity.ParityState;

/**
 * An implementation of a compact Safra tree described in the paper below:
 * https://arxiv.org/pdf/0705.2205.pdf
 *
 */
public class SafraTree<STATE> implements IParityState {

	private Set<Integer> mStates;
	private Map<Integer, Integer> mNameMap;
	private int mRoot;
	private Map<Integer, Integer> mParentMap;
	private Map<Integer, Set<Integer>> mChildrenMap;
	private Map<Integer, Set<STATE>> mLabelMap;
	private Set<Integer> mRem;
	private Set<STATE> mInitialStates;
	private int mE;
	private int mF;
	private int mNumBuchiStates;
	private int mGreatestName;
	
	private boolean mInitializeRoot;

	public SafraTree(Set<STATE> initialStates, int numBuchiStates, boolean initializeRoot) {
		this.mInitialStates = initialStates;
		this.mStates = new HashSet<>();
		this.mNameMap = new HashMap<>();
		this.mRoot = 1;
		this.mParentMap = new HashMap<>();
		this.mChildrenMap = new HashMap<>();
		this.mLabelMap = new HashMap<>();
		this.mRem = new HashSet<>();
		this.mGreatestName = 0;
		this.mE = 2;
		this.mF = 1;
		this.mNumBuchiStates = numBuchiStates;
		this.mInitializeRoot = initializeRoot;
		
		if (this.mInitializeRoot)
			addRoot(initialStates);
	}

	private void addRoot(Set<STATE> initialStates) {
		mStates.add(mRoot);
		mNameMap.put(mRoot, 1);
		mChildrenMap.put(mRoot, new HashSet<>());
		mParentMap.put(mRoot, null);
		mLabelMap.put(mRoot, initialStates);
		this.mGreatestName++;
	}

	public Set<Integer> getStates() {
		return this.mStates;
	}

	public Integer addNode(Integer parent, Set<STATE> label) {
		Integer next = mGreatestName + 1;
		mGreatestName += 1;
		mStates.add(next);
		mNameMap.put(next, next);
		mParentMap.put(next, parent);
		mChildrenMap.put(next, new HashSet<>());
		mChildrenMap.get(parent).add(next);
		mLabelMap.put(next, label);
		return next;
	}

	public Set<Integer> getSiblings(Integer node) {
		if (mParentMap.get(node) != null) {
			Integer parent = mParentMap.get(node);
			return mChildrenMap.get(parent);
		}
		return null;
	}

	public Set<STATE> getLabels(Integer node) {
		return mLabelMap.get(node);
	}

	public void setLabels(Integer node, Set<STATE> labels) {
		this.mLabelMap.put(node, labels);
	}

	public void removeLabel(Integer node, STATE label) {
		if (mLabelMap.get(node).contains(label)) {
			mLabelMap.get(node).remove(label);
			for (Integer child : mChildrenMap.get(node)) {
				removeLabel(child, label);
			}
		}
	}

	public boolean checkUnionOfChildren(Integer node) {
		Set<STATE> union = new HashSet<>();
		for (Integer child : mChildrenMap.get(node)) {
			union.addAll(mLabelMap.get(child));
		}
		return union.equals(mLabelMap.get(node));
	}

	public int getName(Integer node) {
		return mNameMap.get(node);
	}

	public void setGreenNode(Integer node) {
		if (!mStates.contains(node))
			return;
		mF = Math.min(mF, node);
		for (Integer child : mChildrenMap.get(node)) {
			removeSubtree(child);
		}
		mChildrenMap.get(node).clear();
	}

	@Override
	public String toString() {
		return "SafraTree [states=" + mStates + ", nameMap=" + mNameMap + ", root=" + mRoot + ", parentMap=" + mParentMap
			+ ", childrenMap=" + mChildrenMap + ", labelMap=" + mLabelMap + ", rem=" + mRem + ", initialStates="
			+ mInitialStates + ", e=" + mE + ", f=" + mF + ", numBuchiStates=" + mNumBuchiStates + ", greatestName="
			+ mGreatestName + ", rank=" + getRank() + "]";
	}

	public Set<Integer> getChildren(Integer node) {
		return mChildrenMap.get(node);
	}

	public void removeNode(Integer node) {
		if (!mStates.contains(node))
			return;
		mE = Math.min(mE, node);
		removeSubtree(node);
		// states.remove(node);
		// nameMap.remove(node);
		// if (parentMap.get(node) != null) {
		// childrenMap.get(parentMap.get(node)).remove(node);
		// }
		// parentMap.remove(node);
		// labelMap.remove(node);
		// rem.add(node);
	}

	private void removeSubtree(Integer node) {
		if (!mStates.contains(node))
			return;
		Set<Integer> children = mChildrenMap.get(node);
		mStates.remove(node);
		mParentMap.remove(node);
		mLabelMap.remove(node);
		mNameMap.remove(node);
		for (Integer child : children) {
			removeSubtree(child);
		}
		mChildrenMap.remove(node);
		mRem.add(node);
	}

	public void compressTree() {
		int sum = 0;
		for (int i = 1; i <= this.mGreatestName; i++) {
			if (mRem.contains(i)) {
				sum++;
			} else if (sum > 0) {
				mStates.add(i - sum);
				mStates.remove(i);
				mNameMap.put(i - sum, mNameMap.get(i) - sum);
				mNameMap.remove(i);
				for (Integer child : mChildrenMap.get(i)) {
					mParentMap.put(child, i - sum);
				}
				mChildrenMap.put(i - sum, mChildrenMap.get(i));
				mChildrenMap.remove(i);
				if (mParentMap.get(i) != null) {
					mChildrenMap.get(mParentMap.get(i)).remove(i);
					mChildrenMap.get(mParentMap.get(i)).add(i- sum);
				}
				mParentMap.put(i - sum, mParentMap.get(i));
				mParentMap.remove(i);
				mLabelMap.put(i - sum, mLabelMap.get(i));
				mLabelMap.remove(i);
			}
		}
		mGreatestName = mGreatestName - sum;
		mRem.clear();
	}

	public void resetEF() {
		mE = this.mNumBuchiStates + 1;
		mF = this.mNumBuchiStates + 1;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mChildrenMap == null) ? 0 : mChildrenMap.hashCode());
		result = prime * result + mE;
		result = prime * result + mF;
		result = prime * result + mGreatestName;
		result = prime * result + ((mInitialStates == null) ? 0 : mInitialStates.hashCode());
		result = prime * result + ((mLabelMap == null) ? 0 : mLabelMap.hashCode());
		result = prime * result + ((mNameMap == null) ? 0 : mNameMap.hashCode());
		result = prime * result + mNumBuchiStates;
		result = prime * result + ((mParentMap == null) ? 0 : mParentMap.hashCode());
		result = prime * result + ((mRem == null) ? 0 : mRem.hashCode());
		result = prime * result + mRoot;
		result = prime * result + ((mStates == null) ? 0 : mStates.hashCode());
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
		if (mChildrenMap == null) {
			if (other.mChildrenMap != null)
				return false;
		} else if (!mChildrenMap.equals(other.mChildrenMap))
			return false;
		if (mE != other.mE)
			return false;
		if (mF != other.mF)
			return false;
		if (mGreatestName != other.mGreatestName)
			return false;
		if (mInitialStates == null) {
			if (other.mInitialStates != null)
				return false;
		} else if (!mInitialStates.equals(other.mInitialStates))
			return false;
		if (mLabelMap == null) {
			if (other.mLabelMap != null)
				return false;
		} else if (!mLabelMap.equals(other.mLabelMap))
			return false;
		if (mNameMap == null) {
			if (other.mNameMap != null)
				return false;
		} else if (!mNameMap.equals(other.mNameMap))
			return false;
		if (mNumBuchiStates != other.mNumBuchiStates)
			return false;
		if (mParentMap == null) {
			if (other.mParentMap != null)
				return false;
		} else if (!mParentMap.equals(other.mParentMap))
			return false;
		if (mRem == null) {
			if (other.mRem != null)
				return false;
		} else if (!mRem.equals(other.mRem))
			return false;
		if (mRoot != other.mRoot)
			return false;
		if (mStates == null) {
			if (other.mStates != null)
				return false;
		} else if (!mStates.equals(other.mStates))
			return false;
		return true;
	}

	@Override
	public int getRank() {
		int invertedPriority;
		if (mE == 1) {
			invertedPriority = 2 * this.mNumBuchiStates - 1;
		} else if (mF == 1 && mE > 1) {
			invertedPriority = 0;
		} else if (mF >= mE) {
			int i = mE - 2;
			invertedPriority = 2 * i + 1;
		} else {
			int i = mF - 2;
			invertedPriority = 2 * i + 2;
		}
		return this.mNumBuchiStates * 2 - invertedPriority;
	}

	public IParityState makeCpy() {
		SafraTree<STATE> tree = new SafraTree<>(mInitialStates, this.mNumBuchiStates, false);
		tree.mStates.addAll(mStates);
		tree.mNameMap.putAll(mNameMap);
		tree.mRoot = mRoot;
		tree.mParentMap.putAll(mParentMap);
		for (Integer key : this.mChildrenMap.keySet()) {
			tree.mChildrenMap.put(key, new HashSet<>());
			tree.mChildrenMap.get(key).addAll(this.mChildrenMap.get(key));
		}
		for (Integer key : this.mLabelMap.keySet()) {
			tree.mLabelMap.put(key, new HashSet<>());
			tree.mLabelMap.get(key).addAll(this.mLabelMap.get(key));
		}
		tree.mRem.addAll(mRem);
		tree.mE = mE;
		tree.mF = mF;
		tree.mGreatestName = this.mGreatestName;
		return tree;
	}

	public Set<STATE> getInitialStates(){
		return this.mInitialStates;
	}
	
	public int getNumBuchiStates() {
		return this.mNumBuchiStates;
	}
	
	public boolean getInitializeRoot() {
		return this.mInitializeRoot;
	}
	
	@SuppressWarnings("unchecked")
	public IParityState getSimpleRepresentation() {
		SafraTree<STATE> cpy = (SafraTree<STATE>)this.makeCpy();
		
		Iterator<STATE> it = cpy.mInitialStates.iterator();
		IParityState rep = new ParityState<STATE>(it.next(), cpy.getRank());
		
		return rep;
	}
}
