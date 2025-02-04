package org.reactome.web.diagram.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.events.CanvasNotSupportedEvent;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface CanvasNotSupportedHandler extends EventHandler {

    void onCanvasNotSupported(CanvasNotSupportedEvent event);

}
