package uniolunisaar.adam.util.logics;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import uniolunisaar.adam.ds.circuits.AigerFile;
import uniolunisaar.adam.ds.circuits.AigerFileOptimizedGates;
import uniolunisaar.adam.tools.Logger;
import uniolunisaar.adam.tools.Tools;

/**
 *
 * @author Manuel Gieseking
 */
public class OptimizingAigerCircuitByDataStructure {

    /**
     * Currently the symbols and comments are rerendered or deleted,
     * respectively.
     *
     * @param output
     * @param withIdxSqueezing
     * @throws IOException
     */
    public static void optimizeByCreatingAigerfileAndRendering(String output, boolean withIdxSqueezing) throws IOException {
        Logger.getInstance().addMessage("Optimizing the aiger file ...", false);
        AigerFile file = new AigerFileOptimizedGates(false);
        // read original file and map it into the new one
        String symbols = "";
        String comments = "";
        try (BufferedReader reader = new BufferedReader(new FileReader(output + ".aag"))) {
            String header = reader.readLine();
            // 0 - aag; 1 - max idx; 2 - nb in; 3 - nb latches; 4 - nb out; 5 - nb ands
            String[] idxs = header.split(" ");
            // inputs
            for (int i = 0; i < Integer.parseInt(idxs[2]); i++) {
                file.addInput(reader.readLine());
            }
            // latches
            for (int i = 0; i < Integer.parseInt(idxs[3]); i++) {
                String[] latch = reader.readLine().split(" ");
                file.addLatch(latch[0]);
                int val = Integer.parseInt(latch[1]);
                String id = (val == 0) ? AigerFile.FALSE : (val == 1) ? AigerFile.TRUE : (val % 2 == 0) ? String.valueOf(val) : "!" + (val - 1);
                file.copyValues(latch[0] + AigerFile.NEW_VALUE_OF_LATCH_SUFFIX, id);
            }
            // outputs
            for (int i = 0; i < Integer.parseInt(idxs[4]); i++) {
                file.addOutput("out" + i);
                int val = Integer.parseInt(reader.readLine());
                String id = (val == 0) ? AigerFile.FALSE : (val == 1) ? AigerFile.TRUE : (val % 2 == 0) ? String.valueOf(val) : "!" + (val - 1);
                file.copyValues("out" + i, id);
            }
            // gates
            for (int i = 0; i < Integer.parseInt(idxs[5]); i++) {
                String[] gate = reader.readLine().split(" ");
                int val1 = Integer.parseInt(gate[1]);
                String id1 = (val1 == 0) ? AigerFile.FALSE : (val1 == 1) ? AigerFile.TRUE : (val1 % 2 == 0) ? String.valueOf(val1) : "!" + (val1 - 1);
                int val2 = Integer.parseInt(gate[2]);
                String id2 = (val2 == 0) ? AigerFile.FALSE : (val2 == 1) ? AigerFile.TRUE : (val2 % 2 == 0) ? String.valueOf(val2) : "!" + (val2 - 1);
                file.addGate(gate[0], id1, id2);
            }
            // symbols
            String line = reader.readLine();
            while (line != null && !line.equals("c" + System.lineSeparator())) {
                symbols += line + System.lineSeparator();
                line = reader.readLine();
            }
            // comment section
            while (line != null) {
                comments += line + System.lineSeparator();
                line = reader.readLine();
            }
        }

        String content = file.render(false).trim() + System.lineSeparator() + symbols + comments;

        Tools.saveFile(output + ".aag", content.trim());
        Logger.getInstance().addMessage("... finished optimizing the aiger file.", false);
    }
}
