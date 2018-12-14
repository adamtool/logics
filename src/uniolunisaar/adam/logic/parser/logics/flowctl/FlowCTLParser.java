package uniolunisaar.adam.logic.parser.logics.flowctl;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import uniol.apt.adt.pn.PetriNet;
import uniol.apt.io.parser.ParseException;
import uniolunisaar.adam.ds.logics.ctl.flowctl.FlowCTLFormula;
import uniolunisaar.adam.logic.parser.logics.flowctl.antlr.FlowCTLFormatLexer;
import uniolunisaar.adam.logic.parser.logics.flowctl.antlr.FlowCTLFormatParser;
import uniolunisaar.adam.logic.parser.logics.FlowLogicsParseException;
import uniolunisaar.adam.logic.parser.logics.FlowLogicsParser;
import static uniolunisaar.adam.logic.parser.logics.FlowLogicsParser.handleErrors;
import static uniolunisaar.adam.logic.parser.logics.FlowLogicsParser.walk;

/**
 *
 * @author Manuel Gieseking
 */
public class FlowCTLParser extends FlowLogicsParser {

    public static FlowCTLFormula parse(PetriNet net, String formula) throws ParseException {
        try {
            FlowCTLFormatLexer lexer = new FlowCTLFormatLexer(new ANTLRInputStream(formula));

            // Get a list of matched tokens
            CommonTokenStream tokens = new CommonTokenStream(lexer);

            // Pass the tokens to the parser
            FlowCTLFormatParser parser = new FlowCTLFormatParser(tokens);

            handleErrors(lexer, parser);

            // Specify our entry point
            FlowCTLFormatParser.FlowCTLContext context = parser.flowCTL();

            // Walk it and attach our listener
            FlowCTLListener listener = new FlowCTLListener(net);
            walk(listener, context);
            return listener.getFormula();
        } catch (FlowLogicsParseException e) {
            throw new ParseException("Error while parsing formula '" + formula + "'", e);
        }
    }
}
