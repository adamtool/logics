package uniolunisaar.adam.util.logics.transformers.logics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.ArrayList;
import java.util.List;
import uniol.apt.adt.pn.PetriNet;
import uniolunisaar.adam.ds.logics.ltl.flowltl.FlowFormula;
import uniolunisaar.adam.ds.logics.FormulaBinary;
import uniolunisaar.adam.ds.logics.IFormula;
import uniolunisaar.adam.ds.logics.ltl.ILTLFormula;
import uniolunisaar.adam.ds.logics.ltl.flowltl.RunFormula;
import uniolunisaar.adam.exceptions.ExternalToolException;
import uniolunisaar.adam.exceptions.ProcessNotStartedException;
import uniolunisaar.adam.logic.externaltools.logics.AigToDot;
import uniolunisaar.adam.util.logics.FormulaCreatorIngoingSemantics;
import uniolunisaar.adam.util.logics.FormulaCreatorOutgoingSemantics;
import uniolunisaar.adam.logic.transformers.pn2aiger.AigerRenderer;
import uniolunisaar.adam.logic.transformers.pnandformula2aiger.PnAndLTLtoCircuit.Maximality;
import static uniolunisaar.adam.logic.transformers.pnandformula2aiger.PnAndLTLtoCircuit.Maximality.MAX_CONCURRENT;
import static uniolunisaar.adam.logic.transformers.pnandformula2aiger.PnAndLTLtoCircuit.Maximality.MAX_INTERLEAVING;
import static uniolunisaar.adam.logic.transformers.pnandformula2aiger.PnAndLTLtoCircuit.Maximality.MAX_INTERLEAVING_IN_CIRCUIT;
import static uniolunisaar.adam.logic.transformers.pnandformula2aiger.PnAndLTLtoCircuit.Maximality.MAX_NONE;
import uniolunisaar.adam.logic.transformers.pnandformula2aiger.PnAndLTLtoCircuit.TransitionSemantics;
import uniolunisaar.adam.tools.AdamProperties;
import uniolunisaar.adam.tools.ExternalProcessHandler;
import uniolunisaar.adam.tools.Logger;
import uniolunisaar.adam.tools.ProcessPool;

/**
 *
 * @author Manuel Gieseking
 */
public class TransformerTools {

    public static List<FlowFormula> getFlowFormulas(IFormula formula) {
        List<FlowFormula> flowFormulas = new ArrayList<>();
        if (formula instanceof FlowFormula) {
            flowFormulas.add((FlowFormula) formula);
            return flowFormulas;
        } else if (formula instanceof ILTLFormula) {
            return flowFormulas;
        } else if (formula instanceof RunFormula) {
            return getFlowFormulas(((RunFormula) formula).getPhi());
        } else if (formula instanceof FormulaBinary) {
            FormulaBinary binF = (FormulaBinary) formula;
            flowFormulas.addAll(getFlowFormulas(binF.getPhi1()));
            flowFormulas.addAll(getFlowFormulas(binF.getPhi2()));
        }
        return flowFormulas;
    }

    /**
     *
     * @param maximality
     * @param semantics
     * @param net
     * @return null iff MAX_NONE
     */
    public static ILTLFormula getMaximality(Maximality maximality, TransitionSemantics semantics, PetriNet net) {
        switch (maximality) {
            case MAX_INTERLEAVING:
                if (semantics == TransitionSemantics.INGOING) {
                    return FormulaCreatorIngoingSemantics.getMaximalityInterleavingDirectAsObject(net);
                } else {
                    return FormulaCreatorOutgoingSemantics.getMaximalityInterleavingDirectAsObject(net);
                }
            case MAX_CONCURRENT:
                if (semantics == TransitionSemantics.INGOING) {
                    return FormulaCreatorIngoingSemantics.getMaximalityConcurrentDirectAsObject(net);
                } else {
                    return FormulaCreatorOutgoingSemantics.getMaximalityConcurrentDirectAsObject(net);
                }
            case MAX_NONE:
                return null;
            case MAX_INTERLEAVING_IN_CIRCUIT:
                return null;
        }
        throw new RuntimeException("Not all maximality terms had been considered: " + maximality);
    }

    public static void save2Aiger(PetriNet net, AigerRenderer renderer, String path) throws FileNotFoundException {
        String aigerFile = renderer.render(net).toString();
        // save aiger file
        try (PrintStream out = new PrintStream(path + ".aag")) {
            out.println(aigerFile);
        }
    }

    public static void saveAiger2Dot(String input, String output, String procFamilyID) throws ExternalToolException, IOException, InterruptedException {
        AigToDot.call(input, output, procFamilyID);
    }

    public static Thread saveAiger2DotAndPDF(String input, String output, String procFamilyID) throws IOException, InterruptedException, ExternalToolException {
        saveAiger2Dot(input, output, procFamilyID);        
        String dot = AdamProperties.getInstance().getProperty(AdamProperties.DOT);
        String[] command = {dot, "-Tpdf", output + ".dot", "-o", output + ".pdf"};
        // Mac:
        // String[] command = {"/usr/local/bin/dot", "-Tpdf", output + ".dot", "-o", output + ".pdf"};
        ExternalProcessHandler procH = new ExternalProcessHandler(true, command);
        ProcessPool.getInstance().putProcess(procFamilyID + "#dot2pdf", procH);
        // start it in an extra thread
        Thread thread = new Thread(() -> {
            try {
                procH.startAndWaitFor();
//                    if (deleteDot) {
//                        // Delete dot file
//                        new File(path + ".dot").delete();
//                        Logger.getInstance().addMessage("Deleted: " + path + ".dot", true);
//                    }
            } catch (IOException | InterruptedException ex) {
                String errors = "";
                try {
                    errors = procH.getErrors();
                } catch (ProcessNotStartedException e) {
                }
                Logger.getInstance().addError("Saving pdf from dot failed.\n" + errors, ex);
            }
        });
        thread.start();
        return thread;
    }

    public static Thread saveAiger2PDF(String input, String output, String procFamiliyID) throws IOException, InterruptedException, ExternalToolException {
        String bufferpath = output + "_" + System.currentTimeMillis();
        Thread dot = saveAiger2DotAndPDF(input, bufferpath, procFamiliyID);
        Thread mvPdf = new Thread(() -> {
            try {
                dot.join();
                // Delete dot file
                new File(bufferpath + ".dot").delete();
                Logger.getInstance().addMessage("Deleted: " + bufferpath + ".dot", true);
                // move to original name
                Files.move(new File(bufferpath + ".pdf").toPath(), new File(output + ".pdf").toPath(), REPLACE_EXISTING);
                Logger.getInstance().addMessage("Moved: " + bufferpath + ".pdf --> " + output + ".pdf", true);
            } catch (IOException | InterruptedException ex) {
                Logger.getInstance().addError("Deleting the buffer files and moving the pdf failed", ex);
            }
        });
        mvPdf.start();
        return mvPdf;
    }
}
