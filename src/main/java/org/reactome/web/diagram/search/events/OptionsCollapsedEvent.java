package org.reactome.web.diagram.search.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.search.handlers.OptionsCollapsedHandler;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class OptionsCollapsedEvent extends GwtEvent<OptionsCollapsedHandler> {
    public static Type<OptionsCollapsedHandler> TYPE = new Type<>();

    @Override
    public Type<OptionsCollapsedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(OptionsCollapsedHandler handler) {
        handler.onOptionsCollapsed(this);
    }

}
