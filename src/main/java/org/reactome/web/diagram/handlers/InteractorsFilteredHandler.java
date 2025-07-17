package org.reactome.web.diagram.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.events.InteractorsFilteredEvent;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface InteractorsFilteredHandler extends EventHandler {
    void onInteractorsFiltered(InteractorsFilteredEvent event);
}
