package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.handlers.InteractorsLayoutUpdatedHandler;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class InteractorsLayoutUpdatedEvent extends GwtEvent<InteractorsLayoutUpdatedHandler> {
    public static final Type<InteractorsLayoutUpdatedHandler> TYPE = new Type<>();

    @Override
    public Type<InteractorsLayoutUpdatedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(InteractorsLayoutUpdatedHandler handler) {
        handler.onInteractorsLayoutUpdated(this);
    }

    @Override
    public String toString() {
        return "InteractorsLayoutUpdatedEvent{}";
    }
}
