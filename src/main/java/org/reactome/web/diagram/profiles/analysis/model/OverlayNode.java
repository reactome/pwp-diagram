package org.reactome.web.diagram.profiles.analysis.model;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface OverlayNode {

    ProfileGradient getGradient();

    String getText();

    OverlayLegend getLegend();
}
