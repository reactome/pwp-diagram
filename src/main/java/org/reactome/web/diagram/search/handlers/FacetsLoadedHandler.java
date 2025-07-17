package org.reactome.web.diagram.search.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.search.events.FacetsLoadedEvent;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public interface FacetsLoadedHandler extends EventHandler {
    void onFacetsLoaded(FacetsLoadedEvent event);
}
