package org.reactome.web.diagram.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.events.InteractorsRequestCanceledEvent;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface InteractorsRequestCanceledHandler extends EventHandler {
    void onInteractorsRequestCanceled(InteractorsRequestCanceledEvent event);
}
