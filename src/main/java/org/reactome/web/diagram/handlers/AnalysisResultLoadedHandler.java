package org.reactome.web.diagram.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.events.AnalysisResultLoadedEvent;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface AnalysisResultLoadedHandler extends EventHandler {
    void onAnalysisResultLoaded(AnalysisResultLoadedEvent event);
}
