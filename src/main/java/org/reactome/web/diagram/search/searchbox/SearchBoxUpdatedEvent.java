package org.reactome.web.diagram.search.searchbox;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class SearchBoxUpdatedEvent extends GwtEvent<SearchBoxUpdatedHandler> {
    public static Type<SearchBoxUpdatedHandler> TYPE = new Type<SearchBoxUpdatedHandler>();

    private String value;

    public SearchBoxUpdatedEvent(String value) {
        this.value = value.trim();
    }

    @Override
    public Type<SearchBoxUpdatedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(SearchBoxUpdatedHandler handler) {
        handler.onSearchBoxUpdated(this);
    }

    public String getValue() {
        return value;
    }
}
