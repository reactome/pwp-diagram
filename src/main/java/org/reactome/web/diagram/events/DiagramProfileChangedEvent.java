package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.handlers.DiagramProfileChangedHandler;
import org.reactome.web.diagram.profiles.diagram.model.DiagramProfile;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class DiagramProfileChangedEvent extends GwtEvent<DiagramProfileChangedHandler> {
    public static Type<DiagramProfileChangedHandler> TYPE = new Type<>();

    private DiagramProfile profile;

    public DiagramProfileChangedEvent(DiagramProfile profile) {
        this.profile = profile;
    }

    @Override
    public Type<DiagramProfileChangedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(DiagramProfileChangedHandler handler) {
        handler.onDiagramProfileChanged(this);
    }

    public DiagramProfile getDiagramProfile() {
        return profile;
    }

    @Override
    public String toString() {
        return "DiagramProfileChangedEvent{" +
                "profile=" + profile.getName() +
                '}';
    }
}
