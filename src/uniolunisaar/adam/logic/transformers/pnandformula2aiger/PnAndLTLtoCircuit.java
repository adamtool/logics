package uniolunisaar.adam.logic.transformers.pnandformula2aiger;

import uniolunisaar.adam.logic.transformers.pn2aiger.Circuit;
import uniolunisaar.adam.logic.transformers.pn2aiger.AigerRenderer;
import java.io.IOException;
import uniol.apt.io.parser.ParseException;
import uniolunisaar.adam.ds.logics.ltl.ILTLFormula;
import uniolunisaar.adam.ds.logics.ltl.LTLFormula;
import uniolunisaar.adam.ds.logics.ltl.LTLOperators;
import uniolunisaar.adam.util.logics.transformers.logics.ModelCheckingOutputData;
import uniolunisaar.adam.ds.petrinetwithtransits.PetriNetWithTransits;
import uniolunisaar.adam.exceptions.ExternalToolException;
import uniolunisaar.adam.logic.transformers.flowltl.FlowLTLTransformer;
import uniolunisaar.adam.logic.transformers.flowltl.FlowLTLTransformerHyperLTL;
import uniolunisaar.adam.util.logics.transformers.logics.TransformerTools;
import uniolunisaar.adam.tools.Logger;
import uniolunisaar.adam.exceptions.ProcessNotStartedException;
import uniolunisaar.adam.logic.transformers.pn2aiger.AigerRenderer.OptimizationsComplete;
import uniolunisaar.adam.logic.transformers.pn2aiger.AigerRenderer.OptimizationsSystem;
import uniolunisaar.adam.util.logics.transformers.logics.PnAndLTLtoCircuitStatistics;

/**
 *
 * @author Manuel Gieseking
 */
public class PnAndLTLtoCircuit {

    public enum Stuttering {
        REPLACEMENT,
        REPLACEMENT_REGISTER,
        PREFIX,
        PREFIX_REGISTER
    }

    public enum TransitionSemantics {
        INGOING,
        OUTGOING
    }

    public enum Maximality {
        MAX_CONCURRENT,
        MAX_INTERLEAVING,
        MAX_INTERLEAVING_IN_CIRCUIT,
        MAX_NONE
    }

    private TransitionSemantics semantics = TransitionSemantics.OUTGOING;
//    private Maximality maximality = Maximality.MAX_INTERLEAVING;
    private Maximality maximality = Maximality.MAX_NONE;
    private Stuttering stuttering = Stuttering.PREFIX_REGISTER;
    private final OptimizationsSystem optsSys;
    private final OptimizationsComplete optsComp;

    public PnAndLTLtoCircuit() {
        this.optsSys = OptimizationsSystem.NONE;
        this.optsComp = OptimizationsComplete.NONE;
    }

    public PnAndLTLtoCircuit(OptimizationsSystem optsSys, AigerRenderer.OptimizationsComplete optsComp) {
        this.optsSys = optsSys;
        this.optsComp = optsComp;
    }

    public PnAndLTLtoCircuit(TransitionSemantics semantics, Maximality maximality, Stuttering stuttering, OptimizationsSystem optsSys, AigerRenderer.OptimizationsComplete optsComp) {
        this.semantics = semantics;
        this.maximality = maximality;
        this.stuttering = stuttering;
        this.optsSys = optsSys;
        this.optsComp = optsComp;
    }

    /**
     *
     * @param net
     * @param formula
     * @param data
     * @return
     * @throws InterruptedException
     * @throws IOException
     * @throws uniol.apt.io.parser.ParseException
     * @throws uniolunisaar.adam.exceptions.ProcessNotStartedException
     * @throws uniolunisaar.adam.exceptions.ExternalToolException
     */
    public AigerRenderer createCircuit(PetriNetWithTransits net, ILTLFormula formula, ModelCheckingOutputData data) throws InterruptedException, IOException, ParseException, ProcessNotStartedException, ExternalToolException {
        return createCircuit(net, formula, data, null);
    }

    /**
     *
     * @param net
     * @param formula
     * @param data
     * @param stats
     * @return
     * @throws InterruptedException
     * @throws IOException
     * @throws uniol.apt.io.parser.ParseException
     * @throws uniolunisaar.adam.exceptions.ProcessNotStartedException
     * @throws uniolunisaar.adam.exceptions.ExternalToolException
     */
    public AigerRenderer createCircuit(PetriNetWithTransits net, ILTLFormula formula, ModelCheckingOutputData data, PnAndLTLtoCircuitStatistics stats) throws InterruptedException, IOException, ParseException, ProcessNotStartedException, ExternalToolException {
        return createCircuit(net, formula, data, stats, false);
    }

    /**
     *
     * @param net
     * @param formula
     * @param stats
     * @param skipMax - used if this method is called from the FlowLTL-Part and
     * the maximality is already handled there.
     * @return
     * @throws InterruptedException
     * @throws IOException
     * @throws ParseException
     * @throws ProcessNotStartedException
     * @throws ExternalToolException
     */
    AigerRenderer createCircuit(PetriNetWithTransits net, ILTLFormula formula, ModelCheckingOutputData data, PnAndLTLtoCircuitStatistics stats, boolean skipMax) throws InterruptedException, IOException, ParseException, ProcessNotStartedException, ExternalToolException {
        Logger.getInstance().addMessage("Creating the net '" + net.getName() + "' for the formula '" + formula.toSymbolString() + "'.\n"
                + " With maximality term: " + maximality
                + " semantics: " + semantics
                + " stuttering: " + stuttering, true);

        // Add Fairness
        formula = FlowLTLTransformer.addFairness(net, formula);

        // Add Maximality
        if (!skipMax) {
            ILTLFormula max = TransformerTools.getMaximality(maximality, semantics, net);
            if (max != null) {
                formula = new LTLFormula(max, LTLOperators.Binary.IMP, formula);
            }
        }

        // Choose renderer and add the corresponding stuttering
        AigerRenderer renderer;
        if (semantics == TransitionSemantics.INGOING) {
            // todo: do the stuttering here
            renderer = Circuit.getRenderer(Circuit.Renderer.INGOING);
        } else {
            formula = FlowLTLTransformer.handleStutteringOutGoingSemantics(net, formula, stuttering, maximality);
            if (maximality == Maximality.MAX_INTERLEAVING_IN_CIRCUIT) {
                renderer = Circuit.getRenderer(Circuit.Renderer.OUTGOING_REGISTER_MAX_INTERLEAVING);
            } else {
                renderer = Circuit.getRenderer(Circuit.Renderer.OUTGOING_REGISTER);
            }
        }
        renderer.setSystemOptimizations(optsSys);
        renderer.setMCHyperResultOptimizations(optsComp);

            // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% COLLECT STATISTICS
        if (stats != null) {
            // input model checking net
            stats.setMc_net(net);
            stats.setMc_formula(formula);
        }
        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% END COLLECT STATISTICS
        
        Logger.getInstance().addMessage("This means we create the product for F='" + formula.toSymbolString() + "'.");
        CircuitAndLTLtoCircuit.createCircuit(net, renderer, FlowLTLTransformerHyperLTL.toMCHyperFormat(formula), data, stats);
        return renderer;
    }

    public TransitionSemantics getSemantics() {
        return semantics;
    }

    public void setSemantics(TransitionSemantics semantics) {
        this.semantics = semantics;
    }

    public Maximality getMaximality() {
        return maximality;
    }

    public void setMaximality(Maximality maximality) {
        this.maximality = maximality;
    }

    public Stuttering getStuttering() {
        return stuttering;
    }

    public void setStuttering(Stuttering stuttering) {
        this.stuttering = stuttering;
    }

}
