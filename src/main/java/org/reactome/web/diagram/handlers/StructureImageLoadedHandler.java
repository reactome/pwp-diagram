package org.reactome.web.diagram.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.events.StructureImageLoadedEvent;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface StructureImageLoadedHandler extends EventHandler {
    void onLayoutImageLoaded(StructureImageLoadedEvent event);
}
