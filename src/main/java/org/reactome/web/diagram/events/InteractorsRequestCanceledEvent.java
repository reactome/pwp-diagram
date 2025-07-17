package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.handlers.InteractorsRequestCanceledHandler;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class InteractorsRequestCanceledEvent extends GwtEvent<InteractorsRequestCanceledHandler> {
    public static final Type<InteractorsRequestCanceledHandler> TYPE = new Type<>();

    @Override
    public Type<InteractorsRequestCanceledHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(InteractorsRequestCanceledHandler handler) {
        handler.onInteractorsRequestCanceled(this);
    }

    @Override
    public String toString() {
        return "InteractorsRequestCanceledEvent{}";
    }
}
