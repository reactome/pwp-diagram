package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.handlers.GraphObjectHoveredHandler;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class GraphObjectHoveredEvent extends GwtEvent<GraphObjectHoveredHandler> {
    public static Type<GraphObjectHoveredHandler> TYPE = new Type<>();

    private GraphObject graphObject;
    private DiagramObject hoveredObject;

    public GraphObjectHoveredEvent(){
        this.graphObject = null;
        this.hoveredObject = null;
    }

    public GraphObjectHoveredEvent(GraphObject graphObject) {
        this.graphObject = graphObject;
        this.hoveredObject = null; //undefined!
    }

    public GraphObjectHoveredEvent(GraphObject graphObject, DiagramObject diagramObject) {
        this.graphObject = graphObject;
        this.hoveredObject = diagramObject;
    }

    @Override
    public Type<GraphObjectHoveredHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(GraphObjectHoveredHandler handler) {
        handler.onGraphObjectHovered(this);
    }

    public GraphObject getGraphObject() {
        return graphObject;
    }

    public List<DiagramObject> getHoveredObjects() {
        return graphObject != null ? graphObject.getDiagramObjects() : new LinkedList<DiagramObject>();
    }

    public DiagramObject getHoveredObject() {
        return hoveredObject;
    }

    @Override
    public String toString() {
        return "GraphObjectHoveredEvent{" +
                "graphObject=" + graphObject +
                '}';
    }
}
