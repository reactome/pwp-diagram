package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.handlers.DiagramObjectsFlagResetHandler;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class DiagramObjectsFlagResetEvent extends GwtEvent<DiagramObjectsFlagResetHandler> {
    public static final Type<DiagramObjectsFlagResetHandler> TYPE = new Type<>();

    @Override
    public Type<DiagramObjectsFlagResetHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(DiagramObjectsFlagResetHandler handler) {
        handler.onDiagramObjectsFlagReset(this);
    }

    @Override
    public String toString() {
        return "DiagramObjectsFlagResetEvent{}";
    }
}
