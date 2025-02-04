package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.data.content.Content;
import org.reactome.web.diagram.handlers.DiagramRenderedHandler;
import uk.ac.ebi.pwp.structures.quadtree.client.Box;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class DiagramRenderedEvent extends GwtEvent<DiagramRenderedHandler> {
    public static Type<DiagramRenderedHandler> TYPE = new Type<DiagramRenderedHandler>();

    private Content content;
    private Box visibleArea;
    private int items;
    private double time;

    public DiagramRenderedEvent(Content content, Box visibleArea, int items, double time) {
        this.content = content;
        this.visibleArea = visibleArea;
        this.items = items;
        this.time = time;
    }

    @Override
    public Type<DiagramRenderedHandler> getAssociatedType() {
        return TYPE;
    }

    public Content getContent() {
        return content;
    }

    public int getItems() {
        return items;
    }

    public double getTime() {
        return time;
    }

    public Box getVisibleArea() {
        return visibleArea;
    }

    @Override
    protected void dispatch(DiagramRenderedHandler handler) {
        handler.onDiagramRendered(this);
    }

    @Override
    public String toString() {
        return "### DiagramRenderedEvent{" +
                "st_id='" + content.getStableId() + '\'' +
                ", time=" + time +
                ", items=" + items +
                "} ###";
    }
}
