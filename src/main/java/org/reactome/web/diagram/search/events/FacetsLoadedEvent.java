package org.reactome.web.diagram.search.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.search.handlers.FacetsLoadedHandler;
import org.reactome.web.diagram.search.results.data.model.FacetContainer;

import java.util.List;
import java.util.Set;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class FacetsLoadedEvent extends GwtEvent<FacetsLoadedHandler> {
    public static Type<FacetsLoadedHandler> TYPE = new Type<>();

    private List<FacetContainer> facets;
    private Set<String> selectedFacets;
    private int scope = -1;

    public FacetsLoadedEvent(List<FacetContainer> facets, Set<String> selectedFacets, int scope) {
        this.facets = facets;
        this.selectedFacets = selectedFacets;
        this.scope = scope;
    }

    @Override
    public Type<FacetsLoadedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(FacetsLoadedHandler handler) {
        handler.onFacetsLoaded(this);
    }

    public List<FacetContainer> getFacets() {
        return facets;
    }

    public Set<String> getSelectedFacets() {
        return selectedFacets;
    }

    public int getScope() {
        return scope;
    }

    @Override
    public String toString() {
        return "FacetsLoadedEvent{" +
                "facets=" + facets +
                ", selectedFacets=" + selectedFacets +
                ", scope=" + scope +
                '}';
    }
}
