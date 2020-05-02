package uniolunisaar.adam.tests.logics.ctl;

import org.testng.annotations.Test;
import uniol.apt.adt.pn.PetriNet;
import uniol.apt.adt.pn.Place;
import uniolunisaar.adam.ds.logics.Closure;
import uniolunisaar.adam.ds.logics.ctl.CTLAtomicProposition;
import uniolunisaar.adam.ds.logics.ctl.CTLFormula;
import uniolunisaar.adam.ds.logics.ctl.CTLOperators;
import uniolunisaar.adam.ds.logics.ctl.ICTLFormula;

/**
 *
 * @author Manuel Gieseking
 */
@Test
public class TestCTL {

    @Test
    public void testClosure() {
        PetriNet net = new PetriNet();
        Place p = net.createPlace();

        ICTLFormula f = new CTLFormula(new CTLAtomicProposition(p), CTLOperators.Binary.OR, new CTLAtomicProposition(p));

        Closure c = f.getClosure();
        System.out.println(c.toString());
    }
}
