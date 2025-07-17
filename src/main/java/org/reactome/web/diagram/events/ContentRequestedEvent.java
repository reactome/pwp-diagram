package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.handlers.ContentRequestedHandler;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class ContentRequestedEvent extends GwtEvent<ContentRequestedHandler> {
    public static Type<ContentRequestedHandler> TYPE = new Type<>();

    private String identifier;

    public ContentRequestedEvent(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public Type<ContentRequestedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ContentRequestedHandler handler) {
        handler.onContentRequested(this);
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        return "ContentRequestedEvent{" +
                "identifier='" + identifier + '\'' +
                '}';
    }
}
