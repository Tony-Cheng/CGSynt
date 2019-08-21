package cgsynt.termination;

import java.util.ArrayList;
import java.util.Collections;

import cgsynt.dfa.parity.intersect.operations.DfaParityCounterexample;
import cgsynt.interpol.TraceGlobalVariables;
import cgsynt.interpol.TraceToInterpolants;
import cgsynt.tree.parity.IParityState;
import de.uni_freiburg.informatik.ultimate.automata.AutomatonDefinitionPrinter.Format;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedRun;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWord;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.buchi.NestedLassoRun;
import de.uni_freiburg.informatik.ultimate.core.model.models.Payload;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.BasicIcfg;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.CfgSmtToolkit;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IcfgEdgeFactory;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IcfgInternalTransition;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IcfgLocation;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.debugidentifiers.StringDebugIdentifier;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.SmtUtils.SimplificationTechnique;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.SmtUtils.XnfConversionTechnique;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.managedscript.ManagedScript;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.taskidentifier.SubtaskFileIdentifier;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.taskidentifier.TaskIdentifier;
import de.uni_freiburg.informatik.ultimate.plugins.generator.buchiautomizer.BinaryStatePredicateManager;
import de.uni_freiburg.informatik.ultimate.plugins.generator.buchiautomizer.BuchiCegarLoopBenchmarkGenerator;
import de.uni_freiburg.informatik.ultimate.plugins.generator.buchiautomizer.LassoCheck;
import de.uni_freiburg.informatik.ultimate.plugins.generator.buchiautomizer.RankVarConstructor;
import de.uni_freiburg.informatik.ultimate.plugins.generator.buchiautomizer.RefineBuchi;
import de.uni_freiburg.informatik.ultimate.plugins.generator.buchiautomizer.preferences.BuchiAutomizerPreferenceInitializer.NcsbImplementation;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.CegarAbsIntRunner;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.HoareAnnotation;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.PathProgramCache;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.PredicateFactoryForInterpolantAutomata;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.PredicateFactoryRefinement;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.TraceAbstraction;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.interpolantautomata.builders.InterpolantAutomatonBuilderFactory;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.predicates.PredicateFactory;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.preferences.TAPreferences;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.preferences.TraceAbstractionPreferenceInitializer;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.preferences.TraceAbstractionPreferenceInitializer.InterpolationTechnique;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.tracehandling.RefinementStrategyFactory;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.tracehandling.TaCheckAndRefinementPreferences;
import de.uni_freiburg.informatik.ultimate.util.datastructures.SerialProvider;

public class OmegaRefiner {
	public static final SerialProvider SERIAL_PROVIDER = new SerialProvider();
	
	private TraceGlobalVariables mGlobalVars;
	
	private ILogger mLogger;
	private ManagedScript mMScript;
	private TraceToInterpolants mTTI;
	private PredicateFactory mOldPredicateFactory, mPredicateFactory;
	private IcfgEdgeFactory mEdgeFactory;
	private IUltimateServiceProvider mServiceProvider;
	private CfgSmtToolkit mCsToolkitWithRankVars;
	private BinaryStatePredicateManager mBspm;
	private BuchiCegarLoopBenchmarkGenerator mBenchmarker;
	private RankVarConstructor mRankVarConstructor;
	
	public OmegaRefiner(TraceGlobalVariables globalVars) {
		mGlobalVars = globalVars;
		
		mServiceProvider = globalVars.getService();
		mLogger = mGlobalVars.getLogger();
		mMScript = mGlobalVars.getManagedScript();
		mTTI = mGlobalVars.getTraceInterpolator();
		mOldPredicateFactory = mGlobalVars.getPredicateFactory();
		mEdgeFactory = new IcfgEdgeFactory(SERIAL_PROVIDER);
		
		mRankVarConstructor = new RankVarConstructor(mTTI.getCfgSmtToolkit());
		mPredicateFactory = new PredicateFactory(mServiceProvider, mMScript, 
				mRankVarConstructor.getCsToolkitWithRankVariables().getSymbolTable(),
				SimplificationTechnique.NONE, XnfConversionTechnique.BDD_BASED);
		
		mCsToolkitWithRankVars = mRankVarConstructor.getCsToolkitWithRankVariables();
		
		mBspm = new BinaryStatePredicateManager(mCsToolkitWithRankVars,
				mPredicateFactory, mRankVarConstructor.getUnseededVariable(), 
				mRankVarConstructor.getOldRankVariables(), mServiceProvider,
				SimplificationTechnique.NONE, XnfConversionTechnique.BDD_BASED);
		
		mBenchmarker = new BuchiCegarLoopBenchmarkGenerator();
	}
	
	public NestedWordAutomaton<IcfgInternalTransition, IPredicate> certifyCE(
			DfaParityCounterexample<IcfgInternalTransition, IPredicate, IParityState> ce,
			NestedWordAutomaton<IcfgInternalTransition, IPredicate> omega,
			int curNumOmegaRefinementIterations) throws Exception {
		
		BasicIcfg<IcfgLocation> icfg = new BasicIcfg<>("certify", mTTI.getCfgSmtToolkit(), IcfgLocation.class);
		DfaParityCounterexample<IcfgInternalTransition, IPredicate, IParityState> trace = ce.makeCopy();
		
		TransitionStatePackage[] packages = getTransitionStatePackages(trace, icfg);
		
		////////////////////
		// Set up stem
		IcfgInternalTransition[] stemTransitions = packages[0].getTransitions();
		HoareAnnotation[] stemPredicates = packages[0].getAnnotations();
		
		int[] stemNestingRelation = new int[stemTransitions.length];
		for (int i = 0; i < stemTransitions.length; i++) 
			stemNestingRelation[i] = NestedWord.INTERNAL_POSITION;
		
		ArrayList<IPredicate> stemStates = new ArrayList<>();
		for (int i = 0; i < stemPredicates.length; i++)
			stemStates.add(stemPredicates[i]);
		
		NestedWord<IcfgInternalTransition> stemWord = new NestedWord<IcfgInternalTransition>(stemTransitions, stemNestingRelation);
		NestedRun<IcfgInternalTransition, IPredicate> stem = new NestedRun<>(stemWord, stemStates);
		
		////////////////////
		// Set up Loop
		IcfgInternalTransition[] loopTransitions = packages[1].getTransitions();
		HoareAnnotation[] loopPredicates = packages[1].getAnnotations();
		
		int[] loopNestingRelation = new int[loopTransitions.length];
		for (int i = 0; i < loopTransitions.length; i++)
			loopNestingRelation[i] = NestedWord.INTERNAL_POSITION;
		
		ArrayList<IPredicate> loopStates = new ArrayList<>();
		for (int i = 0; i < loopPredicates.length; i++)
			loopStates.add(loopPredicates[i]);
		
		NestedWord<IcfgInternalTransition> loopWord = new NestedWord<IcfgInternalTransition>(loopTransitions, loopNestingRelation);
		NestedRun<IcfgInternalTransition, IPredicate> loop = new NestedRun<>(loopWord, loopStates);
		
		////////////////////
		// Set up Lasso Run
		NestedLassoRun<IcfgInternalTransition, IPredicate> counterexample = new NestedLassoRun<>(stem, loop);
		
		TaskIdentifier taskIdentifier = new SubtaskFileIdentifier(null, icfg.getIdentifier());
		
		LassoCheck<IcfgInternalTransition> check = new LassoCheck<>(
				InterpolationTechnique.Craig_NestedInterpolation,
				mTTI.getCfgSmtToolkit(), 
				mPredicateFactory,
				mCsToolkitWithRankVars.getSymbolTable(),
				mTTI.getCfgSmtToolkit().getModifiableGlobalsTable(),
				mTTI.getCfgSmtToolkit().getSmtSymbols(),
				mBspm, counterexample,
				"LassoCheck", mServiceProvider,
				SimplificationTechnique.NONE, XnfConversionTechnique.BOTTOM_UP_WITH_LOCAL_SIMPLIFICATION,
				setUpRefinementFactory(icfg),
				omega,
				taskIdentifier, mBenchmarker);
		
		//Refinement portion
		PredicateFactoryForInterpolantAutomata defaultStateFactory = 
				new PredicateFactoryForInterpolantAutomata(this.mCsToolkitWithRankVars.getManagedScript(), 
						this.mPredicateFactory, false);
		
		PredicateFactoryRefinement stateFactoryForRefinement = new PredicateFactoryRefinement(this.mServiceProvider,
				mCsToolkitWithRankVars.getManagedScript(), this.mPredicateFactory, false, Collections.emptySet());
		
		RefineBuchi<IcfgInternalTransition> refiner = new RefineBuchi<>(
				icfg, this.mCsToolkitWithRankVars, this.mPredicateFactory,
				false, true, defaultStateFactory, stateFactoryForRefinement,
				true, "", Format.ATS_NUMERATE, 
				InterpolationTechnique.ForwardPredicates, this.mServiceProvider,
				mLogger, SimplificationTechnique.SIMPLIFY_BDD_FIRST_ORDER,
				XnfConversionTechnique.BDD_BASED, NcsbImplementation.ORIGINAL);
		
		// Some parameters still need to be filled out
		// Examples of how to call refineBuchi can be found in BuchiCegarLoop.java
		
		//return refiner.refineBuchi(omega, counterexample, curNumOmegaRefinementIterations, setting, bspm, modifiableGlobalsTable, interpolation, benchmarkGenerator, complementationConstruction);
		return null;
	}
	
	private RefinementStrategyFactory<IcfgInternalTransition> setUpRefinementFactory(BasicIcfg<IcfgLocation> icfg){
		TAPreferences taPrefs = new TAPreferences(mServiceProvider);
		
		PathProgramCache<IcfgInternalTransition> pathProgramCache = new PathProgramCache<>(mLogger);
		
		CegarAbsIntRunner<IcfgInternalTransition> absIntRunner =
				new CegarAbsIntRunner<>(mServiceProvider, mBenchmarker, icfg, SimplificationTechnique.NONE,
						XnfConversionTechnique.BDD_BASED, mTTI.getCfgSmtToolkit(), pathProgramCache, taPrefs);
		
		PredicateFactoryForInterpolantAutomata stateFactory = new PredicateFactoryForInterpolantAutomata(mCsToolkitWithRankVars.getManagedScript(),
				mPredicateFactory, taPrefs.computeHoareAnnotation());
		
		InterpolantAutomatonBuilderFactory<IcfgInternalTransition> interpolantAutomatonBuilderFactory =
				new InterpolantAutomatonBuilderFactory<>(mServiceProvider, mTTI.getCfgSmtToolkit(), stateFactory,
						icfg, absIntRunner, taPrefs, InterpolationTechnique.Craig_NestedInterpolation, taPrefs.interpolantAutomaton(),
						mBenchmarker);
		
		TaCheckAndRefinementPreferences<IcfgInternalTransition> taCheckAndRefinementPrefs =
				new TaCheckAndRefinementPreferences<>(mServiceProvider, taPrefs, InterpolationTechnique.Craig_NestedInterpolation,
						SimplificationTechnique.NONE, XnfConversionTechnique.BDD_BASED,
						mTTI.getCfgSmtToolkit(), mPredicateFactory, icfg,
						interpolantAutomatonBuilderFactory);
		
		RefinementStrategyFactory<IcfgInternalTransition> refinementFactory = new RefinementStrategyFactory<>(
				mLogger, mServiceProvider, taPrefs, taCheckAndRefinementPrefs, absIntRunner, icfg, mPredicateFactory,
				pathProgramCache);
		
		return refinementFactory;
	}
	
	private TransitionStatePackage[] getTransitionStatePackages(DfaParityCounterexample<IcfgInternalTransition, IPredicate, IParityState> trace, BasicIcfg<IcfgLocation> icfg) {
		
		int stemStatesSize = trace.stemStates.size(); 
		
		IcfgLocation prevLocation;
		
		IcfgInternalTransition[] stemTransitions = new IcfgInternalTransition[stemStatesSize - 1];
		HoareAnnotation[] stemPredicates = new HoareAnnotation[stemStatesSize];
		
		IcfgInternalTransition[] loopTransitions = new IcfgInternalTransition[trace.loopStates.size()];
		HoareAnnotation[] loopPredicates = new HoareAnnotation[trace.loopStates.size() + 1];

		// Add the first location
		prevLocation = new IcfgLocation(new StringDebugIdentifier("0"/*"p1l0"*/), "p1");
		icfg.addLocation(prevLocation, true, false, true, false, false);
		HoareAnnotation startAnnot = mOldPredicateFactory.getNewHoareAnnotation(prevLocation, mTTI.getModifiableGlobalsTable());
		stemPredicates[0] = startAnnot;
		trace.stemStates.pop();
		
		int stemCount = 1;
		while (!trace.stemStates.isEmpty()) {
			trace.stemStates.pop();
			
			IcfgLocation curLocation = new IcfgLocation(new StringDebugIdentifier("0"/*"p1l" + stemCount*/), "p1");
			icfg.addLocation(curLocation, false, false, false, false, (stemCount == stemStatesSize - 1) ? true : false);
			HoareAnnotation annot = mOldPredicateFactory.getNewHoareAnnotation(curLocation, mTTI.getModifiableGlobalsTable());
			
			stemTransitions[stemCount - 1] = mEdgeFactory.createInternalTransition(prevLocation, curLocation, new Payload(), trace.stemTransitions.pop().getTransformula());
			stemPredicates[stemCount] = annot;
			
			prevLocation = curLocation;
			stemCount++;
		}
		
		// Create loop transitions
		loopPredicates[0] = stemPredicates[stemPredicates.length - 1]; 
		IcfgLocation loopLocation = prevLocation;
		trace.loopStates.pop();
		
		int loopCount = 1;
		while (!trace.loopStates.empty()) {
			trace.loopStates.pop();
			
			IcfgLocation curLocation = new IcfgLocation(new StringDebugIdentifier("0"/*"p1l" + (stemCount + loopCount)*/), "p1");
			icfg.addLocation(curLocation, false, false, false, false, false);
			HoareAnnotation annot = mOldPredicateFactory.getNewHoareAnnotation(curLocation, mTTI.getModifiableGlobalsTable());
			
			loopTransitions[loopCount - 1] = mEdgeFactory.createInternalTransition(prevLocation, curLocation, new Payload(), trace.loopTransitions.pop().getTransformula()); 
			loopPredicates[loopCount] = annot;
			
			prevLocation = curLocation;
			
			loopCount++;
		}
		
		// Add final transition
		loopTransitions[loopCount - 1] = mEdgeFactory.createInternalTransition(prevLocation, loopLocation, new Payload(), trace.loopTransitions.pop().getTransformula());
		loopPredicates[loopCount] = loopPredicates[0];
		
		// Add last transition
		TransitionStatePackage[] packages = new TransitionStatePackage[2];
		packages[0] = new TransitionStatePackage(stemTransitions, stemPredicates);
		packages[1] = new TransitionStatePackage(loopTransitions, loopPredicates);
		
		return packages;
	}
	
	class TransitionStatePackage{
		private IcfgInternalTransition[] mTransitions;
		private HoareAnnotation[] mAnnotations;
		
		public TransitionStatePackage(IcfgInternalTransition[] transitions, HoareAnnotation[] annotations) {
			mTransitions = transitions;
			mAnnotations = annotations;
		}
		
		public IcfgInternalTransition[] getTransitions() {
			return mTransitions;
		}
		
		public HoareAnnotation[] getAnnotations() {
			return mAnnotations;
		}
	}
}
