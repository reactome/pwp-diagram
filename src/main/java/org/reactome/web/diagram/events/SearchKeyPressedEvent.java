package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.handlers.SearchKeyPressedHandler;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class SearchKeyPressedEvent extends GwtEvent<SearchKeyPressedHandler> {
    public static final Type<SearchKeyPressedHandler> TYPE = new Type<>();

    @Override
    public Type<SearchKeyPressedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(SearchKeyPressedHandler handler) {
        handler.onSearchKeyPressed(this);
    }

    @Override
    public String toString() {
        return "SearchKeyPressedEvent{}";
    }
}
