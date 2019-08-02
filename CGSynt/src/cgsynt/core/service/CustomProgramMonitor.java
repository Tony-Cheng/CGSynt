package cgsynt.core.service;

import java.util.concurrent.CountDownLatch;

import de.uni_freiburg.informatik.ultimate.core.model.services.IProgressAwareTimer;
import de.uni_freiburg.informatik.ultimate.core.model.services.IProgressMonitorService;

public class CustomProgramMonitor implements IProgressMonitorService{
	private long mDeadline = Long.MAX_VALUE;

	@Override
	public boolean continueProcessing() {
		return System.currentTimeMillis() < mDeadline;
	}

	@Override
	public IProgressAwareTimer getChildTimer(final long timeout) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IProgressAwareTimer getChildTimer(final double percentage) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setSubtask(final String task) {
		// mock
	}

	@Override
	public void setDeadline(final long date) {
		mDeadline = date;
	}

	@Override
	public CountDownLatch cancelToolchain() {
		return new CountDownLatch(0);
	}

	@Override
	public IProgressAwareTimer getParent() {
		return null;
	}

	@Override
	public long getDeadline() {
		return 0;
	}

	@Override
	public void addChildTimer(final IProgressAwareTimer timer) {
		// mock
	}

	@Override
	public IProgressAwareTimer removeChildTimer() {
		// mock
		return null;
	}

	@Override
	public IProgressAwareTimer getTimer(final long timeout) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean continueProcessingRoot() {
		return true;
	}
}
