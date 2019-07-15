package cgsynt.nfa;

import java.util.Iterator;
import java.util.Set;

import cgsynt.interpol.IInterpol;
import cgsynt.interpol.IStatement;
import cgsynt.interpol.TraceToInterpolants;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.INestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;

/**
 * Key things to do when using this class - In the initialization of the
 * currentInterpolants set in the container for this class, you must add the
 * true and false predicate. - After calculating the interpolants in a given
 * loop iteration, first pass the new and old interpolants to this class, and
 * once the generalization is done, then you can update the list of current
 * interpolants with the new ones.
 */
public class MultiThreadGeneralization {
	private Set<IPredicate> mCurrentInterpolants;
	private Set<IPredicate> mAdditionalInterpolantSet;
	private Set<IStatement> mAllStatements;
	private IInterpol mInterpolator;

	private NestedWordAutomaton<IStatement, IPredicate> mInterpolantNfa;

	private Iterator<IPredicate> mPreconditionIterator;
	private Iterator<IStatement> mStatementIterator;
	private Iterator<IStatement> mSavedStatementIterator;
	private IPredicate mPre;
	private boolean mNoJobsLeft = false;
	private Set<IPredicate> currentPosts;

	public MultiThreadGeneralization(Set<IPredicate> currentInterpolants, Set<IPredicate> additionalInterpolants,
			Set<IStatement> allStatements, INestedWordAutomaton<IStatement, IPredicate> PI,
			TraceToInterpolants interpolator) {
		this.mCurrentInterpolants = currentInterpolants;
		this.mAdditionalInterpolantSet = additionalInterpolants;
		this.mAllStatements = allStatements;
		this.mInterpolantNfa = (NestedWordAutomaton<IStatement, IPredicate>) PI;

		this.mInterpolator = interpolator;

		this.computeResult();
	}

	public NestedWordAutomaton<IStatement, IPredicate> getResult() {
		return mInterpolantNfa;
	}

	private void computeResult() {
		addTransitions(this.mCurrentInterpolants, this.mAdditionalInterpolantSet);
		addTransitions(this.mAdditionalInterpolantSet, this.mCurrentInterpolants);
		addTransitions(this.mAdditionalInterpolantSet, this.mAdditionalInterpolantSet);
	}

	private void addTransitions(Set<IPredicate> preSet, Set<IPredicate> postSet) {
		mPreconditionIterator = preSet.iterator();
		mStatementIterator = this.mAllStatements.iterator();

		mSavedStatementIterator = this.mAllStatements.iterator();

		currentPosts = postSet;

		initJobs();

		int numCores = Runtime.getRuntime().availableProcessors();
		TeamStats stats = new TeamStats(numCores);

		while (!mNoJobsLeft) {
			while (stats.getActiveWorkerCount() == numCores)
				;

			int freeID = stats.getFreeWorkerID();
			Tuple<IPredicate, IStatement> job = nextJob();

			if (job != null) {
				SMTWorker worker = new SMTWorker(freeID, stats, nextJob());
				worker.start();
			}
		}
	}

	private void initJobs() {
		mPre = mPreconditionIterator.next();
	}

	private Tuple<IPredicate, IStatement> nextJob() {
		Tuple<IPredicate, IStatement> job = null;

		if (mPreconditionIterator.hasNext()) {
			if (mStatementIterator.hasNext()) {
				job = new Tuple<>(mPre, mStatementIterator.next());
			} else {
				mStatementIterator = mSavedStatementIterator;
				mPre = mPreconditionIterator.next();
			}
		} else {
			mNoJobsLeft = true;
			return null;
		}

		return job;
	}

	class SMTWorker extends Thread {
		private TeamStats stats;
		private int id;
		private Tuple<IPredicate, IStatement> job;

		public SMTWorker(int freeID, TeamStats stats, Tuple<IPredicate, IStatement> job) {
			super("" + freeID);

			this.id = freeID;
			this.stats = stats;
			this.job = job;

			stats.setActive(freeID);
		}

		@Override
		public void run() {
			for (IPredicate post : currentPosts) {
				boolean unsat = mInterpolator.isCorrect(job.getFirst(), job.getSecond(), post);
				if (unsat) {
					Set<IPredicate> states = mInterpolantNfa.getStates();

					if (!states.contains(job.getFirst()))
						mInterpolantNfa.addState(false, false, job.getFirst());
					if (!states.contains(post))
						mInterpolantNfa.addState(false, false, post);

					mInterpolantNfa.addInternalTransition(job.getFirst(), job.getSecond(), post);
				}
				stats.setInactive(id);
			}
		}
	}
}

class Tuple<F, S> {
	private F first;
	private S second;

	public Tuple(F first, S second) {
		this.first = first;
		this.second = second;
	}

	public F getFirst() {
		return first;
	}

	public S getSecond() {
		return second;
	}
}

class TeamStats {
	private int numCores, activeWorkersNum;
	private boolean[] workerList;

	public TeamStats(int numCores) {
		this.numCores = numCores;
		this.activeWorkersNum = 0;
		this.workerList = newWorkerList(numCores);
	}

	private boolean[] newWorkerList(int numWorkers) {
		boolean[] workerList = new boolean[numWorkers];
		for (int i = 0; i < numWorkers; i++)
			workerList[i] = false;

		return workerList;
	}

	public int getFreeWorkerID() {
		int id = -1;

		for (int i = 0; i < workerList.length; i++) {
			if (workerList[i] == false) {
				id = i;
				break;
			}
		}

		return id;
	}

	public void setActive(int id) {
		this.activeWorkersNum++;
		this.workerList[id] = true;
	}

	public void setInactive(int id) {
		this.activeWorkersNum--;
		this.workerList[id] = false;
	}

	public int getActiveWorkerCount() {
		return this.activeWorkersNum;
	}
}