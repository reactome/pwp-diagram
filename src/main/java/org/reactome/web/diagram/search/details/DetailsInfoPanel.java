package org.reactome.web.diagram.search.details;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.Response;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.reactome.web.diagram.client.DiagramFactory;
import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.graph.model.*;
import org.reactome.web.diagram.data.interactors.common.OverlayResource;
import org.reactome.web.diagram.data.interactors.model.InteractorSearchResult;
import org.reactome.web.diagram.data.loader.LoaderManager;
import org.reactome.web.diagram.events.ContentLoadedEvent;
import org.reactome.web.diagram.events.ContentRequestedEvent;
import org.reactome.web.diagram.handlers.ContentLoadedHandler;
import org.reactome.web.diagram.handlers.ContentRequestedHandler;
import org.reactome.web.diagram.search.SearchArguments;
import org.reactome.web.diagram.search.SearchResultObject;
import org.reactome.web.diagram.search.events.*;
import org.reactome.web.diagram.search.handlers.AutoCompleteRequestedHandler;
import org.reactome.web.diagram.search.handlers.ResultSelectedHandler;
import org.reactome.web.diagram.search.handlers.SearchPerformedHandler;
import org.reactome.web.diagram.search.infopanel.DatabaseObjectListPanel;
import org.reactome.web.diagram.search.infopanel.EnhancedListPanel;
import org.reactome.web.diagram.search.infopanel.InteractorsListPanel;
import org.reactome.web.diagram.search.panels.AbstractAccordionPanel;
import org.reactome.web.diagram.search.results.ResultItem;
import org.reactome.web.diagram.search.results.data.InteractorOccurrencesFactory;
import org.reactome.web.diagram.search.results.data.model.Occurrences;
import org.reactome.web.diagram.search.results.data.model.SearchError;
import org.reactome.web.diagram.search.results.local.LocalOccurrencesFactory;
import org.reactome.web.diagram.util.Console;
import org.reactome.web.diagram.util.MapSet;
import org.reactome.web.pwp.model.client.classes.DatabaseObject;
import org.reactome.web.pwp.model.client.classes.Pathway;
import org.reactome.web.pwp.model.client.classes.Regulation;
import org.reactome.web.pwp.model.client.common.ContentClientHandler;
import org.reactome.web.pwp.model.client.content.ContentClient;
import org.reactome.web.pwp.model.client.content.ContentClientError;
import org.reactome.web.pwp.model.client.util.Ancestors;
import org.reactome.web.pwp.model.client.util.Path;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class DetailsInfoPanel extends AbstractAccordionPanel implements ResultSelectedHandler,
        ContentRequestedHandler, ContentLoadedHandler,
        SearchPerformedHandler, AutoCompleteRequestedHandler,
        LocalOccurrencesFactory.Handler,
        ContentClientHandler.ObjectListLoaded<Pathway> {

    private List<String> regulationTypes = Arrays.asList("regulation", "requirement", "positiveregulation", "negativeregulation");

    private EventBus eventBus;
    private Context context;
    private SearchArguments args;
    private SearchResultObject selectedResultItem;

    private FlowPanel mainPanel;
    private TitlePanel titlePanel;

    private FlowPanel spinner;

    private List<Widget> resultWidgets = new ArrayList<>();

    private final OverlayResource staticResource = new OverlayResource(DiagramFactory.INTERACTORS_INITIAL_RESOURCE, DiagramFactory.INTERACTORS_INITIAL_RESOURCE_NAME, OverlayResource.ResourceType.STATIC);
    private Collection<GraphObject> interactingPathways;

    public DetailsInfoPanel(EventBus eventBus) {
        this.eventBus = eventBus;
        show(false);

        Label header = new Label("Details");
        header.addStyleName(RESOURCES.getCSS().header());

        FlowPanel main = new FlowPanel();
        main.setStyleName(RESOURCES.getCSS().container());

        mainPanel = new FlowPanel();
        mainPanel.setStyleName(RESOURCES.getCSS().mainPanel());

        main.add(header);
        main.add(mainPanel);
        add(main);

        titlePanel = new TitlePanel(eventBus);

        spinner = getSpinner();

        eventBus.addHandler(ContentLoadedEvent.TYPE, this);
        eventBus.addHandler(ContentRequestedEvent.TYPE, this);
    }

    @Override
    public void onAutoCompleteRequested(AutoCompleteRequestedEvent event) {
        selectedResultItem = null;
        show(false);
    }

    @Override
    public void onContentLoaded(ContentLoadedEvent event) {
        context = event.getContext();
    }

    @Override
    public void onContentRequested(ContentRequestedEvent event) {
        context = null;
        selectedResultItem = null;
    }

    @Override
    public void onOccurrencesSuccess(Occurrences occurrences) {
        if (context == null)    return;

        populateWithLocalOccurrences(occurrences);
        hideSpinner();
    }

    @Override
    public void onOccurrencesError(SearchError error) {
        hideSpinner();
        switch(error.getCode()) {
            case Response.SC_NOT_FOUND:
                // In case of a 404 there are simply no occurrences and
                // we suppress the error.
                break;
            default:
                Console.error(new StringBuilder()
                        .append(error.getReason())
                        .append(" (")
                        .append(error.getCode())
                        .append(") ")
                        .append(error.getMessages())
                        .toString()
                );
        }
    }

    @Override
    public void onOccurrencesException(String msg) {
        Console.error(msg);
        hideSpinner();
    }

    @Override
    public void onResultSelected(ResultSelectedEvent event) {
        selectedResultItem = event.getSelectedResultItem();
        interactingPathways = new ArrayList<>();

        clearMainPanel();
        clearResults();
        mainPanel.add(titlePanel.setSelectedItem(selectedResultItem));
        showSpinner();

        if (selectedResultItem == null) {
            show(false);
        } else if (selectedResultItem instanceof ResultItem) {
            ResultItem item = (ResultItem) selectedResultItem;
            if(item.getExactType().equalsIgnoreCase("Interactor")) {
                populateWithLocalInteractor(item);
                queryForInteractorOccurrences(item);
            } else if(regulationTypes.contains(item.getExactType().toLowerCase())) {
                // In case this is a regulation we need an additional query to the server
                ContentClient.query(item.getStId(), new ObjectLoaded<DatabaseObject>() {
                    @Override
                    public void onObjectLoaded(DatabaseObject databaseObject) {
                        DatabaseObject r = ((Regulation) databaseObject).getRegulator();
                        selectedResultItem = new ResultItem(r);
                        titlePanel.setIdentifier(r.getStId());
                        LocalOccurrencesFactory.searchForInstanceInDiagram(r.getStId(), args.getDiagramStId(), DetailsInfoPanel.this);
                    }

                    @Override
                    public void onContentClientException(Type type, String message) {
                        //Nothing here
                    }

                    @Override
                    public void onContentClientError(ContentClientError error) {
                        //Nothing here
                    }
                });
            } else {
                LocalOccurrencesFactory.searchForInstanceInDiagram(item.getStId(), args.getDiagramStId(), this);
                Scheduler.get().scheduleDeferred(() -> queryForContainingPathways(item));
            }
            show(true);
        } else if (selectedResultItem instanceof InteractorSearchResult) {
            InteractorSearchResult item = (InteractorSearchResult) selectedResultItem;
            populateWithLocalInteractor(item);
            show(true);
        }
    }

    @Override
    public void onPanelCollapsed(PanelCollapsedEvent event) {
        super.onPanelCollapsed(event);
    }

    @Override
    public void onPanelExpanded(PanelExpandedEvent event) {
        show(selectedResultItem!=null);
    }

    @Override
    public void onSearchPerformed(SearchPerformedEvent event) {
        args = event.getSearchArguments();
        selectedResultItem = null;
        show(false);
    }

    @Override
    public void onObjectListLoaded(List<Pathway> list) {
        hideSpinner();
        // The following code removes the intersecting pathways between the 2 lists
        if(list != null && !list.isEmpty() && interactingPathways != null) {
            Set<String> interactingPathwayIds = interactingPathways.stream()
                                                                   .map(GraphObject::getStId)
                                                                   .collect(Collectors.toSet());
            list = list.stream()
                       .filter(pathway -> !interactingPathwayIds.contains(pathway.getStId()))
                       .collect(Collectors.toList());

            int size = list.size();
            if(!list.isEmpty()) {
                includeResultWidget(new EnhancedListPanel("Present in " + size + " pathway diagram" + (size > 1 ? "s:" : ":"), list, eventBus, context.getContent()));
            }
        }
    }

    @Override
    public void onContentClientException(Type type, String message) {
        hideSpinner();
        Console.error("Error while retrieving containing pathways: " + message);
    }

    @Override
    public void onContentClientError(ContentClientError error) {
        hideSpinner();
        switch(error.getCode()) {
            case Response.SC_NOT_FOUND:
                // In case of a 404 there are simply no occurrences and
                // we suppress the error.
                break;
            default:
                Console.error(new StringBuilder()
                        .append(error.getReason())
                        .append(" (")
                        .append(error.getCode())
                        .append(") ")
                        .append(error.getMessage())
                        .toString()
                );
        }
    }

    private void populateWithLocalOccurrences(Occurrences occurrences) {
        ResultItem selection = (ResultItem) selectedResultItem;
        if (occurrences.getInDiagram()) {
            //Selected entity is inside the diagram
            GraphObject graphObject = context.getContent().getDatabaseObject(selection.getStId());

            Collection<GraphReactionLikeEvent> participatesIn = new HashSet<>();
            if(graphObject != null && !graphObject.getDiagramObjects().isEmpty()){
                includeResultWidget(new DatabaseObjectListPanel("Directly in the diagram:", Collections.singletonList(graphObject), eventBus));

                if(graphObject instanceof GraphPhysicalEntity) {
                    participatesIn = ((GraphPhysicalEntity) graphObject).participatesIn();
                }else if(graphObject instanceof GraphReactionLikeEvent){
                    //TODO encapsulate it into a method in ReactionLikeEvent
                    Collection<GraphPhysicalEntity> rleParticipants = new HashSet<>();
                    GraphReactionLikeEvent rle = (GraphReactionLikeEvent) graphObject;
                    rleParticipants.addAll(rle.getInputs());
                    rleParticipants.addAll(rle.getOutputs());
                    rleParticipants.addAll(rle.getCatalysts());
                    rleParticipants.addAll(rle.getActivators());
                    rleParticipants.addAll(rle.getInhibitors());
                    rleParticipants.addAll(rle.getRequirements());
                    if (!rleParticipants.isEmpty()) {
                        int size = rleParticipants.size();
                        String title = "Participant" + (size>1?"s(":"(") + size + "):";
                        includeResultWidget(new DatabaseObjectListPanel(title, rleParticipants, eventBus));
                    }
                }

            }

            if(graphObject instanceof GraphPhysicalEntity) {
                GraphPhysicalEntity pe = (GraphPhysicalEntity) graphObject;
                Set<GraphPhysicalEntity> parentLocations = pe.getParentLocations();
                if (!parentLocations.isEmpty()) {
                    Set<GraphComplex> complexes = new HashSet<>();
                    Set<GraphEntitySet> sets = new HashSet<>();
                    for (GraphPhysicalEntity parentLocation : parentLocations) {
                        participatesIn.addAll(parentLocation.participatesIn());
                        if (parentLocation instanceof GraphComplex) complexes.add((GraphComplex) parentLocation);
                        else if (parentLocation instanceof GraphEntitySet) sets.add((GraphEntitySet) parentLocation);
                    }

                    if (!complexes.isEmpty()) {
                        int size = complexes.size();
                        String title = "Part of " + size + " complex" + (size > 1 ? "es:" : ":");
                        includeResultWidget(new DatabaseObjectListPanel(title, complexes, eventBus));
                    }

                    if (!sets.isEmpty()) {
                        int size = sets.size();
                        String title = "Part of " + size + " set" + (size > 1 ? "s:" : ":");
                        includeResultWidget(new DatabaseObjectListPanel(title, sets, eventBus));
                    }
                }
            }

            if(!participatesIn.isEmpty()){
                int size = participatesIn.size();
                String title = "Participates in " + size + " reaction" + (size>1?"s:":":");
                includeResultWidget(new DatabaseObjectListPanel(title, participatesIn, eventBus));
            }

            // Include information about the interactors of this entity
            if(context!=null && (graphObject instanceof GraphEntityWithAccessionedSequence || graphObject instanceof GraphSimpleEntity) ){
                String resource = LoaderManager.INTERACTORS_RESOURCE.getName();
                includeResultWidget(new InteractorsListPanel("According to " + resource + ", it interacts with:", context, (GraphPhysicalEntity) graphObject, eventBus));
            }
        }


        List<String> occList = occurrences.getOccurrences();
        if (occList != null && !occList.isEmpty()){
            //Selected entity is inside an encapsulated "Interacting" pathway
            interactingPathways = occList.stream()
                    .map(occ -> context.getContent().getDatabaseObject(occ))
                    .collect(Collectors.toList());
            includeResultWidget(new DatabaseObjectListPanel("Inside " + interactingPathways.size() + " subpathway" + (interactingPathways.size()>1 ? "s:" : ":"), interactingPathways, eventBus));
        }

        List<String> interactsWithList = occurrences.getInteractsWith();
        if (interactsWithList != null && !interactsWithList.isEmpty()){
            //Selected entity is inside an encapsulated "Interacting" pathway
            Collection<GraphObject> list = interactsWithList.stream()
                    .map(occ -> context.getContent().getDatabaseObject(occ))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            int size = list.size();
            if (size > 0) {
                includeResultWidget(new DatabaseObjectListPanel("Interacts with " + size + " diagram entit" + (size > 1 ? "ies" : "y"), list, eventBus));
            }
        }

    }

    private void populateWithLocalInteractor(InteractorSearchResult selection) {
        MapSet<Long, GraphObject> interactsWith = selection.getInteractsWith();
        for(Long interactionId:interactsWith.keySet()) {
            Set<GraphObject> interactors = new HashSet<>();
            // Identify only those entities that are directly in the
            // diagram and not part of a complex or a set
            for (GraphObject graphObject : interactsWith.getElements(interactionId)) {
                if(!graphObject.getDiagramObjects().isEmpty()){
                    interactors.add(graphObject);
                }
            }
            if (!interactors.isEmpty()) {
                int interactorsSize = selection.getEvidences(interactionId) != null ? selection.getEvidences(interactionId) : 0;
                String evidences = interactorsSize == 0 ? "" : (interactorsSize == 1 ? "(" + interactorsSize + " evidence)" : "(" + interactorsSize + " pieces of evidence)");

                Double score = selection.getInteractionScore(interactionId);
                String title = "Interacts with ";
                title = title + "score: " + (score!=null ? NumberFormat.getFormat("0.000").format(score): "-") + " " + evidences;
                includeResultWidget(new DatabaseObjectListPanel(title, interactors, eventBus));
            }
        }
    }

    private void populateWithLocalInteractor(ResultItem selection) {
        List<SearchResultObject> rtn = context.getInteractors().queryForInteractors(staticResource, context.getContent(), selection.getIdentifier().toLowerCase());
        if(rtn != null && rtn.size() == 1) {
            populateWithLocalInteractor((InteractorSearchResult) rtn.get(0));
        }
    }

    private void queryForContainingPathways(ResultItem item) {
        ContentClient.getAncestors(item.getStId(), new AncestorsLoaded() {
            @Override
            public void onAncestorsLoaded(Ancestors ancestors) {
                if (context == null) return;

                Set<Pathway> pathways = new HashSet<>();
                for (Path ancestor : ancestors) {
                    pathways.add(ancestor.getLastPathwayWithDiagram()); //We do not include subpathways in the list
                }

                if (!pathways.isEmpty() && !item.isDisplayed()) {

                    Set<String> interactingPathwayIds = interactingPathways.stream()
                                                                           .map(GraphObject::getStId)
                                                                           .collect(Collectors.toSet());

                    pathways = pathways.stream()
                                       .filter(pathway -> !interactingPathwayIds.contains(pathway.getStId()))
                                       .collect(Collectors.toSet());
                    int size = pathways.size();
                    if(!pathways.isEmpty()) {
                        includeResultWidget(new EnhancedListPanel("Present in " + size + " pathway diagram" + (size > 1 ? "s:" : ":"), pathways, eventBus, context.getContent()));
                    }
                }
            }

            @Override
            public void onContentClientException(Type type, String message) {
                getPathways();
            }

            @Override
            public void onContentClientError(ContentClientError error) {
                getPathways();
            }

            private void getPathways() {
                if (context == null) return;
                ContentClient.getPathwaysWithDiagramForEntity(((ResultItem) selectedResultItem).getStId(), false, context.getContent().getSpeciesName(), DetailsInfoPanel.this);
            }
        });
    }

    private void queryForInteractorOccurrences(ResultItem item) {
        InteractorOccurrencesFactory.query(item.getId(), args.getSpecies(), new InteractorOccurrencesFactory.Handler() {
            @Override
            public void onInteractorOccurrencesReceived(List<Pathway> pathways) {
                hideSpinner();
                if(pathways != null && !pathways.isEmpty()) {
                    int size = pathways.size();
                    includeResultWidget(new EnhancedListPanel("Present in " + size + " pathway diagram" + (size > 1 ? "s:" : ":"), pathways, eventBus, context.getContent()));
                }
            }

            @Override
            public void onInteractorOccurrencesError(String msg) {
                Console.warn("Error while retrieving interactor occurrences: " + msg);
                hideSpinner();
            }
        });
    }

    private void includeResultWidget(Widget widget) {
        mainPanel.add(widget);
        resultWidgets.add(widget);
    }

    private void clearMainPanel() {
        mainPanel.clear();
    }

    private void clearResults() {
        resultWidgets.forEach(w -> mainPanel.remove(w));
        resultWidgets.clear();
    }

    private FlowPanel getSpinner() {
        SimplePanel spinner = new SimplePanel();
        spinner.setStyleName(RESOURCES.getCSS().loader());
        SimplePanel spinnerContainer = new SimplePanel();
        spinnerContainer.setStyleName(RESOURCES.getCSS().loaderContainer());
        spinnerContainer.add(spinner);

        Label msgLabel = new Label("Loading...");
        FlowPanel rtn = new FlowPanel();
        rtn.setStyleName(RESOURCES.getCSS().loaderPanel());
        rtn.add(spinnerContainer);
        rtn.add(msgLabel);
        return rtn;
    }

    private void showSpinner() {
        mainPanel.add(spinner);
    }

    private void hideSpinner() {
        spinner.removeFromParent();
    }

    private void show(boolean visible) {
        if (visible) {
            getElement().getStyle().setDisplay(Style.Display.INLINE);
        } else {
            getElement().getStyle().setDisplay(Style.Display.NONE);
        }
    }

    public static Resources RESOURCES;
    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    public interface Resources extends ClientBundle {
        @Source(ResourceCSS.CSS)
        ResourceCSS getCSS();
    }

    @CssResource.ImportedWithPrefix("diagram-DetailsInfoPanel")
    public interface ResourceCSS extends CssResource {

        String CSS = "org/reactome/web/diagram/search/details/DetailsInfoPanel.css";

        String container();

        String header();

        String mainPanel();

        String loaderPanel();

        String loaderContainer();

        String loader();
    }
}
