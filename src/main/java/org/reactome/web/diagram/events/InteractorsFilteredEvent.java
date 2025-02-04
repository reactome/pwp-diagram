package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.handlers.InteractorsFilteredHandler;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class InteractorsFilteredEvent extends GwtEvent<InteractorsFilteredHandler> {
    public static final Type<InteractorsFilteredHandler> TYPE = new Type<>();

    private double score;

    public InteractorsFilteredEvent(double score) {
        this.score = score;
    }

    @Override
    public Type<InteractorsFilteredHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(InteractorsFilteredHandler handler) {
        handler.onInteractorsFiltered(this);
    }

    public double getScore() {
        return score;
    }

    @Override
    public String toString() {
        return "InteractorsFilteredEvent{" +
                "score=" + score +
                '}';
    }
}
