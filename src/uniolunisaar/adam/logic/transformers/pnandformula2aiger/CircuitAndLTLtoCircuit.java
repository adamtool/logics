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
import uniolunisaar.adam.exceptions.ProcessNotStartedException;
import uniolunisaar.adam.tools.Tools;
import uniolunisaar.adam.util.logics.benchmarks.mc.BenchmarksMC;
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
     * @throws uniolunisaar.adam.exceptions.ProcessNotStartedException
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
        McHyper.call(inputFile, formula, outputPath, verbose, net.getName());

        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% COLLECT STATISTICS
        if (stats != null) {
            // system size
            int nb_latches = circuit.getNbOfLatches();
            int nb_gates = circuit.getNbOfGates();
            // total size             
            int nb_total_latches = -1;
            int nb_total_gates = -1;
            try ( BufferedReader mcHyperAag = new BufferedReader(new FileReader(outputPath + ".aag"))) {
                String header = mcHyperAag.readLine();
                String[] vals = header.split(" ");
                nb_total_latches = Integer.parseInt(vals[3]);
                nb_total_gates = Integer.parseInt(vals[5]);
            }
            // set the values
            stats.setSys_nb_latches(nb_latches);
            stats.setSys_nb_gates(nb_gates);
            stats.setTotal_nb_latches(nb_total_latches);
            stats.setTotal_nb_gates(nb_total_gates);
            // output the values
            if (BenchmarksMC.EDACC) {
                Logger.getInstance().addMessage("nb_places: " + stats.getIn_nb_places(), "edacc");
                Logger.getInstance().addMessage("nb_transitions: " + stats.getIn_nb_transitions(), "edacc");
                Logger.getInstance().addMessage("size_f: " + stats.getIn_size_formula(), "edacc");
                Logger.getInstance().addMessage("nb_mc_places: " + stats.getMc_nb_places(), "edacc");
                Logger.getInstance().addMessage("nb_mc_transitions: " + stats.getMc_nb_transitions(), "edacc");
                Logger.getInstance().addMessage("size_mc_f: " + stats.getMc_size_formula(), "edacc");
                Logger.getInstance().addMessage("nb_total_latches: " + nb_total_latches, "edacc");
                Logger.getInstance().addMessage("nb_total_gates: " + nb_total_gates, "edacc");
            } else {
                Logger.getInstance().addMessage(stats.getInputSizes(), true);
                // if a file is given already write them to the file
                stats.writeInputSizesToFile();
            }
        }
        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% END COLLECT STATISTICS

        // %%%%%%%%%%%%%%%% Aiger
        inputFile = outputPath + ".aag";
        outputPath = output + ".aig";
        AigToAig.call(inputFile, outputPath, verbose, net.getName());
    }

    /**
     *
     * @param net
     * @param circ
     * @param output
     * @param formula - in MCHyper format
     * @throws InterruptedException
     * @throws IOException
     * @throws uniolunisaar.adam.exceptions.ProcessNotStartedException
     * @throws uniolunisaar.adam.exceptions.ExternalToolException
     */
    public static void createCircuit(PetriNet net, AigerRenderer circ, String formula, String output) throws InterruptedException, IOException, ProcessNotStartedException, ExternalToolException {
        createCircuit(net, circ, formula, output, null, true);
    }

}
