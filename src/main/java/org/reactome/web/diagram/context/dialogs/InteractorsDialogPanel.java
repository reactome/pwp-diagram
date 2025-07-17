package org.reactome.web.diagram.context.dialogs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import org.reactome.web.analysis.client.model.AnalysisType;
import org.reactome.web.analysis.client.model.ExpressionSummary;
import org.reactome.web.diagram.context.dialogs.interactors.InteractorsTable;
import org.reactome.web.diagram.context.dialogs.interactors.TableItemSelectedEvent;
import org.reactome.web.diagram.context.dialogs.interactors.TableItemSelectedHandler;
import org.reactome.web.diagram.context.dialogs.molecules.ChangeLabelsEvent;
import org.reactome.web.diagram.context.dialogs.molecules.ChangeLabelsHandler;
import org.reactome.web.diagram.data.AnalysisStatus;
import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.InteractorsContent;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.data.interactors.model.DiagramInteractor;
import org.reactome.web.diagram.data.interactors.raw.RawInteractor;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.loader.LoaderManager;
import org.reactome.web.diagram.events.*;
import org.reactome.web.diagram.handlers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class InteractorsDialogPanel extends Composite implements TableItemSelectedHandler, InteractorsLoadedHandler,
        InteractorsResourceChangedHandler, InteractorsErrorHandler, InteractorHoveredHandler, InteractorsFilteredHandler,
        AnalysisResultLoadedHandler, AnalysisResetHandler, ExpressionColumnChangedHandler, AnalysisProfileChangedHandler,
        GraphObjectHoveredHandler, ChangeLabelsHandler {

    private EventBus eventBus;
    private FlowPanel container;
    private Image loadingIcon;
    private Context context;
    private GraphPhysicalEntity physicalEntity;

    private AnalysisType analysisType;
    private List<String> expColumns;
    private Double min;
    private Double max;
    private int selectedExpCol = 0;

    private String currentResource;

    private List<RawInteractor> interactions;
    private InteractorsTable<RawInteractor> interactorsTable;

    public InteractorsDialogPanel(EventBus eventBus, DiagramObject diagramObject, Context context) {
        this.eventBus = eventBus;
        this.context = context;
        this.physicalEntity = diagramObject.getGraphObject();
        this.currentResource = LoaderManager.INTERACTORS_RESOURCE.getIdentifier();
        this.interactions = new LinkedList<>();

        AnalysisStatus analysisStatus = context.getAnalysisStatus();
        if(analysisStatus != null){
            this.analysisType = analysisStatus.getAnalysisType();
            ExpressionSummary expressionSummary = analysisStatus.getExpressionSummary();
            if(expressionSummary!=null) {
                this.expColumns = expressionSummary.getColumnNames();
                this.max = expressionSummary.getMax();
                this.min = expressionSummary.getMin();
                this.selectedExpCol = analysisStatus.getColumn();
            }
        }

        initialiseWidget();
        initHandlers();
    }

    @SuppressWarnings("Duplicates")
    private void initHandlers(){
        this.eventBus.addHandler(InteractorsLoadedEvent.TYPE, this);
        this.eventBus.addHandler(InteractorsErrorEvent.TYPE, this);
        this.eventBus.addHandler(InteractorsResourceChangedEvent.TYPE, this);
        this.eventBus.addHandler(InteractorHoveredEvent.TYPE, this);
        this.eventBus.addHandler(InteractorsFilteredEvent.TYPE, this);
        this.eventBus.addHandler(GraphObjectHoveredEvent.TYPE, this);
        this.eventBus.addHandler(AnalysisResultLoadedEvent.TYPE, this);
        this.eventBus.addHandler(AnalysisResetEvent.TYPE, this);
        this.eventBus.addHandler(ExpressionColumnChangedEvent.TYPE, this);
        this.eventBus.addHandler(AnalysisProfileChangedEvent.TYPE, this);
    }

    public void forceDraw(){
        if(interactorsTable!=null) {
            interactorsTable.redraw();
        }
    }

    @Override
    public void onAnalysisProfileChanged(AnalysisProfileChangedEvent event) {
        interactorsTable.addExpressionColumns(expColumns, min, max, selectedExpCol);
        interactorsTable.removeExpressionColumns();
        loadExpressionValues();
    }

    @Override
    public void onAnalysisReset(AnalysisResetEvent event) {
        interactorsTable.removeExpressionColumns();
        interactorsTable.setAnalysisType(null);
    }

    @Override
    public void onAnalysisResultLoaded(AnalysisResultLoadedEvent event) {
        if(event.getSummary().getInteractors()) {
            analysisType = event.getType();
            interactorsTable.setAnalysisType(analysisType);
            ExpressionSummary expressionSummary = event.getExpressionSummary();
            interactorsTable.removeExpressionColumns();
            if(expressionSummary!=null) {
                expColumns = expressionSummary.getColumnNames();
                min = expressionSummary.getMin();
                max = expressionSummary.getMax();
                loadExpressionValues();
            }
        }
    }

    @Override
    public void onExpressionColumnChanged(ExpressionColumnChangedEvent e) {
        selectedExpCol = e.getColumn();
        interactorsTable.highlightExpColumn(selectedExpCol);
    }

    @Override
    public void onChangeLabels(ChangeLabelsEvent event) {
        if(interactorsTable!=null) interactorsTable.setInteractorsLabels(event.getShowIds());
    }

    @Override
    public void onInteractorsLoaded(InteractorsLoadedEvent event) {
        updateDialogContent();
        showLoading(false);
    }

    @Override
    public void onInteractorsError(InteractorsErrorEvent event) {
        updateDialogContent();
        showLoading(false);
    }

    @Override
    public void onInteractorsFiltered(InteractorsFilteredEvent event) {
        interactorsTable.setThreshold(event.getScore());
    }

    @Override
    public void onInteractorHovered(InteractorHoveredEvent event) {
        DiagramInteractor interactor = event.getInteractor();
        interactorsTable.setHovered(interactor);
    }

    @Override
    public void onGraphObjectHovered(GraphObjectHoveredEvent event) {
        GraphObject obj = event.getGraphObject();
        String acc = null;
        if(obj instanceof GraphPhysicalEntity) {
            GraphPhysicalEntity pe = (GraphPhysicalEntity) obj;
            acc = pe.getIdentifier();
        }
        interactorsTable.setHovered(acc);
    }

    @Override
    public void onInteractorsResourceChanged(InteractorsResourceChangedEvent event) {
        currentResource = event.getResource().getIdentifier();
        showLoading(true);
        updateDialogContent();
    }

    @Override
    public void onTableItemSelected(TableItemSelectedEvent event) {
        RawInteractor interactor = event.getValue();
        TableItemSelectedEvent.Selection selectedColumn =  event.getSelectedColumn();

        String url = null;
        switch(selectedColumn) {
            case ACCESSION:
                url = interactor.getAccURL();
                break;
            case ID:
            case SCORE:
                url = interactor.getEvidencesURL();
                break;
        }
        eventBus.fireEventFromSource(new InteractorSelectedEvent(url), this);
    }

    private void initialiseWidget(){
        container = new FlowPanel();
        container.setStyleName(RESOURCES.getCSS().container());

        double threshold = InteractorsContent.getInteractorsThreshold(currentResource);
        interactorsTable = new InteractorsTable<>("Interactors", threshold, analysisType, expColumns, min, max, selectedExpCol);
        interactorsTable.setHeight("150px");
        interactorsTable.addTableItemSelectedHandler(this);

        loadingIcon = new Image(PathwaysDialogPanel.RESOURCES.loader());
        container.add(loadingIcon);
        container.add(interactorsTable);
        initWidget(container);

        updateDialogContent();
        showLoading(false);
    }

    private void loadExpressionValues(){
        if(min==null || max==null) return;
        if(interactorsTable!=null) {
            interactorsTable.addExpressionColumns(expColumns, min, max, selectedExpCol);
            interactorsTable.highlightExpColumn(selectedExpCol);
        }

    }

    private void updateDialogContent(){
        if(context==null) return;
        clearInteractors();
        if(context.getInteractors().isResourceLoaded(currentResource)){
            setInteractions();
        } else {
            showLoading(true);
        }
    }

    private void setInteractions() {
        if (context != null) {
            interactions.addAll(context.getInteractors().getRawInteractors(currentResource, physicalEntity.getIdentifier()));
            interactorsTable.updateRows(interactions);
            showLoading(false);
        }
    }

    private void clearInteractors(){
        interactions.clear();
        interactorsTable.updateRows(interactions);
    }

    private void showLoading(boolean isLoading){
        loadingIcon.setVisible(isLoading);
        interactorsTable.setVisible(!isLoading);
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

    @CssResource.ImportedWithPrefix("diagram-MoleculesDialogPanel")
    public interface ResourceCSS extends CssResource {

        String CSS = "org/reactome/web/diagram/context/DialogPanelsCommon.css";

        String container();
    }
}
