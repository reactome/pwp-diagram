package org.reactome.web.diagram.data.graph.model;

import org.reactome.web.diagram.data.graph.raw.EntityNode;
import org.reactome.web.diagram.data.graph.raw.EventNode;
import org.reactome.web.diagram.data.graph.raw.SubpathwayNode;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public abstract class GraphEvent extends GraphObject {

    public GraphEvent(EventNode node) {
        super(node);
    }

    public GraphEvent(SubpathwayNode subpathway) {
        super(subpathway);
    }

    //Needed when an Event is created from a Encapsulated Pathway in a diagram
    public GraphEvent(EntityNode node) {
        super(node);
    }

}
