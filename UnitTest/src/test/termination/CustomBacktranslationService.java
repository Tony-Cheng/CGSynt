package test.termination;

import java.util.Collections;
import java.util.List;

import de.uni_freiburg.informatik.ultimate.core.model.services.IBacktranslationService;
import de.uni_freiburg.informatik.ultimate.core.model.translation.IBacktranslatedCFG;
import de.uni_freiburg.informatik.ultimate.core.model.translation.IProgramExecution;
import de.uni_freiburg.informatik.ultimate.core.model.translation.ITranslator;
import de.uni_freiburg.informatik.ultimate.core.model.translation.IProgramExecution.ProgramState;

public class CustomBacktranslationService implements IBacktranslationService{
	@Override
	public <STE, TTE, SE, TE, SVL, TVL> void addTranslator(final ITranslator<STE, TTE, SE, TE, SVL, TVL> translator) {
		// does nothing
	}

	@Override
	public <SE, TE> TE translateExpression(final SE expression, final Class<SE> sourceExpressionClass) {
		return null;
	}

	@Override
	public <SE> String translateExpressionToString(final SE expression, final Class<SE> clazz) {
		return "";
	}

	@Override
	public <STE> List<?> translateTrace(final List<STE> trace, final Class<STE> clazz) {
		return Collections.emptyList();
	}

	@Override
	public <STE> List<String> translateTraceToHumanReadableString(final List<STE> trace, final Class<STE> clazz) {
		return Collections.emptyList();
	}

	@Override
	public <STE, SE> IProgramExecution<?, ?>
			translateProgramExecution(final IProgramExecution<STE, SE> programExecution) {
		return new CustomProgramExecutionMock<>(null, null);
	}
	
	@Override
	public <SE> ProgramState<?> translateProgramState(final ProgramState<SE> programState) {
		return null;
	}

	@Override
	public <SE> String translateProgramStateToString(ProgramState<SE> programState) {
		return null;
	}

	@Override
	public <STE, SE> IBacktranslatedCFG<?, ?> translateCFG(final IBacktranslatedCFG<?, STE> cfg) {
		return null;
	}

	@Override
	public IBacktranslationService getTranslationServiceCopy() {
		return this;
	}
}
