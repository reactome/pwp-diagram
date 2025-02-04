package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.data.interactors.model.InteractorEntity;
import org.reactome.web.diagram.handlers.InteractorDraggedHandler;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class InteractorDraggedEvent extends GwtEvent<InteractorDraggedHandler> {
    public static final Type<InteractorDraggedHandler> TYPE = new Type<>();

    private InteractorEntity interactorEntity;

    public InteractorDraggedEvent(InteractorEntity interactorEntity) {
        this.interactorEntity = interactorEntity;
    }

    @Override
    public Type<InteractorDraggedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(InteractorDraggedHandler handler) {
        handler.onInteractorDragged(this);
    }

    public InteractorEntity getInteractorEntity() {
        return interactorEntity;
    }

    @Override
    public String toString() {
        return "InteractorDraggedEvent{" +
                "interactorEntity=" + interactorEntity +
                '}';
    }
}
