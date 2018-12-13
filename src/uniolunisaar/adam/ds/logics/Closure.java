package uniolunisaar.adam.ds.logics;

import java.util.HashSet;

/**
 *
 * @author Manuel Gieseking
 */
public class Closure extends HashSet<IFormula> {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean remove(Object o) {
        for (IFormula thi : this) { // todo: not the cheapest way, but don't want to overide the equals and hashcode methods
            // and java don't have the ability to add a custom comparator/equilizer to a Set ... 
            if (thi.toString().equals(o.toString())) {
                return super.remove(thi);
            }
        }
        return false;
    }

    @Override
    public boolean add(IFormula e) {
        if (!this.contains(e)) {
            return super.add(e);
        }
        return true;
    }

    @Override
    public boolean contains(Object o) {
        for (IFormula thi : this) { // todo: not the cheapest way
            if (thi.toString().equals(o.toString())) {
                return true;
            }
        }
        return false;
    }

}
