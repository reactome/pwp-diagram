package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.handlers.FireworksOpenedHandler;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class FireworksOpenedEvent extends GwtEvent<FireworksOpenedHandler> {
    public static final Type<FireworksOpenedHandler> TYPE = new Type<FireworksOpenedHandler>();

    private Long pathwayId;

    public FireworksOpenedEvent(Long pathwayId) {
        this.pathwayId = pathwayId;
    }

    @Override
    public Type<FireworksOpenedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(FireworksOpenedHandler handler) {
        handler.onFireworksOpened(this);
    }

    public Long getPathwayId() {
        return pathwayId;
    }

    @Override
    public String toString() {
        return "FireworksOpenedEvent{}";
    }
}
