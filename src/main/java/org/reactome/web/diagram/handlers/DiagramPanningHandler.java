package org.reactome.web.diagram.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.events.DiagramPanningEvent;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface DiagramPanningHandler extends EventHandler {

    void onDiagramPanningEvent(DiagramPanningEvent event);

}
