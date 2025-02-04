package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.handlers.DiagramPanningHandler;
import uk.ac.ebi.pwp.structures.quadtree.client.Box;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class DiagramPanningEvent extends GwtEvent<DiagramPanningHandler> {
    public static Type<DiagramPanningHandler> TYPE = new Type<DiagramPanningHandler>();

    private Box visibleArea;

    public DiagramPanningEvent(Box visibleArea) {
        this.visibleArea = visibleArea;
    }

    @Override
    public Type<DiagramPanningHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(DiagramPanningHandler handler) {
        handler.onDiagramPanningEvent(this);
    }

    public Box getVisibleArea() {
        return visibleArea;
    }

    @Override
    public String toString() {
        return "DiagramPanningEvent{" +
                " visibleArea=" +
                " from:{x:" + visibleArea.getMinX() + ", y:" + visibleArea.getMinY() + "}" +
                ", to:{x:" + visibleArea.getMaxX() +" , y:" + visibleArea.getMaxY() +"}" +
                '}';
    }
}
