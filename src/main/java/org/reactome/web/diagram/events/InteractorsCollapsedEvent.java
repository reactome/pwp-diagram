package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.handlers.InteractorsCollapsedHandler;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class InteractorsCollapsedEvent extends GwtEvent<InteractorsCollapsedHandler> {
    public static final Type<InteractorsCollapsedHandler> TYPE = new Type<>();

    private String resource;

    public InteractorsCollapsedEvent(String resource) {
        this.resource = resource;
    }

    @Override
    public Type<InteractorsCollapsedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(InteractorsCollapsedHandler handler) {
        handler.onInteractorsCollapsed(this);
    }

    public String getResource() {
        return resource;
    }

    @Override
    public String toString() {
        return "InteractorsCollapsedEvent{" +
                "resource='" + resource + '\'' +
                '}';
    }
}
