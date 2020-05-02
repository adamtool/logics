package uniolunisaar.adam.logic.transformers.ctl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import uniol.apt.adt.pn.Node;
import uniol.apt.adt.pn.PetriNet;
import uniolunisaar.adam.ds.abta.AlternatingBuchiTreeAutomaton;
import uniolunisaar.adam.ds.abta.TreeDirectionxState;
import uniolunisaar.adam.ds.abta.TreeState;
import uniolunisaar.adam.ds.abta.posbooleanformula.IPositiveBooleanFormula;
import uniolunisaar.adam.ds.abta.posbooleanformula.PositiveBooleanFormulaOperators.Binary;
import uniolunisaar.adam.ds.abta.posbooleanformula.ParameterizedPositiveBooleanFormula;
import uniolunisaar.adam.ds.abta.posbooleanformula.PositiveBooleanFormulaFactory;
import uniolunisaar.adam.ds.automata.NodeLabel;
import uniolunisaar.adam.ds.logics.Closure;
import uniolunisaar.adam.ds.logics.Constants;
import uniolunisaar.adam.ds.logics.IFormula;
import uniolunisaar.adam.ds.logics.ctl.CTLAtomicProposition;
import uniolunisaar.adam.ds.logics.ctl.CTLConstants;
import uniolunisaar.adam.ds.logics.ctl.CTLFormulaBinary;
import uniolunisaar.adam.ds.logics.ctl.CTLFormulaUnary;
import uniolunisaar.adam.ds.logics.ctl.CTLOperators;
import uniolunisaar.adam.ds.logics.ctl.ICTLFormula;
import uniolunisaar.adam.exceptions.logics.NotConvertableException;
import uniolunisaar.adam.tools.Tools;

/**
 *
 * @author Manuel Gieseking
 */
public class CTL2AlternatingBuchiTreeAutomaton {

    /**
     * Creates an alternating Buchi tree automaton from the given CTL
     * formula.The formula must be in positive normal form (i.e., negations are
     * only applied to atomic propositions) and only AX,EX,AU,EU,AU',EU' are
     * allowed as temporal operators.
     *
     * @param inputFormula
     * @param pnwt
     * @return
     * @throws uniolunisaar.adam.exceptions.logics.NotConvertableException
     */
    public static AlternatingBuchiTreeAutomaton<Set<NodeLabel>> transform(ICTLFormula inputFormula, PetriNet pnwt) throws NotConvertableException {
        // todo: handle special case inputFormula = true or false

        Closure closure = inputFormula.getClosure();
        Map<Integer, List<ICTLFormula>> clByDepth = new HashMap<>();
        AlternatingBuchiTreeAutomaton<Set<NodeLabel>> abta = new AlternatingBuchiTreeAutomaton<>(inputFormula.toString());
        // create for each "unique" formula in the closure a state and order them by depth
        // unique means here no smart way like detecting commutativity, the closure 
        // just compares on the strings
        Map<String, String> mapping = new HashMap<>(); // remember the state id to the formula.toString (I have just each formula once) mapping
        for (IFormula f : closure) {
            // true and false should not belong to the closure in this case
            if (f instanceof CTLConstants.True || f instanceof CTLConstants.False) {
                continue;
            }
            int depth = f.getDepth();
            List<ICTLFormula> formulas;
            if (clByDepth.get(depth) == null) {
                formulas = new ArrayList<>();
                clByDepth.put(depth, formulas);
            } else {
                formulas = clByDepth.get(depth);
            }
            String id = "F" + depth + "-" + formulas.size();
            TreeState state = abta.createAndAddState(id);
            state.setLabel(f.toString());
            // set the buchi states
            if (f instanceof CTLFormulaBinary) {
                CTLFormulaBinary binary = (CTLFormulaBinary) f;
                if (binary.getOp() == CTLOperators.Binary.AUD || binary.getOp() == CTLOperators.Binary.EUD) {
                    abta.setBuchi(state, true);
                }
            }
            ICTLFormula form = (ICTLFormula) f;// the elements of the closure of a CTL formula are CTL formulas
            formulas.add(form);
            mapping.put(form.toString(), id);
        }
        // set initial state
        abta.setInitialState(abta.getState(mapping.get(inputFormula.toString())));

        // the atomic proposition have the depth 0 (but also true and false)
        Set<NodeLabel> atoms = new HashSet<>();
        for (ICTLFormula atom : clByDepth.get(0)) {
            // not needed anymore because already not added to the clByDepth
//            if (!atom.toString().equals(Constants.TO_STRING_RESULT_TRUE) && !atom.toString().equals(Constants.TO_STRING_RESULT_FALSE)) { 
            Node n = pnwt.getNode(((CTLAtomicProposition) atom).getId());// depth 0 without true and false can only be atomic propositions
            atoms.add(new NodeLabel(n));
//            }
        }

        Set<Set<NodeLabel>> powerSetAtoms = Tools.powerSet(atoms);

        int depth = 0;
        List<ICTLFormula> formulas = clByDepth.get(depth);
        while (formulas != null) { // the depth is increasing without any gaps
            for (ICTLFormula formula : formulas) { // for each formula of the depth
                if (formula instanceof CTLAtomicProposition) {
                    CTLAtomicProposition f = (CTLAtomicProposition) formula;
                    NodeLabel label = new NodeLabel(pnwt.getNode(f.getId()));
                    for (Set<NodeLabel> sigma : powerSetAtoms) { // for each sigma
                        if (sigma.contains(label)) {
                            abta.createAndAddEdge(mapping.get(f.toString()), sigma, -1, PositiveBooleanFormulaFactory.createTrue());
                        } else {
                            abta.createAndAddEdge(mapping.get(f.toString()), sigma, -1, PositiveBooleanFormulaFactory.createFalse());
                        }
                    }
                } else if (formula instanceof CTLFormulaUnary) {
                    // because of the input of the formula this can only be
                    // AX, EX, or the negation of an atomic proposition
                    // for fastness we skip the tests here
                    CTLFormulaUnary f = (CTLFormulaUnary) formula;
                    if (null != f.getOp()) {
                        switch (f.getOp()) {
                            case NEG:
                                /// because of the structure of the input formula the child must be an AtomicProposition
                                CTLAtomicProposition prop = (CTLAtomicProposition) f.getPhi();
                                NodeLabel label = new NodeLabel(pnwt.getNode(prop.getId()));
                                for (Set<NodeLabel> sigma : powerSetAtoms) { // for each sigma
                                    if (sigma.contains(label)) {
                                        abta.createAndAddEdge(mapping.get(f.toString()), sigma, -1, PositiveBooleanFormulaFactory.createFalse());
                                    } else {
                                        abta.createAndAddEdge(mapping.get(f.toString()), sigma, -1, PositiveBooleanFormulaFactory.createTrue());
                                    }
                                }
                                break;
                            case AX:
                                for (Set<NodeLabel> powerSetAtom : powerSetAtoms) { // for each sigma
                                    // next true or false is still  true and false
                                    if (f.getPhi().toString().equals(Constants.TO_STRING_RESULT_TRUE)) {
                                        abta.createAndAddEdge(mapping.get(f.toString()), powerSetAtom, -1, PositiveBooleanFormulaFactory.createTrue());
                                    } else if (f.getPhi().toString().equals(Constants.TO_STRING_RESULT_FALSE)) {
                                        abta.createAndAddEdge(mapping.get(f.toString()), powerSetAtom, -1, PositiveBooleanFormulaFactory.createFalse());
                                    } else {
                                        ParameterizedPositiveBooleanFormula para = PositiveBooleanFormulaFactory.createParameterizedPositiveBooleanFormula(-1, Binary.AND,
                                                new TreeDirectionxState(abta.getState(mapping.get(f.getPhi().toString())), -1));
                                        abta.createAndAddEdge(mapping.get(f.toString()), powerSetAtom, -1, para);
                                    }
                                }
                                break;
                            case EX:
                                for (Set<NodeLabel> powerSetAtom : powerSetAtoms) { // for each sigma
                                    // next true or false is still  true and false
                                    if (f.getPhi().toString().equals(Constants.TO_STRING_RESULT_TRUE)) {
                                        abta.createAndAddEdge(mapping.get(f.toString()), powerSetAtom, -1, PositiveBooleanFormulaFactory.createTrue());
                                    } else if (f.getPhi().toString().equals(Constants.TO_STRING_RESULT_FALSE)) {
                                        abta.createAndAddEdge(mapping.get(f.toString()), powerSetAtom, -1, PositiveBooleanFormulaFactory.createFalse());
                                    } else {
                                        ParameterizedPositiveBooleanFormula para = PositiveBooleanFormulaFactory.createParameterizedPositiveBooleanFormula(-1, Binary.OR,
                                                new TreeDirectionxState(abta.getState(mapping.get(f.getPhi().toString())), -1));
                                        abta.createAndAddEdge(mapping.get(f.toString()), powerSetAtom, -1, para);
                                    }
                                }
                                break;
                            default:
                                throw new NotConvertableException("The operator '" + f.getOp() + "' is not allowed in the transformation to an alternating Buchi tree automaton");

                        }
                    }

                } else if (formula instanceof CTLFormulaBinary) {
                    // because of the input of the formula this can only be
                    // AND, OR, AU, EU, AU_, EU_
                    // for fastness we skip the tests here
                    CTLFormulaBinary f = (CTLFormulaBinary) formula;
                    if (null != f.getOp()) {
                        switch (f.getOp()) {
                            case AND:
                                for (Set<NodeLabel> powerSetAtom : powerSetAtoms) { // for each sigma
                                    // get each successor
                                    IPositiveBooleanFormula succ1 = getSuccessorOrTrueFalse(f.getPhi1(), powerSetAtom, mapping, abta);
                                    IPositiveBooleanFormula succ2 = getSuccessorOrTrueFalse(f.getPhi2(), powerSetAtom, mapping, abta);
                                    IPositiveBooleanFormula successors = PositiveBooleanFormulaFactory.createBinaryFormula(succ1, Binary.AND, succ2);
                                    abta.createAndAddEdge(mapping.get(f.toString()), powerSetAtom, -1, successors);
                                }
                                break;
                            case OR:
                                for (Set<NodeLabel> powerSetAtom : powerSetAtoms) { // for each sigma
                                    // get each successor
                                    IPositiveBooleanFormula succ1 = getSuccessorOrTrueFalse(f.getPhi1(), powerSetAtom, mapping, abta);
                                    IPositiveBooleanFormula succ2 = getSuccessorOrTrueFalse(f.getPhi2(), powerSetAtom, mapping, abta);
                                    IPositiveBooleanFormula successors = PositiveBooleanFormulaFactory.createBinaryFormula(succ1, Binary.OR, succ2);
                                    abta.createAndAddEdge(mapping.get(f.toString()), powerSetAtom, -1, successors);
                                }
                                break;
                            case AU:
                                for (Set<NodeLabel> powerSetAtom : powerSetAtoms) { // for each sigma
                                    // get each successor
                                    IPositiveBooleanFormula succ1 = getSuccessorOrTrueFalse(f.getPhi1(), powerSetAtom, mapping, abta);
                                    IPositiveBooleanFormula succ2 = getSuccessorOrTrueFalse(f.getPhi2(), powerSetAtom, mapping, abta);
                                    ParameterizedPositiveBooleanFormula para = PositiveBooleanFormulaFactory.createParameterizedPositiveBooleanFormula(-1, Binary.AND,
                                            new TreeDirectionxState(abta.getState(mapping.get(f.toString())), -1));
                                    IPositiveBooleanFormula successors = PositiveBooleanFormulaFactory.createBinaryFormula(succ1, Binary.AND, para);
                                    successors = PositiveBooleanFormulaFactory.createBinaryFormula(succ2, Binary.OR, successors);
                                    abta.createAndAddEdge(mapping.get(f.toString()), powerSetAtom, -1, successors);
                                }
                                break;
                            case EU:
                                for (Set<NodeLabel> powerSetAtom : powerSetAtoms) { // for each sigma
                                    // get each successor
                                    IPositiveBooleanFormula succ1 = getSuccessorOrTrueFalse(f.getPhi1(), powerSetAtom, mapping, abta);
                                    IPositiveBooleanFormula succ2 = getSuccessorOrTrueFalse(f.getPhi2(), powerSetAtom, mapping, abta);
                                    ParameterizedPositiveBooleanFormula para = PositiveBooleanFormulaFactory.createParameterizedPositiveBooleanFormula(-1, Binary.OR,
                                            new TreeDirectionxState(abta.getState(mapping.get(f.toString())), -1));
                                    IPositiveBooleanFormula successors = PositiveBooleanFormulaFactory.createBinaryFormula(succ1, Binary.AND, para);
                                    successors = PositiveBooleanFormulaFactory.createBinaryFormula(succ2, Binary.OR, successors);
                                    abta.createAndAddEdge(mapping.get(f.toString()), powerSetAtom, -1, successors);
                                }
                                break;
                            case AUD:
                                for (Set<NodeLabel> powerSetAtom : powerSetAtoms) { // for each sigma
                                    // get each successor
                                    IPositiveBooleanFormula succ1 = getSuccessorOrTrueFalse(f.getPhi1(), powerSetAtom, mapping, abta);
                                    IPositiveBooleanFormula succ2 = getSuccessorOrTrueFalse(f.getPhi2(), powerSetAtom, mapping, abta);
                                    ParameterizedPositiveBooleanFormula para = PositiveBooleanFormulaFactory.createParameterizedPositiveBooleanFormula(-1, Binary.AND,
                                            new TreeDirectionxState(abta.getState(mapping.get(f.toString())), -1));
                                    IPositiveBooleanFormula successors = PositiveBooleanFormulaFactory.createBinaryFormula(succ1, Binary.OR, para);
                                    successors = PositiveBooleanFormulaFactory.createBinaryFormula(succ2, Binary.AND, successors);
                                    abta.createAndAddEdge(mapping.get(f.toString()), powerSetAtom, -1, successors);
                                }
                                break;
                            case EUD:
                                for (Set<NodeLabel> powerSetAtom : powerSetAtoms) { // for each sigma
                                    // get each successor
                                    IPositiveBooleanFormula succ1 = getSuccessorOrTrueFalse(f.getPhi1(), powerSetAtom, mapping, abta);
                                    IPositiveBooleanFormula succ2 = getSuccessorOrTrueFalse(f.getPhi2(), powerSetAtom, mapping, abta);
                                    ParameterizedPositiveBooleanFormula para = PositiveBooleanFormulaFactory.createParameterizedPositiveBooleanFormula(-1, Binary.OR,
                                            new TreeDirectionxState(abta.getState(mapping.get(f.toString())), -1));
                                    IPositiveBooleanFormula successors = PositiveBooleanFormulaFactory.createBinaryFormula(succ1, Binary.OR, para);
                                    successors = PositiveBooleanFormulaFactory.createBinaryFormula(succ2, Binary.AND, successors);
                                    abta.createAndAddEdge(mapping.get(f.toString()), powerSetAtom, -1, successors);
                                }
                                break;
                            default:
                                throw new NotConvertableException("The operator '" + f.getOp() + "' is not allowed in the transformation to an alternating Buchi tree automaton");
                        }
                    }
                } else {
                    // all other than true and false should throw an exception
                }
            }

            formulas = clByDepth.get(++depth);
        }
        return abta;
    }

    private static IPositiveBooleanFormula getSuccessorOrTrueFalse(ICTLFormula f, Set<NodeLabel> sigma, Map<String, String> mapping, AlternatingBuchiTreeAutomaton<Set<NodeLabel>> abta) {
        // the successor of false and true are false and true
        if (f.toString().equals(Constants.TO_STRING_RESULT_TRUE)) {
            return PositiveBooleanFormulaFactory.createTrue();
        } else if (f.toString().equals(Constants.TO_STRING_RESULT_FALSE)) {
            return PositiveBooleanFormulaFactory.createFalse();
        }
        return abta.getEdge(mapping.get(f.toString()), sigma).getSuccessor();
    }

}
