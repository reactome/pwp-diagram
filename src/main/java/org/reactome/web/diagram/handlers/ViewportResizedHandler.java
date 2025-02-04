package org.reactome.web.diagram.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.events.ViewportResizedEvent;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface ViewportResizedHandler extends EventHandler {

    void onViewportResized(ViewportResizedEvent event);

}
