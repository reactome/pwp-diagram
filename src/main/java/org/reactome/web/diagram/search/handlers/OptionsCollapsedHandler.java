package org.reactome.web.diagram.search.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.search.events.OptionsCollapsedEvent;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface OptionsCollapsedHandler extends EventHandler {
    void onOptionsCollapsed(OptionsCollapsedEvent event);
}
