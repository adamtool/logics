package uniolunisaar.adam.tests.logics.flowltl;

import uniolunisaar.adam.ds.logics.flowlogics.IFlowFormula;
import uniolunisaar.adam.ds.logics.flowlogics.IRunFormula;
import uniolunisaar.adam.ds.logics.flowlogics.RunOperators;
import uniolunisaar.adam.ds.logics.ltl.flowltl.RunLTLFormula;
import uniolunisaar.adam.ds.logics.IFormula;
import uniolunisaar.adam.ds.logics.ltl.LTLFormula;
import uniolunisaar.adam.ds.logics.ltl.flowltl.FlowLTLFormula;
import uniolunisaar.adam.ds.logics.ltl.LTLOperators;
import org.testng.Assert;
import org.testng.annotations.Test;
import uniol.apt.adt.pn.PetriNet;
import uniolunisaar.adam.ds.logics.ltl.ILTLFormula;
import uniolunisaar.adam.exceptions.logics.NotSubstitutableException;
import uniolunisaar.adam.ds.logics.ltl.LTLAtomicProposition;

/**
 *
 * @author Manuel Gieseking
 */
@Test
public class TestFlowLTL {

    @Test
    public void testCreateFormula() throws Exception {
//        System.out.println(
//                LTLOperators.Unary.F.toSymbol()
//                + LTLOperators.Unary.G.toSymbol()
//                + LTLOperators.Unary.NEG.toSymbol()
//                + LTLOperators.Unary.X.toSymbol()
//                + LTLOperators.Binary.AND.toSymbol()
//                + LTLOperators.Binary.OR.toSymbol()
//                + LTLOperators.Binary.BIMP.toSymbol()
//                + LTLOperators.Binary.IMP.toSymbol()
//                + LTLOperators.Binary.U.toSymbol()
//                + LTLOperators.Binary.W.toSymbol()
//                + LTLOperators.Binary.R.toSymbol()
//                + RunOperators.Binary.AND.toSymbol()
//                + RunOperators.Binary.OR.toSymbol()
//                + RunOperators.Implication.IMP.toSymbol()
//                + FlowFormula.FlowOperator.A.toSymbol());
        // dummy net
        PetriNet net = new PetriNet("asdf");
        // Propositions 
        LTLAtomicProposition a = new LTLAtomicProposition(net.createPlace("a1"));
        LTLAtomicProposition b = new LTLAtomicProposition(net.createTransition("b1"));
        IRunFormula r = new RunLTLFormula(a, LTLOperators.Binary.OR, b);
//        System.out.println(r.getClosure().toString());
//        System.out.println(r.toString());
        Assert.assertEquals(r.toSymbolString(), "(a1 ⋎ b1)");
//        System.out.println('\u25C7');
//        System.out.println('\u20DF');

        ILTLFormula ltl1 = new LTLFormula(LTLOperators.Unary.F, b);
//        System.out.println(ltl1.toString()); 
//        System.out.println(ltl1.getClosure().toString());
        Assert.assertEquals(ltl1.toSymbolString(), "◇ b1");

        IFlowFormula flow1 = new FlowLTLFormula(ltl1);
//        System.out.println(flow1.toString());
//        System.out.println(flow1.getClosure().toString());
        Assert.assertEquals(flow1.toSymbolString(), "𝔸 ◇ b1");

        IRunFormula r1 = new RunLTLFormula(r, RunOperators.Binary.AND, flow1);
//        System.out.println(r1.toString());
//        System.out.println(r1.getClosure().toString());
        Assert.assertEquals(r1.toSymbolString(), "((a1 ⋎ b1) ⋀ 𝔸 ◇ b1)");

        IRunFormula r2 = new RunLTLFormula(ltl1, LTLOperators.Binary.U, new LTLFormula(LTLOperators.Unary.G, b));
//        System.out.println(r2.toString());
//        System.out.println(r2.getClosure().toString());
        Assert.assertEquals(r2.toSymbolString(), "(◇ b1 𝓤 ⬜ b1)");
    }

    @Test
    public void testSubstitution() throws NotSubstitutableException {
        // dummy net
        PetriNet net = new PetriNet("asdf");
        // Propositions 
        LTLAtomicProposition a = new LTLAtomicProposition(net.createPlace("a1"));
        LTLAtomicProposition b = new LTLAtomicProposition(net.createTransition("b1"));
        IFormula subsa = a.substitute(a, b);
        Assert.assertEquals(subsa.toString(), "b1");

        RunLTLFormula r = new RunLTLFormula(a, LTLOperators.Binary.OR, b);
//        ILTLFormula rltl = ((RunFormula) r.getPhi()).toLTLFormula();

        IFormula subsr = r.substitute(b, a);
        if (!(subsr instanceof RunLTLFormula)) {
            Assert.fail("Should be a run formula");
        }
        Assert.assertEquals(subsr.toString(), "(a1 OR a1)");

        IFormula subs = subsr.substitute(subsr, b);
        if (!(subsr instanceof RunLTLFormula)) {
            Assert.fail("Should be a run formula");
        }
        Assert.assertEquals(subs.toString(), "b1");

        subs = subsr.substitute(a, b);
        Assert.assertEquals(subs.toString(), "(b1 OR b1)");
        subs = subs.substitute(a, r.getPhi());
        Assert.assertEquals(subs.toString(), "(b1 OR b1)");

        subs = subsr.substitute(a, ((RunLTLFormula) r.getPhi()).toLTLFormula());
        Assert.assertEquals(subs.toString(), "((a1 OR b1) OR (a1 OR b1))");
//
//        IRunFormula rNested = new RunFormula(a, LTLOperators.Binary.OR, new LTLFormula(LTLOperators.Unary.F, new LTLFormula(a, LTLOperators.Binary.W, b)));
//        subs = rNested.substitute(a, b);
//        Assert.assertEquals(subs.toString(), "(b1 OR F((b1 W b1)))");
//        try {
//            rNested.substitute(new LTLFormula(a, LTLOperators.Binary.W, b), subsr);
//            Assert.fail("Should not be reachable");
//        } catch (NotSubstitutableException nse) {
//            Assert.assertEquals(nse.getMessage(), "The substituted subformula '(a1 OR a1)', created by substituting '(a1 W b1)' with '(a1 OR a1)' is not an LTL formula and thus cannot be used for 'F((a1 W b1))'.");
//        }
//        subs = rNested.substitute(new LTLFormula(a, LTLOperators.Binary.W, b), ((RunFormula) subsr).getPhi());
//        Assert.assertEquals(subs.toString(), "(a1 OR F((a1 OR a1)))");
//
//        ILTLFormula ltl1 = new LTLFormula(LTLOperators.Unary.F, b);
//        IFlowFormula flow1 = new FlowFormula(ltl1);
//        IRunFormula r1 = new RunFormula(r, RunOperators.Binary.AND, flow1);
//        IRunFormula r2 = new RunFormula(ltl1, LTLOperators.Binary.U, new LTLFormula(LTLOperators.Unary.G, b));
//        IRunFormula r3 = new RunFormula(ltl1, RunOperators.Implication.IMP, flow1);
//
//        subs = r1.substitute(flow1, r2);
//        Assert.assertEquals(subs.toString(), "((a1 OR b1) AND (F(b1) U G(b1)))");
//
//        try {
//            r3.substitute(ltl1, r1);
//            Assert.fail("Should not be reachable");
//        } catch (NotSubstitutableException nse) {
//            Assert.assertEquals(nse.getMessage(), "The substituted subformula '((a1 OR b1) AND A(F(b1)))', created by substituting 'F(b1)' with '((a1 OR b1) AND A(F(b1)))' is not an LTL formula and thus cannot be used for 'A(F(b1))'.");
//        }
//
//        subs = r3.substitute(ltl1, new LTLFormula(LTLOperators.Unary.G, a));
//        Assert.assertEquals(subs.toString(), "(G(a1) IMP A(G(a1)))");
    }
}
