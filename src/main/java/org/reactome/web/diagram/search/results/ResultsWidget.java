package org.reactome.web.diagram.search.results;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;
import org.reactome.web.diagram.data.interactors.common.OverlayResource;
import org.reactome.web.diagram.search.SearchArguments;
import org.reactome.web.diagram.search.SearchResultObject;
import org.reactome.web.diagram.search.handlers.FacetsLoadedHandler;
import org.reactome.web.diagram.search.handlers.ResultSelectedHandler;
import org.reactome.web.diagram.search.results.data.model.FacetContainer;

import java.util.List;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public interface ResultsWidget extends IsWidget {

    HandlerRegistration addResultSelectedHandler(ResultSelectedHandler handler);

    HandlerRegistration addFacetsLoadedHandler(FacetsLoadedHandler handler);

    /**
     * Clears any selected list item
     */
    void clearSelection();

    /**
     * Updates the view with results based on the SearchArguments and the extra interactors to be added
     */
    void updateResults(SearchArguments searchArguments, OverlayResource overlayResource, List<SearchResultObject> interactors);

    /**
     * Sets the available facets
     *
     * @param facets the list of facets
     */
    void setFacets(List<FacetContainer> facets);

    /**
     * Temporarily suspends highlighting of the selection
     * without clearing the selected item
     */
    void suspendSelection();

}
