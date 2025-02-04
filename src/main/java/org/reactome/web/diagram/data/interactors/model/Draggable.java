package org.reactome.web.diagram.data.interactors.model;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface Draggable {

    void setMinX(double minX);

    void setMinY(double minY);

    void setMaxX(double maxY);

    void setMaxY(double maxY);

    void drag(double deltaX, double deltaY);
}
