package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.handlers.GraphObjectSelectedHandler;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class GraphObjectSelectedEvent extends GwtEvent<GraphObjectSelectedHandler> {
    public static Type<GraphObjectSelectedHandler> TYPE = new Type<>();

    private GraphObject graphObject;
    private boolean zoom;
    private boolean fireExternally;

    public GraphObjectSelectedEvent(GraphObject graphObject, boolean zoom) {
        this(graphObject, zoom, true);
    }

    public GraphObjectSelectedEvent(GraphObject graphObject, boolean zoom, boolean fireExternally) {
        this.graphObject = graphObject;
        this.zoom = zoom;
        this.fireExternally = fireExternally;
    }

    @Override
    public Type<GraphObjectSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(GraphObjectSelectedHandler handler) {
        handler.onGraphObjectSelected(this);
    }

    public GraphObject getGraphObject() {
        return graphObject;
    }

    public boolean getZoom() {
        return zoom;
    }

    public boolean getFireExternally() {
        return fireExternally;
    }

    @Override
    public String toString() {
        return "GraphObjectSelectedEvent{" +
                "graphObject=" + graphObject +
                '}';
    }
}
