package uniolunisaar.adam.exceptions.logics;

/**
 *
 * @author Manuel Gieseking
 */
public class NotSubstitutableException extends Exception {

    private static final long serialVersionUID = 1L;

    public NotSubstitutableException() {
    }

    public NotSubstitutableException(String message) {
        super(message);
    }

    public NotSubstitutableException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotSubstitutableException(Throwable cause) {
        super(cause);
    }

    public NotSubstitutableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
