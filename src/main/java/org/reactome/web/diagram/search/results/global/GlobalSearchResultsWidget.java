package org.reactome.web.diagram.search.results.global;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.reactome.web.diagram.client.DiagramFactory;
import org.reactome.web.diagram.data.interactors.common.OverlayResource;
import org.reactome.web.diagram.search.SearchArguments;
import org.reactome.web.diagram.search.SearchResultObject;
import org.reactome.web.diagram.search.events.FacetsLoadedEvent;
import org.reactome.web.diagram.search.events.ResultSelectedEvent;
import org.reactome.web.diagram.search.handlers.FacetsLoadedHandler;
import org.reactome.web.diagram.search.handlers.ResultSelectedHandler;
import org.reactome.web.diagram.search.results.ResultsPanel;
import org.reactome.web.diagram.search.results.ResultsWidget;
import org.reactome.web.diagram.search.results.cells.SearchResultCell;
import org.reactome.web.diagram.search.results.data.model.FacetContainer;
import org.reactome.web.scroller.client.InfiniteScrollList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.reactome.web.diagram.search.events.ResultSelectedEvent.ResultType.GLOBAL;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class GlobalSearchResultsWidget extends Composite implements ResultsWidget, SelectionChangeEvent.Handler {

    public final static String PREFIX = DiagramFactory.SERVER + "/ContentService/search/fireworks/";

    private int scope = -1;

    private SearchArguments arguments;
    private SearchResultObject selectedItem;

    private SingleSelectionModel<SearchResultObject> selectionModel = new SingleSelectionModel<>(ResultsPanel.KEY_PROVIDER);
    private InfiniteScrollList<SearchResultObject> resultsList;
    private GlobalSearchProvider dataProvider;

    private List<FacetContainer> facets = new ArrayList<>();
    private Set<String> selectedFacets = new HashSet<>();

    private FlowPanel main;

    public GlobalSearchResultsWidget(int scope) {
        this.scope = scope;

        SearchResultCell cell = new SearchResultCell();
        dataProvider = new GlobalSearchProvider();
        resultsList = new InfiniteScrollList(cell, ResultsPanel.KEY_PROVIDER, dataProvider, ResultsPanel.CUSTOM_LIST_STYLE);
        resultsList.setSelectionModel(selectionModel);

        main = new FlowPanel();
        main.add(resultsList);

        selectionModel.addSelectionChangeHandler(this);

        initWidget(main);
    }

    @Override
    public HandlerRegistration addResultSelectedHandler(ResultSelectedHandler handler) {
        return addHandler(handler, ResultSelectedEvent.TYPE);
    }

    public HandlerRegistration addFacetsLoadedHandler(FacetsLoadedHandler handler) {
        return addHandler(handler, FacetsLoadedEvent.TYPE);
    }

    @Override
    public void clearSelection() {
        selectionModel.clear();
        selectedItem = null;
    }

    @Override
    public void updateResults(SearchArguments args, OverlayResource overlayResource, List<SearchResultObject> interactors) {
        if(args == null) return;

        if(scope == args.getFacetsScope()) {
            selectedFacets = args.getFacets();
        }

        if(arguments == null || !arguments.equals(args)) {
            arguments = args;

            dataProvider.setSearchArguments(args, selectedFacets, PREFIX);

            resultsList.setPageSize(30);
            resultsList.loadFirstPage();
        }
        restoreSelection();

        fireEvent(new FacetsLoadedEvent(facets, selectedFacets, scope));
    }

    @Override
    public void setFacets(List<FacetContainer> facets) {
        if (facets!=null) {
            this.facets = new ArrayList<>(facets);
        }

        if(selectedFacets.isEmpty()) { return; }

        List<String> allFacets = facets.stream()
                                       .map(facetContainer -> facetContainer.getName())
                                       .collect(Collectors.toList());
        selectedFacets = selectedFacets.stream()
                                       .filter(aFacet -> allFacets.contains(aFacet))
                                       .collect(Collectors.toSet());
    }

    @Override
    public void onSelectionChange(SelectionChangeEvent event) {
        selectedItem = selectionModel.getSelectedObject();
        fireEvent(new ResultSelectedEvent(selectedItem, GLOBAL));
    }

    @Override
    public void suspendSelection() {
        if (selectedItem != null) {
            fireEvent(new ResultSelectedEvent(null, GLOBAL));
        }
    }

    private void restoreSelection() {
        if (selectedItem != null) {
            fireEvent(new ResultSelectedEvent(selectedItem, GLOBAL));
        }
    }

}
