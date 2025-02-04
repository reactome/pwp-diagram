package org.reactome.web.diagram.search.searchbox;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public interface SearchBoxArrowKeysHandler extends EventHandler {
    void onArrowKeysPressed(SearchBoxArrowKeysEvent event);
}
