package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.handlers.ContentLoadedHandler;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class ContentLoadedEvent extends GwtEvent<ContentLoadedHandler> {

    public static Type<ContentLoadedHandler> TYPE = new Type<>();

    private Context context;

    public ContentLoadedEvent(Context context) {
        this.context = context;
    }

    @Override
    public Type<ContentLoadedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ContentLoadedHandler handler) {
        handler.onContentLoaded(this);
    }

    public Context getContext() {
        return context;
    }

    @Override
    public String toString() {
        return "ContentLoadedEvent{" +
                "context=" + context +
                '}';
    }
}
