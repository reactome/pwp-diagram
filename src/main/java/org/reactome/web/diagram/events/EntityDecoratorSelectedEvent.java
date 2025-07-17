package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.layout.ContextMenuTrigger;
import org.reactome.web.diagram.data.layout.NodeAttachment;
import org.reactome.web.diagram.data.layout.SummaryItem;
import org.reactome.web.diagram.handlers.EntityDecoratorSelectedHandler;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class EntityDecoratorSelectedEvent extends GwtEvent<EntityDecoratorSelectedHandler> {
    public final static Type<EntityDecoratorSelectedHandler> TYPE = new Type<>();

    private GraphObject graphObject;
    private NodeAttachment attachment;
    private SummaryItem summaryItem;
    private ContextMenuTrigger trigger;

    public EntityDecoratorSelectedEvent(GraphObject graphObject, NodeAttachment attachment) {
        this.graphObject = graphObject;
        this.attachment = attachment;
    }

    public EntityDecoratorSelectedEvent(GraphObject graphObject, SummaryItem summaryItem) {
        this.graphObject = graphObject;
        this.summaryItem = summaryItem;
    }

    public EntityDecoratorSelectedEvent(GraphObject graphObject, ContextMenuTrigger trigger) {
        this.graphObject = graphObject;
        this.trigger = trigger;
    }

    @Override
    public Type<EntityDecoratorSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    public GraphObject getGraphObject() {
        return graphObject;
    }

    public NodeAttachment getAttachment() {
        return attachment;
    }

    public SummaryItem getSummaryItem() {
        return summaryItem;
    }

    public ContextMenuTrigger getTrigger() {
        return trigger;
    }

    @Override
    protected void dispatch(EntityDecoratorSelectedHandler handler) {
        handler.onEntityDecoratorSelected(this);
    }

    @Override
    public String toString() {
        return "EntityDecoratorSelectedEvent{" +
                "graphObject=" + graphObject +
                (attachment != null ? ", attachment=" + attachment.getReactomeId() : "") +
                (summaryItem != null ? ", summaryItem=" + summaryItem.getType() : "") +
                (trigger != null ? ", ContextMenuTrigger -> Clicked" : "") +
                '}';
    }
}
