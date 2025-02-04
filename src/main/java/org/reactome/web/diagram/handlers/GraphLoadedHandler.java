package org.reactome.web.diagram.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.events.GraphLoadedEvent;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface GraphLoadedHandler extends EventHandler {
    void onGraphLoaded(GraphLoadedEvent event);
}
