package org.reactome.web.diagram.search.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.search.events.AutoCompleteRequestedEvent;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface AutoCompleteRequestedHandler extends EventHandler {
    void onAutoCompleteRequested(AutoCompleteRequestedEvent event);
}
