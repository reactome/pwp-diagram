package org.reactome.web.diagram.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.events.FireworksOpenedEvent;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface FireworksOpenedHandler extends EventHandler {
    void onFireworksOpened(FireworksOpenedEvent event);
}
