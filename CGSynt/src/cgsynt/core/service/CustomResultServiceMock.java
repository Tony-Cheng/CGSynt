package cgsynt.core.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import de.uni_freiburg.informatik.ultimate.core.model.results.IResult;
import de.uni_freiburg.informatik.ultimate.core.model.services.IResultService;

public class CustomResultServiceMock implements IResultService{
	@Override
	public Map<String, List<IResult>> getResults() {
		return Collections.emptyMap();
	}

	@Override
	public void reportResult(final String pluginId, final IResult result) {
		// do nothing
	}

	@Override
	public void registerTransformer(final String name, final Function<IResult, IResult> resultTransformer) {
		// do nothing
	}
}
