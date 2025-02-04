package org.reactome.web.diagram.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.events.CanvasExportRequestedEvent;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface CanvasExportRequestedHandler extends EventHandler {

    void onCanvasExportRequested(CanvasExportRequestedEvent event);

}
