package org.reactome.web.diagram.client.visualisers.ehld.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.client.visualisers.ehld.events.SVGThumbnailAreaMovedEvent;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public interface SVGThumbnailAreaMovedHandler extends EventHandler {

    void onSVGThumbnailAreaMoved(SVGThumbnailAreaMovedEvent event);
}
