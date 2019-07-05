package uniolunisaar.adam.util.logics;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import uniolunisaar.adam.tools.Logger;
import uniolunisaar.adam.tools.Tools;

/**
 *
 * @author Manuel Gieseking
 */
public class OptimizeAigerCircuitsByText {

    public static boolean withEqualInputs = false;
    public static boolean withEqualOrCommGates = false;
    public static boolean withOneInputisZero = true;
    public static boolean withOneInputisOne = true;

    /**
     * This method only optimizes for gates with<br/>
     * 1) one input is 0<br/>
     * 2) one input is 1<br/>
     *
     * Commented out:<br/>
     * 1) both inputs are equal - regEx with backreference is too expensive
     * <br/>
     * 2) two gates with the same or interchanged inputs - regExr with
     * backreference is too expensive <br/>
     *
     * Missing:<br/>
     * 1) on input is the negation of the other - not practical with
     * regexpr<br/>
     *
     * @param output
     * @param withIdxSqueezing
     * @throws IOException
     */
    public static void optimizeByTextReplacement(String output, boolean withIdxSqueezing) throws IOException {
        Logger.getInstance().addMessage("Optimizing the aiger file ...", false);
        // read file splitted into the different sections
        String header;
        String inputs = "";
        String latches = "";
        String outputs = "";
        String gates = "";
        String symbols = "";
        String comments = "";
        try (BufferedReader reader = new BufferedReader(new FileReader(output + ".aag"))) {
            header = reader.readLine();
            // 0 - aag; 1 - max idx; 2 - nb in; 3 - nb latches; 4 - nb out; 5 - nb ands
            String[] idxs = header.split(" ");
            // inputs
            for (int i = 0; i < Integer.parseInt(idxs[2]); i++) {
                inputs += reader.readLine() + System.lineSeparator();
            }
            // latches
            for (int i = 0; i < Integer.parseInt(idxs[3]); i++) {
                latches += reader.readLine() + System.lineSeparator();
            }
            // outputs
            for (int i = 0; i < Integer.parseInt(idxs[4]); i++) {
                outputs += reader.readLine() + System.lineSeparator();
            }
            // gates
            for (int i = 0; i < Integer.parseInt(idxs[5]); i++) {
                gates += reader.readLine() + System.lineSeparator();
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
        String linSep = System.lineSeparator();
        String regEx, regExA, regExB;
        Matcher m;
        int count = 0;

        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% pattern match two equal inputs        
        if (withEqualInputs) {
            regEx = "(^|" + linSep + ")(\\d+) (\\d+) (\\3)" + linSep;
            Pattern patEqual = Pattern.compile(regEx);
            m = patEqual.matcher(gates);
            while (m.find()) {
//            System.out.println(m.group(0));
                gates = m.replaceFirst(m.group(1));// could be faster since only first occurence is searched?
//            gates = gates.replace(m.group(0), m.group(1));
                String old = m.group(2);
                String with = m.group(3);
                latches = replaceLatches(latches, old, with);
                outputs = replaceOutputs(outputs, old, with);
                gates = replaceGates(gates, old, with);
                old = String.valueOf(Integer.parseInt(old) + 1);
                int replace = Integer.parseInt(with);
                with = String.valueOf((replace % 2 == 0) ? replace + 1 : replace - 1);
                latches = replaceLatches(latches, old, with);
                outputs = replaceOutputs(outputs, old, with);
                gates = replaceGates(gates, old, with);
                ++count;
                m = patEqual.matcher(gates);
            }
        }

        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% pattern match two equal gates with the same inputs      
        if (withEqualOrCommGates) {
            regEx = "(^|" + linSep + ")(\\d+) (\\d+) (\\d+)" + linSep;
            regExA = linSep + "(\\d+) (\\4) (\\5)" + linSep;
            regExB = linSep + "(\\d+) (\\5) (\\4)" + linSep;
            Pattern patEqComGat = Pattern.compile("(" + regEx + ").*((" + regExA + ")|(" + regExB + "))");
            m = patEqComGat.matcher(gates);
            while (m.find()) {
                gates = gates.replace(m.group(1), m.group(2));
                String old = m.group(3);
                String with = (m.group(7) != null) ? m.group(8) : m.group(12); // else should m.group(11) !=null
                latches = replaceLatches(latches, old, with);
                outputs = replaceOutputs(outputs, old, with);
                gates = replaceGates(gates, old, with);
                old = String.valueOf(Integer.parseInt(old) + 1);
                int replace = Integer.parseInt(with);
                with = String.valueOf((replace % 2 == 0) ? replace + 1 : replace - 1);
                latches = replaceLatches(latches, old, with);
                outputs = replaceOutputs(outputs, old, with);
                gates = replaceGates(gates, old, with);
                ++count;
                m = patEqComGat.matcher(gates);
            }
        }

        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% pattern for one input is zero:
        if (withOneInputisZero) {
            // group(0) - whole string
            // group(1) - zero is at in2
            // group(2) - out iff zero is at in2
            // group(3) - in1 iff zero is at in2
            // group(4) - zero is at in1
            // group(5) - out iff zero is at in1
            // group(6) - in2 iff zero is at in1
            regExA = "(^|" + linSep + ")(\\d+) (\\d+) 0" + linSep;
            regExB = "(^|" + linSep + ")(\\d+) 0 (\\d+)" + linSep;
            Pattern patZero = Pattern.compile("(" + regExA + ")|(" + regExB + ")");
            m = patZero.matcher(gates);
            while (m.find()) {
                String start = null, old = null;
                if (m.group(1) != null) {
                    start = m.group(2);
                    old = m.group(3);
                } else if (m.group(5) != null) {
                    start = m.group(6);
                    old = m.group(7);
                }
//            System.out.println("Out here: " + m.group(0) + " -> " + old + " - " );
                gates = m.replaceFirst(start);// could be faster since only first occurence is searched?
//            gates = gates.replace(m.group(0), start);
                String with = "0";
                latches = replaceLatches(latches, old, with);
                outputs = replaceOutputs(outputs, old, with);
                gates = replaceGates(gates, old, with);
                old = String.valueOf(Integer.parseInt(old) + 1);
                with = "1";
                latches = replaceLatches(latches, old, with);
                outputs = replaceOutputs(outputs, old, with);
                gates = replaceGates(gates, old, with);
                ++count;
                m = patZero.matcher(gates);
            }
        }

        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% pattern for one input is one:
        if (withOneInputisOne) {
            regExA = "(^|" + linSep + ")(\\d+) (\\d+) 1" + linSep;
            regExB = "(^|" + linSep + ")(\\d+) 1 (\\d+)" + linSep;
            Pattern patOne = Pattern.compile("(" + regExA + ")|(" + regExB + ")");
            m = patOne.matcher(gates);
            while (m.find()) {
                String start = null, old = null, with = null;
                if (m.group(1) != null) { // It's the pattern out in1 1                
                    start = m.group(2);
                    old = m.group(3);
                    with = m.group(4);
                } else if (m.group(5) != null) {
                    start = m.group(6);
                    old = m.group(7);
                    with = m.group(8);
                }
//            System.out.println("Out here: " + m.group(0) + " -> " + old + " - " + with);  
                gates = m.replaceFirst(start);// could be faster since only first occurence is searched?
//            gates = gates.replace(m.group(0), start);
                latches = replaceLatches(latches, old, with);
                outputs = replaceOutputs(outputs, old, with);
                gates = replaceGates(gates, old, with);
                old = String.valueOf(Integer.parseInt(old) + 1);
                int replace = Integer.parseInt(with);
                with = String.valueOf((replace % 2 == 0) ? replace + 1 : replace - 1);
                latches = replaceLatches(latches, old, with);
                outputs = replaceOutputs(outputs, old, with);
                gates = replaceGates(gates, old, with);
                ++count;
                m = patOne.matcher(gates);
            }
        }
        if(withIdxSqueezing) {
            
        }
        
        // adapt the header to the new number of gates
        String[] idxs = header.split(" ");
        // 0 - aag; 1 - max idx; 2 - nb in; 3 - nb latches; 4 - nb out; 5 - nb ands
        header = idxs[0] + " " + idxs[1] + " " + idxs[2] + " " + idxs[3] + " " + idxs[4] + " " + (Integer.parseInt(idxs[5]) - count);

        // Save file
        String optFile = header + System.lineSeparator() + inputs + latches + outputs + gates + symbols + comments;
        Tools.saveFile(output + ".aag", optFile.trim());
        Logger.getInstance().addMessage("... finished optimizing the aiger file.", false);
    }

    private static String replaceLatches(String latches, String old, String with) {
        return latches.replace(" " + old + " 0", " " + with + " 0"); // this is only because mchyper adds " 0" to the latches
    }

    private static String replaceOutputs(String outputs, String old, String with) {
        String linSep = System.lineSeparator();
        return outputs.replaceFirst("(^|" + linSep + ")" + old + linSep, "$1" + with + linSep);
    }

    private static String replaceGates(String gates, String old, String with) {
        String linSep = System.lineSeparator();
        gates = gates.replace(" " + old + " ", " " + with + " ");
        return gates.replace(" " + old + linSep, " " + with + linSep);
    }
}
