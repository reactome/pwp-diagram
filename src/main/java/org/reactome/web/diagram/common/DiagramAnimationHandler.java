package org.reactome.web.diagram.common;

import org.reactome.web.diagram.data.DiagramStatus;
import org.reactome.web.diagram.data.layout.Coordinate;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface DiagramAnimationHandler {

    DiagramStatus getDiagramStatus();

    void transform(Coordinate offset, double factor);

    int getViewportWidth();

    int getViewportHeight();
}
