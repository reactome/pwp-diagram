package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.handlers.CanvasExportRequestedHandler;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class CanvasExportRequestedEvent extends GwtEvent<CanvasExportRequestedHandler> {
    public static Type<CanvasExportRequestedHandler> TYPE = new Type<>();

    @Override
    public Type<CanvasExportRequestedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(CanvasExportRequestedHandler handler) {
        handler.onCanvasExportRequested(this);
    }

    @Override
    public String toString() {
        return "CanvasExportRequestedEvent{}";
    }
}
