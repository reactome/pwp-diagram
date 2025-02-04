package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.handlers.InteractorProfileChangedHandler;
import org.reactome.web.diagram.profiles.interactors.model.InteractorProfile;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class InteractorProfileChangedEvent extends GwtEvent<InteractorProfileChangedHandler> {
    public static final Type<InteractorProfileChangedHandler> TYPE = new Type<>();

    private InteractorProfile profile;

    public InteractorProfileChangedEvent(InteractorProfile profile) {
        this.profile = profile;
    }

    @Override
    public Type<InteractorProfileChangedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(InteractorProfileChangedHandler handler) {
        handler.onInteractorProfileChanged(this);
    }

    public InteractorProfile getInteractorProfile() {
        return profile;
    }

    @Override
    public String toString() {
        return "InteractorProfileChangedEvent{" +
                "profile=" + profile.getName() +
                '}';
    }
}
