package org.reactome.web.diagram.search.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.search.events.SearchPerformedEvent;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface SearchPerformedHandler extends EventHandler {
    void onSearchPerformed(SearchPerformedEvent event);
}
