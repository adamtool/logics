package uniolunisaar.adam.logic.parser.logics.flowltl;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import uniol.apt.adt.pn.PetriNet;
import uniol.apt.io.parser.ParseException;
import uniolunisaar.adam.ds.logics.ltl.flowltl.RunLTLFormula;
import uniolunisaar.adam.exceptions.pnwt.LineParseException;
import uniolunisaar.adam.logic.parser.ParsingUtils;
import uniolunisaar.adam.logic.parser.logics.flowltl.antlr.FlowLTLFormatLexer;
import uniolunisaar.adam.logic.parser.logics.flowltl.antlr.FlowLTLFormatParser;

/**
 *
 * @author Manuel Gieseking
 */
public class FlowLTLParser {

    public static RunLTLFormula parse(PetriNet net, String formula) throws ParseException {
        try {
            FlowLTLFormatLexer lexer = new FlowLTLFormatLexer(new ANTLRInputStream(formula));

            // Get a list of matched tokens
            CommonTokenStream tokens = new CommonTokenStream(lexer);

            // Pass the tokens to the parser
            FlowLTLFormatParser parser = new FlowLTLFormatParser(tokens);

            ParsingUtils.handleErrors(lexer, parser, true);

            // Specify our entry point
            FlowLTLFormatParser.FlowLTLContext context = parser.flowLTL();

            // Walk it and attach our listener
            FlowLTLListener listener = new FlowLTLListener(net);
            ParsingUtils.walk(listener, context);
            return listener.getFormula();
        } catch (LineParseException e) {
            throw new ParseException("Error while parsing formula '" + formula + "'", e);
        }
    }
}
