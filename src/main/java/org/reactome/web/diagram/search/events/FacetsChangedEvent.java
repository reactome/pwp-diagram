package org.reactome.web.diagram.search.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.search.handlers.FacetsChangedHandler;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class FacetsChangedEvent extends GwtEvent<FacetsChangedHandler> {
    public static Type<FacetsChangedHandler> TYPE = new Type<>();

    @Override
    public Type<FacetsChangedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(FacetsChangedHandler handler) {
        handler.onSelectedFacetsChanged(this);
    }

    @Override
    public String toString() {
        return "FacetsChangedEvent{}";
    }
}
