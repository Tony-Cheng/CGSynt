package test.termination;

import org.junit.jupiter.api.Test;

import cgsynt.interpol.TraceGlobalVariables;
import cgsynt.interpol.TraceToInterpolants;
import cgsynt.interpol.VariableFactory;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.CfgSmtToolkit;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IcfgInternalTransition;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.SmtUtils.SimplificationTechnique;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.SmtUtils.XnfConversionTechnique;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.managedscript.ManagedScript;
import de.uni_freiburg.informatik.ultimate.plugins.generator.buchiautomizer.BinaryStatePredicateManager;
import de.uni_freiburg.informatik.ultimate.plugins.generator.buchiautomizer.LassoCheck;
import de.uni_freiburg.informatik.ultimate.plugins.generator.buchiautomizer.RankVarConstructor;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.predicates.PredicateFactory;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.preferences.TraceAbstractionPreferenceInitializer.InterpolationTechnique;

public class TestLassoCheck {
	
	@Test
	public void test() {
		TraceGlobalVariables globalVars = new TraceGlobalVariables();
		
		ManagedScript mScript = globalVars.getManagedScript();
		IUltimateServiceProvider serviceProvider = globalVars.getService();
		VariableFactory vf = globalVars.getVariableFactory();
		
		TraceToInterpolants tti = new TraceToInterpolants(mScript, serviceProvider, vf.getSymbolTable());
		
		RankVarConstructor rankVarConstructor = new RankVarConstructor(tti.getCfgSmtToolkit());
		
		PredicateFactory predicateFactory = new PredicateFactory(serviceProvider, mScript, 
				rankVarConstructor.getCsToolkitWithRankVariables().getSymbolTable(),
				SimplificationTechnique.NONE, XnfConversionTechnique.BDD_BASED);
		
		CfgSmtToolkit csToolkitWithRankVars = rankVarConstructor.getCsToolkitWithRankVariables();
		
		BinaryStatePredicateManager bspm = new BinaryStatePredicateManager(csToolkitWithRankVars,
				predicateFactory, rankVarConstructor.getUnseededVariable(), 
				rankVarConstructor.getOldRankVariables(), serviceProvider,
				SimplificationTechnique.NONE, XnfConversionTechnique.BDD_BASED);
		
		//IcfgEdgeFactory edgeFactory = new IcfgEdgeFactory(new SerialProvider());
		
		LassoCheck<IcfgInternalTransition> check = new LassoCheck<>(
				InterpolationTechnique.Craig_NestedInterpolation,
				tti.getCfgSmtToolkit(), 
				predicateFactory,
				csToolkitWithRankVars.getSymbolTable(),
				tti.getCfgSmtToolkit().getModifiableGlobalsTable(),
				tti.getCfgSmtToolkit().getSmtSymbols(),
				bspm, /*COUNTEREXAMPLE*/, 
				"Test", serviceProvider,
				SimplificationTechnique.NONE, XnfConversionTechnique.BDD_BASED,
				);
	}
}
