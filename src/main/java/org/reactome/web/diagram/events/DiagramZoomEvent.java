package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.i18n.client.NumberFormat;
import org.reactome.web.diagram.handlers.DiagramZoomHandler;
import uk.ac.ebi.pwp.structures.quadtree.client.Box;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class DiagramZoomEvent extends GwtEvent<DiagramZoomHandler> {
    public static Type<DiagramZoomHandler> TYPE = new Type<DiagramZoomHandler>();

    private double factor;
    private Box visibleArea;

    public DiagramZoomEvent(double factor, Box visibleArea) {
        this.factor = factor;
        this.visibleArea = visibleArea;
    }

    @Override
    public Type<DiagramZoomHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(DiagramZoomHandler handler) {
        handler.onDiagramZoomEvent(this);
    }

    public double getFactor() {
        return factor;
    }

    public Box getVisibleArea() {
        return visibleArea;
    }

    @Override
    public String toString() {
        return "DiagramZoomEvent{" +
                " factor=" + NumberFormat.getFormat("0.00000").format(factor) +
                ", visibleArea=" +
                " from:{x:" + (int) visibleArea.getMinX() + ", y:" + (int) visibleArea.getMinY() + "}" +
                " to:{x:" + (int) visibleArea.getMaxX() +" , y:" + (int) visibleArea.getMaxY() +"}" +
                '}';
    }
}
