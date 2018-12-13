package uniolunisaar.adam.logic.parser.logics;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

/**
 *
 * @author Manuel Gieseking
 */
public class FlowLogicsParser {
 

    public static void walk(ParseTreeListener listener, ParseTree context) {
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(listener, context);
    }

    public static void handleErrors(Lexer lexer, Parser parser) {
        FlowLogicsParseExceptionListener errorlist = new FlowLogicsParseExceptionListener();
        handleErrors(lexer, errorlist);
        handleErrors(parser, errorlist);
    }

    public static void handleErrors(Recognizer<?,?> rec, FlowLogicsParseExceptionListener errorlist) {
        rec.removeErrorListeners(); // don't spam on stderr
        rec.addErrorListener(errorlist);
    }
}
