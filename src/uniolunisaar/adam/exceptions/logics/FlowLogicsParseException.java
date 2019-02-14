package uniolunisaar.adam.exceptions.logics;

/**
 *
 * @author Manuel Gieseking
 */
public class FlowLogicsParseException extends RuntimeException {

    public static final long serialVersionUID = 0x1l;
    private final int column;

    public FlowLogicsParseException(String message, int column, Throwable cause) {
        super(message, cause);
        this.column = column;
    }

    public int getColumn() {
        return column;
    }

}
