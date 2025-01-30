package org.reactome.web.diagram.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RequiresResize;
import org.reactome.web.diagram.client.visualisers.Visualiser;
import org.reactome.web.diagram.client.visualisers.diagram.DiagramVisualiser;
import org.reactome.web.diagram.client.visualisers.ehld.SVGVisualiser;
import org.reactome.web.diagram.controls.navigation.NavigationControlPanel;
import org.reactome.web.diagram.controls.notifications.NotificationsContainer;
import org.reactome.web.diagram.controls.settings.HideableContainerPanel;
import org.reactome.web.diagram.controls.settings.RightContainerPanel;
import org.reactome.web.diagram.controls.top.LeftTopLauncherPanel;
import org.reactome.web.diagram.controls.top.RightTopLauncherPanel;
import org.reactome.web.diagram.data.AnalysisStatus;
import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.content.Content;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.interactors.common.OverlayResource;
import org.reactome.web.diagram.data.interactors.model.DiagramInteractor;
import org.reactome.web.diagram.events.*;
import org.reactome.web.diagram.handlers.*;
import org.reactome.web.diagram.legends.*;
import org.reactome.web.diagram.messages.AnalysisMessage;
import org.reactome.web.diagram.messages.ErrorMessage;
import org.reactome.web.diagram.messages.LoadingMessage;
import org.reactome.web.pwp.model.client.classes.DatabaseIdentifier;
import org.reactome.web.pwp.model.client.classes.DatabaseObject;
import org.reactome.web.pwp.model.client.classes.Pathway;
import org.reactome.web.pwp.model.client.common.ContentClientHandler;
import org.reactome.web.pwp.model.client.content.ContentClient;
import org.reactome.web.pwp.model.client.content.ContentClientError;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.reactome.web.diagram.data.content.Content.Type.DIAGRAM;
import static org.reactome.web.diagram.data.content.Content.Type.SVG;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class ViewerContainer extends AbsolutePanel implements RequiresResize,
        CanvasExportRequestedHandler, ControlActionHandler,
        DiagramObjectsFlaggedHandler, DiagramObjectsFlagResetHandler, DiagramObjectsFlagRequestHandler,
        GraphObjectSelectedHandler, OptionalWidget.Handler {

    protected EventBus eventBus;
    protected Context context;

    protected Map<Content.Type, Visualiser> visualisers;
    protected Visualiser activeVisualiser;

    protected LeftTopLauncherPanel leftTopLauncher;
    protected RightContainerPanel rightContainerPanel;
    protected BottomContainerPanel bottomContainerPanel;
    protected InteractorsControl interactorsControl;
    protected HideableContainerPanel hideableContainerPanel;
    private Anchor watermark;
    private Anchor pharmGKB;

    public static Timer windowScrolling = new Timer() {
        @Override
        public void run() { /* Nothing here */ }
    };

    protected LeftTopLauncherPanel leftTopLauncherPanel;
    protected RightTopLauncherPanel rightTopLauncherPanel;
    protected NavigationControlPanel navigationPanel;

    public ViewerContainer(EventBus eventBus) {
        this.getElement().setClassName("pwp-ViewerContainer");
        this.eventBus = eventBus;

        visualisers = new HashMap<>();
        leftTopLauncher = new LeftTopLauncherPanel(eventBus);
        rightContainerPanel = new RightContainerPanel();
        bottomContainerPanel = new BottomContainerPanel();


        initialise();
        initHandlers();
    }


    protected void initialise() {
        //All Viewers with their thumbnails
        visualisers.put(DIAGRAM, new DiagramVisualiser(eventBus));
        visualisers.put(SVG, new SVGVisualiser(eventBus));

        for (Visualiser vis : visualisers.values()) {
            this.add(vis);
        }

        addExternalVisualisers();

        //Set this as default
        activeVisualiser = visualisers.get(DIAGRAM);

        //DO NOT CHANGE THE ORDER OF THE FOLLOWING TWO LINES
        this.add(new LoadingMessage(eventBus));                 //Loading message panel
        this.add(new AnalysisMessage(eventBus));                //Analysis overlay message panel
        this.add(new ErrorMessage(eventBus));                   //Error message panel

        //Watermark
        this.addWatermark();

        //collaborative PharmGKB
        this.addPharmGKBLogo();

        //Right container
        this.add(rightContainerPanel);

        //Control panel
        if (OptionalWidget.NAVIGATION.isVisible()) {
            navigationPanel = new NavigationControlPanel(eventBus);
            this.add(navigationPanel);
        }

        //Top Controls container
        TopContainerPanel topContainerPanel = new TopContainerPanel();
        this.add(topContainerPanel);

        //Bottom Controls container
        if (OptionalWidget.BOTTOM_POP_UP.isVisible()) {
            this.add(bottomContainerPanel);
        }

        //Panel notifying that a filter is present and has affected the display;
        topContainerPanel.add(new FilterAlertControl(eventBus));

        //Flagged Objects control panel
        bottomContainerPanel.add(new FlaggedItemsControl(eventBus));

        //Enrichment legend and control panels
        rightContainerPanel.add(new EnrichmentLegend(eventBus));
        bottomContainerPanel.add(new EnrichmentControl(eventBus));

        //Expression legend and control panels
        rightContainerPanel.add(new ExpressionLegend(eventBus));
        bottomContainerPanel.add(new ExpressionControl(eventBus));

        //Regulation legend
        rightContainerPanel.add(new RegulationLegend(eventBus));

        //Interactors control panel
        bottomContainerPanel.add(interactorsControl = new InteractorsControl(eventBus));

        //Info panel
        if (OptionalWidget.INFO.isVisible()) {
            this.add(new DiagramInfo(eventBus));
        }

        //Notifications Panel
        this.add(new NotificationsContainer(eventBus));

        //Launcher panels
        this.add(leftTopLauncher);
        this.add(new RightTopLauncherPanel(eventBus));

        //Settings panel
        if (OptionalWidget.COLLAPSABLE_MENU.isVisible()) {
            rightContainerPanel.add(hideableContainerPanel = new HideableContainerPanel(eventBus));
        }

    }

    protected void addExternalVisualisers() {/* Nothing here */}


    public boolean highlightGraphObject(GraphObject graphObject, boolean notify) {
        return activeVisualiser.highlightGraphObject(graphObject, notify);
    }

    public void highlightInteractor(DiagramInteractor diagramInteractor) {
        activeVisualiser.highlightInteractor(diagramInteractor);
    }

    public void contentLoaded(final Context context) {
        this.context = context;
        setPharmGKBLogo(context);
        setWatermarkVisible(true);
        setWatermarkURL(context, null);
        setActiveVisualiser(context);
        activeVisualiser.contentLoaded(context);
    }

    public void contentRequested() {
        activeVisualiser.resetSelection(false);
        activeVisualiser.resetHighlight(false);
        activeVisualiser.contentRequested();
        setWatermarkVisible(false);
        setPharmGKBVisible(false);
        context = null;
    }

    public void expressionColumnChanged() {
        activeVisualiser.expressionColumnChanged();
    }

    public void interactorsCollapsed(String resource) {
        activeVisualiser.interactorsCollapsed(resource);
    }

    public void interactorsFiltered() {
        activeVisualiser.interactorsFiltered();
    }

    public void interactorsLayoutUpdated() {
        activeVisualiser.interactorsLayoutUpdated();
    }

    public void interactorsLoaded() {
        activeVisualiser.interactorsLoaded();
    }

    public void interactorsResourceChanged(OverlayResource resource) {
        activeVisualiser.interactorsResourceChanged(resource);
    }

    @Override
    public void onCanvasExportRequested(CanvasExportRequestedEvent event) {
        activeVisualiser.exportView();
    }

    @Override
    public void onDiagramObjectsFlagRequested(DiagramObjectsFlagRequestedEvent event) {
        context.setFlagTerm(null);
        setWatermarkURL(context, activeVisualiser.getSelected());
        activeVisualiser.resetFlag();
    }

    @Override
    public void onDiagramObjectsFlagged(DiagramObjectsFlaggedEvent event) {
        context.setFlagTerm(event.getTerm());
        setWatermarkURL(context, activeVisualiser.getSelected());
        activeVisualiser.flagItems(event.getFlaggedItems(), event.getIncludeInteractors());
    }

    @Override
    public void onDiagramObjectsFlagReset(DiagramObjectsFlagResetEvent event) {
        context.setFlagTerm(null);
        setWatermarkURL(context, activeVisualiser.getSelected());
        activeVisualiser.resetFlag();
    }

    @Override
    public void onControlAction(ControlActionEvent event) {
        switch (event.getAction()) {
            case FIT_ALL:
                activeVisualiser.fitDiagram(true);
                break;
            case ZOOM_IN:
                activeVisualiser.zoomIn();
                break;
            case ZOOM_OUT:
                activeVisualiser.zoomOut();
                break;
            case UP:
                activeVisualiser.padding(0, 10);
                break;
            case RIGHT:
                activeVisualiser.padding(-10, 0);
                break;
            case DOWN:
                activeVisualiser.padding(0, -10);
                break;
            case LEFT:
                activeVisualiser.padding(10, 0);
                break;
            case FIREWORKS:
                overview();
                break;
        }
    }

    @Override
    public void onGraphObjectSelected(final GraphObjectSelectedEvent event) {
        setWatermarkURL(this.context, event.getGraphObject());
    }

    @Override
    public void onResize() {
        final int width = this.getOffsetWidth();
        final int height = this.getOffsetHeight();
        activeVisualiser.setSize(width, height);

        //Deffer the resizing of the rest
        Scheduler.get().scheduleDeferred(() -> {
            visualisers.values().stream()
                    .filter(v -> v != activeVisualiser)
                    .forEach(v -> v.setSize(width, height));
        });
    }

    public boolean selectItem(GraphObject item, boolean notify) {
        return activeVisualiser.selectGraphObject(item, notify);
    }

    public boolean resetHighlight(boolean notify) {
        return activeVisualiser.resetHighlight(notify);
    }

    public boolean resetSelection(boolean notify) {
        return activeVisualiser.resetSelection(notify);
    }

    public void resetAnalysis() {
        setWatermarkURL(this.context, activeVisualiser.getSelected());
        activeVisualiser.resetAnalysis();
    }

    public void loadAnalysis() {
        setWatermarkURL(context, activeVisualiser.getSelected());
        activeVisualiser.loadAnalysis();
    }

    public void resetContext() {
        this.context = null;
        activeVisualiser.resetFlag();
        activeVisualiser.resetContext();
    }

    public void setContext(final Context context) {
        this.context = context;
        setActiveVisualiser(context);
        activeVisualiser.setContext(context);
        activeVisualiser.fitDiagram(false);
    }

    private void setWatermarkURL(Context context, GraphObject selection) {
        if (watermark != null) {
            StringBuilder href = new StringBuilder(DiagramFactory.WATERMARK_BASE_URL);
            String pathwayStId = context == null ? null : context.getContent().getStableId();
            if (pathwayStId != null && !pathwayStId.isEmpty()) {
                href.append("#/").append(pathwayStId);
                if (selection != null) {
                    if (selection.getStId() != null && !selection.getStId().isEmpty()) {
                        href.append("&SEL=").append(selection.getStId());
                    }
                }
                AnalysisStatus analysisStatus = context.getAnalysisStatus();
                if (analysisStatus != null) {
                    href.append("&DTAB=AN").append("&ANALYSIS=").append(analysisStatus.getToken()).append("&RESOURCE=").append(analysisStatus.getResource());
                }
                if (context.getFlagTerm() != null && !context.getFlagTerm().isEmpty()) {
                    href.append("&FLG=").append(context.getFlagTerm());
                }
            }
            watermark.setHref(href.toString());
        }
    }

    private void setWatermarkVisible(boolean visible) {
        if (watermark != null) {
            watermark.setVisible(visible);
        }
    }

    private void setPharmGKBVisible(boolean visible) {
        if (pharmGKB != null) {
            pharmGKB.setVisible(visible);
        }
    }

    private void adjustPharmGKBPosition() {
        if (pharmGKB != null) {
            pharmGKB.setStyleName(RESOURCES.getCSS().pharmGKBAdjustPosition());
        }
    }

    private void addWatermark() {
        if (DiagramFactory.WATERMARK) {
            Image img = new Image(RESOURCES.logo());
            SafeHtml image = SafeHtmlUtils.fromSafeConstant(img.toString());
            watermark = new Anchor(image, DiagramFactory.WATERMARK_BASE_URL, "_blank");
            watermark.setTitle("Open this pathway in Reactome Pathway Browser");
            watermark.setStyleName(RESOURCES.getCSS().watermark());
            watermark.setVisible(false);
            add(watermark);
        }
    }

    private void addPharmGKBLogo() {
        if (DiagramFactory.PHARMGKB) {
            Image img = new Image(RESOURCES.pharmGKB_logo());
            SafeHtml image = SafeHtmlUtils.fromSafeConstant(img.toString());
            pharmGKB = new Anchor(image, DiagramFactory.PHARMGKB_BASE_URL, "_blank");
            pharmGKB.getElement().setId("pharmGKB");
            pharmGKB.setStyleName(RESOURCES.getCSS().pharmGKB());
            pharmGKB.setVisible(false);
            add(pharmGKB);
        }
    }

    private void initHandlers() {
        //Only add the window scroll handler if it makes sense
        if (DiagramFactory.SCROLL_SENSITIVITY > 0) {
            Window.addWindowScrollHandler(event -> windowScrolling.schedule(DiagramFactory.SCROLL_SENSITIVITY));
        }

        eventBus.addHandler(GraphObjectSelectedEvent.TYPE, this);

        eventBus.addHandler(DiagramObjectsFlagRequestedEvent.TYPE, this);
        eventBus.addHandler(DiagramObjectsFlaggedEvent.TYPE, this);
        eventBus.addHandler(DiagramObjectsFlagResetEvent.TYPE, this);

        eventBus.addHandler(CanvasExportRequestedEvent.TYPE, this);

        eventBus.addHandler(ControlActionEvent.TYPE, this);
    }

    private void overview() {
        eventBus.fireEventFromSource(new FireworksOpenedEvent(context.getContent().getDbId()), this);
    }

    protected void setActiveVisualiser(Context context) {
        if (context != null) {
            Visualiser visualiser = visualisers.get(context.getContent().getType());
            if (visualiser != null && activeVisualiser != visualiser) {
                for (Visualiser vis : visualisers.values()) {
                    if (vis == visualiser) {
                        vis.asWidget().setVisible(true);
                    } else {
                        vis.asWidget().setVisible(false);
                    }
                }
                activeVisualiser = visualiser;
            }
        }
    }

    public void setPharmGKBLogo(Context context) {
        if (pharmGKB != null) {
            String stId = context == null ? null : context.getContent().getStableId();
            ContentClient.query(stId, new ContentClientHandler.ObjectLoaded<DatabaseObject>() {
                @Override
                public void onObjectLoaded(DatabaseObject databaseObject) {
                    if (databaseObject instanceof Pathway) {
                        Pathway pathway = (Pathway) databaseObject;
                        if(!pathway.getCrossReference().isEmpty()){
                            setPharmGKBVisible(pathway.getCrossReference().stream().filter(Objects::nonNull).anyMatch(id -> id.getDatabaseName().contains(DiagramFactory.PHARMGKB_RESOURCE)));
                            for(DatabaseIdentifier databaseIdentifier: pathway.getCrossReference()){
                                if(databaseIdentifier.getDatabaseName().equalsIgnoreCase(DiagramFactory.PHARMGKB_RESOURCE)){
                                    pharmGKB.setHref(databaseIdentifier.getUrl());
                                }
                            }
                        }
                    }
                }

                @Override
                public void onContentClientException(Type type, String s) {
                    // nothing here
                }

                @Override
                public void onContentClientError(ContentClientError contentClientError) {
                    //nothing here
                }
            });
        }
    }

    public Context getContext() {
        return this.context;
    }


    public static Resources RESOURCES;

    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    public interface Resources extends ClientBundle {
        @Source(ResourceCSS.CSS)
        ResourceCSS getCSS();

        @Source("images/watermark.png")
        ImageResource logo();

        @Source("images/pharmGKB.png")
        ImageResource pharmGKB_logo();
    }

    @CssResource.ImportedWithPrefix("diagram-ViewerContainer")
    public interface ResourceCSS extends CssResource {
        String CSS = "org/reactome/web/diagram/client/ViewerContainer.css";

        String watermark();

        String pharmGKB();

        String pharmGKBAdjustPosition();
    }
}