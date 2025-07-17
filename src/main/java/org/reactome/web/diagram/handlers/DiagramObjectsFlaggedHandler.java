package org.reactome.web.diagram.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.events.DiagramObjectsFlaggedEvent;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface DiagramObjectsFlaggedHandler extends EventHandler {
    void onDiagramObjectsFlagged(DiagramObjectsFlaggedEvent event);
}
