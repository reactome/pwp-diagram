package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.data.interactors.common.OverlayResource;
import org.reactome.web.diagram.handlers.InteractorsResourceChangedHandler;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class InteractorsResourceChangedEvent extends GwtEvent<InteractorsResourceChangedHandler> {
    public static final Type<InteractorsResourceChangedHandler> TYPE = new Type<>();

    OverlayResource resource;

    public InteractorsResourceChangedEvent(OverlayResource resource) {
        this.resource = resource;
    }

    @Override
    public Type<InteractorsResourceChangedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(InteractorsResourceChangedHandler handler) {
        handler.onInteractorsResourceChanged(this);
    }

    public OverlayResource getResource() {
        return resource;
    }

    @Override
    public String toString() {
        return "InteractorsResourceChangedEvent{" +
                "resource=" + resource.toString() +
                '}';
    }
}
