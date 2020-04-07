package uniolunisaar.adam.logic.parser.logics.flowctl;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import uniol.apt.adt.pn.PetriNet;
import uniol.apt.io.parser.ParseException;
import uniolunisaar.adam.ds.logics.ctl.flowctl.RunCTLFormula;
import uniolunisaar.adam.exceptions.pnwt.LineParseException;
import uniolunisaar.adam.logic.parser.ParsingUtils;
import uniolunisaar.adam.logic.parser.logics.flowctl.antlr.FlowCTLFormatLexer;
import uniolunisaar.adam.logic.parser.logics.flowctl.antlr.FlowCTLFormatParser;

/**
 *
 * @author Manuel Gieseking
 */
public class FlowCTLParser {

    public static RunCTLFormula parse(PetriNet net, String formula) throws ParseException {
        try {
            FlowCTLFormatLexer lexer = new FlowCTLFormatLexer(new ANTLRInputStream(formula));

            // Get a list of matched tokens
            CommonTokenStream tokens = new CommonTokenStream(lexer);

            // Pass the tokens to the parser
            FlowCTLFormatParser parser = new FlowCTLFormatParser(tokens);

            ParsingUtils.handleErrors(lexer, parser, true);

            // Specify our entry point
            FlowCTLFormatParser.FlowCTLContext context = parser.flowCTL();

            // Walk it and attach our listener
            FlowCTLListener listener = new FlowCTLListener(net);
            ParsingUtils.walk(listener, context);
            return listener.getFormula();
        } catch (LineParseException e) {
            throw new ParseException("Error while parsing formula '" + formula + "'", e);
        }
    }
}
