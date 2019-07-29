package test.termination;

import java.util.Map;

import de.uni_freiburg.informatik.ultimate.core.coreplugin.services.PreferenceLayer;
import de.uni_freiburg.informatik.ultimate.core.coreplugin.services.ToolchainStorage;
import de.uni_freiburg.informatik.ultimate.core.model.IServiceFactory;
import de.uni_freiburg.informatik.ultimate.core.model.preferences.IPreferenceProvider;
import de.uni_freiburg.informatik.ultimate.core.model.services.IBacktranslationService;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILoggingService;
import de.uni_freiburg.informatik.ultimate.core.model.services.IProgressMonitorService;
import de.uni_freiburg.informatik.ultimate.core.model.services.IResultService;
import de.uni_freiburg.informatik.ultimate.core.model.services.IService;
import de.uni_freiburg.informatik.ultimate.core.model.services.IToolchainStorage;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger.LogLevel;

public class CustomServiceProvider implements IUltimateServiceProvider{

	private final LogLevel mDefaultLevel;
	private final IProgressMonitorService mProgressMonitorService = new CustomProgramMonitor();
	private final IToolchainStorage mStorage;
	private Map<String, PreferenceLayer> mPreferenceLayers;

	public CustomServiceProvider(final LogLevel defaultLevel) {
		mDefaultLevel = defaultLevel;
		mStorage = new ToolchainStorage();
	}

	@Override
	public IBacktranslationService getBacktranslationService() {
		return new CustomBacktranslationService();
	}

	@Override
	public ILoggingService getLoggingService() {
		return new CustomLoggingServiceMock(mDefaultLevel);
	}

	@Override
	public IResultService getResultService() {
		return new CustomResultServiceMock();
	}

	@Override
	public IProgressMonitorService getProgressMonitorService() {
		return mProgressMonitorService;
	}

	@Override
	public <T extends IService, K extends IServiceFactory<T>> T getServiceInstance(final Class<K> serviceType) {
		// never find the matching service
		return null;
	}

	@Override
	public IPreferenceProvider getPreferenceProvider(final String pluginId) {
		return mPreferenceLayers.get(pluginId);
	}
	
	public void addPreferenceProvider(PreferenceLayer prefLayer, String preferenceId) {
		mPreferenceLayers.put(preferenceId, prefLayer);
	}

	@Override
	public IUltimateServiceProvider registerPreferenceLayer(final Class<?> creator, final String... pluginIds) {
		throw new UnsupportedOperationException("Not yet supported");
	}

	@Override
	public IUltimateServiceProvider registerDefaultPreferenceLayer(final Class<?> creator, final String... pluginIds) {
		throw new UnsupportedOperationException("Not yet supported");
	}

	@Override
	public IToolchainStorage getStorage() {
		return mStorage;
	}

}
