package cgsynt.core.service;

import java.io.Writer;

import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILoggingService;
import de.uni_freiburg.informatik.ultimate.core.model.services.IToolchainStorage;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger.LogLevel;
import de.uni_freiburg.informatik.ultimate.test.mocks.ConsoleLogger;

public class CustomLoggingServiceMock implements ILoggingService{
	private final LogLevel mDefaultLevel;

	CustomLoggingServiceMock(final LogLevel defaultLevel) {
		mDefaultLevel = defaultLevel;
	}

	@Override
	public ILogger getLogger(final String pluginId) {
		return new ConsoleLogger(mDefaultLevel);
	}

	@Override
	public ILogger getLogger(final Class<?> clazz) {
		return new ConsoleLogger(mDefaultLevel);
	}

	@Override
	public ILogger getLoggerForExternalTool(final String id) {
		return new ConsoleLogger(mDefaultLevel);
	}

	@Override
	public ILogger getControllerLogger() {
		return new ConsoleLogger(mDefaultLevel);
	}

	@Override
	public Object getBacking(final ILogger logger, final Class<?> backingType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addWriter(final Writer writer, final String logPattern) {
		// do nothing
	}

	@Override
	public void removeWriter(final Writer writer) {
		// do nothing
	}

	@Override
	public void reloadLoggers() {
		// do nothing
	}

	@Override
	public void setCurrentControllerID(final String name) {
		// do nothing
	}

	@Override
	public void store(final IToolchainStorage storage) {
		// do nothing
	}
}
