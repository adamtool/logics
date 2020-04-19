package uniolunisaar.adam.logic.parser.logics.flowctl.separate;

import java.util.HashMap;
import java.util.Map;
import uniol.apt.adt.pn.PetriNet;
import uniolunisaar.adam.ds.logics.ctl.CTLAtomicProposition;
import uniolunisaar.adam.ds.logics.ctl.CTLConstants;
import uniolunisaar.adam.ds.logics.ctl.CTLFormula;
import uniolunisaar.adam.ds.logics.ctl.CTLOperators;
import uniolunisaar.adam.ds.logics.ctl.flowctl.FlowCTLFormula;
import uniolunisaar.adam.ds.logics.ctl.ICTLFormula;
import uniolunisaar.adam.ds.logics.ctl.flowctl.separate.RunCTLSeparateFormula;
import uniolunisaar.adam.ds.logics.ctl.flowctl.RunCTLOperators;
import uniolunisaar.adam.logic.parser.logics.flowctl.separate.antlr.FlowCTLSeparateFormatBaseListener;
import uniolunisaar.adam.logic.parser.logics.flowctl.separate.antlr.FlowCTLSeparateFormatParser;
import uniolunisaar.adam.logic.parser.logics.flowctl.separate.antlr.FlowCTLSeparateFormatParser.CtlContext;
import uniolunisaar.adam.logic.parser.logics.flowctl.separate.antlr.FlowCTLSeparateFormatParser.FlowFormulaContext;
import uniolunisaar.adam.logic.parser.logics.flowctl.separate.antlr.FlowCTLSeparateFormatParser.RunFormulaContext;

/**
 *
 * @author Manuel Gieseking
 */
public class FlowCTLSeparateListener extends FlowCTLSeparateFormatBaseListener {

    private final PetriNet net;
    private RunCTLSeparateFormula formula;
    private final Map<CtlContext, ICTLFormula> ctlFormulas = new HashMap<>();
    private final Map<RunFormulaContext, RunCTLSeparateFormula> runFormulas = new HashMap<>();
    private final Map<FlowFormulaContext, FlowCTLFormula> flowFormulas = new HashMap<>();

    public FlowCTLSeparateListener(PetriNet net) {
        this.net = net;
    }

    @Override
    public void exitFlowCTL(FlowCTLSeparateFormatParser.FlowCTLContext ctx) {
        formula = runFormulas.get(ctx.runFormula());
    }

    @Override
    public void exitRunFormula(FlowCTLSeparateFormatParser.RunFormulaContext ctx) {
        RunCTLSeparateFormula f = null;
        if (ctx.ctl() != null) {
            f = new RunCTLSeparateFormula(ctlFormulas.get(ctx.ctl()));
        } else if (ctx.runUnary() != null) {
            f = new RunCTLSeparateFormula(RunCTLOperators.Unary.NEG, runFormulas.get(ctx.runUnary().phi));
        } else if (ctx.runBinary() != null) {
            if (ctx.runBinary().op.rand() != null) {
                f = new RunCTLSeparateFormula(runFormulas.get(ctx.runBinary().phi1), RunCTLOperators.Binary.AND, runFormulas.get(ctx.runBinary().phi2));
            } else if (ctx.runBinary().op.ror() != null) {
                f = new RunCTLSeparateFormula(runFormulas.get(ctx.runBinary().phi1), RunCTLOperators.Binary.OR, runFormulas.get(ctx.runBinary().phi2));
            } else if (ctx.runBinary().op.rimp() != null) {
                f = new RunCTLSeparateFormula(runFormulas.get(ctx.runBinary().phi1), RunCTLOperators.Binary.IMP, runFormulas.get(ctx.runBinary().phi2));
            } else if (ctx.runBinary().op.rbimp() != null) {
                f = new RunCTLSeparateFormula(runFormulas.get(ctx.runBinary().phi1), RunCTLOperators.Binary.BIMP, runFormulas.get(ctx.runBinary().phi2));
            } else {
                // todo: throw proper exception
                throw new RuntimeException("Could not parse the run formula. There should be a binary run formula, but the operators are different to 'AND', 'OR', 'IMP', and 'BIMP'.");
            }
        } else if (ctx.flowFormula() != null) {
            f = new RunCTLSeparateFormula(flowFormulas.get(ctx.flowFormula()));
        }
        if (f != null) {
            runFormulas.put(ctx, f);
        } else {
            // todo: throw proper exception
            throw new RuntimeException("Could not parse the run formula. The context '" + ctx.toString() + "' does not fit any alternative.");
        }
    }

    @Override
    public void exitFlowFormula(FlowCTLSeparateFormatParser.FlowFormulaContext ctx) {
        if (ctx.op.existsFlows() != null) {
            flowFormulas.put(ctx, new FlowCTLFormula(FlowCTLFormula.FlowCTLOperator.Exists, ctlFormulas.get(ctx.phi)));
        } else if (ctx.op.forallFlows() != null) {
            flowFormulas.put(ctx, new FlowCTLFormula(FlowCTLFormula.FlowCTLOperator.All, ctlFormulas.get(ctx.phi)));
        } else {
            // todo: throw proper exception
            throw new RuntimeException("Could not parse the flow formula. The context '" + ctx.toString() + "' does not fit any alternative.");
        }
    }

    @Override
    public void exitCtl(FlowCTLSeparateFormatParser.CtlContext ctx) {
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
            FlowCTLSeparateFormatParser.UnaryOpContext opCtx = ctx.ctlUnary().op;
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
            if (ctx.ctlBinary().stdOp != null) {
                FlowCTLSeparateFormatParser.BinaryOpContext opCtx = ctx.ctlBinary().stdOp;
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
                    throw new RuntimeException("Didn't considered all CTL binary operators (AND, OR, IMP, BIMP).");
                }
            } else if (ctx.ctlBinary().op != null) {
                FlowCTLSeparateFormatParser.BinaryTempOpContext opCtx = ctx.ctlBinary().op;
                if (opCtx.until() != null) {
                    if (ctx.ctlBinary().all() != null) {
                        f = new CTLFormula(phi1, CTLOperators.Binary.AU, phi2);
                    } else if (ctx.ctlBinary().exists() != null) {
                        f = new CTLFormula(phi1, CTLOperators.Binary.EU, phi2);
                    }
                } else if (opCtx.weak() != null) {
                    if (ctx.ctlBinary().all() != null) {
                        f = new CTLFormula(phi1, CTLOperators.Binary.AW, phi2);
                    } else if (ctx.ctlBinary().exists() != null) {
                        f = new CTLFormula(phi1, CTLOperators.Binary.EW, phi2);
                    }
                } else if (opCtx.opRelease() != null) {
                    if (ctx.ctlBinary().all() != null) {
                        f = new CTLFormula(phi1, CTLOperators.Binary.AR, phi2);
                    } else if (ctx.ctlBinary().exists() != null) {
                        f = new CTLFormula(phi1, CTLOperators.Binary.ER, phi2);
                    }
                } else {
                    // todo: throw a ParseException when we learned how to teach antlr to throw own exceptions on rules
                    throw new RuntimeException("Didn't considered all CTL binary operators regarding U, R, W.");
                }
                // also working code, when not having E and A as nonterminals
//                FlowCTLFormatParser.BinaryTempOpContext opCtx = ctx.ctlBinary().op;
//                String start = ctx.ctlBinary().getStart().getText();
//                if (opCtx.until() != null) {
//                    if (start.equals("A")) {
//                        f = new CTLFormula(phi1, CTLOperators.Binary.AU, phi2);
//                    } else if (start.equals("E")) {
//                        f = new CTLFormula(phi1, CTLOperators.Binary.EU, phi2);
//                    }
//                } else if (opCtx.weak() != null) {
//                    if (start.equals("A")) {
//                        f = new CTLFormula(phi1, CTLOperators.Binary.AW, phi2);
//                    } else if (start.equals("E")) {
//                        f = new CTLFormula(phi1, CTLOperators.Binary.EW, phi2);
//                    }
//                } else if (opCtx.opRelease() != null) {
//                    if (start.equals("A")) {
//                        f = new CTLFormula(phi1, CTLOperators.Binary.AR, phi2);
//                    } else if (start.equals("E")) {
//                        f = new CTLFormula(phi1, CTLOperators.Binary.ER, phi2);
//                    }
//                } else {
//                    // todo: throw a ParseException when we learned how to teach antlr to throw own exceptions on rules
//                    throw new RuntimeException("Didn't considered all CTL binary operators regarding U, R, W.");
//                }
            } else {
                // todo: throw a ParseException when we learned how to teach antlr to throw own exceptions on rules
                throw new RuntimeException("Didn't considered all CTL binary operators.");
            }
        }
        if (f != null) {
            ctlFormulas.put(ctx, f);
        } else {
            // todo: throw proper exception
            throw new RuntimeException("Could not parse the CTL formula. The context '" + ctx.getText() + "' does not fit any alternative.");
        }
    }

    public RunCTLSeparateFormula getFormula() {
        return formula;
    }

}
