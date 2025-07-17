package org.reactome.web.diagram.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.events.ExpressionColumnChangedEvent;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface ExpressionColumnChangedHandler extends EventHandler {

    void onExpressionColumnChanged(ExpressionColumnChangedEvent e);

}
