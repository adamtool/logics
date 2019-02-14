package uniolunisaar.adam.util.logics.transformers.logics;

/**
 *
 * @author Manuel Gieseking
 */
public class ModelCheckingOutputData {

    private String outputPath;
    private boolean outputCircuit;
    private boolean outputTransformedNet;
    private boolean verbose;

    public ModelCheckingOutputData(String path, boolean outputCircuit, boolean outputTransformedNet, boolean verbose) {
        this.outputPath = path;
        this.outputCircuit = outputCircuit;
        this.outputTransformedNet = outputTransformedNet;
        this.verbose = verbose;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public String getPath() {
        return outputPath;
    }

    public void setPath(String path) {
        this.outputPath = path;
    }

    public boolean isOutputCircuit() {
        return outputCircuit;
    }

    public void setOutputCircuit(boolean outputCircuit) {
        this.outputCircuit = outputCircuit;
    }

    public boolean isOutputTransformedNet() {
        return outputTransformedNet;
    }

    public void setOutputTransformedNet(boolean outputTransformedNet) {
        this.outputTransformedNet = outputTransformedNet;
    }

}
