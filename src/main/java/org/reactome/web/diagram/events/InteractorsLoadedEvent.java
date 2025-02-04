package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.data.interactors.raw.RawInteractors;
import org.reactome.web.diagram.handlers.InteractorsLoadedHandler;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class InteractorsLoadedEvent extends GwtEvent<InteractorsLoadedHandler> {
    public static final Type<InteractorsLoadedHandler> TYPE = new Type<>();

    private RawInteractors interactors;
    private long time;

    public InteractorsLoadedEvent(RawInteractors interactors, long time) {
        this.interactors = interactors;
        this.time = time;
    }

    @Override
    public Type<InteractorsLoadedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(InteractorsLoadedHandler handler) {
        handler.onInteractorsLoaded(this);
    }

    public RawInteractors getInteractors() {
        return interactors;
    }

    @Override
    public String toString() {
        return "InteractorsLoadedEvent{" +
                "time=" + time +
                ", resource=" + interactors.getResource() +
                '}';
    }
}
