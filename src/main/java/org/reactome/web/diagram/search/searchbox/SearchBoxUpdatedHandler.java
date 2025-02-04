package org.reactome.web.diagram.search.searchbox;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface SearchBoxUpdatedHandler extends EventHandler {
    void onSearchBoxUpdated(SearchBoxUpdatedEvent event);
}
