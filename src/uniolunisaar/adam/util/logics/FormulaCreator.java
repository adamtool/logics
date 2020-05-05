package uniolunisaar.adam.util.logics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import uniol.apt.adt.pn.Flow;
import uniol.apt.adt.pn.PetriNet;
import uniol.apt.adt.pn.Place;
import uniol.apt.adt.pn.Transition;
import uniolunisaar.adam.ds.logics.ctl.CTLConstants;
import uniolunisaar.adam.ds.logics.ctl.CTLFormula;
import uniolunisaar.adam.ds.logics.ctl.CTLOperators;
import uniolunisaar.adam.ds.logics.ctl.ICTLFormula;
import uniolunisaar.adam.ds.logics.ltl.ILTLFormula;
import uniolunisaar.adam.ds.petrinet.objectives.Condition;
import uniolunisaar.adam.ds.logics.ltl.flowltl.FlowLTLFormula;
import uniolunisaar.adam.ds.logics.ltl.LTLAtomicProposition;
import uniolunisaar.adam.ds.logics.ltl.LTLConstants;
import uniolunisaar.adam.ds.logics.ltl.LTLFormula;
import uniolunisaar.adam.ds.logics.ltl.LTLOperators;
import uniolunisaar.adam.ds.petrinet.PetriNetExtensionHandler;
import uniolunisaar.adam.ds.petrinetwithtransits.PetriNetWithTransits;
import uniolunisaar.adam.util.logics.LogicsTools.TransitionSemantics;

/**
 *
 * @author Manuel Gieseking
 */
public class FormulaCreator {

    @Deprecated
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

    public static ICTLFormula bigWedgeOrVeeObjectCTL(Collection<? extends ICTLFormula> elements, boolean wedge) {
        if (elements.isEmpty()) {
//            throw new RuntimeException("Iteration over an empty set."); 
            // and means all must be fullfilled -> true, or at least one must be fullfilled -> false
            if (wedge) {
                return new CTLConstants.True();
            } else {
                return new CTLConstants.False();
            }
        }
        CTLOperators.Binary op = (wedge) ? CTLOperators.Binary.AND : CTLOperators.Binary.OR;
        if (elements.size() == 1) {
            return elements.iterator().next();
        } else {
            Iterator<? extends ICTLFormula> it = elements.iterator();
            ICTLFormula last = new CTLFormula(it.next(), op, it.next());
            while (it.hasNext()) {
                ICTLFormula next = it.next();
                last = new CTLFormula(next, op, last);
            }
            return last;
        }
    }

    @Deprecated
    public static String enabled(Transition t) {
        Collection<String> elements = new ArrayList<>();
        for (Flow edge : t.getPresetEdges()) {
            if (PetriNetExtensionHandler.isInhibitor(edge)) {
                elements.add("NEG" + edge.getPlace().getId());
            } else {
                elements.add(edge.getPlace().getId());
            }
        }
        return bigWedgeOrVee(elements, true);
    }

    public static ILTLFormula enabledObject(Transition t) {
        Collection<ILTLFormula> elements = new ArrayList<>();
        for (Flow edge : t.getPresetEdges()) {
            LTLAtomicProposition p = new LTLAtomicProposition(edge.getPlace());
            if (PetriNetExtensionHandler.isInhibitor(edge)) {
                elements.add(new LTLFormula(LTLOperators.Unary.NEG, p));
            } else {
                elements.add(p);
            }
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

    public static FlowLTLFormula createLTLFormulaOfWinCon(PetriNetWithTransits net, Condition.Objective condition) {
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
        return new FlowLTLFormula(f);
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

    public static ILTLFormula getInterleavingMaximality(TransitionSemantics semantics, PetriNet net) {
        if (semantics == TransitionSemantics.INGOING) {
            return FormulaCreatorIngoingSemantics.getMaximalityInterleavingDirectAsObject(net);
        } else {
            return FormulaCreatorOutgoingSemantics.getMaximalityInterleavingDirectAsObject(net);
        }
    }

    public static ILTLFormula getConcurrentMaximality(TransitionSemantics semantics, PetriNet net) {
        if (semantics == TransitionSemantics.INGOING) {
            return FormulaCreatorIngoingSemantics.getMaximalityConcurrentDirectAsObject(net);
        } else {
            return FormulaCreatorOutgoingSemantics.getMaximalityConcurrentDirectAsObject(net);
        }
    }
}
