package org.reactome.web.diagram.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.events.EntityDecoratorSelectedEvent;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface EntityDecoratorSelectedHandler extends EventHandler {
    void onEntityDecoratorSelected(EntityDecoratorSelectedEvent event);
}
