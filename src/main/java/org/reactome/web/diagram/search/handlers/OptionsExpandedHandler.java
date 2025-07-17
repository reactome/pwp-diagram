package org.reactome.web.diagram.search.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.search.events.OptionsExpandedEvent;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface OptionsExpandedHandler extends EventHandler {
    void onOptionsExpanded(OptionsExpandedEvent event);
}
