package org.reactome.web.diagram.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.events.ContentLoadedEvent;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface ContentLoadedHandler extends EventHandler {

    void onContentLoaded(ContentLoadedEvent event);
}
