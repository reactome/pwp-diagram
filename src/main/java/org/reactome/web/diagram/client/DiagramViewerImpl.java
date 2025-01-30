package org.reactome.web.diagram.client;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import org.reactome.web.analysis.client.filter.ResultFilter;
import org.reactome.web.diagram.client.visualisers.Visualiser;
import org.reactome.web.diagram.client.visualisers.diagram.InteractorsManager;
import org.reactome.web.diagram.controls.top.search.SearchPanel;
import org.reactome.web.diagram.data.AnalysisStatus;
import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.GraphObjectFactory;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.loader.AnalysisDataLoader;
import org.reactome.web.diagram.data.loader.AnalysisTokenValidator;
import org.reactome.web.diagram.data.loader.FlaggedElementsLoader;
import org.reactome.web.diagram.data.loader.LoaderManager;
import org.reactome.web.diagram.events.*;
import org.reactome.web.diagram.handlers.*;
import org.reactome.web.diagram.search.handlers.SearchPerformedHandler;
import org.reactome.web.diagram.search.results.data.model.Occurrences;
import org.reactome.web.diagram.util.Console;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class DiagramViewerImpl extends AbstractDiagramViewer implements
        LayoutLoadedHandler, ContentRequestedHandler, ContentLoadedHandler, KeyDownHandler,
        InteractorsLoadedHandler, InteractorsResourceChangedHandler, InteractorsCollapsedHandler, InteractorHoveredHandler,
        InteractorsLayoutUpdatedHandler, InteractorsFilteredHandler, InteractorSelectedHandler,
        AnalysisResultRequestedHandler, AnalysisResultLoadedHandler, AnalysisResetHandler, ExpressionColumnChangedHandler,
        GraphObjectHoveredHandler, GraphObjectSelectedHandler,
        DiagramObjectsFlagRequestHandler, DiagramObjectsFlaggedHandler, DiagramObjectsFlagResetHandler,
        DiagramProfileChangedHandler, AnalysisProfileChangedHandler,
        FireworksOpenedHandler, FlaggedElementsLoader.Handler {

    protected Context context;
    protected final ViewerContainer viewerContainer;
    protected LoaderManager loaderManager;
    private AnalysisStatus analysisStatus;
    private InteractorsManager interactorsManager;
    private FlaggedElementsLoader flaggedElementsLoader = new FlaggedElementsLoader(this);
    protected Boolean includeInteractors = false;

    public DiagramViewerImpl() {
        super();
        this.viewerContainer = createViewerContainer();
        this.loaderManager = createLoaderManager();
        AnalysisDataLoader.initialise(eventBus);
        this.interactorsManager = new InteractorsManager(eventBus);
        this.initWidget(this.viewerContainer);
        this.getElement().addClassName("pwp-DiagramViewer"); //IMPORTANT!
    }

    protected ViewerContainer createViewerContainer() {
        return new ViewerContainer(eventBus);
    }

    protected LoaderManager createLoaderManager() {
        return new LoaderManager(eventBus);
    }

    protected void initialise() {
        if (!initialised) { //initialised is defined in the AbstractDiagramViewer
            super.initialise();
            this.initHandlers();
        }
    }

    private void initHandlers() {
        //Attaching this as a KeyDownHandler
        RootPanel.get().addDomHandler(this, KeyDownEvent.getType());

        eventBus.addHandler(AnalysisResultRequestedEvent.TYPE, this);
        eventBus.addHandler(AnalysisResultLoadedEvent.TYPE, this);
        eventBus.addHandler(AnalysisResetEvent.TYPE, this);
        eventBus.addHandler(ExpressionColumnChangedEvent.TYPE, this);

        eventBus.addHandler(GraphObjectSelectedEvent.TYPE, this);
        eventBus.addHandler(GraphObjectHoveredEvent.TYPE, this);

        eventBus.addHandler(ContentLoadedEvent.TYPE, this);
        eventBus.addHandler(ContentRequestedEvent.TYPE, this);
        eventBus.addHandler(DiagramObjectsFlaggedEvent.TYPE, this);
        eventBus.addHandler(DiagramObjectsFlagRequestedEvent.TYPE, this);
        eventBus.addHandler(DiagramObjectsFlagResetEvent.TYPE, this);

        eventBus.addHandler(InteractorsCollapsedEvent.TYPE, this);
        eventBus.addHandler(InteractorHoveredEvent.TYPE, this);
        eventBus.addHandler(InteractorsResourceChangedEvent.TYPE, this);
        eventBus.addHandler(InteractorsLayoutUpdatedEvent.TYPE, this);
        eventBus.addHandler(InteractorsFilteredEvent.TYPE, this);
        eventBus.addHandler(InteractorSelectedEvent.TYPE, this);

        eventBus.addHandler(LayoutLoadedEvent.TYPE, this);
        eventBus.addHandler(InteractorsLoadedEvent.TYPE, this);

        eventBus.addHandler(FireworksOpenedEvent.TYPE, this);

        eventBus.addHandler(DiagramProfileChangedEvent.TYPE, this);
        eventBus.addHandler(AnalysisProfileChangedEvent.TYPE, this);
    }

    @Override
    public void flagItems(String identifier, Boolean includeInteractors) {
        if (context != null && identifier != null) {
            if (!identifier.equalsIgnoreCase(context.getFlagTerm()) || !this.includeInteractors.equals(includeInteractors)) {
                eventBus.fireEventFromSource(new DiagramObjectsFlagRequestedEvent(identifier, includeInteractors), this);
            }
        }
    }


    @Override
    public void highlightItem(String stableIdentifier) {
        try {
            GraphObject item = this.context.getContent().getDatabaseObject(stableIdentifier);
            viewerContainer.highlightGraphObject(item, true);
        } catch (Exception e) {/*Nothing here*/}
    }

    @Override
    public void highlightItem(Long dbIdentifier) {
        try {
            GraphObject item = this.context.getContent().getDatabaseObject(dbIdentifier);
            viewerContainer.highlightGraphObject(item, true);
        } catch (Exception e) {/*Nothing here*/}
    }

    @Override
    public void loadDiagram(String stId) {
        if (stId != null) {
            if (context == null || !stId.equals(context.getContent().getStableId())) {
                load(stId); //Names are interchangeable because there are symlinks
            }
        }
    }

    @Override
    public void loadDiagram(Long dbId) {
        if (dbId != null) {
            if (context == null || !dbId.equals(context.getContent().getDbId())) {
                load("" + dbId); //Names are interchangeable because there are symlinks
            }
        }
    }

    protected void load(String identifier) { //TODO: stay here
        loaderManager.load(identifier);
    }

    private void clearAnalysisOverlay() {
        context.clearAnalysisOverlay();
        interactorsManager.clearAnalysisOverlay();
    }

    private void loadAnalysis(AnalysisStatus analysisStatus) {
        if (analysisStatus == null) {
            if (this.analysisStatus != null) {
                this.eventBus.fireEventFromSource(new AnalysisResetEvent(false), this);
            }
        } else if (!analysisStatus.equals(this.context.getAnalysisStatus())) {
            this.analysisStatus = analysisStatus;
            clearAnalysisOverlay();
            AnalysisDataLoader.get().loadAnalysisResult(analysisStatus, this.context.getContent());
        }
    }

    @Override
    public HandlerRegistration addSearchPerformedHandler(SearchPerformedHandler handler) {
        SearchPanel searchPanel = this.viewerContainer.leftTopLauncher.getSearchPanel();
        if (searchPanel != null) {
            return searchPanel.launcher.addSearchPerformedHandler(handler);
        } else {
            return () -> {
            };
        }
    }

    @Override
    public void onAnalysisReset(AnalysisResetEvent event) {
        if (event.getFireExternally()) {
            fireEvent(event);
        }
        this.resetAnalysis();
    }

    @Override
    public void onAnalysisResultLoaded(AnalysisResultLoadedEvent event) {
        analysisStatus.setAnalysisSummary(event.getSummary());
        analysisStatus.setExpressionSummary(event.getExpressionSummary());
        context.setAnalysisOverlay(analysisStatus, event.getFoundElements(), event.getPathwaySummaries());
        interactorsManager.setAnalysisOverlay(event.getFoundElements(), context.getContent().getIdentifierMap());
        Scheduler.get().scheduleDeferred(viewerContainer::loadAnalysis);
    }

    @Override
    public void onAnalysisResultRequested(AnalysisResultRequestedEvent event) {
        clearAnalysisOverlay();
        this.analysisStatus.setExpressionSummary(null);
        viewerContainer.resetAnalysis();
    }

    @Override
    public void onDiagramObjectsFlagged(DiagramObjectsFlaggedEvent event) {
        if (event.getNotify()) {
            this.fireEvent(event);
        }
    }

    @Override
    public void onDiagramObjectsFlagRequested(DiagramObjectsFlagRequestedEvent event) {
        boolean notify = !event.getSource().equals(this);

        context.setFlagTerm(event.getTerm());
        this.includeInteractors = event.getIncludeInteractors();
        Set<DiagramObject> flagged = context.getFlagged(context.getFlagTerm() + includeInteractors);
        if (flagged == null) {
            flaggedElementsLoader.load(context.getContent(), event.getTerm(), notify);
        } else {
            eventBus.fireEventFromSource(new DiagramObjectsFlaggedEvent(event.getTerm(), includeInteractors, flagged, notify), this);
        }
    }

    @Override
    public void flaggedElementsLoaded(String term, Occurrences toFlag, boolean notify) {
        Set<DiagramObject> flagged = new HashSet<>();
        if (toFlag != null && toFlag.getOccurrences() != null) {
            for (String stId : toFlag.getOccurrences()) {
                GraphObject graphObject = context.getContent().getDatabaseObject(stId);
                if (graphObject != null) {
                    flagged.addAll(graphObject.getDiagramObjects());
                    //Next step gets all glyph in the diagram containing the target object
                    if (graphObject instanceof GraphPhysicalEntity) {
                        GraphPhysicalEntity pe = (GraphPhysicalEntity) graphObject;
                        for (GraphPhysicalEntity parentLocation : pe.getParentLocations()) {
                            flagged.addAll(parentLocation.getDiagramObjects());
                        }
                    }
                }
            }
        }

        //Flag those diagram entities that interact with the term
        if (toFlag != null && toFlag.getInteractsWith() != null && includeInteractors) {
            for (String stId : toFlag.getInteractsWith()) {
                GraphObject graphObject = context.getContent().getDatabaseObject(stId);
                if (graphObject != null) {
                    flagged.addAll(graphObject.getDiagramObjects());
                }
            }
        }

        context.setFlagged(term + includeInteractors, flagged);
        eventBus.fireEventFromSource(new DiagramObjectsFlaggedEvent(term, includeInteractors, flagged, notify), this);
    }

    @Override
    public void onFlaggedElementsLoaderError(Throwable exception) {
        context.setFlagTerm(null);
        Console.error(exception.getMessage());
    }

    @Override
    public void onDiagramObjectsFlagReset(DiagramObjectsFlagResetEvent event) {
        if (!event.getSource().equals(this)) {
            this.fireEvent(event);
        }
    }

    @Override
    public void onContentLoaded(ContentLoadedEvent event) {
        this.context = event.getContext();
        viewerContainer.contentLoaded(context);
        fireEvent(event);
    }

    @Override
    public void onContentRequested(ContentRequestedEvent event) {
        flaggedElementsLoader.cancel();
        viewerContainer.contentRequested();
        this.resetContext();
    }

    @Override
    public void onExpressionColumnChanged(ExpressionColumnChangedEvent e) {
        Scheduler.get().scheduleDeferred(viewerContainer::expressionColumnChanged);
    }

    @Override
    public void onFireworksOpened(FireworksOpenedEvent event) {
        fireEvent(event);
    }

    @Override
    public void onGraphObjectHovered(GraphObjectHoveredEvent event) {
        //In order to have fine grain hovering capabilities, this class is not taking actions for onGraphObjectHovered
        //when it is fired by a visualiser, so we ONLY want to do the STANDARD action (highlight) when the event comes from
        //the outside. That is the reason of the next line of code
        if (event.getSource() instanceof Visualiser) {
            // Handles outside notification in case the event comes from a visualiser.
            fireEvent(event);
        } else {
            // Highlight and notify depending on the outcome
            if (context == null) return;
            if (viewerContainer.highlightGraphObject(event.getGraphObject(), false)) {
                fireEvent(event);
            }
        }
    }

    @Override
    public void onGraphObjectSelected(final GraphObjectSelectedEvent event) {
        if (event.getSource() instanceof Visualiser) {
            // Handles outside notification in case the event comes from a visualiser.
            if (event.getFireExternally()) {
                fireEvent(event);
            }
        } else {
            // Highlight and notify depending on the outcome
            if (context == null) return;
            if (viewerContainer.selectItem(event.getGraphObject(), false)) {
                if (event.getFireExternally()) {
                    fireEvent(event);
                }
            }
        }
    }

    @Override
    public void onInteractorHovered(InteractorHoveredEvent event) {
        //In order to have fine grain hovering capabilities, this class is not taking actions for onInteractorHovered
        //when it is fired by its own, so we ONLY want to do the STANDARD action (highlight) when the event comes from
        //the outside. That is the reason of the next line of code
        if (event.getSource() instanceof Visualiser) {
            fireEvent(event); //needs outside notification
        } else {
            if (context != null) {
                viewerContainer.highlightInteractor(event.getInteractor());
            }
        }
    }

    @Override
    public void onInteractorsCollapsed(InteractorsCollapsedEvent event) {
        viewerContainer.interactorsCollapsed(event.getResource());
    }

    @Override
    public void onInteractorsFiltered(InteractorsFilteredEvent event) {
        viewerContainer.interactorsFiltered();
    }

    @Override
    public void onInteractorsLayoutUpdated(InteractorsLayoutUpdatedEvent event) {
        viewerContainer.interactorsLayoutUpdated();
    }

    @Override
    public void onInteractorsLoaded(InteractorsLoadedEvent event) {
        viewerContainer.interactorsLoaded();
    }

    @Override
    public void onInteractorsResourceChanged(InteractorsResourceChangedEvent event) {
        viewerContainer.interactorsResourceChanged(event.getResource());
    }

    @Override
    public void onInteractorSelected(InteractorSelectedEvent event) {
        String url = event.getUrl();
        if (url != null) {
            Window.open(url, "_blank", "");
        }
    }

    @Override
    public void onLayoutLoaded(LayoutLoadedEvent event) {
        setContext(event.getContext());
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        Scheduler.get().scheduleDeferred(this::initialise);
    }

    @Override
    public void onResize() {
        super.onResize(); //Need to call super to propagate the resizing to the contained elements
        this.viewportWidth = getOffsetWidth();
        this.viewportHeight = getOffsetHeight();
    }

    @Override
    public void resetAnalysis() {
        this.analysisStatus = null;
        clearAnalysisOverlay();
        viewerContainer.resetAnalysis();
    }

    private void resetDialogs() {
        if (this.context != null) {
            this.context.hideDialogs();
        }
    }

    @Override
    public void resetFlaggedItems() {
        this.eventBus.fireEventFromSource(new DiagramObjectsFlagResetEvent(), this);
    }

    @Override
    public void resetHighlight() {
        viewerContainer.resetHighlight(true);
    }

    @Override
    public void resetSelection() {
        viewerContainer.resetSelection(true);
    }

    @Override
    public void selectItem(String stableIdentifier) {
        selectItem(context.getContent().getDatabaseObject(stableIdentifier), true);
    }

    @Override
    public void selectItem(Long dbIdentifier) {
        selectItem(context.getContent().getDatabaseObject(dbIdentifier), true);
    }

    private void selectItem(GraphObject item, boolean notify) {
        viewerContainer.selectItem(item, notify);
    }

    @Override
    public void setAnalysisToken(String token, ResultFilter filter) {
        final AnalysisStatus analysisStatus = (token == null) ? null : new AnalysisStatus(eventBus, token, filter);
        AnalysisTokenValidator.checkTokenAvailability(token, (available, message) -> {
            if (available) {
                loadAnalysis(analysisStatus);
            } else {
                eventBus.fireEventFromSource(new DiagramInternalErrorEvent(message), DiagramViewerImpl.this);
            }
        });
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) onResize();
        if (context != null) {
            if (visible) context.restoreDialogs();
            else context.hideDialogs();
        }
    }

    private void resetContext() {
        viewerContainer.resetContext();
        if (this.context != null) {
            //Once a context is due to be replaced, the analysis overlay has to be cleaned up
            clearAnalysisOverlay();
            this.context = null;
        }
        GraphObjectFactory.content = null;
    }

    private void setContext(final Context context) {
        this.resetContext();

        this.context = context;
        GraphObjectFactory.content = context.getContent();

        viewerContainer.setContext(context);

        if (this.context.getContent().isGraphLoaded()) {
            this.loadAnalysis(this.analysisStatus); //IMPORTANT: This needs to be done once context is been set up above
            this.eventBus.fireEventFromSource(new ContentLoadedEvent(context), this);
        }
    }

    @Override
    public void onKeyDown(KeyDownEvent keyDownEvent) {
        if (isVisible() && DiagramFactory.RESPOND_TO_SEARCH_SHORTCUT) {
            int keyCode = keyDownEvent.getNativeKeyCode();
            String platform = Window.Navigator.getPlatform();
            // If this is a Mac, check for the cmd key. In case of any other platform, check for the ctrl key
            boolean isModifierKeyPressed = platform.toLowerCase().contains("mac") ? keyDownEvent.isMetaKeyDown() : keyDownEvent.isControlKeyDown();
            if (keyCode == KeyCodes.KEY_F && isModifierKeyPressed) {
                keyDownEvent.preventDefault();
                keyDownEvent.stopPropagation();
                eventBus.fireEventFromSource(new SearchKeyPressedEvent(), this);
            }
        }
    }

    @Override
    public void onDiagramProfileChanged(DiagramProfileChangedEvent event) {
        fireEvent(event);
    }

    @Override
    public void onAnalysisProfileChanged(AnalysisProfileChangedEvent event) {
        fireEvent(event);
    }
}