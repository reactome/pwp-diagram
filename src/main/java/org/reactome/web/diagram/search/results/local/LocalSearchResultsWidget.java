package org.reactome.web.diagram.search.results.local;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.reactome.web.diagram.client.DiagramFactory;
import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.interactors.common.OverlayResource;
import org.reactome.web.diagram.events.ContentLoadedEvent;
import org.reactome.web.diagram.events.ContentRequestedEvent;
import org.reactome.web.diagram.handlers.ContentLoadedHandler;
import org.reactome.web.diagram.handlers.ContentRequestedHandler;
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

import static org.reactome.web.diagram.search.events.ResultSelectedEvent.ResultType.LOCAL;


/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
@SuppressWarnings("all")
public class LocalSearchResultsWidget extends Composite implements ResultsWidget, SelectionChangeEvent.Handler,
        ContentLoadedHandler, ContentRequestedHandler {

    public final static String PREFIX = DiagramFactory.SERVER + "/ContentService/search/diagram/";

    private EventBus eventBus;
    private Context context;
    private OverlayResource overlayResource;
    private int scope = -1;

    private SearchArguments arguments;
    private SearchResultObject selectedItem;

    private SingleSelectionModel<SearchResultObject> selectionModel = new SingleSelectionModel<>(ResultsPanel.KEY_PROVIDER);
    private InfiniteScrollList<SearchResultObject> resultsList;
    private LocalSearchProvider dataProvider;

    private List<FacetContainer> facets = new ArrayList<>();
    private Set<String> selectedFacets = new HashSet<>();

    private FlowPanel main;

    public LocalSearchResultsWidget(int scope, EventBus eventBus) {
        this.eventBus = eventBus;
        this.scope = scope;

        SearchResultCell cell = new SearchResultCell();
        dataProvider = new LocalSearchProvider();
        resultsList = new InfiniteScrollList(cell, ResultsPanel.KEY_PROVIDER, dataProvider, ResultsPanel.CUSTOM_LIST_STYLE);
        resultsList.setSelectionModel(selectionModel);

        main = new FlowPanel();
        main.add(resultsList);

        selectionModel.addSelectionChangeHandler(this);

        initWidget(main);

        eventBus.addHandler(ContentRequestedEvent.TYPE, this);
        eventBus.addHandler(ContentLoadedEvent.TYPE, this);
    }

    @Override
    public HandlerRegistration addResultSelectedHandler(ResultSelectedHandler handler) {
        return addHandler(handler, ResultSelectedEvent.TYPE);
    }

    public HandlerRegistration addFacetsLoadedHandler(FacetsLoadedHandler handler) {
        return addHandler(handler, FacetsLoadedEvent.TYPE);
    }

    @Override
    public void onContentRequested(ContentRequestedEvent event) {
        this.context = null;
//        this.selectedItem = null;
    }

    @Override
    public void onContentLoaded(ContentLoadedEvent event) {
        this.context = event.getContext();
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

        if(arguments == null || !(arguments.equals(args) && overlayResource.equals(this.overlayResource))) {
            arguments = args;
            this.overlayResource = overlayResource;

            dataProvider.setSearchArguments(args, selectedFacets, overlayResource, PREFIX);
            dataProvider.setExtraItemsToShow(null);

            //Include the results of the search in the interactors (by searching inside the graph)
            if(selectedFacets.isEmpty() || selectedFacets.contains("Interactor")) {
                dataProvider.setExtraItemsToShow(interactors);
            }

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
        fireEvent(new ResultSelectedEvent(selectedItem, LOCAL));
    }

    @Override
    public void suspendSelection() {
        if (selectedItem != null) {
            fireEvent(new ResultSelectedEvent(null, LOCAL));
        }
    }

    private void restoreSelection() {
        if (selectedItem != null) {
            fireEvent(new ResultSelectedEvent(selectedItem, LOCAL));
        }
    }
}
