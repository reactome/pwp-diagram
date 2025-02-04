package org.reactome.web.diagram.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.events.AnalysisResetEvent;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface AnalysisResetHandler extends EventHandler {
    void onAnalysisReset(AnalysisResetEvent event);
}
