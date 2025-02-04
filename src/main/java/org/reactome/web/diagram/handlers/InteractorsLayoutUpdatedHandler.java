package org.reactome.web.diagram.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.events.InteractorsLayoutUpdatedEvent;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface InteractorsLayoutUpdatedHandler extends EventHandler {
    void onInteractorsLayoutUpdated(InteractorsLayoutUpdatedEvent event);
}
