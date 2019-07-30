package termination;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cgsynt.interpol.IStatement;
import cgsynt.interpol.ScriptAssignmentStatement;
import cgsynt.interpol.TraceGlobalVariables;
import cgsynt.interpol.VariableFactory;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.lassoranker.DefaultLassoRankerPreferences;
import de.uni_freiburg.informatik.ultimate.lassoranker.Lasso;
import de.uni_freiburg.informatik.ultimate.lassoranker.LinearInequality;
import de.uni_freiburg.informatik.ultimate.lassoranker.LinearTransition;
import de.uni_freiburg.informatik.ultimate.lassoranker.termination.DefaultTerminationAnalysisSettings;
import de.uni_freiburg.informatik.ultimate.lassoranker.termination.TerminationAnalysisSettings;
import de.uni_freiburg.informatik.ultimate.lassoranker.termination.TerminationArgumentSynthesizer;
import de.uni_freiburg.informatik.ultimate.lassoranker.termination.templates.AffineTemplate;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Term;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.boogie.BoogieNonOldVar;
import de.uni_freiburg.informatik.ultimate.test.mocks.UltimateMocks;

public class TestTerminationArgument {

	public static void main(String[] args) throws Exception {
		IUltimateServiceProvider mock = UltimateMocks.createUltimateServiceProviderMock();
		DefaultTerminationAnalysisSettings termSettings = new DefaultTerminationAnalysisSettings();
		TerminationAnalysisSettings termAnalysisSettings = new TerminationAnalysisSettings(termSettings);
		DefaultLassoRankerPreferences lassoPrefs = new AnotherLassoRankerPref();
		AffineTemplate template = new AffineTemplate();
		Set<Term> arrayIndexSupportingInvariants = new HashSet<>();

		TraceGlobalVariables globalVars = new TraceGlobalVariables();
		VariableFactory vf = globalVars.getVariableFactory();
		BoogieNonOldVar x = vf.constructVariable("x", VariableFactory.INT);
		Script script = globalVars.getManagedScript().getScript();
		LinearInequality xppIneq = LinearInequality.fromTerm(script.term("+", x.getTerm(), script.numeral("1")));
		List<LinearInequality> singleStatement = new ArrayList<>();
		singleStatement.add(xppIneq);
		List<List<LinearInequality>> singleExample = new ArrayList<>();
		singleExample.add(singleStatement);
		LinearTransition loop = new LinearTransition(singleExample, new HashMap<>(), new HashMap<>());
		Lasso lasso = new Lasso(null, null);
		TerminationArgumentSynthesizer synthesizer = new TerminationArgumentSynthesizer(lasso, template, lassoPrefs,
				termAnalysisSettings, arrayIndexSupportingInvariants, mock);
		System.out.println(synthesizer.synthesisSuccessful());
	}
}

class AnotherLassoRankerPref extends DefaultLassoRankerPreferences {
	@Override
	public boolean isExternalSolver() {
		return false;
	}
}