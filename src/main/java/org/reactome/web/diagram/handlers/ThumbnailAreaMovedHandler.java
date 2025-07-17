package org.reactome.web.diagram.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.events.ThumbnailAreaMovedEvent;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface ThumbnailAreaMovedHandler extends EventHandler {

    void onThumbnailAreaMoved(ThumbnailAreaMovedEvent event);

}
