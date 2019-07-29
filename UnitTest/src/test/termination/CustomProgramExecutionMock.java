package test.termination;

import java.util.Collections;

import de.uni_freiburg.informatik.ultimate.core.lib.results.NoBacktranslationValueProvider;
import de.uni_freiburg.informatik.ultimate.core.model.translation.AtomicTraceElement;
import de.uni_freiburg.informatik.ultimate.core.model.translation.IBacktranslationValueProvider;
import de.uni_freiburg.informatik.ultimate.core.model.translation.IProgramExecution;

public class CustomProgramExecutionMock<TE, E> implements IProgramExecution<TE, E> {
	private final Class<E> mExprClazz;
	private final Class<TE> mTraceElementClazz;

	public CustomProgramExecutionMock(final Class<E> exprClazz, final Class<TE> traceElementClazz) {
		mExprClazz = exprClazz;
		mTraceElementClazz = traceElementClazz;
	}

	@Override
	public int getLength() {
		return 0;
	}

	@Override
	public AtomicTraceElement<TE> getTraceElement(final int index) {
		throw new IndexOutOfBoundsException();
	}

	@Override
	public ProgramState<E> getProgramState(final int index) {
		throw new IndexOutOfBoundsException();
	}

	@Override
	public ProgramState<E> getInitialProgramState() {
		return new ProgramState<>(Collections.emptyMap(), null);
	}

	@Override
	public Class<E> getExpressionClass() {
		return mExprClazz;
	}

	@Override
	public Class<TE> getTraceElementClass() {
		return mTraceElementClazz;
	}

	@Override
	public boolean isConcurrent() {
		return false;
	}

	@Override
	public IBacktranslationValueProvider<TE, E> getBacktranslationValueProvider() {
		return new NoBacktranslationValueProvider<>();
	}
}
