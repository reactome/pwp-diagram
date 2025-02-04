package org.reactome.web.diagram.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.events.SearchKeyPressedEvent;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface SearchKeyPressedHandler extends EventHandler {
    void onSearchKeyPressed(SearchKeyPressedEvent event);
}
