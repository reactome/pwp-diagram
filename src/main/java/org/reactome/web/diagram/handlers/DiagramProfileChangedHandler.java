package org.reactome.web.diagram.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.events.DiagramProfileChangedEvent;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface DiagramProfileChangedHandler extends EventHandler {

    void onDiagramProfileChanged(DiagramProfileChangedEvent event);

}
