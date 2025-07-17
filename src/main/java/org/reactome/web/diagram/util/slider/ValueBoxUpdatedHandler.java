package org.reactome.web.diagram.util.slider;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public interface ValueBoxUpdatedHandler extends EventHandler {
    void onValueBoxUpdated(ValueBoxUpdatedEvent event);
}
