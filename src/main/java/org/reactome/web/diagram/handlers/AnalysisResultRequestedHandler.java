package org.reactome.web.diagram.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.events.AnalysisResultRequestedEvent;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface AnalysisResultRequestedHandler extends EventHandler {
    void onAnalysisResultRequested(AnalysisResultRequestedEvent event);
}
