package org.reactome.web.diagram.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.events.ControlActionEvent;


/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface ControlActionHandler extends EventHandler {

    void onControlAction(ControlActionEvent event);
}
