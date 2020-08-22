package uniolunisaar.adam.logic.parser.logics.flowctl.forall;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import uniol.apt.adt.pn.PetriNet;
import uniol.apt.io.parser.ParseException;
import uniolunisaar.adam.ds.logics.ctl.flowctl.forall.RunCTLForAllFormula;
import uniolunisaar.adam.exceptions.pnwt.LineParseException;
import uniolunisaar.adam.logic.parser.ParsingUtils;
import uniolunisaar.adam.logic.parser.logics.flowctl.forall.antlr.FlowCTLForAllFormatLexer;
import uniolunisaar.adam.logic.parser.logics.flowctl.forall.antlr.FlowCTLForAllFormatParser;

/**
 *
 * @author Manuel Gieseking
 */
public class FlowCTLForAllParser {

    public static RunCTLForAllFormula parse(PetriNet net, String formula) throws ParseException {
        try {
            FlowCTLForAllFormatLexer lexer = new FlowCTLForAllFormatLexer(new ANTLRInputStream(formula));

            // Get a list of matched tokens
            CommonTokenStream tokens = new CommonTokenStream(lexer);

            // Pass the tokens to the parser
            FlowCTLForAllFormatParser parser = new FlowCTLForAllFormatParser(tokens);

            ParsingUtils.handleErrors(lexer, parser, true);

            // Specify our entry point
            FlowCTLForAllFormatParser.FlowCTLContext context = parser.flowCTL();

            // Walk it and attach our listener
            FlowCTLForAllListener listener = new FlowCTLForAllListener(net);
            ParsingUtils.walk(listener, context);
            return listener.getFormula();
        } catch (LineParseException e) {
            throw new ParseException("Error while parsing formula '" + formula + "'", e);
        }
    }
}
