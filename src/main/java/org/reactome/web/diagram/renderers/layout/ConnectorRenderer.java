package org.reactome.web.diagram.renderers.layout;

import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.Node;
import org.reactome.web.diagram.util.AdvancedContext2d;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface ConnectorRenderer {

    void draw(AdvancedContext2d ctx, AdvancedContext2d fadeout, AdvancedContext2d decorator, Node node, Double factor, Coordinate offset);

    boolean stoichiometryVisible();

    void setColourProperties(AdvancedContext2d ctx);

    void setTextProperties(AdvancedContext2d ctx);

}
