package org.reactome.web.diagram.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.events.DiagramZoomEvent;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface DiagramZoomHandler extends EventHandler {

    void onDiagramZoomEvent(DiagramZoomEvent event);
}
