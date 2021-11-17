package uniolunisaar.adam.tests.transformers;

import java.util.Set;
import org.testng.annotations.Test;
import uniol.apt.adt.pn.PetriNet;
import uniol.apt.adt.pn.Place;
import uniol.apt.adt.pn.Transition;
import uniolunisaar.adam.ds.abta.AlternatingBuchiTreeAutomaton;
import uniolunisaar.adam.ds.automata.NodeLabel;
import uniolunisaar.adam.ds.logics.ctl.CTLAtomicProposition;
import uniolunisaar.adam.ds.logics.ctl.CTLConstants;
import uniolunisaar.adam.ds.logics.ctl.CTLFormula;
import uniolunisaar.adam.ds.logics.ctl.CTLFormulaBinary;
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

    @Test
    public void testLectureHall() throws Exception {
        PetriNet net = new PetriNet();
        Place yard = net.createPlace("y");
        Transition emergency = net.createTransition("em");
        CTLAtomicProposition y = new CTLAtomicProposition(yard);
        CTLAtomicProposition em = new CTLAtomicProposition(emergency);

        // want to check "𝔸 AG(emergency -> EF yard)"
        ICTLFormula f = new CTLFormula(CTLOperators.Unary.AG, new CTLFormula(em, CTLOperators.Binary.IMP, new CTLFormula(CTLOperators.Unary.EF, y)));
        // transformed is this "𝔸 E(false U' \neg emergency v E(true U yard))
//        ICTLFormula ftrans = new CTLFormula(new CTLConstants.False(), CTLOperators.Binary.EUD, new CTLFormula(new CTLFormula(CTLOperators.Unary.NEG, em), CTLOperators.Binary.OR,
//                new CTLFormula(new CTLConstants.True(), CTLOperators.Binary.EU, y)));        
        // todo: don't I have to take the negation?
        // negation: "𝔸 \neg AG(emergency -> EF yard) = E(true U emergency \wedge E(false U' \neg yard))"
        ICTLFormula ftrans = new CTLFormula(new CTLConstants.True(), CTLOperators.Binary.EU, new CTLFormula(em, CTLOperators.Binary.AND,
                new CTLFormula(new CTLConstants.False(), CTLOperators.Binary.EUD, new CTLFormula(CTLOperators.Unary.NEG, y))));
        
        AlternatingBuchiTreeAutomaton<Set<NodeLabel>> abta = CTL2AlternatingBuchiTreeAutomaton.transform(ftrans, net);
        System.out.println(abta.toString());

    }
}
