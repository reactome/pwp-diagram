package org.reactome.web.diagram.profiles.interactors.model;

import java.io.Serializable;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public interface InteractorProfileNode extends Serializable {

    String getFill();

    String getStroke();

    String getLighterFill();

    String getLighterStroke();

    String getDarkerStroke();

    String getText();

    String getLighterText();
}
