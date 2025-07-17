package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.handlers.InteractorSelectedHandler;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class InteractorSelectedEvent extends GwtEvent<InteractorSelectedHandler> {
    public static final Type<InteractorSelectedHandler> TYPE = new Type<>();

    private String url;

    public InteractorSelectedEvent(String url) {
        this.url = url;
    }

    @Override
    public Type<InteractorSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(InteractorSelectedHandler handler) {
        handler.onInteractorSelected(this);
    }


    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "InteractorSelectedEvent{" +
                "url='" + url + '\'' +
                '}';
    }
}
