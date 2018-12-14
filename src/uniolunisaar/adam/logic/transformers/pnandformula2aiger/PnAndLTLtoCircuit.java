package uniolunisaar.adam.logic.transformers.pnandformula2aiger;

import uniolunisaar.adam.logic.transformers.pn2aiger.Circuit;
import uniolunisaar.adam.logic.transformers.pn2aiger.AigerRenderer;
import java.io.IOException;
import uniol.apt.io.parser.ParseException;
import uniolunisaar.adam.ds.logics.ltl.ILTLFormula;
import uniolunisaar.adam.ds.petrigame.PetriGame;
import uniolunisaar.adam.ds.logics.ltl.LTLFormula;
import uniolunisaar.adam.ds.logics.ltl.LTLOperators;
import uniolunisaar.adam.exceptions.ExternalToolException;
import uniolunisaar.adam.logic.transformers.flowltl.FlowLTLTransformer;
import uniolunisaar.adam.logic.transformers.flowltl.FlowLTLTransformerHyperLTL;
import uniolunisaar.adam.util.logics.transformers.logics.TransformerTools;
import uniolunisaar.adam.tools.Logger;
import uniolunisaar.adam.tools.ProcessNotStartedException;
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
    private Maximality maximality = Maximality.MAX_INTERLEAVING;
    private Stuttering stuttering = Stuttering.PREFIX_REGISTER;

    public PnAndLTLtoCircuit() {
    }

    public PnAndLTLtoCircuit(TransitionSemantics semantics, Maximality maximality, Stuttering stuttering) {
        this.semantics = semantics;
        this.maximality = maximality;
        this.stuttering = stuttering;
    }

    /**
     *
     * @param net
     * @param formula
     * @param path
     * @param verbose
     * @return
     * @throws InterruptedException
     * @throws IOException
     * @throws uniol.apt.io.parser.ParseException
     * @throws uniolunisaar.adam.tools.ProcessNotStartedException
     * @throws uniolunisaar.adam.exceptions.ExternalToolException
     */
    public AigerRenderer createCircuit(PetriGame net, ILTLFormula formula, String path, boolean verbose) throws InterruptedException, IOException, ParseException, ProcessNotStartedException, ExternalToolException {
        return createCircuit(net, formula, path, verbose, null);
    }

    /**
     *
     * @param net
     * @param formula
     * @param path
     * @param verbose
     * @param stats
     * @return
     * @throws InterruptedException
     * @throws IOException
     * @throws uniol.apt.io.parser.ParseException
     * @throws uniolunisaar.adam.tools.ProcessNotStartedException
     * @throws uniolunisaar.adam.exceptions.ExternalToolException
     */
    public AigerRenderer createCircuit(PetriGame net, ILTLFormula formula, String path, boolean verbose, PnAndLTLtoCircuitStatistics stats) throws InterruptedException, IOException, ParseException, ProcessNotStartedException, ExternalToolException {
        return createCircuit(net, formula, path, verbose, stats, false);
    }

    /**
     * 
     * @param net
     * @param formula
     * @param path
     * @param verbose
     * @param stats
     * @param skipMax - used if this method is called from the FlowLTL-Part and the maximality is allready handled there.
     * @return
     * @throws InterruptedException
     * @throws IOException
     * @throws ParseException
     * @throws ProcessNotStartedException
     * @throws ExternalToolException 
     */
    AigerRenderer createCircuit(PetriGame net, ILTLFormula formula, String path, boolean verbose, PnAndLTLtoCircuitStatistics stats, boolean skipMax) throws InterruptedException, IOException, ParseException, ProcessNotStartedException, ExternalToolException {
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

        Logger.getInstance().addMessage("This means we create the product for F='" + formula.toSymbolString() + "'.");
        CircuitAndLTLtoCircuit.createCircuit(net, renderer, FlowLTLTransformerHyperLTL.toMCHyperFormat(formula), path, stats, verbose);
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
