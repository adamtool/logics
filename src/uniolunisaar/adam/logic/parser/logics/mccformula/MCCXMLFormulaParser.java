package uniolunisaar.adam.logic.parser.logics.mccformula;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import uniol.apt.adt.pn.PetriNet;
import uniol.apt.adt.pn.Place;
import uniol.apt.io.parser.ParseException;
import uniolunisaar.adam.ds.logics.ltl.ILTLFormula;
import uniolunisaar.adam.ds.logics.ltl.LTLAtomicProposition;
import uniolunisaar.adam.ds.logics.ltl.LTLConstants;
import uniolunisaar.adam.ds.logics.ltl.LTLFormula;
import uniolunisaar.adam.ds.logics.ltl.LTLOperators;
import uniolunisaar.adam.util.logics.FormulaCreator;

/**
 *
 * @author Manuel Gieseking
 */
public class MCCXMLFormulaParser {

    public static Map<String, ILTLFormula> parseLTL(String inputFormula, PetriNet net) throws SAXException, ParserConfigurationException, IOException, ParseException {
        InputStream stream = new ByteArrayInputStream(inputFormula.getBytes());
        return parseLTL(stream, net);
    }

    public static Map<String, ILTLFormula> parseLTLFromFile(String path, PetriNet net) throws SAXException, ParserConfigurationException, IOException, ParseException {
        return parseLTL(new FileInputStream(new File(path)), net);
    }

    public static Map<String, ILTLFormula> parseLTL(InputStream in, PetriNet net) throws SAXException, ParserConfigurationException, IOException, ParseException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document document = builder.parse(in);

        NodeList properties = document.getDocumentElement().getElementsByTagName("property");
        Map<String, ILTLFormula> list = new HashMap<>();
        for (int i = 0; i < properties.getLength(); i++) {
            Element property = (Element) properties.item(i);
            String id = property.getElementsByTagName("id").item(0).getFirstChild().getNodeValue();
//            System.out.println("ID: " + id);
            Node formula = property.getElementsByTagName("formula").item(0).getFirstChild();
            Node allPaths = formula.getNextSibling();
            // LTL should start with an all-paths node
            if (!allPaths.getNodeName().equals("all-paths")) {
//                System.out.println("Expecting the LTL MCC formulas to start with an all-path operator");
                throw new ParseException("Expecting the LTL MCC formulas to start with an all-path operator.");
            }
            list.put(id, parseFormula(allPaths.getFirstChild(), net));
        }
        return list;
    }

    private static ILTLFormula parseFormula(Node formula, PetriNet net) throws ParseException {
        Node f = formula.getNextSibling();
        String op = f.getNodeName();
        if (op.equals("globally")) {
            return new LTLFormula(LTLOperators.Unary.G, parseFormula(f.getFirstChild(), net));
        } else if (op.equals("finally")) {
            return new LTLFormula(LTLOperators.Unary.F, parseFormula(f.getFirstChild(), net));
        } else if (op.equals("next")) {
            return new LTLFormula(LTLOperators.Unary.X, parseFormula(f.getFirstChild(), net));
        } else if (op.equals("until")) {
            // before
            Node before = f.getFirstChild().getNextSibling();
            if (!before.getNodeName().equals("before")) {
                throw new ParseException("Something is wrong while parsing an until formula.");
            }
            //until
            Node reach = f.getChildNodes().item(2).getNextSibling();
            if (!reach.getNodeName().equals("reach")) {
                throw new ParseException("Something is wrong while parsing an until formula.");
            }
            return new LTLFormula(parseFormula(before.getFirstChild(), net), LTLOperators.Binary.U, parseFormula(reach.getFirstChild(), net));
        } else if (op.equals("negation")) {
            return new LTLFormula(LTLOperators.Unary.NEG, parseFormula(f.getFirstChild(), net));
        } else if (op.equals("conjunction")) {
            NodeList childs = f.getFirstChild().getNextSibling().getChildNodes();
            Collection<ILTLFormula> subs = new ArrayList<>();
            for (int i = 0; i < childs.getLength(); i++) {
                subs.add(parseFormula(childs.item(i).getFirstChild(), net));
            }
            return FormulaCreator.bigWedgeOrVeeObject(subs, true);
        } else if (op.equals("disjunction")) {
            NodeList childs = f.getFirstChild().getNextSibling().getChildNodes();
            Collection<ILTLFormula> subs = new ArrayList<>();
            for (int i = 0; i < childs.getLength(); i++) {
                subs.add(parseFormula(childs.item(i).getFirstChild(), net));
            }
            return FormulaCreator.bigWedgeOrVeeObject(subs, false);
        } else if (op.equals("deadlock")) {
            return FormulaCreator.deadlock(net);
        } else if (op.equals("is-fireable")) {
            NodeList childs = ((Element) f).getElementsByTagName("transition");
            Collection<ILTLFormula> firable = new ArrayList<>();
            for (int i = 0; i < childs.getLength(); i++) {
                Node transition = childs.item(i);
                if (!transition.getNodeName().equals("transition")) {
                    throw new ParseException("Something is wrong while parsing an is-firable part.");
                }
                String id = transition.getFirstChild().getNodeValue();
                Collection<ILTLFormula> places = new ArrayList<>();
                for (Place place : net.getTransition(id).getPreset()) {
                    places.add(new LTLAtomicProposition(place));
                }
                firable.add(FormulaCreator.bigWedgeOrVeeObject(places, true));
            }
            return FormulaCreator.bigWedgeOrVeeObject(firable, true);
        } else if (op.equals("integer-le")) {
            Node first = f.getFirstChild().getNextSibling();
            Node second = f.getChildNodes().item(2).getNextSibling();
            return parseIntegerExpr(first, second, net);
        } else {
            throw new ParseException("Not supported operator " + op);
        }
    }

    /**
     * Currently only formulas for safe nets are supported
     *
     * @param first
     * @param second
     * @param net
     * @return
     */
    private static ILTLFormula parseIntegerExpr(Node first, Node second, PetriNet net) throws ParseException {
        String opFirst = first.getNodeName();
        String opSecond = second.getNodeName();
        if (opFirst.equals("integer-sum") || opSecond.equals("integer-sum")) {
            throw new ParseException("The operator 'integer-sum' is not yet supported.");
        }
        if (opFirst.equals("integer-difference") || opSecond.equals("integer-difference")) {
            throw new ParseException("The operator 'integer-difference' is not yet supported.");
        }
        Integer valFirst = null;
        Integer valSecond = null;
        if (opFirst.equals("integer-constant")) {
            valFirst = Integer.parseInt(first.getFirstChild().getNodeValue());
        }
        if (opSecond.equals("integer-constant")) {
            valSecond = Integer.parseInt(second.getFirstChild().getNodeValue());
        }
        LTLAtomicProposition tokCFirst = null;
        LTLAtomicProposition tokCSecond = null;
        if (opFirst.equals("tokens-count")) {
            tokCFirst = parseTokensCount(first, net);
        }
        if (opSecond.equals("tokens-count")) {
            tokCSecond = parseTokensCount(second, net);
        }
        if (valFirst != null) {
            if (valSecond != null) { // int <= int
                if (valFirst <= valSecond) {
                    return new LTLConstants.True();
                } else {
                    return new LTLConstants.False();
                }
            } else { // int <= place
                if (valFirst > 1) {
                    throw new ParseException("Currently only safe nets are supported.");
                }
                if (valFirst == 1) {
                    return tokCSecond;
                } else { // 0 \leq p
                    return new LTLConstants.True();
                }
            }
        } else {
            if (valSecond != null) { // place <= int
                if (valSecond > 1) {
                    throw new ParseException("Currently only safe nets are supported.");
                }
                if (valSecond == 0) {
                    return new LTLFormula(LTLOperators.Unary.NEG, tokCFirst);
                } else { // p \leq 1
                    return new LTLConstants.True();
                }
            } else { // place <= place
                return new LTLFormula(LTLOperators.Unary.NEG, new LTLFormula(tokCFirst, LTLOperators.Binary.AND, new LTLFormula(LTLOperators.Unary.NEG, tokCSecond)));
            }
        }
    }

    /**
     * Currently only on place is allowed
     *
     * @param n
     * @return
     */
    private static LTLAtomicProposition parseTokensCount(Node n, PetriNet net) throws ParseException {
        if (n.getChildNodes().getLength() > 3) { // todo: check this! Is there really each time the second child with the #text name? And a last one I cannot call?
            throw new ParseException("A list of places for the tokens-count is currently not supported.");
        }
        String id = n.getFirstChild().getNextSibling().getFirstChild().getNodeValue();
        return new LTLAtomicProposition(net.getPlace(id));
    }
}
