package org.reactome.web.diagram.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.events.GraphObjectSelectedEvent;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface GraphObjectSelectedHandler extends EventHandler {

    void onGraphObjectSelected(GraphObjectSelectedEvent event);
}
