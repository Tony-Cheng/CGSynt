package test.dfa.to.trace;

import java.util.HashSet;
import java.util.Set;

import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.VpAlphabet;
import de.uni_freiburg.informatik.ultimate.automata.statefactory.StringFactory;
import usra.trace.Formula;
import usra.trace.FormulaFactory;
import usra.trace.Token;

public class ConstructDFA {

	public static NestedWordAutomaton<Formula, String> dfa1(AutomataLibraryServices service, Token x, Token y) {
		// (x:=0)(y:=0)(x++)*(x==-1)
		Formula form1 = FormulaFactory.assign(x, 0);
		Formula form2 = FormulaFactory.assign(y, 0);
		Formula form3 = FormulaFactory.assign(x, FormulaFactory.addition(x, 1));
		Formula form4 = FormulaFactory.assume(x, -1);

		Set<Formula> letters = new HashSet<>();
		letters.add(form1);
		letters.add(form2);
		letters.add(form3);
		letters.add(form4);

		VpAlphabet<Formula> alpha = new VpAlphabet<>(letters);

		NestedWordAutomaton<Formula, String> nwa = new NestedWordAutomaton<>(service, alpha, new StringFactory());

		nwa.addState(true, false, "q0");
		nwa.addState(false, false, "q1");
		nwa.addState(false, false, "q2");
		nwa.addState(false, false, "q3");
		nwa.addState(false, true, "q4");


		nwa.addInternalTransition("q0", form1, "q1");
		nwa.addInternalTransition("q1", form2, "q2");
		nwa.addInternalTransition("q2", form3, "q3");
		nwa.addInternalTransition("q3", form3, "q3");
		nwa.addInternalTransition("q3", form4, "q4");

		return nwa;

	}
}
