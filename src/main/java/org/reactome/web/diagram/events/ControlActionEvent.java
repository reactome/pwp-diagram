package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.controls.navigation.ControlAction;
import org.reactome.web.diagram.handlers.ControlActionHandler;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class ControlActionEvent extends GwtEvent<ControlActionHandler> {
    public static Type<ControlActionHandler> TYPE = new Type<>();

    ControlAction action;

    public ControlActionEvent(ControlAction action) {
        this.action = action;
    }

    @Override
    public Type<ControlActionHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ControlActionHandler handler) {
        handler.onControlAction(this);
    }

    public ControlAction getAction() {
        return action;
    }

    @Override
    public String toString() {
        return "ControlActionEvent{" +
                "action=" + action +
                '}';
    }
}
