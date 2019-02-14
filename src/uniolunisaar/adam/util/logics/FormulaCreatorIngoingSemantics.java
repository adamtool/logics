package uniolunisaar.adam.util.logics;

import java.util.ArrayList;
import java.util.Collection;
import uniol.apt.adt.pn.PetriNet;
import uniol.apt.adt.pn.Place;
import uniol.apt.adt.pn.Transition;
import uniol.apt.io.parser.ParseException;
import uniolunisaar.adam.ds.logics.ltl.ILTLFormula;
import uniolunisaar.adam.ds.logics.ltl.flowltl.IRunFormula;
import uniolunisaar.adam.ds.logics.ltl.LTLAtomicProposition;
import uniolunisaar.adam.ds.logics.ltl.LTLFormula;
import uniolunisaar.adam.ds.logics.ltl.LTLOperators;
import uniolunisaar.adam.ds.logics.ltl.flowltl.RunFormula;
import uniolunisaar.adam.logic.parser.logics.flowltl.FlowLTLParser;

/**
 *
 * @author Manuel Gieseking
 */
public class FormulaCreatorIngoingSemantics {

    public static ILTLFormula getMaximalityInterleavingDirectAsObject(PetriNet net) {
        // big wedge no transition fires
        Collection<ILTLFormula> elements = new ArrayList<>();
        for (Transition t : net.getTransitions()) {
            elements.add(new LTLFormula(LTLOperators.Unary.NEG, new LTLAtomicProposition(t)));
        }

        // big wedge no transition is enabled
        Collection<ILTLFormula> elements2 = new ArrayList<>();
        for (Transition t : net.getTransitions()) {
            elements2.add(new LTLFormula(LTLOperators.Unary.NEG, FormulaCreator.enabledObject(t)));
        }

        // implies        
        ILTLFormula imp = new LTLFormula(
                new LTLFormula(LTLOperators.Unary.X, FormulaCreator.bigWedgeOrVeeObject(elements, true)),
                LTLOperators.Binary.IMP,
                FormulaCreator.bigWedgeOrVeeObject(elements2, true)
        );

        return new LTLFormula(LTLOperators.Unary.G, imp);
    }

    @Deprecated
    public static RunFormula getMaximalityInterleavingObject(PetriNet net) {
        String formula = getMaximalityInterleaving(net);
        try {
            return FlowLTLParser.parse(net, formula);
        } catch (ParseException ex) {
//            ex.printStackTrace();
            // Cannot happen
            return null;
        }
    }

    @Deprecated
    public static String getMaximalityInterleaving(PetriNet net) {
        StringBuilder sb = new StringBuilder("G (X ");

        // big wedge no transition fires
        Collection<String> elements = new ArrayList<>();
        for (Transition t : net.getTransitions()) {
            elements.add("! " + t.getId() + "");
        }
        sb.append(FormulaCreator.bigWedgeOrVee(elements, true));

        // implies
        sb.append(" -> ");

        // big wedge no transition is enabled
        elements = new ArrayList<>();
        for (Transition t : net.getTransitions()) {
            elements.add("! " + FormulaCreator.enabled(t) + " ");
        }
        sb.append(FormulaCreator.bigWedgeOrVee(elements, true));

        // closing implies and globally
        sb.append(")");

        return sb.toString();
    }

    @Deprecated
    public static IRunFormula getMaximalityConcurrentObject(PetriNet net) {
        String formula = getMaximalityConcurrent(net);
        try {
            return FlowLTLParser.parse(net, formula);
        } catch (ParseException ex) {
            System.out.println(formula);
            ex.printStackTrace();
            // Cannot happen
            return null;
        }
    }

    @Deprecated
    public static String getMaximalityConcurrent(PetriNet net) {
        // all transitions have to globally be eventually not enabled or another transition with a place in the transitions preset fires
        Collection<String> elements = new ArrayList<>();
        for (Transition t : net.getTransitions()) {
            StringBuilder sb = new StringBuilder("G F ");
            sb.append("(! ").append(FormulaCreator.enabled(t)).append(" OR X ");
            Collection<String> elems = new ArrayList<>();
            for (Place p : t.getPreset()) {
                for (Transition t2 : p.getPostset()) {
                    elems.add(t2.getId());
                }
            }
            sb.append(FormulaCreator.bigWedgeOrVee(elems, false));
            sb.append(")");
            elements.add(sb.toString());
        }
        return FormulaCreator.bigWedgeOrVee(elements, true);
    }

    public static ILTLFormula getMaximalityConcurrentDirectAsObject(PetriNet net) {
        // all transitions have to globally be eventually not enabled or another transition with a place in the transitions preset fires
        Collection<ILTLFormula> elements = new ArrayList<>();
        for (Transition t : net.getTransitions()) {
            Collection<ILTLFormula> elems = new ArrayList<>();
            for (Place p : t.getPreset()) {
                for (Transition t2 : p.getPostset()) {
                    elems.add(new LTLAtomicProposition(t2));
                }
            }
            ILTLFormula bigvee = FormulaCreator.bigWedgeOrVeeObject(elems, false);
            ILTLFormula f = new LTLFormula(new LTLFormula(LTLOperators.Unary.NEG, FormulaCreator.enabledObject(t)), LTLOperators.Binary.OR, new LTLFormula(LTLOperators.Unary.X, bigvee));
            f = new LTLFormula(LTLOperators.Unary.F, f);
            f = new LTLFormula(LTLOperators.Unary.G, f);
            elements.add(f);
        }
        return FormulaCreator.bigWedgeOrVeeObject(elements, true);
    }

}
