package org.reactome.web.diagram.search.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.search.events.ResultSelectedEvent;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public interface ResultSelectedHandler extends EventHandler {
    void onResultSelected(ResultSelectedEvent event);
}
