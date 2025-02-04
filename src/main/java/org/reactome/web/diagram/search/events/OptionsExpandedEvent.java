package org.reactome.web.diagram.search.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.search.handlers.OptionsExpandedHandler;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class OptionsExpandedEvent extends GwtEvent<OptionsExpandedHandler> {
    public static Type<OptionsExpandedHandler> TYPE = new Type<>();

    @Override
    public Type<OptionsExpandedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(OptionsExpandedHandler handler) {
        handler.onOptionsExpanded(this);
    }

}
