package org.reactome.web.diagram.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.events.InteractorsErrorEvent;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface InteractorsErrorHandler extends EventHandler {
    void onInteractorsError(InteractorsErrorEvent event);
}
