package cgsynt.termination;

import cgsynt.dfa.parity.operations.ParityCounterexample;
import cgsynt.interpol.IStatement;
import cgsynt.interpol.TraceGlobalVariables;
import cgsynt.interpol.TraceToInterpolants;
import cgsynt.interpol.VariableFactory;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.BasicIcfg;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IcfgEdgeFactory;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IcfgInternalTransition;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IcfgLocation;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.debugidentifiers.StringDebugIdentifier;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.managedscript.ManagedScript;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.HoareAnnotation;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.predicates.PredicateFactory;
import de.uni_freiburg.informatik.ultimate.util.datastructures.SerialProvider;

public class OmegaRefiner {
	private NestedWordAutomaton<IcfgInternalTransition, IPredicate> mOmega;
	private TraceGlobalVariables mGlobalVars;
	
	private ILogger mLogger;
	private ManagedScript mMScript;
	private VariableFactory mVf;
	private TraceToInterpolants mTTI;
	private PredicateFactory mOldPredicateFactory;
	private Script mScript;
	private IcfgEdgeFactory mEdgeFactory;
	
	public OmegaRefiner(TraceGlobalVariables globalVars, NestedWordAutomaton<IcfgInternalTransition, IPredicate> omega) {
		mGlobalVars = globalVars;
		mOmega = omega;
		
		mLogger = mGlobalVars.getLogger();
		mMScript = mGlobalVars.getManagedScript();
		mVf = mGlobalVars.getVariableFactory();
		mTTI = mGlobalVars.getTraceInterpolator();
		mOldPredicateFactory = mGlobalVars.getPredicateFactory();
		mScript = mMScript.getScript();
		mEdgeFactory = new IcfgEdgeFactory(new SerialProvider());
	}
	
	public void certifyCE(ParityCounterexample<IStatement, HoareAnnotation> ce) {
		BasicIcfg<IcfgLocation> icfg = new BasicIcfg<>("certify", mTTI.getCfgSmtToolkit(), IcfgLocation.class);
		
		IcfgLocation[] stemLocations = new IcfgLocation[ce.stemStates.size()];
		IcfgLocation[] loopLocations = new IcfgLocation[ce.loopStates.size() - 1];
		
		HoareAnnotation[] stemPredicates = new HoareAnnotation[ce.stemStates.size()];
		HoareAnnotation[] loopPredicates = new HoareAnnotation[ce.loopStates.size() - 1];
		
		ParityCounterexample<IStatement, HoareAnnotation> trace = ce.makeCopy();

		IcfgLocation start = new IcfgLocation(new StringDebugIdentifier("p1l0"), "p1");
		icfg.addLocation(start, true, false, true, false, false);
		HoareAnnotation startAnnot = mOldPredicateFactory.getNewHoareAnnotation(start, mTTI.getModifiableGlobalsTable());
		stemLocations[0] = start;
		stemPredicates[0] = startAnnot;
		trace.stemStates.pop();
		
		int stemCount = 1;
		while (!trace.stemStates.isEmpty()) {
			trace.stemStates.pop();
			
			IcfgLocation location = new IcfgLocation(new StringDebugIdentifier("p1l" + stemCount), "p1");
			icfg.addLocation(location, false, false, false, false, (stemCount == stemLocations.length - 1) ? true : false);
			stemLocations[stemCount] = location;
			
			//
			
			HoareAnnotation annot = mOldPredicateFactory.getNewHoareAnnotation(location, mTTI.getModifiableGlobalsTable());
			
			stemCount++;
		}
	}
}
