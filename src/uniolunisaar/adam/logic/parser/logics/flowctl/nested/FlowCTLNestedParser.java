package uniolunisaar.adam.logic.parser.logics.flowctl.nested;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import uniol.apt.adt.pn.PetriNet;
import uniol.apt.io.parser.ParseException;
import uniolunisaar.adam.ds.logics.ctl.flowctl.RunCTLFormula;
import uniolunisaar.adam.exceptions.pnwt.LineParseException;
import uniolunisaar.adam.logic.parser.ParsingUtils;
import uniolunisaar.adam.logic.parser.logics.flowctl.nested.antlr.FlowCTLNestedFormatLexer;
import uniolunisaar.adam.logic.parser.logics.flowctl.nested.antlr.FlowCTLNestedFormatParser;

/**
 *
 * @author Manuel Gieseking
 */
public class FlowCTLNestedParser {

    public static RunCTLFormula parse(PetriNet net, String formula) throws ParseException {
        try {
            FlowCTLNestedFormatLexer lexer = new FlowCTLNestedFormatLexer(new ANTLRInputStream(formula));

            // Get a list of matched tokens
            CommonTokenStream tokens = new CommonTokenStream(lexer);

            // Pass the tokens to the parser
            FlowCTLNestedFormatParser parser = new FlowCTLNestedFormatParser(tokens);

            ParsingUtils.handleErrors(lexer, parser, true);

            // Specify our entry point
            FlowCTLNestedFormatParser.FlowCTLContext context = parser.flowCTL();

            // Walk it and attach our listener
            FlowCTLNestedListener listener = new FlowCTLNestedListener(net);
            ParsingUtils.walk(listener, context);
            return listener.getFormula();
        } catch (LineParseException e) {
            throw new ParseException("Error while parsing formula '" + formula + "'", e);
        }
    }
}
