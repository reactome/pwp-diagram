package org.reactome.web.diagram.search.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.search.handlers.AutoCompleteSelectedHandler;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class AutoCompleteSelectedEvent extends GwtEvent<AutoCompleteSelectedHandler> {
    public static Type<AutoCompleteSelectedHandler> TYPE = new Type<>();

    private String term;

    public AutoCompleteSelectedEvent(String term) {
        this.term = term;
    }

    @Override
    public Type<AutoCompleteSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AutoCompleteSelectedHandler handler) {
        handler.onAutoCompleteSelected(this);
    }

    public String getTerm() {
        return term;
    }

    @Override
    public String toString() {
        return "AutoCompleteSelectedEvent{" +
                ", term=" + term +
                '}';
    }
}
