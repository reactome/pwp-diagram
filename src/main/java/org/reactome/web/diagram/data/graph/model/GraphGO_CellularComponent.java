package org.reactome.web.diagram.data.graph.model;

import com.google.gwt.resources.client.ImageResource;
import org.reactome.web.diagram.data.graph.raw.EntityNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class GraphGO_CellularComponent extends GraphObject {

    private List<GraphObject> containedElements = new ArrayList<>();

    public GraphGO_CellularComponent(EntityNode node) {
        super(node);
    }

    public boolean addContainedElement(GraphObject graphObject){
        return this.containedElements.add(graphObject);
    }

    public List<GraphObject> getContainedElements() {
        return containedElements;
    }

    @Override
    public ImageResource getImageResource() {
        return null;
    }
}
