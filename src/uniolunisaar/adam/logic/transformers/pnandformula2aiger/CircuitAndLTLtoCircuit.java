package uniolunisaar.adam.logic.transformers.pnandformula2aiger;

import uniolunisaar.adam.ds.circuits.AigerFile;
import java.io.BufferedReader;
import java.io.FileReader;
import uniolunisaar.adam.logic.transformers.pn2aiger.AigerRenderer;
import java.io.IOException;
import uniol.apt.adt.pn.PetriNet;
import uniolunisaar.adam.logic.externaltools.logics.AigToAig;
import uniolunisaar.adam.logic.externaltools.logics.McHyper;
import uniolunisaar.adam.exceptions.ExternalToolException;
import uniolunisaar.adam.tools.Logger;
import uniolunisaar.adam.tools.ProcessNotStartedException;
import uniolunisaar.adam.tools.Tools;
import uniolunisaar.adam.util.logics.transformers.logics.PnAndLTLtoCircuitStatistics;

/**
 *
 * @author Manuel Gieseking
 */
public class CircuitAndLTLtoCircuit {

    /**
     *
     *
     * @param net
     * @param circ
     * @param formula - in MCHyper format
     * @param output
     * @param stats
     * @param verbose
     * @throws InterruptedException
     * @throws IOException
     * @throws uniolunisaar.adam.tools.ProcessNotStartedException
     * @throws uniolunisaar.adam.exceptions.ExternalToolException
     */
    public static void createCircuit(PetriNet net, AigerRenderer circ, String formula, String output, PnAndLTLtoCircuitStatistics stats, boolean verbose) throws InterruptedException, IOException, ProcessNotStartedException, ExternalToolException {
        // Create System 
        String input = output + "_system.aag";
        AigerFile circuit = circ.render(net);
        Tools.saveFile(input, circuit.toString());

//        ModelCheckerTools.save2Aiger(net, circ, path);
//        ProcessBuilder procBuilder = new ProcessBuilder(AdamProperties.getInstance().getProperty(AdamProperties.MC_HYPER), path + ".aag", formula, path + "_mcHyperOut");
//
//        Logger.getInstance().addMessage(procBuilder.command().toString(), true);
//        Process procAiger = procBuilder.start();
//        // buffering the output and error as it comes
//        try (BufferedReader is = new BufferedReader(new InputStreamReader(procAiger.getInputStream()))) {
//            String line;
//            while ((line = is.readLine()) != null) {
//                Logger.getInstance().addMessage("[MCHyper-Out]: " + line, true);
//            }
//        }
//        try (BufferedReader is = new BufferedReader(new InputStreamReader(procAiger.getErrorStream()))) {
//            String line;
//            while ((line = is.readLine()) != null) {
//                Logger.getInstance().addMessage("[MCHyper-ERROR]: " + line, true);
//            }
//        }
        // buffering in total
//        String error = IOUtils.toString(procAiger.getErrorStream());
//        Logger.getInstance().addMessage(error, true); // todo: print it as error and a proper exception
//        String output = IOUtils.toString(procAiger.getInputStream());
//        Logger.getInstance().addMessage(output, true);
//        procAiger.waitFor();
        final String timeCommand = "/usr/bin/time";
        final String fileOutput = "-f";
        final String fileArgument = "wall_time_(s)%e\\nCPU_time_(s)%U\\nmemory_(KB)%M\\n";
        final String outputOption = "-o";
        final String outputFile = output + "_stats_time_mem.txt";

        //%%%%%%%%%%%%%%%%%% MCHyper
        String inputFile = input;
        String outputPath = output;
        McHyper.call(inputFile, formula, outputPath, verbose);

        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% COLLECT STATISTICS
        if (stats != null) {
            // system size
            stats.setSys_nb_latches(circuit.getNbOfLatches());
            stats.setSys_nb_gates(circuit.getNbOfGates());
            // total size 
            try (BufferedReader mcHyperAag = new BufferedReader(new FileReader(outputPath + ".aag"))) {
                String header = mcHyperAag.readLine();
                String[] vals = header.split(" ");
                stats.setTotal_nb_latches(Integer.parseInt(vals[3]));
                stats.setTotal_nb_gates(Integer.parseInt(vals[5]));
            }
            Logger.getInstance().addMessage(stats.getInputSizes(), true);
            // if a file is given already write them to the file
            stats.writeInputSizesToFile();
        }
        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% END COLLECT STATISTICS

        // %%%%%%%%%%%%%%%% Aiger
        inputFile = outputPath + ".aag";
        outputPath = output + ".aig";
        AigToAig.call(inputFile, outputPath, verbose);
    }

    /**
     *
     * @param net
     * @param circ
     * @param output
     * @param formula - in MCHyper format
     * @throws InterruptedException
     * @throws IOException
     * @throws uniolunisaar.adam.tools.ProcessNotStartedException
     * @throws uniolunisaar.adam.exceptions.ExternalToolException
     */
    public static void createCircuit(PetriNet net, AigerRenderer circ, String formula, String output) throws InterruptedException, IOException, ProcessNotStartedException, ExternalToolException {
        createCircuit(net, circ, formula, output, null, true);
    }

}
