package uniolunisaar.adam.logic.parser.logics.flowltl;

import uniolunisaar.adam.logic.parser.logics.FlowLogicsParseException;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import uniol.apt.adt.pn.PetriNet;
import uniol.apt.io.parser.ParseException;
import uniolunisaar.adam.ds.logics.ltl.flowltl.RunFormula;
import uniolunisaar.adam.logic.logics.ltl.flowltlparser.antlr.FlowLTLFormatLexer;
import uniolunisaar.adam.logic.logics.ltl.flowltlparser.antlr.FlowLTLFormatParser;
import uniolunisaar.adam.logic.parser.logics.FlowLogicsParser;

/**
 *
 * @author Manuel Gieseking
 */
public class FlowLTLParser extends FlowLogicsParser {

    public static RunFormula parse(PetriNet net, String formula) throws ParseException {
        try {
            FlowLTLFormatLexer lexer = new FlowLTLFormatLexer(new ANTLRInputStream(formula));

            // Get a list of matched tokens
            CommonTokenStream tokens = new CommonTokenStream(lexer);

            // Pass the tokens to the parser
            FlowLTLFormatParser parser = new FlowLTLFormatParser(tokens);

            handleErrors(lexer, parser);

            // Specify our entry point
            FlowLTLFormatParser.FlowLTLContext context = parser.flowLTL();

            // Walk it and attach our listener
            FlowLTLListener listener = new FlowLTLListener(net);
            walk(listener, context);
            return listener.getFormula();
        } catch (FlowLogicsParseException e) {
            throw new ParseException("Error while parsing formula '" + formula + "'", e);
        }
    }
}
