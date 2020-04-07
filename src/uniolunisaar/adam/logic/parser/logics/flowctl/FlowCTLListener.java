package uniolunisaar.adam.logic.parser.logics.flowctl;

import java.util.HashMap;
import java.util.Map;
import uniol.apt.adt.pn.PetriNet;
import uniolunisaar.adam.ds.logics.ctl.CTLAtomicProposition;
import uniolunisaar.adam.ds.logics.ctl.CTLConstants;
import uniolunisaar.adam.ds.logics.ctl.CTLFormula;
import uniolunisaar.adam.ds.logics.ctl.CTLOperators;
import uniolunisaar.adam.ds.logics.ctl.flowctl.FlowCTLFormula;
import uniolunisaar.adam.ds.logics.ctl.ICTLFormula;
import uniolunisaar.adam.ds.logics.ltl.flowltl.RunLTLFormula;
import uniolunisaar.adam.ds.logics.flowlogics.RunOperators;
import uniolunisaar.adam.logic.parser.logics.flowctl.antlr.FlowCTLFormatBaseListener;
import uniolunisaar.adam.logic.parser.logics.flowctl.antlr.FlowCTLFormatParser;
import uniolunisaar.adam.logic.parser.logics.flowctl.antlr.FlowCTLFormatParser.CtlContext;
import uniolunisaar.adam.logic.parser.logics.flowctl.antlr.FlowCTLFormatParser.FlowFormulaContext;
import uniolunisaar.adam.logic.parser.logics.flowctl.antlr.FlowCTLFormatParser.RunFormulaContext;

/**
 *
 * @author Manuel Gieseking
 */
public class FlowCTLListener extends FlowCTLFormatBaseListener {

    private final PetriNet net;
//    private RunFormula formula;
    private FlowCTLFormula formula;
    private final Map<CtlContext, ICTLFormula> ctlFormulas = new HashMap<>();
    private final Map<RunFormulaContext, RunLTLFormula> runFormulas = new HashMap<>();
    private final Map<FlowFormulaContext, FlowCTLFormula> flowFormulas = new HashMap<>();

    public FlowCTLListener(PetriNet net) {
        this.net = net;
    }

    @Override
    public void exitFlowCTL(FlowCTLFormatParser.FlowCTLContext ctx) {
//        formula = runFormulas.get(ctx.runFormula());
//        formula = flowFormulas.get(ctx.flowFormula()); // todo: original old version
    }

    @Override
    public void exitRunFormula(FlowCTLFormatParser.RunFormulaContext ctx) {
        RunLTLFormula f = null;
//        if (ctx.rimp() != null) {
//            f = new RunLTLFormula(ctlFormulas.get(ctx.phi1), RunOperators.Implication.IMP, runFormulas.get(ctx.phi2)); // todo: do it
//        } else
        if (ctx.ctl() != null) {
            f = new RunLTLFormula(ctlFormulas.get(ctx.ctl()));
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
    public void exitFlowFormula(FlowCTLFormatParser.FlowFormulaContext ctx) {
//        flowFormulas.put(ctx, new FlowCTLFormula(ctlFormulas.get(ctx.phi))); /todo: do it when we know the concrete syntax
    }

    @Override
    public void exitCtl(FlowCTLFormatParser.CtlContext ctx) {
        ICTLFormula f = null;
        if (ctx.atom() != null) {
            String id = ctx.atom().getText();
            f = net.containsPlace(id) ? new CTLAtomicProposition(net.getPlace(id)) : (net.containsTransition(id) ? new CTLAtomicProposition(net.getTransition(id)) : null);
            if (f == null) {
                // todo: throw a ParseException when we learned how to teach antlr to throw own exceptions on rules
                throw new RuntimeException("The atom '" + id + "' is no identifier of a place or a transition of the net '" + net.getName() + "'."
                        + "\nThe places are " + net.getPlaces().toString()
                        + "\nThe transitions are " + net.getTransitions().toString());
            }
        } else if (ctx.tt() != null) {
            f = new CTLConstants.True();
        } else if (ctx.ff() != null) {
            f = new CTLConstants.False();
        } else if (ctx.ctlUnary() != null) {
            FlowCTLFormatParser.UnaryOpContext opCtx = ctx.ctlUnary().op;
            ICTLFormula phi = ctlFormulas.get(ctx.ctlUnary().phi);
            if (opCtx.ex() != null) {
                f = new CTLFormula(CTLOperators.Unary.EX, phi);
            } else if (opCtx.ax() != null) {
                f = new CTLFormula(CTLOperators.Unary.AX, phi);
            } else if (opCtx.ef() != null) {
                f = new CTLFormula(CTLOperators.Unary.EF, phi);
            } else if (opCtx.af() != null) {
                f = new CTLFormula(CTLOperators.Unary.AF, phi);
            } else if (opCtx.eg() != null) {
                f = new CTLFormula(CTLOperators.Unary.EG, phi);
            } else if (opCtx.ag() != null) {
                f = new CTLFormula(CTLOperators.Unary.AG, phi);
            } else if (opCtx.neg() != null) {
                f = new CTLFormula(CTLOperators.Unary.NEG, phi);
            } else {
                // todo: throw a ParseException when we learned how to teach antlr to throw own exceptions on rules
                throw new RuntimeException("Didn't considered all CTL unary operators");
            }
        } else if (ctx.ctlBinary() != null) {
            ICTLFormula phi1 = ctlFormulas.get(ctx.ctlBinary().phi1);
            ICTLFormula phi2 = ctlFormulas.get(ctx.ctlBinary().phi2);
            if (ctx.ctlBinary().op != null) {
                if (ctx.ctlBinary().all() != null) {
                    f = new CTLFormula(phi1, CTLOperators.Binary.AU, phi2);
                } else if (ctx.ctlBinary().exists() != null) {
                    f = new CTLFormula(phi1, CTLOperators.Binary.EU, phi2);
                } else {
                    // todo: throw a ParseException when we learned how to teach antlr to throw own exceptions on rules
                    throw new RuntimeException("Didn't considered all CTL binary operators for until.");
                }
            } else if (ctx.ctlBinary().stdOp != null) {
                FlowCTLFormatParser.BinaryOpContext opCtx = ctx.ctlBinary().stdOp;
                if (opCtx.and() != null) {
                    f = new CTLFormula(phi1, CTLOperators.Binary.AND, phi2);
                } else if (opCtx.or() != null) {
                    f = new CTLFormula(phi1, CTLOperators.Binary.OR, phi2);
                } else if (opCtx.imp() != null) {
                    f = new CTLFormula(phi1, CTLOperators.Binary.IMP, phi2);
                } else if (opCtx.bimp() != null) {
                    f = new CTLFormula(phi1, CTLOperators.Binary.BIMP, phi2);
                } else {
                    // todo: throw a ParseException when we learned how to teach antlr to throw own exceptions on rules
                    throw new RuntimeException("Didn't considered all CTL binary operators for until.");
                }
            } else {
                // todo: throw a ParseException when we learned how to teach antlr to throw own exceptions on rules
                throw new RuntimeException("Didn't considered all CTL binary operators.");
            }
        }
        if (f != null) {
            ctlFormulas.put(ctx, f);
        } else {
            // todo: throw proper exception
            throw new RuntimeException("Could not parse the LTL formula. The context '" + ctx.toString() + "' does not fit any alternative.");
        }
    }
    //    public RunFormula getFormula() {
    //        return formula;
    //    }

    public FlowCTLFormula getFormula() {
        return formula;
    }

}
