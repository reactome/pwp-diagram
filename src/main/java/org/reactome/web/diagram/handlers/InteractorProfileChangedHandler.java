package org.reactome.web.diagram.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.events.InteractorProfileChangedEvent;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface InteractorProfileChangedHandler extends EventHandler {
    void onInteractorProfileChanged(InteractorProfileChangedEvent event);
}
