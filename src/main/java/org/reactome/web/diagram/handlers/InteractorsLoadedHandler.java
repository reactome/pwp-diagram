package org.reactome.web.diagram.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.events.InteractorsLoadedEvent;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface InteractorsLoadedHandler extends EventHandler {
    void onInteractorsLoaded(InteractorsLoadedEvent event);
}
