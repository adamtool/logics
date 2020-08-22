package uniolunisaar.adam.logic.parser.logics.flowltl;

import java.util.HashMap;
import java.util.Map;
import uniol.apt.adt.pn.PetriNet;
import uniolunisaar.adam.ds.logics.ltl.ILTLFormula;
import uniolunisaar.adam.ds.logics.ltl.flowltl.FlowLTLFormula;
import uniolunisaar.adam.ds.logics.flowlogics.IFlowFormula;
import uniolunisaar.adam.ds.logics.ltl.LTLAtomicProposition;
import uniolunisaar.adam.ds.logics.ltl.LTLConstants;
import uniolunisaar.adam.ds.logics.ltl.LTLFormula;
import uniolunisaar.adam.ds.logics.ltl.LTLOperators;
import uniolunisaar.adam.ds.logics.ltl.flowltl.RunLTLFormula;
import uniolunisaar.adam.ds.logics.flowlogics.RunOperators;
import uniolunisaar.adam.logic.parser.logics.flowltl.antlr.FlowLTLFormatBaseListener;
import uniolunisaar.adam.logic.parser.logics.flowltl.antlr.FlowLTLFormatParser;
import uniolunisaar.adam.logic.parser.logics.flowltl.antlr.FlowLTLFormatParser.FlowFormulaContext;
import uniolunisaar.adam.logic.parser.logics.flowltl.antlr.FlowLTLFormatParser.LtlContext;
import uniolunisaar.adam.logic.parser.logics.flowltl.antlr.FlowLTLFormatParser.RunFormulaContext;

/**
 *
 * @author Manuel Gieseking
 */
public class FlowLTLListener extends FlowLTLFormatBaseListener {

    private final PetriNet net;
    private RunLTLFormula formula;
    private final Map<LtlContext, ILTLFormula> ltlFormulas = new HashMap<>();
    private final Map<RunFormulaContext, RunLTLFormula> runFormulas = new HashMap<>();
    private final Map<FlowFormulaContext, IFlowFormula> flowFormulas = new HashMap<>();

    public FlowLTLListener(PetriNet net) {
        this.net = net;
    }

    @Override
    public void exitFlowLTL(FlowLTLFormatParser.FlowLTLContext ctx) {
        formula = runFormulas.get(ctx.runFormula());
    }

    @Override
    public void exitRunFormula(FlowLTLFormatParser.RunFormulaContext ctx) {
        RunLTLFormula f = null;
        if (ctx.rimp() != null) {
            f = new RunLTLFormula(ltlFormulas.get(ctx.phi1), RunOperators.Implication.IMP, runFormulas.get(ctx.phi2));
        } else if (ctx.ltl() != null) {
            f = new RunLTLFormula(ltlFormulas.get(ctx.ltl()));
        } else if (ctx.flowFormula() != null) {
            f = new RunLTLFormula(flowFormulas.get(ctx.flowFormula()));
        } else if (ctx.runBinary() != null) {
            if (ctx.runBinary().op.rand() != null) {
                f = new RunLTLFormula(runFormulas.get(ctx.runBinary().phi1), RunOperators.Binary.AND, runFormulas.get(ctx.runBinary().phi2));
            } else if (ctx.runBinary().op.ror() != null) {
                f = new RunLTLFormula(runFormulas.get(ctx.runBinary().phi1), RunOperators.Binary.OR, runFormulas.get(ctx.runBinary().phi2));
            } else {
                // todo: throw proper exception
                throw new RuntimeException("Could not parse the Run formula. There should be a binary run formula, but the operators are different to 'AND' and 'OR'.");
            }
        }
        if (f != null) {
            runFormulas.put(ctx, f);
        } else {
            // todo: throw proper exception
            throw new RuntimeException("Could not parse the Run formula. The context '" + ctx.toString() + "' does not fit any alternative.");
        }
    }

    @Override
    public void exitFlowFormula(FlowLTLFormatParser.FlowFormulaContext ctx) {
        flowFormulas.put(ctx, new FlowLTLFormula(ltlFormulas.get(ctx.phi)));
    }

    @Override
    public void exitLtl(FlowLTLFormatParser.LtlContext ctx) {
        ILTLFormula f = null;
        if (ctx.atom() != null) {
            String id = ctx.atom().getText();
            f = net.containsPlace(id) ? new LTLAtomicProposition(net.getPlace(id)) : (net.containsTransition(id) ? new LTLAtomicProposition(net.getTransition(id)) : null);
            if (f == null) { //todo: could also say in these cases 'false' holds
                // todo: throw a ParseException when we learned how to teach antlr to throw own exceptions on rules
                throw new RuntimeException("The atom '" + id + "' is no identifier of a place or a transition of the net '" + net.getName() + "'."
                        + "\nThe places are " + net.getPlaces().toString()
                        + "\nThe transitions are " + net.getTransitions().toString());
            }
        } else if (ctx.tt() != null) {
            f = new LTLConstants.True();
        } else if (ctx.ff() != null) {
            f = new LTLConstants.False();
        } else if (ctx.ltlUnary() != null) {
            String operator = ctx.ltlUnary().op.getText();
            ILTLFormula phi = ltlFormulas.get(ctx.ltlUnary().phi);
            if (operator.equals(LTLOperators.Unary.F.name()) || operator.equals(LTLOperators.Unary.F.toSymbol())) {
                f = new LTLFormula(LTLOperators.Unary.F, phi);
            } else if (operator.equals(LTLOperators.Unary.G.name()) || operator.equals(LTLOperators.Unary.G.toSymbol())) {
                f = new LTLFormula(LTLOperators.Unary.G, phi);
            } else if (operator.equals(LTLOperators.Unary.X.name()) || operator.equals(LTLOperators.Unary.X.toSymbol())) {
                f = new LTLFormula(LTLOperators.Unary.X, phi);
            } else if (operator.equals("!") || operator.equals(LTLOperators.Unary.NEG.name()) || operator.equals(LTLOperators.Unary.NEG.toSymbol())) {
                f = new LTLFormula(LTLOperators.Unary.NEG, phi);
            }
        } else if (ctx.ltlBinary() != null) {
            String operator = ctx.ltlBinary().op.getText();
            ILTLFormula phi1 = ltlFormulas.get(ctx.ltlBinary().phi1);
            ILTLFormula phi2 = ltlFormulas.get(ctx.ltlBinary().phi2);
            if (operator.equals(LTLOperators.Binary.AND.name()) || operator.equals(LTLOperators.Binary.AND.toSymbol())) {
                f = new LTLFormula(phi1, LTLOperators.Binary.AND, phi2);
            } else if (operator.equals(LTLOperators.Binary.OR.name()) || operator.equals(LTLOperators.Binary.OR.toSymbol())) {
                f = new LTLFormula(phi1, LTLOperators.Binary.OR, phi2);
            } else if (operator.equals(LTLOperators.Binary.IMP.name()) || operator.equals("->") || operator.equals(LTLOperators.Binary.IMP.toSymbol())) {
                f = new LTLFormula(phi1, LTLOperators.Binary.IMP, phi2);
            } else if (operator.equals(LTLOperators.Binary.BIMP.name()) || operator.equals("<->") || operator.equals(LTLOperators.Binary.BIMP.toSymbol())) {
                f = new LTLFormula(phi1, LTLOperators.Binary.BIMP, phi2);
            } else if (operator.equals(LTLOperators.Binary.W.name()) || operator.equals(LTLOperators.Binary.W.toSymbol())) {
                f = new LTLFormula(phi1, LTLOperators.Binary.W, phi2);
            } else if (operator.equals(LTLOperators.Binary.U.name()) || operator.equals(LTLOperators.Binary.U.toSymbol())) {
                f = new LTLFormula(phi1, LTLOperators.Binary.U, phi2);
            } else if (operator.equals(LTLOperators.Binary.R.name()) || operator.equals(LTLOperators.Binary.R.toSymbol())) {
                f = new LTLFormula(phi1, LTLOperators.Binary.R, phi2);
            }
        }
        if (f != null) {
            ltlFormulas.put(ctx, f);
        } else {
            // todo: throw proper exception
            throw new RuntimeException("Could not parse the LTL formula. The context '" + ctx.toString() + "' does not fit any alternative.");
        }
    }

    public RunLTLFormula getFormula() {
        return formula;
    }

}
