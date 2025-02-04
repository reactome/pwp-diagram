package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.data.layout.ContextMenuTrigger;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.NodeAttachment;
import org.reactome.web.diagram.data.layout.SummaryItem;
import org.reactome.web.diagram.handlers.EntityDecoratorHoveredHandler;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class EntityDecoratorHoveredEvent extends GwtEvent<EntityDecoratorHoveredHandler> {
    public static final Type<EntityDecoratorHoveredHandler> TYPE = new Type<>();

    private DiagramObject diagramObject;
    private NodeAttachment attachment;
    private SummaryItem summaryItem;
    private ContextMenuTrigger trigger;

    public EntityDecoratorHoveredEvent(DiagramObject diagramObject) {
        this.diagramObject = diagramObject;
    }

    public EntityDecoratorHoveredEvent(DiagramObject diagramObject, NodeAttachment attachment) {
        this.diagramObject = diagramObject;
        this.attachment = attachment;
    }

    public EntityDecoratorHoveredEvent(DiagramObject diagramObject, SummaryItem summaryItem) {
        this.diagramObject = diagramObject;
        this.summaryItem = summaryItem;
    }

    public EntityDecoratorHoveredEvent(DiagramObject diagramObject, ContextMenuTrigger trigger) {
        this.diagramObject = diagramObject;
        this.trigger = trigger;
    }

    @Override
    public Type<EntityDecoratorHoveredHandler> getAssociatedType() {
        return TYPE;
    }

    public DiagramObject getDiagramObject() {
        return diagramObject;
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
    protected void dispatch(EntityDecoratorHoveredHandler handler) {
        handler.onEntityDecoratorHovered(this);
    }

    @Override
    public String toString() {
        return "EntityDecoratorHoveredEvent{" +
                "diagramObject=" + diagramObject +
                (attachment != null ? ", attachment=" + attachment.getDescription() : "") +
                (summaryItem != null ? ", summaryItem=" + summaryItem.getType() : "") +
                (trigger != null ? ", ContextMenuTrigger -> Hovered" : "") +
                '}';
    }
}
