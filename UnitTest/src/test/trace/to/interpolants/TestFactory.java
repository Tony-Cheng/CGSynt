package test.trace.to.interpolants;

import de.uni_freiburg.informatik.ultimate.boogie.type.BoogiePrimitiveType;
import de.uni_freiburg.informatik.ultimate.boogie.type.BoogieType;
import de.uni_freiburg.informatik.ultimate.core.model.models.IBoogieType;
import de.uni_freiburg.informatik.ultimate.logic.ApplicationTerm;
import de.uni_freiburg.informatik.ultimate.logic.Sort;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.boogie.BoogieNonOldVar;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.boogie.BoogieOldVar;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.managedscript.ManagedScript;

public class TestFactory {
	private ManagedScript mManagedScript;
	
	public TestFactory(ManagedScript managedScript) {
		mManagedScript = managedScript;
	}
	
	public Tuple<BoogieOldVar, BoogieNonOldVar> makeVar(String name, String sort){
		mManagedScript.getScript().declareFun(name, new Sort[0], mManagedScript.getScript().sort(sort));
		mManagedScript.getScript().declareFun(name + "'", new Sort[0], mManagedScript.getScript().sort(sort));
		
		BoogiePrimitiveType type = null;
		if (sort.equals("Int"))
			type = BoogieType.TYPE_INT;
		else if (sort.equals("Bool"))
			type = BoogieType.TYPE_BOOL;
		
		BoogieOldVar old = new BoogieOldVar(name, (IBoogieType) type,
				mManagedScript.getScript().variable(name, mManagedScript.getScript().sort(sort)),
				(ApplicationTerm) mManagedScript.getScript().term(name),
				(ApplicationTerm) mManagedScript.getScript().term(name + "'"));
		BoogieNonOldVar nonOld = new BoogieNonOldVar(name + "'", (IBoogieType) type,
				mManagedScript.getScript().variable(name + "'", mManagedScript.getScript().sort(sort)),
				(ApplicationTerm) mManagedScript.getScript().term(name),
				(ApplicationTerm) mManagedScript.getScript().term(name + "'"), old);
		
		old.setNonOldVar(nonOld);
		
		return new Tuple<BoogieOldVar, BoogieNonOldVar>(old, nonOld);
	}
}

class Tuple<X, Y>{
	private X mX;
	private Y mY;
	
	public Tuple(X x, Y y) {
		mX = x;
		mY = y;
	}
	
	public X getOne() {
		return mX;
	}
	
	public Y getTwo() {
		return mY;
	}
}