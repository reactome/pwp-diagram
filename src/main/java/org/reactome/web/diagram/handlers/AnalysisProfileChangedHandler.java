package org.reactome.web.diagram.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.events.AnalysisProfileChangedEvent;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface AnalysisProfileChangedHandler extends EventHandler {
    void onAnalysisProfileChanged(AnalysisProfileChangedEvent event);
}
