package uniolunisaar.adam.util.logics.transformers.logics;

import java.io.FileNotFoundException;
import uniolunisaar.adam.ds.logics.ltl.ILTLFormula;
import uniolunisaar.adam.ds.petrinetwithtransits.PetriNetWithTransits;
import uniolunisaar.adam.tools.Tools;

/**
 *
 * @author Manuel Gieseking
 */
public class PnAndLTLtoCircuitStatistics {

    // input tool
    private long in_nb_places;
    private long in_nb_transitions;
    private long in_size_formula;
    // input transformed
    private PetriNetWithTransits transformedNet;
    private ILTLFormula transformedFormula;
//    private long mc_nb_places;
//    private long mc_nb_transitions;
//    private long mc_size_formula;
    // input circuit
    private long sys_nb_latches;
    private long sys_nb_gates;
    private long total_nb_latches;
    private long total_nb_gates;
    // data sub tools
    private long mcHyper_sec;
    private long mcHyper_mem;
    private long aiger_sec;
    private long aiger_mem;

    private boolean append = false;

    // Write the input sizes directly into a file before the checking starts
    // for time outs
    private String path = null;

    // flag to indicate whether the circuit sizes of the system should 
    // separately be printed
    private boolean printSysCircuitSizes = true;

    public PnAndLTLtoCircuitStatistics() {

    }

    public PnAndLTLtoCircuitStatistics(String path) {
        this.path = path;
    }

    public long getIn_nb_places() {
        return in_nb_places;
    }

    public void setIn_nb_places(long in_nb_places) {
        this.in_nb_places = in_nb_places;
    }

    public long getIn_nb_transitions() {
        return in_nb_transitions;
    }

    public void setIn_nb_transitions(long in_nb_transitions) {
        this.in_nb_transitions = in_nb_transitions;
    }

    public long getIn_size_formula() {
        return in_size_formula;
    }

    public void setIn_size_formula(long in_size_formula) {
        this.in_size_formula = in_size_formula;
    }

    public long getMc_nb_places() {
        if (transformedNet == null) {
            return -1;
        }
        return transformedNet.getPlaces().size();
    }

//    public void setMc_nb_places(long mc_nb_places) {
//        this.mc_nb_places = mc_nb_places;
//    }
    public long getMc_nb_transitions() {
        if (transformedNet == null) {
            return -1;
        }
        return transformedNet.getTransitions().size();
    }

//    public void setMc_nb_transitions(long mc_nb_transitions) {
//        this.mc_nb_transitions = mc_nb_transitions;
//    }
    public long getMc_size_formula() {
        if (transformedFormula == null) {
            return -1;
        }
        return transformedFormula.getSize();
    }

//    public void setMc_size_formula(long mc_size_formula) {
//        this.mc_size_formula = mc_size_formula;
//    }
    public PetriNetWithTransits getMc_net() {
        return transformedNet;
    }

    public void setMc_net(PetriNetWithTransits mc_net) {
        this.transformedNet = mc_net;
    }

    public ILTLFormula getMc_formula() {
        return transformedFormula;
    }

    public void setMc_formula(ILTLFormula mc_formula) {
        this.transformedFormula = mc_formula;
    }

    public long getSys_nb_latches() {
        return sys_nb_latches;
    }

    public void setSys_nb_latches(long sys_nb_latches) {
        this.sys_nb_latches = sys_nb_latches;
    }

    public long getSys_nb_gates() {
        return sys_nb_gates;
    }

    public void setSys_nb_gates(long sys_nb_gates) {
        this.sys_nb_gates = sys_nb_gates;
    }

    public long getTotal_nb_latches() {
        return total_nb_latches;
    }

    public void setTotal_nb_latches(long total_nb_latches) {
        this.total_nb_latches = total_nb_latches;
    }

    public long getTotal_nb_gates() {
        return total_nb_gates;
    }

    public void setTotal_nb_gates(long total_nb_gates) {
        this.total_nb_gates = total_nb_gates;
    }

    public long getMcHyper_sec() {
        return mcHyper_sec;
    }

    public void setMcHyper_sec(long mcHyper_sec) {
        this.mcHyper_sec = mcHyper_sec;
    }

    public long getMcHyper_mem() {
        return mcHyper_mem;
    }

    public void setMcHyper_mem(long mcHyper_mem) {
        this.mcHyper_mem = mcHyper_mem;
    }

    public long getAiger_sec() {
        return aiger_sec;
    }

    public void setAiger_sec(long aiger_sec) {
        this.aiger_sec = aiger_sec;
    }

    public long getAiger_mem() {
        return aiger_mem;
    }

    public void setAiger_mem(long aiger_mem) {
        this.aiger_mem = aiger_mem;
    }

    public boolean isPrintSysCircuitSizes() {
        return printSysCircuitSizes;
    }

    public void setPrintSysCircuitSizes(boolean printSysCircuitSizes) {
        this.printSysCircuitSizes = printSysCircuitSizes;
    }

    public void writeInputSizesToFile() throws FileNotFoundException {
        if (path != null) {
            Tools.saveFile(path, getInputSizes(), append);
        }
    }

    public String getPath() {
        return path;
    }

    public boolean isAppend() {
        return append;
    }

    public void setAppend(boolean append) {
        this.append = append;
    }

    public String getInputSizes() { // todo: when it's only LTL?
        StringBuilder sb = new StringBuilder();
        sb.append("#P, #T, #F, #Pmc, #Tmc, #Fmc, #L, #G, #Lt, #Gt, |=\n");
        sb.append("sizes:")
                .append(in_nb_places).append("  &  ")
                .append(in_nb_transitions).append("  &  ")
                .append(in_size_formula).append("  &  ")
                .append(getMc_nb_places()).append("  &  ")
                .append(getMc_nb_transitions()).append("  &  ")
                .append(getMc_size_formula()).append("  &  ");
        if (printSysCircuitSizes) {
            sb.append(sys_nb_latches).append("  &  ")
                    .append(sys_nb_gates).append("  &  ");
        }
        sb.append(total_nb_latches).append("  &  ")
                .append(total_nb_gates);
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getInputSizes());
        return sb.toString();
    }

}
