import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.managedscript.ManagedScript;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.BasicPredicate;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.BasicPredicateFactory;
import de.uni_freiburg.informatik.ultimate.test.mocks.UltimateMocks;

public class TestPredicates {
	public static void main(String[] args) {
		IUltimateServiceProvider mock = UltimateMocks.createUltimateServiceProviderMock();
		Script script = new SMTInterpol(new DefaultLogger());
		
		ManagedScript ms = new ManagedScript(mock);
		
		
		BasicPredicateFactory pf = new BasicPredicateFactory(mock, );
		
		BasicPredicate b = new BasicPredicate(0, new String[0], null, null, null);
	}
}
