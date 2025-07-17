package org.reactome.web.diagram.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.diagram.events.ExpressionValueHoveredEvent;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface ExpressionValueHoveredHandler extends EventHandler {

    void onExpressionValueHovered(ExpressionValueHoveredEvent event);
}
