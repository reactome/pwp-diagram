package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.handlers.ViewportResizedHandler;
import uk.ac.ebi.pwp.structures.quadtree.client.Box;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class ViewportResizedEvent extends GwtEvent<ViewportResizedHandler> {
    public static Type<ViewportResizedHandler> TYPE = new Type<ViewportResizedHandler>();

    private int width;
    private int height;
    private Box visibleArea;

    public ViewportResizedEvent(int width, int height, Box visibleArea) {
        this.width = width;
        this.height = height;
        this.visibleArea = visibleArea;
    }

    @Override
    public Type<ViewportResizedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ViewportResizedHandler handler) {
        handler.onViewportResized(this);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Box getVisibleArea() {
        return visibleArea;
    }

    @Override
    public String toString() {
        return "ViewportResizedEvent{" +
                "width=" + width +
                ", height=" + height +
                ", visibleArea=" +
                " from:{x:" + visibleArea.getMinX() + ", y:" + visibleArea.getMinY() + "}" +
                " to:{x:" + visibleArea.getMaxX() +" , y:" + visibleArea.getMaxY() +"}" +
                '}';
    }
}
