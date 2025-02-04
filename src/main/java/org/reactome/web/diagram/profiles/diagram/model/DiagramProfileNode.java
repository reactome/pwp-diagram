package org.reactome.web.diagram.profiles.diagram.model;

import java.io.Serializable;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public interface DiagramProfileNode extends Serializable{

    String getFill();

    String getStroke();

    String getLineWidth();

    String getFadeOutFill();

    String getFadeOutStroke();

    String getLighterFill();

    String getLighterStroke();

    String getText();

    String getFadeOutText();

    String getLighterText();

}
