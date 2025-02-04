package org.reactome.web.diagram.search.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.search.events.PanelCollapsedEvent;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface PanelCollapsedHandler extends EventHandler {
    void onPanelCollapsed(PanelCollapsedEvent event);
}
