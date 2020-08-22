package uniolunisaar.adam.logic.parser.logics.flowctl.separate;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import uniol.apt.adt.pn.PetriNet;
import uniol.apt.io.parser.ParseException;
import uniolunisaar.adam.ds.logics.ctl.flowctl.separate.RunCTLSeparateFormula;
import uniolunisaar.adam.exceptions.pnwt.LineParseException;
import uniolunisaar.adam.logic.parser.ParsingUtils;
import uniolunisaar.adam.logic.parser.logics.flowctl.separate.antlr.FlowCTLSeparateFormatLexer;
import uniolunisaar.adam.logic.parser.logics.flowctl.separate.antlr.FlowCTLSeparateFormatParser;

/**
 *
 * @author Manuel Gieseking
 */
public class FlowCTLSeparateParser {

    public static RunCTLSeparateFormula parse(PetriNet net, String formula) throws ParseException {
        try {
            FlowCTLSeparateFormatLexer lexer = new FlowCTLSeparateFormatLexer(new ANTLRInputStream(formula));

            // Get a list of matched tokens
            CommonTokenStream tokens = new CommonTokenStream(lexer);

            // Pass the tokens to the parser
            FlowCTLSeparateFormatParser parser = new FlowCTLSeparateFormatParser(tokens);

            ParsingUtils.handleErrors(lexer, parser, true);

            // Specify our entry point
            FlowCTLSeparateFormatParser.FlowCTLContext context = parser.flowCTL();

            // Walk it and attach our listener
            FlowCTLSeparateListener listener = new FlowCTLSeparateListener(net);
            ParsingUtils.walk(listener, context);
            return listener.getFormula();
        } catch (LineParseException e) {
            throw new ParseException("Error while parsing formula '" + formula + "'", e);
        }
    }
}
