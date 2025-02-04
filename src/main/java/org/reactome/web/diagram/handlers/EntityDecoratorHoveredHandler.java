package org.reactome.web.diagram.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.events.EntityDecoratorHoveredEvent;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface EntityDecoratorHoveredHandler extends EventHandler {
    void onEntityDecoratorHovered(EntityDecoratorHoveredEvent event);
}
