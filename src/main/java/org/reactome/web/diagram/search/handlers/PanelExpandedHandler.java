package org.reactome.web.diagram.search.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.search.events.PanelExpandedEvent;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface PanelExpandedHandler extends EventHandler {
    void onPanelExpanded(PanelExpandedEvent event);
}
