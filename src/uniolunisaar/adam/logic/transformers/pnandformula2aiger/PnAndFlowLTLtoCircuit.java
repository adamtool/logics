package uniolunisaar.adam.logic.transformers.pnandformula2aiger;

import java.io.FileNotFoundException;
import java.io.IOException;
import uniol.apt.adt.pn.Place;
import uniol.apt.io.parser.ParseException;
import uniol.apt.io.renderer.RenderException;
import uniolunisaar.adam.ds.logics.ltl.ILTLFormula;
import uniolunisaar.adam.ds.logics.ltl.flowltl.RunFormula;
import uniolunisaar.adam.ds.logics.ltl.flowltl.RunOperators;
import uniolunisaar.adam.util.logics.transformers.logics.ModelCheckingOutputData;
import uniolunisaar.adam.ds.petrinetwithtransits.PetriNetWithTransits;
import uniolunisaar.adam.util.PNWTTools;
import uniolunisaar.adam.exceptions.ExternalToolException;
import uniolunisaar.adam.exceptions.logics.NotConvertableException;
import uniolunisaar.adam.logic.transformers.flowltl.FlowLTLTransformer;
import uniolunisaar.adam.logic.transformers.flowltl.FlowLTLTransformerParallel;
import uniolunisaar.adam.logic.transformers.flowltl.FlowLTLTransformerSequential;
import uniolunisaar.adam.logic.transformers.pn2aiger.AigerRenderer;
import uniolunisaar.adam.logic.transformers.pnwt2pn.PnwtAndFlowLTLtoPNParallel;
import uniolunisaar.adam.logic.transformers.pnwt2pn.PnwtAndFlowLTLtoPNSequential;
import uniolunisaar.adam.logic.transformers.pnwt2pn.PnwtAndFlowLTLtoPNSequentialInhibitor;
import uniolunisaar.adam.util.logics.transformers.logics.TransformerTools;
import uniolunisaar.adam.tools.Logger;
import uniolunisaar.adam.exceptions.ProcessNotStartedException;
import uniolunisaar.adam.util.logics.transformers.logics.PnAndLTLtoCircuitStatistics;

/**
 *
 * @author Manuel Gieseking
 */
public class PnAndFlowLTLtoCircuit extends PnAndLTLtoCircuit {

    public enum Approach {
        PARALLEL,
        SEQUENTIAL,
        SEQUENTIAL_INHIBITOR
    }

    private Approach approach = Approach.SEQUENTIAL_INHIBITOR;
    private boolean initFirst = true;

    public PnAndFlowLTLtoCircuit() {
    }

    public PnAndFlowLTLtoCircuit(TransitionSemantics semantics, Approach approach, Maximality maximality, PnAndLTLtoCircuit.Stuttering stuttering, boolean initFirst) {
        super(semantics, maximality, stuttering);
        this.approach = approach;
        this.initFirst = initFirst;
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
     * @throws uniolunisaar.adam.exceptions.logics.NotConvertableException
     * @throws uniolunisaar.adam.exceptions.ProcessNotStartedException
     * @throws uniolunisaar.adam.exceptions.ExternalToolException
     */
    public AigerRenderer createCircuit(PetriNetWithTransits net, RunFormula formula, ModelCheckingOutputData data) throws InterruptedException, IOException, ParseException, NotConvertableException, ProcessNotStartedException, ExternalToolException {
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
     * @throws uniolunisaar.adam.exceptions.logics.NotConvertableException
     * @throws uniolunisaar.adam.exceptions.ProcessNotStartedException
     * @throws uniolunisaar.adam.exceptions.ExternalToolException
     */
    public AigerRenderer createCircuit(PetriNetWithTransits net, RunFormula formula, ModelCheckingOutputData data, PnAndLTLtoCircuitStatistics stats) throws InterruptedException, IOException, ParseException, NotConvertableException, ProcessNotStartedException, ExternalToolException {
        Logger.getInstance().addMessage("We create the net '" + net.getName() + "' for the formula '" + formula.toSymbolString() + "'.\n"
                + " With maximality term: " + getMaximality()
                + " approach: " + approach + " semantics: " + getSemantics() + " stuttering: " + getStuttering()
                + " initialization first step: " + initFirst, true);

        // If we have the LTL fragment just use the standard LTLModelchecker
        if (TransformerTools.getFlowFormulas(formula).isEmpty()) {
            Logger.getInstance().addMessage("There is no flow formula within '" + formula.toSymbolString() + "'. Thus, we use the standard model checking algorithm for LTL.");
            return super.createCircuit(net, formula.toLTLFormula(), data, stats);
        }

        // Add Fairness
        RunFormula f = FlowLTLTransformer.addFairness(net, formula);

        // Get the formula for the maximality (null if MAX_NONE)
        boolean skipMax = false;
        ILTLFormula max = TransformerTools.getMaximality(getMaximality(), getSemantics(), net);
        if (max != null) {
            f = new RunFormula(max, RunOperators.Implication.IMP, f);
            skipMax = true; // already done the maximality here
        }
//        IRunFormula f = formula;

        PetriNetWithTransits netMC = null;
        ILTLFormula formulaMC = null;
        if (null != approach) {
            switch (approach) {
                case PARALLEL:
                    netMC = PnwtAndFlowLTLtoPNParallel.createNet4ModelCheckingParallelOneFlowFormula(net);
                    if (data.isOutputTransformedNet()) {
                        PNWTTools.savePnwt2PDF(data.getPath(), netMC, true);
                    }
                    formulaMC = FlowLTLTransformerParallel.createFormula4ModelChecking4CircuitParallel(net, netMC, f);
//            Logger.getInstance().addMessage("Checking the net '" + gameMC.getName() + "' for the formula '" + formulaMC.toSymbolString() + "'.", false);
                    break;
                case SEQUENTIAL:
                    netMC = PnwtAndFlowLTLtoPNSequential.createNet4ModelCheckingSequential(net, f, initFirst);
                    if (data.isOutputTransformedNet()) {
                        // color all original places
                        for (Place p : netMC.getPlaces()) {
                            if (!netMC.hasPartition(p)) {
                                netMC.setPartition(p, 0);
                            }
                        }
                        try {
                            PNWTTools.saveAPT(data.getPath() + "_mc", netMC, false);
                        } catch (RenderException | FileNotFoundException ex) {
                        }
                        PNWTTools.savePnwt2PDF(data.getPath() + "_mc", netMC, true, TransformerTools.getFlowFormulas(formula).size());
                    }
                    formulaMC = FlowLTLTransformerSequential.createFormula4ModelChecking4CircuitSequential(net, netMC, f, initFirst);
                    break;
                case SEQUENTIAL_INHIBITOR:
                    netMC = PnwtAndFlowLTLtoPNSequentialInhibitor.createNet4ModelCheckingSequential(net, f, initFirst);
                    if (data.isOutputTransformedNet()) {
                        // color all original places
                        for (Place p : netMC.getPlaces()) {
                            if (!netMC.hasPartition(p)) {
                                netMC.setPartition(p, 0);
                            }
                        }
                        try {
                            PNWTTools.saveAPT(data.getPath() + "_mc", netMC, false);
                        } catch (RenderException | FileNotFoundException ex) {
                        }
                        PNWTTools.savePnwt2PDF(data.getPath() + "_mc", netMC, true, TransformerTools.getFlowFormulas(formula).size());
                    }
                    formulaMC = FlowLTLTransformerSequential.createFormula4ModelChecking4CircuitSequential(net, netMC, f, initFirst);
                    break;
                default:
                    throw new RuntimeException("Didn't provided a solution for all approaches yet. Approach '" + approach + "' is missing; sry.");
            }
        }
        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% COLLECT STATISTICS
        if (stats != null) {
            int nb_places = net.getPlaces().size();
            int nb_transitions = net.getTransitions().size();
            int size_f = f.getSize();
            // Set the input sizes
            // input orignal net
            stats.setIn_nb_places(nb_places);
            stats.setIn_nb_transitions(nb_transitions);
            stats.setIn_size_formula(size_f);
            // input model checking net
            stats.setMc_net(netMC);
            stats.setMc_formula(formulaMC);
        }
        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% END COLLECT STATISTICS
        return createCircuit(netMC, formulaMC, data, stats, skipMax);
    }

    public Approach getApproach() {
        return approach;
    }

    public void setApproach(Approach approach) {
        this.approach = approach;
    }

    public boolean isInitFirst() {
        return initFirst;
    }

    public void setInitFirst(boolean initFirst) {
        this.initFirst = initFirst;
    }

}
