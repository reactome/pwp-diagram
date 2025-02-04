package org.reactome.web.diagram.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.events.InteractorHoveredEvent;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface InteractorHoveredHandler extends EventHandler {
    void onInteractorHovered(InteractorHoveredEvent event);
}
