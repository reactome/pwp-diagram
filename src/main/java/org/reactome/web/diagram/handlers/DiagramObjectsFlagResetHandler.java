package org.reactome.web.diagram.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.events.DiagramObjectsFlagResetEvent;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface DiagramObjectsFlagResetHandler extends EventHandler {
    void onDiagramObjectsFlagReset(DiagramObjectsFlagResetEvent event);
}
