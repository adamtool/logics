package uniolunisaar.adam.util.logics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import uniol.apt.adt.pn.PetriNet;
import uniol.apt.adt.pn.Place;
import uniol.apt.adt.pn.Transition;
import uniolunisaar.adam.ds.logics.ltl.ILTLFormula;
import uniolunisaar.adam.ds.objectives.Condition;
import uniolunisaar.adam.ds.logics.ltl.flowltl.FlowFormula;
import uniolunisaar.adam.ds.logics.ltl.LTLAtomicProposition;
import uniolunisaar.adam.ds.logics.ltl.LTLConstants;
import uniolunisaar.adam.ds.logics.ltl.LTLFormula;
import uniolunisaar.adam.ds.logics.ltl.LTLOperators;
import uniolunisaar.adam.ds.petrinetwithtransits.PetriNetWithTransits;

/**
 *
 * @author Manuel Gieseking
 */
public class FormulaCreator {

    public static String bigWedgeOrVee(Collection<String> elements, boolean wedge) {
        if (elements.isEmpty()) {
//            throw new RuntimeException("Iteration over an empty set."); 
            // and means all must be fullfilled -> true, or at least one must be fullfilled -> false
            if (wedge) {
                return "TRUE";
            } else {
                return "FALSE";
            }
        }
        String op = (wedge) ? " AND " : " OR ";
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (String element : elements) {
            if (count++ < elements.size() - 1) {
                sb.append("(").append(element).append(op);
            } else {
                sb.append(element);
            }
        }
        for (int i = 0; i < count - 1; i++) {
            sb.append(")");
        }
        return sb.toString();
    }

    public static ILTLFormula bigWedgeOrVeeObject(Collection<? extends ILTLFormula> elements, boolean wedge) {
        if (elements.isEmpty()) {
//            throw new RuntimeException("Iteration over an empty set."); 
            // and means all must be fullfilled -> true, or at least one must be fullfilled -> false
            if (wedge) {
                return new LTLConstants.True();
            } else {
                return new LTLConstants.False();
            }
        }
        LTLOperators.Binary op = (wedge) ? LTLOperators.Binary.AND : LTLOperators.Binary.OR;
        if (elements.size() == 1) {
            return elements.iterator().next();
        } else {
            Iterator<? extends ILTLFormula> it = elements.iterator();
            ILTLFormula last = new LTLFormula(it.next(), op, it.next());
            while (it.hasNext()) {
                ILTLFormula next = it.next();
                last = new LTLFormula(next, op, last);
            }
            return last;
        }
    }

    public static String enabled(Transition t) {
        Collection<String> elements = new ArrayList<>();
        for (Place p : t.getPreset()) {
            elements.add(p.getId());
        }
        return bigWedgeOrVee(elements, true);
    }

    public static ILTLFormula enabledObject(Transition t) {
        Collection<ILTLFormula> elements = new ArrayList<>();
        for (Place p : t.getPreset()) {
            elements.add(new LTLAtomicProposition(p));
        }
        return bigWedgeOrVeeObject(elements, true);
    }

    public static ILTLFormula createStrongFairness(Transition t) {
        LTLFormula infEnabled = new LTLFormula(LTLOperators.Unary.G, new LTLFormula(LTLOperators.Unary.F, enabledObject(t)));
        LTLFormula infFired = new LTLFormula(LTLOperators.Unary.G, new LTLFormula(LTLOperators.Unary.F, new LTLAtomicProposition(t))); // the same for ingoing as well as outgoing, since infinitly often
        return new LTLFormula(infEnabled, LTLOperators.Binary.IMP, infFired);
    }

    public static ILTLFormula createWeakFairness(Transition t) {
        LTLFormula infEvnEnabled = new LTLFormula(LTLOperators.Unary.F, new LTLFormula(LTLOperators.Unary.G, enabledObject(t)));
        LTLFormula infFired = new LTLFormula(LTLOperators.Unary.G, new LTLFormula(LTLOperators.Unary.F, new LTLAtomicProposition(t))); // the same for ingoing as well as outgoing, since infinitly often
        return new LTLFormula(infEvnEnabled, LTLOperators.Binary.IMP, infFired);
    }

    public static FlowFormula createLTLFormulaOfWinCon(PetriNetWithTransits net, Condition.Objective condition) {
        List<Place> specialPlaces = new ArrayList<>();
        for (Place p : net.getPlaces()) {
            if (net.isSpecial(p)) {
                specialPlaces.add(p);
            }
        }
        ILTLFormula f;
        switch (condition) {
            case A_SAFETY: {
                Collection<ILTLFormula> elems = new ArrayList<>();
                for (Place specialPlace : specialPlaces) {
                    elems.add(new LTLFormula(LTLOperators.Unary.NEG, new LTLAtomicProposition(specialPlace)));
                }
                f = new LTLFormula(LTLOperators.Unary.G, bigWedgeOrVeeObject(elems, true));
                break;
            }
            case A_REACHABILITY: {
                Collection<ILTLFormula> elems = new ArrayList<>();
                for (Place specialPlace : specialPlaces) {
                    elems.add(new LTLAtomicProposition(specialPlace));
                }
                f = new LTLFormula(LTLOperators.Unary.F, bigWedgeOrVeeObject(elems, false));
                break;
            }
            case A_BUCHI: {
                Collection<ILTLFormula> elems = new ArrayList<>();
                for (Place specialPlace : specialPlaces) {
                    elems.add(new LTLAtomicProposition(specialPlace));
                }
                f = new LTLFormula(LTLOperators.Unary.G, new LTLFormula(LTLOperators.Unary.F, bigWedgeOrVeeObject(elems, false)));
                break;
            }
            default:
                throw new RuntimeException("Existential acceptance conditions are not yet implemented");
        }
        return new FlowFormula(f);
    }

    public static LTLFormula deadlock(PetriNet net) {
        Collection<ILTLFormula> elems = new ArrayList<>();
        for (Transition t : net.getTransitions()) {
            elems.add(new LTLFormula(LTLOperators.Unary.NEG, new LTLAtomicProposition(t)));
        }
        return new LTLFormula(LTLOperators.Unary.F, bigWedgeOrVeeObject(elems, true));
    }

    public static LTLFormula reversible(PetriNet net) {
        Collection<ILTLFormula> elems = new ArrayList<>();
        for (Place p : net.getPlaces()) {
            if (p.getInitialToken().getValue() > 0) {
                elems.add(new LTLAtomicProposition(p));
            }
        }
        return new LTLFormula(LTLOperators.Unary.G, new LTLFormula(LTLOperators.Unary.F, bigWedgeOrVeeObject(elems, true)));
    }

    public static LTLFormula quasiLive(PetriNet net) {
        Collection<ILTLFormula> elems = new ArrayList<>();
        for (Transition t : net.getTransitions()) {
            elems.add(new LTLFormula(LTLOperators.Unary.F, new LTLAtomicProposition(t)));
        }
        return new LTLFormula(bigWedgeOrVeeObject(elems, true));
    }

    public static LTLFormula live(PetriNet net) {
        return new LTLFormula(LTLOperators.Unary.G, quasiLive(net));
    }
}
