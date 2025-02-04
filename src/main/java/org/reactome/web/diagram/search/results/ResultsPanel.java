package org.reactome.web.diagram.search.results;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.view.client.ProvidesKey;
import org.reactome.web.diagram.client.DiagramFactory;
import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.interactors.common.OverlayResource;
import org.reactome.web.diagram.data.interactors.model.InteractorSearchResult;
import org.reactome.web.diagram.data.loader.LoaderManager;
import org.reactome.web.diagram.events.ContentLoadedEvent;
import org.reactome.web.diagram.events.ContentRequestedEvent;
import org.reactome.web.diagram.events.InteractorsLoadedEvent;
import org.reactome.web.diagram.events.InteractorsResourceChangedEvent;
import org.reactome.web.diagram.handlers.ContentLoadedHandler;
import org.reactome.web.diagram.handlers.ContentRequestedHandler;
import org.reactome.web.diagram.handlers.InteractorsLoadedHandler;
import org.reactome.web.diagram.handlers.InteractorsResourceChangedHandler;
import org.reactome.web.diagram.search.SearchArguments;
import org.reactome.web.diagram.search.SearchResultObject;
import org.reactome.web.diagram.search.events.AutoCompleteRequestedEvent;
import org.reactome.web.diagram.search.events.PanelCollapsedEvent;
import org.reactome.web.diagram.search.events.PanelExpandedEvent;
import org.reactome.web.diagram.search.events.SearchPerformedEvent;
import org.reactome.web.diagram.search.facets.SearchSummaryFactory;
import org.reactome.web.diagram.search.handlers.AutoCompleteRequestedHandler;
import org.reactome.web.diagram.search.handlers.FacetsLoadedHandler;
import org.reactome.web.diagram.search.handlers.ResultSelectedHandler;
import org.reactome.web.diagram.search.handlers.SearchPerformedHandler;
import org.reactome.web.diagram.search.panels.AbstractAccordionPanel;
import org.reactome.web.diagram.search.results.data.model.FacetContainer;
import org.reactome.web.diagram.search.results.data.model.SearchResult;
import org.reactome.web.diagram.search.results.data.model.SearchSummary;
import org.reactome.web.diagram.search.results.global.GlobalSearchResultsWidget;
import org.reactome.web.diagram.search.results.local.LocalSearchResultsWidget;
import org.reactome.web.diagram.search.results.scopebar.ScopeBarPanel;
import org.reactome.web.diagram.util.Console;

import java.util.*;


/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class ResultsPanel extends AbstractAccordionPanel implements ScopeBarPanel.Handler,
        SearchSummaryFactory.Handler, SearchPerformedHandler, AutoCompleteRequestedHandler,
        InteractorsResourceChangedHandler, InteractorsLoadedHandler,
        ContentLoadedHandler, ContentRequestedHandler {

    private final static int LOCAL_SEARCH = 0;
    private final static int GLOBAL_SEARCH = 1;

    private Context context;

    private DeckLayoutPanel content;
    private ScopeBarPanel scopeBar;
    private List<ResultsWidget> resultsWidgets = new ArrayList<>();
    private ResultsWidget activeResultWidget;

    private SearchSummary summary;
    private SearchArguments searchArguments;
    private SearchArguments previousSearchArguments;

    private OverlayResource overlayResource;
    private Map<Integer, Set<String>> selectedFacetsMap = new HashMap<>();

    private boolean isSearchExpanded = false;

    /**
     * The key provider that provides the unique ID of a SearchResult.
     */
    public static final ProvidesKey<SearchResultObject> KEY_PROVIDER = item -> {
        if(item == null) {
            return null;
        } else if (item instanceof ResultItem) {
            ResultItem resultItem = (ResultItem) item;
            return resultItem.getId();
        } else if (item instanceof InteractorSearchResult) {
            InteractorSearchResult interactorSearchResult = (InteractorSearchResult) item;
            return interactorSearchResult.getAccession() + interactorSearchResult.getResource();
        }
        return null;
    };

    public ResultsPanel(EventBus eventBus) {
        this.sinkEvents(Event.ONCLICK);

        overlayResource = LoaderManager.INTERACTORS_RESOURCE;

        scopeBar = new ScopeBarPanel(this);
        scopeBar.addButton("This diagram", "Search only in the displayed diagram",ScopeBarPanel.RESOURCES.scopeLocal());
        scopeBar.addButton("All diagrams", "Expand your search in all our diagrams", ScopeBarPanel.RESOURCES.scopeGlobal());

        resultsWidgets.add(new LocalSearchResultsWidget(LOCAL_SEARCH, eventBus));
        resultsWidgets.add(new GlobalSearchResultsWidget(GLOBAL_SEARCH));

        content = new DeckLayoutPanel();
        content.setStyleName(RESOURCES.getCSS().content());
        resultsWidgets.forEach(w -> content.add(w.asWidget()));
        setActiveResultsWidget(LOCAL_SEARCH);
        content.setAnimationVertical(false);
        content.setAnimationDuration(500);

        FlowPanel main = new FlowPanel();
        main.setStyleName(RESOURCES.getCSS().main());
        main.add(scopeBar);
        main.add(content);
        add(main);

        show(false);

        eventBus.addHandler(ContentRequestedEvent.TYPE, this);
        eventBus.addHandler(ContentLoadedEvent.TYPE, this);
        eventBus.addHandler(InteractorsResourceChangedEvent.TYPE, this);
        eventBus.addHandler(InteractorsLoadedEvent.TYPE, this);
    }

    public HandlerRegistration addClickHandler(ClickHandler handler){
        return this.addHandler(handler, ClickEvent.getType());
    }

    public void addResultSelectedHandler(ResultSelectedHandler handler) {
        resultsWidgets.forEach(resultsWidget -> resultsWidget.addResultSelectedHandler(handler));
    }

    public void addFacetsLoadedHandler(FacetsLoadedHandler handler) {
        resultsWidgets.forEach(resultsWidget -> resultsWidget.addFacetsLoadedHandler(handler));
    }

    @Override
    public void onAutoCompleteRequested(AutoCompleteRequestedEvent event) {
        searchArguments = null;
        show(false);
    }

    @Override
    public void onContentLoaded(ContentLoadedEvent event) {
        context = event.getContext();
    }

    @Override
    public void onContentRequested(ContentRequestedEvent event) {
        context = null;
    }

    @Override
    public void onInteractorsResourceChanged(InteractorsResourceChangedEvent event) {
        if(context != null && context.getInteractors() != null) {
            if(context.getInteractors().isResourceLoaded(event.getResource().getIdentifier())) {
                overlayResource = event.getResource();
                List<SearchResultObject> extraInteractors = isStaticResourceLoaded() ? null : findInDiagramInteractors(searchArguments, overlayResource);
                updateScopeNumbers(summary, extraInteractors);
                updateFacets(summary, extraInteractors);
                updateResult(overlayResource, extraInteractors);

            }
        }
    }

    @Override
    public void onInteractorsLoaded(InteractorsLoadedEvent event) {
        overlayResource = LoaderManager.INTERACTORS_RESOURCE;
        if(context != null && context.getInteractors() != null) {
            List<SearchResultObject> interactors = isStaticResourceLoaded() ? null : findInDiagramInteractors(searchArguments, overlayResource);
            updateScopeNumbers(summary, interactors);
            updateFacets(summary, interactors);
            updateResult(overlayResource, interactors);
        }
    }

    @Override
    public void onSearchSummaryReceived(SearchSummary summary) {
        this.summary = summary;
        List<SearchResultObject> interactors = isStaticResourceLoaded() ? null : findInDiagramInteractors(searchArguments, overlayResource);
        updateScopeNumbers(summary, interactors);
        updateFacets(summary, interactors);
        updateResult(overlayResource, interactors);
    }

    @Override
    public void onSearchSummaryError(String msg) {
        Console.warn("Error retrieving search summary");
        summary = null;
        updateScopeNumbers(null, null);
        updateFacets(null, null);
        updateResult(overlayResource, null);
    }

    @Override
    public void onSearchPerformed(SearchPerformedEvent event) {
        searchArguments = event.getSearchArguments();

        //Store the selected facets
        selectedFacetsMap.put(searchArguments.getFacetsScope(), searchArguments.getFacets());

        boolean clearSelection = previousSearchArguments!=null &&
                (!previousSearchArguments.getQuery().equals(searchArguments.getQuery()) || previousSearchArguments.getFacets().size() != searchArguments.getFacets().size());

        if(searchArguments.hasValidQuery()) {
            // Get facets and numbers from content service before performing the search query
            SearchSummaryFactory.queryForSummary(searchArguments, this);
            if(clearSelection) {
                clearSelection();
            }
        }

        previousSearchArguments = searchArguments;
    }

    @Override
    public void onScopeChanged(int selected) {
        setActiveResultsWidget(selected);
        updateResult(overlayResource, null);
    }

    @Override
    public void onPanelCollapsed(PanelCollapsedEvent event) {
        super.onPanelCollapsed(event);
        isSearchExpanded = false;
    }

    @Override
    public void onPanelExpanded(PanelExpandedEvent event) {
        show(searchArguments != null);
        isSearchExpanded = true;
    }

    private void updateResult(OverlayResource overlayResource, List<SearchResultObject> interactors) {
        show(isSearchExpanded && searchArguments != null && searchArguments.hasValidQuery());
        activeResultWidget.updateResults(searchArguments, overlayResource, interactors);
    }

    private void clearSelection() {
        resultsWidgets.forEach(ResultsWidget::clearSelection);
    }

    private void setActiveResultsWidget(int index) {
        // Suspend the selection before changing scope
        if(activeResultWidget != null) {
            activeResultWidget.suspendSelection();
        }

        activeResultWidget = resultsWidgets.get(index);

        if (activeResultWidget != null) {
            content.showWidget(index);
        }
    }

    private void updateFacets(SearchSummary summary, List<SearchResultObject> interactors) {
        List<FacetContainer> localFacets = null;
        List<FacetContainer> globalFacets = null;
        if(summary!=null) {
            SearchResult localResults = summary.getDiagramResult();
            if (localResults!=null) {
                localFacets = localResults.getFacets()!=null ? localResults.getFacets() : new ArrayList<>();

                if(interactors!=null) {
                    // Include the Interactor facet in the facets received by the server
                    localFacets = includeInteractorFacet(localFacets, interactors.size());
                }
            }

            SearchResult globalResults = summary.getFireworksResult();
            if (globalResults!=null) {
                globalFacets = globalResults.getFacets()!=null ? globalResults.getFacets() : new ArrayList<>();
            }
        }
        resultsWidgets.get(LOCAL_SEARCH).setFacets(localFacets);
        resultsWidgets.get(GLOBAL_SEARCH).setFacets(globalFacets);

        updateCurrentScopeNumbers(LOCAL_SEARCH, localFacets);
        updateCurrentScopeNumbers(GLOBAL_SEARCH, globalFacets);
    }

    private void updateCurrentScopeNumbers(int buttonIndex, List<FacetContainer> facets) {
        int current = 0;
        if (facets!=null) {
            Set<String> selectedFacets = selectedFacetsMap.getOrDefault(buttonIndex, new HashSet<>());
            current = facets.stream()
                    .filter(facetContainer -> selectedFacets.contains(facetContainer.getName()))
                    .mapToInt(FacetContainer::getCount)
                    .sum();
        }
        scopeBar.setCurrentResultsNumber(buttonIndex, current);
    }

    private void updateScopeNumbers(SearchSummary summary, List<SearchResultObject> interactors) {
        int localResultsFound = 0, globalResultsFound = 0;
        if (summary!=null) {
            SearchResult localResults = summary.getDiagramResult();
            if (localResults!=null && localResults.getFound()!=null) {
                localResultsFound = localResults.getFound() + (interactors !=null ? interactors.size() : 0);
            }
            SearchResult globalResults = summary.getFireworksResult();
            if (globalResults!=null && globalResults.getFound()!=null) {
                globalResultsFound = globalResults.getFound();
            }
        }
        scopeBar.setTotalResultsNumber(LOCAL_SEARCH, localResultsFound);
        scopeBar.setTotalResultsNumber(GLOBAL_SEARCH, globalResultsFound);
    }

    private List<FacetContainer> includeInteractorFacet(List<FacetContainer> facets, int count) {
        List<FacetContainer> rtn = new ArrayList<>(facets);
        Optional<FacetContainer> interactorFacet = rtn.stream().filter(f -> f.getName().equals("Interactor")).findAny();

        if (interactorFacet.isPresent()) { //We need to add up the number of interactors provided by the API
            count += interactorFacet.get().getCount();
        }

        int finalCount = count;
        rtn.add(new FacetContainer() {
            @Override
            public String getName() {
                return "Interactor";
            }

            @Override
            public Integer getCount() {
                return finalCount;
            }
        });
        return rtn;
    }

    private void show(boolean visible) {
        if (visible) {
            getElement().getStyle().setDisplay(Style.Display.INLINE);
        } else {
            getElement().getStyle().setDisplay(Style.Display.NONE);
        }
    }

    private List<SearchResultObject> findInDiagramInteractors(SearchArguments args, OverlayResource overlayResource) {
        List<SearchResultObject> rtn = null;
        if(args!=null && context != null && overlayResource != null) {
            rtn = context.getInteractors().queryForInteractors(overlayResource, context.getContent(), args.getQuery());
            if(rtn != null) {
                rtn.forEach(item -> item.setSearchDisplay(args));
            }
        }
        return rtn;
    }

    private boolean isStaticResourceLoaded() {
        return overlayResource.getIdentifier().equalsIgnoreCase(DiagramFactory.INTERACTORS_INITIAL_RESOURCE);
    }

    public static Resources RESOURCES;
    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    /**
     * A ClientBundle of resources used by this widget.
     */
    public interface Resources extends ClientBundle {
        /**
         * The styles used in this widget.
         */
        @Source(ResourceCSS.CSS)
        ResourceCSS getCSS();
    }

    /**
     * Styles used by this widget.
     */
    @CssResource.ImportedWithPrefix("diagram-ResultsPanel")
    public interface ResourceCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/diagram/search/results/ResultsPanel.css";

        String main();

        String content();

    }

    public static CellListResource CUSTOM_LIST_STYLE;
    static {
        CUSTOM_LIST_STYLE = GWT.create(CellListResource.class);
        CUSTOM_LIST_STYLE.cellListStyle().ensureInjected();
    }

    public interface CellListResource extends CellList.Resources {

        @CssResource.ImportedWithPrefix("diagram-CellListResource")
        interface CustomCellList extends CellList.Style {
            String CSS = "org/reactome/web/diagram/search/results/ResultsList.css";
        }

        /**
         * The styles used in this widget.
         */
        @Override
        @Source(CustomCellList.CSS)
        CustomCellList cellListStyle();
    }
}
