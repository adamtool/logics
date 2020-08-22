package uniolunisaar.adam.tests.logics.flowltl;

import uniolunisaar.adam.ds.logics.flowlogics.IFlowFormula;
import uniolunisaar.adam.ds.logics.flowlogics.IRunFormula;
import uniolunisaar.adam.ds.logics.flowlogics.RunOperators;
import uniolunisaar.adam.ds.logics.ltl.flowltl.RunLTLFormula;
import uniolunisaar.adam.ds.logics.ltl.LTLFormula;
import uniolunisaar.adam.ds.logics.ltl.flowltl.FlowLTLFormula;
import uniolunisaar.adam.logic.parser.logics.flowltl.FlowLTLParser;
import uniolunisaar.adam.ds.logics.ltl.LTLOperators;
import org.testng.Assert;
import org.testng.annotations.Test;
import uniol.apt.adt.pn.PetriNet;
import uniol.apt.io.parser.ParseException;
import uniolunisaar.adam.ds.logics.ltl.ILTLFormula;
import uniolunisaar.adam.ds.logics.ltl.LTLAtomicProposition;

/**
 *
 * @author Manuel Gieseking
 */
@Test
public class TestFlowLTLParser {

    @Test
    public void testLexerErrors() {
        // dummy net
        PetriNet net = new PetriNet("asdf");
        try {
            FlowLTLParser.parse(net, "#asdf ");
            Assert.fail("Should not be reachable");
        } catch (ParseException ex) {
            Assert.assertEquals(ex.getMessage(), "Error while parsing formula '#asdf '");
            Assert.assertEquals(ex.getCause().getMessage(), " col 0: token recognition error at: '#'");
        }

        try {
            FlowLTLParser.parse(net, "a1 ^asdf ");
            Assert.fail("Should not be reachable");
        } catch (ParseException ex) {
            Assert.assertEquals(ex.getMessage(), "Error while parsing formula 'a1 ^asdf '");
            Assert.assertEquals(ex.getCause().getMessage(), " col 3: token recognition error at: '^'");
        }
    }

    @Test
    public void testParserErrors() {
        // dummy net
        PetriNet net = new PetriNet("asdf");
        try {
            FlowLTLParser.parse(net, "asdf ");
            Assert.fail("Should not be reachable");
        } catch (ParseException ex) {
            Assert.fail("Should not be reachable");
        } catch (RuntimeException ex) {
            Assert.assertEquals(ex.getMessage(), "The atom 'asdf' is no identifier of a place or a transition of the net 'asdf'.\n"
                    + "The places are []\n"
                    + "The transitions are []");
        }

        net.createPlace("p1");
        net.createTransition();

        try {
            FlowLTLParser.parse(net, "asdf p1");
            Assert.fail("Should not be reachable");
        } catch (ParseException ex) {
            Assert.assertEquals(ex.getMessage(), "Error while parsing formula 'asdf p1'");
            Assert.assertEquals(ex.getCause().getMessage(), " col 5: extraneous input 'p1' expecting <EOF>");
        }

        net.createTransition();
        try {
            FlowLTLParser.parse(net, "p1 asdf");
            Assert.fail("Should not be reachable");
        } catch (ParseException ex) {
            Assert.assertEquals(ex.getMessage(), "Error while parsing formula 'p1 asdf'");
            Assert.assertEquals(ex.getCause().getMessage(), " col 3: extraneous input 'asdf' expecting <EOF>");
        }

        try {
            FlowLTLParser.parse(net, "(p1 OR asdf)");
            Assert.fail("Should not be reachable");
        } catch (ParseException ex) {
            Assert.fail("Should not be reachable");
        } catch (RuntimeException ex) {
            Assert.assertEquals(ex.getMessage(), "The atom 'asdf' is no identifier of a place or a transition of the net 'asdf'.\n"
                    + "The places are [Node{id=p1}]\n"
                    + "The transitions are [Node{id=t0}, Node{id=t1}]");
        }
    }

    @Test
    public void testToyFormulae() throws Exception {
        // dummy net
        PetriNet net = new PetriNet("asdf");
        // Propositions 
        LTLAtomicProposition a = new LTLAtomicProposition(net.createPlace("a1"));
        LTLAtomicProposition b = new LTLAtomicProposition(net.createTransition("b1"));
        IRunFormula r = new RunLTLFormula(a, LTLOperators.Binary.OR, b);
        IRunFormula rout = FlowLTLParser.parse(net, r.toString());
        Assert.assertEquals(rout.toString(), r.toString());

        IRunFormula rNested = new RunLTLFormula(a, LTLOperators.Binary.OR, new LTLFormula(LTLOperators.Unary.F, new LTLFormula(a, LTLOperators.Binary.W, b)));
        IRunFormula rNestedOut = FlowLTLParser.parse(net, rNested.toString());
        Assert.assertEquals(rNestedOut.toString(), rNested.toString());

        ILTLFormula ltl1 = new LTLFormula(LTLOperators.Unary.F, b);
        IRunFormula out = FlowLTLParser.parse(net, ltl1.toString());
        Assert.assertEquals(ltl1.toString(), out.toString());

        IFlowFormula flow1 = new FlowLTLFormula(ltl1);
        out = FlowLTLParser.parse(net, flow1.toString());
        Assert.assertEquals(flow1.toString(), out.toString());

        IRunFormula r1 = new RunLTLFormula(r, RunOperators.Binary.AND, flow1);
        out = FlowLTLParser.parse(net, r1.toString());
        Assert.assertEquals(r1.toString(), out.toString());

        IRunFormula r2 = new RunLTLFormula(ltl1, LTLOperators.Binary.U, new LTLFormula(LTLOperators.Unary.G, b));
        out = FlowLTLParser.parse(net, r2.toString());
        Assert.assertEquals(r2.toString(), out.toString());
        
        net.createTransition("ta");
        net.createTransition("tb");
        String formula = "(G( F(!(b1 OR ta) AND G( F(!(a1 OR tb))";
    }

    @Test
    public void testToyFormulaeOtherSyntax() throws ParseException {
        // dummy net
        PetriNet net = new PetriNet("asdf");
        // Propositions 
        LTLAtomicProposition a = new LTLAtomicProposition(net.createPlace("a1"));
        LTLAtomicProposition b = new LTLAtomicProposition(net.createTransition("b1"));
        IRunFormula r = new RunLTLFormula(a, LTLOperators.Binary.OR, b);
        IRunFormula rout = FlowLTLParser.parse(net, "(a1 OR b1)");
        Assert.assertEquals(rout.toString(), r.toString());

        IRunFormula rNested = new RunLTLFormula(a, LTLOperators.Binary.OR, new LTLFormula(LTLOperators.Unary.F, new LTLFormula(a, LTLOperators.Binary.W, b)));
//        System.out.println(rNested);
        IRunFormula rNestedOut = FlowLTLParser.parse(net, "(a1 OR F((a1 W b1)))");
        Assert.assertEquals(rNestedOut.toString(), rNested.toString());

        ILTLFormula ltl1 = new LTLFormula(LTLOperators.Unary.F, b);
        IRunFormula out = FlowLTLParser.parse(net, "F(b1)");
        Assert.assertEquals(ltl1.toString(), out.toString());

        IFlowFormula flow1 = new FlowLTLFormula(ltl1);
        out = FlowLTLParser.parse(net, "A(F(b1))");
        Assert.assertEquals(flow1.toString(), out.toString());

        IRunFormula r1 = new RunLTLFormula(r, RunOperators.Binary.AND, flow1);
        out = FlowLTLParser.parse(net, "((a1 OR b1) AND A(F(b1)))");
        Assert.assertEquals(r1.toString(), out.toString());

        IRunFormula r2 = new RunLTLFormula(ltl1, LTLOperators.Binary.U, new LTLFormula(LTLOperators.Unary.G, b));
        out = FlowLTLParser.parse(net, "(F(b1) U G(b1))");
        Assert.assertEquals(r2.toString(), out.toString());
    }

    @Test
    public void testNotExistingAtom() {
        // dummy net
        PetriNet net = new PetriNet("asdf");
        net.createPlace();
        net.createPlace();
        net.createPlace();
        net.createTransition();
        net.createTransition();
        net.createTransition();
        try {
            IRunFormula f = FlowLTLParser.parse(net, "F(a)");
            Assert.fail("Should not be reachable");
        } catch (ParseException ex) {
            Assert.fail("Should not be reachable");
        } catch (RuntimeException rt) {
            Assert.assertEquals(rt.getMessage(), "The atom 'a' is no identifier of a place or a transition of the net 'asdf'.\n"
                    + "The places are [Node{id=p0}, Node{id=p1}, Node{id=p2}]\n"
                    + "The transitions are [Node{id=t0}, Node{id=t1}, Node{id=t2}]");
        }
    }

    @Test
    public void testIncorrectOperators() throws ParseException {
        PetriNet net = new PetriNet("asdf");
        LTLAtomicProposition p = new LTLAtomicProposition(net.createPlace("p0"));
        LTLAtomicProposition t = new LTLAtomicProposition(net.createTransition("t0"));

        IRunFormula f = FlowLTLParser.parse(net, "F((p0->t0))");
        Assert.assertEquals(f.toSymbolString(), LTLOperators.Unary.F.toSymbol() + " (p0 " + LTLOperators.Binary.IMP.toSymbol() + " t0)");

        try {
            f = FlowLTLParser.parse(net, "F(p0⋀t0)");
            Assert.fail("Should not be reachable");
        } catch (ParseException ex) {
            Assert.assertEquals(ex.getMessage(), "Error while parsing formula 'F(p0⋀t0)'");
            Assert.assertEquals(ex.getCause().getMessage(), " col 4: mismatched input '⋀' expecting <EOF>");
        }

        try {
            f = FlowLTLParser.parse(net, "F((p0⋀t0))");
            Assert.fail("Should not be reachable");
        } catch (ParseException ex) {
            Assert.assertEquals(ex.getMessage(), "Error while parsing formula 'F((p0⋀t0))'");
            Assert.assertEquals(ex.getCause().getMessage(), " col 5: no viable alternative at input '((p0⋀'");
        }

        f = FlowLTLParser.parse(net, "F((p0⋏t0))");
        Assert.assertEquals(f.toSymbolString(), LTLOperators.Unary.F.toSymbol() + " (p0 " + LTLOperators.Binary.AND.toSymbol() + " t0)");
    }
}
