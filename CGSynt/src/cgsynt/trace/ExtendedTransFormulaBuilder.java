package cgsynt.trace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.uni_freiburg.informatik.ultimate.logic.ApplicationTerm;
import de.uni_freiburg.informatik.ultimate.logic.Term;
import de.uni_freiburg.informatik.ultimate.logic.TermVariable;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.IIcfgSymbolTable;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.transitions.TransFormulaBuilder;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.transitions.UnmodifiableTransFormula;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.transitions.UnmodifiableTransFormula.Infeasibility;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.variables.IProgramConst;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.variables.IProgramVar;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.ConstantFinder;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.SmtUtils;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.Substitution;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.managedscript.ManagedScript;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.TermVarsProc;

public class ExtendedTransFormulaBuilder extends TransFormulaBuilder{

	public ExtendedTransFormulaBuilder(Map<IProgramVar, TermVariable> inVars, Map<IProgramVar, TermVariable> outVars,
			boolean emptyNonTheoryConsts, Set<IProgramConst> nonTheoryConsts, boolean emptyBranchEncoders,
			Collection<TermVariable> branchEncoders, boolean emptyAuxVars) {
		super(inVars, outVars, emptyNonTheoryConsts, nonTheoryConsts, emptyBranchEncoders, branchEncoders, emptyAuxVars);
	}
	
	public static UnmodifiableTransFormula constructAssumption(final List<? extends IProgramVar> lhs,
			final List<Term> rhs, final IIcfgSymbolTable symbolTable, final ManagedScript mgdScript, final String type) {
		return constructInequality(lhs, rhs, symbolTable, mgdScript, type, true);
	}
	
	private static UnmodifiableTransFormula constructInequality(final List<? extends IProgramVar> lhs,
			final List<Term> rhs, final IIcfgSymbolTable symbolTable, final ManagedScript mgdScript, final String type, 
			final boolean lhsAreAlsoInVars) {
		if (lhs.size() != rhs.size()) {
			throw new IllegalArgumentException("different number of argument on LHS and RHS");
		}
		final Set<IProgramVar> rhsPvs = new HashSet<>();
		for (int i = 0; i < rhs.size(); i++) {
			final Set<ApplicationTerm> consts = new ConstantFinder().findConstants(rhs.get(i), false);
			if (!consts.isEmpty()) {
				throw new UnsupportedOperationException("constants not yet supported");
			}

			final TermVarsProc tvp = TermVarsProc.computeTermVarsProc(rhs.get(i), mgdScript.getScript(), symbolTable);
			rhsPvs.addAll(tvp.getVars());
		}

		final TransFormulaBuilder tfb = new TransFormulaBuilder(null, null, true, null, true, null, true);
		final Map<Term, Term> substitutionMapping = new HashMap<>();

		for (final IProgramVar pv : rhsPvs) {
			final TermVariable freshTv = mgdScript.constructFreshTermVariable(pv.getGloballyUniqueId(),
					pv.getTermVariable().getSort());
			substitutionMapping.put(pv.getTermVariable(), freshTv);
			tfb.addInVar(pv, freshTv);
			tfb.addOutVar(pv, freshTv);
		}

		final List<Term> conjuncts = new ArrayList<>();
		final Substitution subst = new Substitution(mgdScript.getScript(), substitutionMapping);
		for (int i = 0; i < lhs.size(); i++) {
			final IProgramVar pv = lhs.get(i);
			final TermVariable freshTv = mgdScript.constructFreshTermVariable(pv.getGloballyUniqueId(),
					pv.getTermVariable().getSort());
			substitutionMapping.put(pv.getTermVariable(), freshTv);
			tfb.addOutVar(pv, freshTv);
			if (lhsAreAlsoInVars) {
				tfb.addInVar(pv, freshTv);
			}
			final Term renamedRightHandSide = subst.transform(rhs.get(i));
			conjuncts.add(mgdScript.getScript().term(type, freshTv, renamedRightHandSide));
		}

		final Term conjunction = SmtUtils.and(mgdScript.getScript(), conjuncts);
		tfb.setFormula(conjunction);
		// an assignment is always feasible
		tfb.setInfeasibility(Infeasibility.UNPROVEABLE);
		return tfb.finishConstruction(mgdScript);
	}

}
