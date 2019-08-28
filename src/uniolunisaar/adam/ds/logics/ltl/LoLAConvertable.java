package uniolunisaar.adam.ds.logics.ltl;

import uniolunisaar.adam.exceptions.logics.NotConvertableException;

/**
 *
 * @author Manuel Gieseking
 */
public interface LoLAConvertable {

    public String toLoLA() throws NotConvertableException;

}
