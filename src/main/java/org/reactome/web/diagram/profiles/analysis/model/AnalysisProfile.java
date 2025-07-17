package org.reactome.web.diagram.profiles.analysis.model;

import java.io.Serializable;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface AnalysisProfile extends Serializable {

    String getName();

    OverlayNode getEnrichment();

    OverlayNode getExpression();

    String getRibbon();

}
