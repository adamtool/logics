package uniolunisaar.adam.logic.parser.logics;

import uniolunisaar.adam.exceptions.logics.FlowLogicsParseException;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

/**
 *
 * Adapt parse error messages
 *
 * Adaption of vsp's uniol.apt.io.parser.impl.ThrowingErrorListener of APT
 *
 * @author Manuel Gieseking
 */
public class FlowLogicsParseExceptionListener extends BaseErrorListener {

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
            String msg, RecognitionException e) {
        throw new FlowLogicsParseException(" col " + charPositionInLine + ": " + msg, charPositionInLine, e);
    }
}
