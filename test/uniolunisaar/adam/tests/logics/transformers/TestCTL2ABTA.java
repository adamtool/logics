package uniolunisaar.adam.tests.logics.transformers;

import java.util.Set;
import org.testng.annotations.Test;
import uniol.apt.adt.pn.PetriNet;
import uniol.apt.adt.pn.Place;
import uniolunisaar.adam.ds.abta.AlternatingBuchiTreeAutomaton;
import uniolunisaar.adam.ds.automata.NodeLabel;
import uniolunisaar.adam.ds.logics.ctl.CTLAtomicProposition;
import uniolunisaar.adam.ds.logics.ctl.CTLConstants;
import uniolunisaar.adam.ds.logics.ctl.CTLFormula;
import uniolunisaar.adam.ds.logics.ctl.CTLOperators;
import uniolunisaar.adam.ds.logics.ctl.ICTLFormula;
import uniolunisaar.adam.logic.transformers.ctl.CTL2AlternatingBuchiTreeAutomaton;

/**
 *
 * @author Manuel Gieseking
 */
@Test
public class TestCTL2ABTA {

    @Test
    public void firstTest() throws Exception {
        PetriNet net = new PetriNet();
        Place p = net.createPlace();
        CTLAtomicProposition ap = new CTLAtomicProposition(p);

        ICTLFormula f = new CTLFormula(new CTLAtomicProposition(p), CTLOperators.Binary.OR, new CTLAtomicProposition(p));
        AlternatingBuchiTreeAutomaton<Set<NodeLabel>> abta = CTL2AlternatingBuchiTreeAutomaton.transform(f, net);
//        System.out.println(abta.toString());

        f = new CTLFormula(new CTLConstants.False(), CTLOperators.Binary.AUD, new CTLFormula(CTLOperators.Unary.NEG, ap));
        abta = CTL2AlternatingBuchiTreeAutomaton.transform(f, net);
//        System.out.println(abta.toString());

        f = new CTLFormula(CTLOperators.Unary.EX, f);
        abta = CTL2AlternatingBuchiTreeAutomaton.transform(f, net);
//        System.out.println(abta.toString());
        
        f = new CTLFormula(CTLOperators.Unary.AX, new CTLFormula(new CTLConstants.True(), CTLOperators.Binary.EU, ap));
        abta = CTL2AlternatingBuchiTreeAutomaton.transform(f, net);
//        System.out.println(abta.toString());
        
        f = new CTLFormula(new CTLConstants.False(), CTLOperators.Binary.AUD, new CTLFormula(new CTLConstants.True(), CTLOperators.Binary.AU, ap));
        abta = CTL2AlternatingBuchiTreeAutomaton.transform(f, net);
        System.out.println(abta.toString());
    }
}
