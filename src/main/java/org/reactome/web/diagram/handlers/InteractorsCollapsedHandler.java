package org.reactome.web.diagram.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.events.InteractorsCollapsedEvent;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface InteractorsCollapsedHandler extends EventHandler {
    void onInteractorsCollapsed(InteractorsCollapsedEvent event);
}
