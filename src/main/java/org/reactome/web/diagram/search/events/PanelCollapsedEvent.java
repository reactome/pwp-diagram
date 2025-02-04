package org.reactome.web.diagram.search.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.search.handlers.PanelCollapsedHandler;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class PanelCollapsedEvent extends GwtEvent<PanelCollapsedHandler> {
    public static Type<PanelCollapsedHandler> TYPE = new Type<PanelCollapsedHandler>();

    @Override
    public Type<PanelCollapsedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(PanelCollapsedHandler handler) {
        handler.onPanelCollapsed(this);
    }

}
