package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.data.interactors.model.DiagramInteractor;
import org.reactome.web.diagram.handlers.InteractorHoveredHandler;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class InteractorHoveredEvent extends GwtEvent<InteractorHoveredHandler> {
    public static final Type<InteractorHoveredHandler> TYPE = new Type<>();

    DiagramInteractor interactor;

    public InteractorHoveredEvent(DiagramInteractor interactor) {
        this.interactor = interactor;
    }

    @Override
    public Type<InteractorHoveredHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(InteractorHoveredHandler handler) {
        handler.onInteractorHovered(this);
    }

    public DiagramInteractor getInteractor() {
        return interactor;
    }

    @Override
    public String toString() {
        return "InteractorHoveredEvent{" +
                "interactor=" + interactor +
                '}';
    }
}
