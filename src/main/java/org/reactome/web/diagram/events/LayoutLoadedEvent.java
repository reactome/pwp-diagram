package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.handlers.LayoutLoadedHandler;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class LayoutLoadedEvent extends GwtEvent<LayoutLoadedHandler> {
    public static Type<LayoutLoadedHandler> TYPE = new Type<LayoutLoadedHandler>();

    private Context context;
    private long time;

    public LayoutLoadedEvent(Context context, long time) {
        this.context = context;
        this.time = time;
    }

    @Override
    public Type<LayoutLoadedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(LayoutLoadedHandler handler) {
        handler.onLayoutLoaded(this);
    }

    public Context getContext() {
        return context;
    }

    public long getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "LayoutLoadedEvent{" +
                "time=" + time +
                ", content=" + context +
                '}';
    }
}
