package org.reactome.web.diagram.profiles.interactors.model;

import java.io.Serializable;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public interface InteractorProfile extends Serializable {

    String getName();

    InteractorProfileNode getChemical();

    InteractorProfileNode getProtein();

    InteractorProfileNode getDisease();

}
