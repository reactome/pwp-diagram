package org.reactome.web.diagram.client.visualisers.diagram;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.SimplePanel;
import org.reactome.web.analysis.client.model.AnalysisType;
import org.reactome.web.diagram.client.visualisers.Visualiser;
import org.reactome.web.diagram.common.DiagramAnimationHandler;
import org.reactome.web.diagram.common.DisplayManager;
import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.DiagramStatus;
import org.reactome.web.diagram.data.graph.model.GraphEvent;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.data.interactors.common.OverlayResource;
import org.reactome.web.diagram.data.interactors.model.DiagramInteractor;
import org.reactome.web.diagram.data.interactors.model.InteractorEntity;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.Node;
import org.reactome.web.diagram.data.layout.SummaryItem;
import org.reactome.web.diagram.data.layout.impl.CoordinateFactory;
import org.reactome.web.diagram.events.*;
import org.reactome.web.diagram.handlers.*;
import org.reactome.web.diagram.renderers.common.HoveredItem;
import org.reactome.web.diagram.thumbnail.Thumbnail;
import org.reactome.web.diagram.thumbnail.diagram.StaticIllustrationThumbnail;
import org.reactome.web.diagram.util.ViewportUtils;
import org.reactome.web.diagram.util.chemical.ChemicalImageLoader;
import org.reactome.web.diagram.util.pdbe.PDBeLoader;
import uk.ac.ebi.pwp.structures.quadtree.client.Box;

import java.util.Collection;
import java.util.Set;

import static org.reactome.web.analysis.client.model.AnalysisType.*;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class DiagramVisualiser extends SimplePanel implements Visualiser,
        UserActionsManager.Handler, DiagramAnimationHandler,
        DiagramProfileChangedHandler, AnalysisProfileChangedHandler, InteractorProfileChangedHandler,
        StructureImageLoadedHandler, ThumbnailAreaMovedHandler {
    protected EventBus eventBus;

    private boolean initialised = false;
    private int viewportWidth = 0;
    private int viewportHeight = 0;

    private final DiagramCanvas canvas; //Canvas only created once and reused every time a new diagram is loaded
    private Thumbnail thumbnail;
    private StaticIllustrationThumbnail staticIllustrationThumbnail;
    private final DiagramManager diagramManager;

    private Context context;

    private UserActionsManager userActionsManager;

    private LayoutManager layoutManager;
    private InteractorsManager interactorsManager;

    // mouse positions relative to canvas (not the model)
    // Do not assign the same value at the beginning
    private Coordinate mouseCurrent = CoordinateFactory.get(-100, -100);
    private Coordinate mousePrevious = CoordinateFactory.get(-200, -200);

    private boolean forceDraw = false;

    private Boolean includeInteractors;

    public DiagramVisualiser(EventBus eventBus) {
        super();
        this.eventBus = eventBus;
        this.canvas = new DiagramCanvas(eventBus);

        PDBeLoader.initialise(eventBus);
        ChemicalImageLoader.initialise(eventBus);

        this.layoutManager = new LayoutManager(eventBus);
        this.interactorsManager = new InteractorsManager(eventBus);

        this.userActionsManager = new UserActionsManager(this, canvas);

        this.diagramManager = new DiagramManager(new DisplayManager(this));
        this.add(this.canvas);
        this.getElement().addClassName("pwp-DiagramVisualiser"); //IMPORTANT!

    }

    protected void initialise() {
        if (!initialised) {
            this.initialised = true;
            this.viewportWidth = getParent().getOffsetWidth();
            this.viewportHeight = getParent().getOffsetHeight();
            this.setWidth(viewportWidth + "px");
            this.setHeight(viewportHeight + "px");

            canvas.initialise();
            thumbnail = canvas.getThumbnail();
            staticIllustrationThumbnail = canvas.getStaticIllustrationThumbnail();

            this.initHandlers();
            AnimationScheduler.get().requestAnimationFrame(new AnimationScheduler.AnimationCallback() {
                @Override
                public void execute(double timestamp) {
                    if (isVisible()) {
                        doUpdate();
                    }
                    AnimationScheduler.get().requestAnimationFrame(this); // Call it again.
                }
            });
        }
    }

    private void initHandlers() {
        canvas.addUserActionsHandlers(userActionsManager);

        eventBus.addHandler(AnalysisProfileChangedEvent.TYPE, this);
        eventBus.addHandler(DiagramProfileChangedEvent.TYPE, this);
        eventBus.addHandler(InteractorProfileChangedEvent.TYPE, this);
        eventBus.addHandler(ThumbnailAreaMovedEvent.TYPE, this);
        eventBus.addHandler(StructureImageLoadedEvent.TYPE, this);
    }

    private void doUpdate() {
        if (context == null) return;
        if (forceDraw) {
            forceDraw = false;
            Box visibleArea = context.getVisibleModelArea(viewportWidth, viewportHeight);
            draw(visibleArea);
            drawInteractors(visibleArea);
        } else if (!mouseCurrent.equals(mousePrevious)) {
            mousePrevious = mouseCurrent;
            DiagramInteractor hoveredInteractor = getHoveredInteractor();
            canvas.setCursor(hoveredInteractor == null ? Style.Cursor.DEFAULT : Style.Cursor.POINTER);
            if (hoveredInteractor != null) {
                resetHighlight(true); // This resets the layout highlight -> please note that the method is defined in the DiagramViewer interface
            } else { // It is needed otherwise the getHoveredDiagramObject will find a possible diagram layout object behind the interactor
                HoveredItem hovered = getHoveredDiagramObject();
                DiagramObject item = hovered != null ? hovered.getHoveredObject() : null;
                canvas.setCursor(item == null ? Style.Cursor.DEFAULT : Style.Cursor.POINTER);
                highlightHoveredItem(hovered);
            }
            highlightInteractor(hoveredInteractor); //if hovered interactor is null, calling highlightInteractor will clear the previous highlight
        }
    }

    private void draw(Box visibleArea) {
        if (context == null) return;
        long start = System.currentTimeMillis();
        canvas.clear();
        Collection<DiagramObject> items = context.getContent().getVisibleItems(visibleArea);
        canvas.render(items, context);
        canvas.select(layoutManager.getSelectedDiagramObjects(), context);
        canvas.highlight(layoutManager.getHovered(), context);
        canvas.decorators(layoutManager.getHovered(), context);
        canvas.halo(layoutManager.getHalo(), context);
        canvas.flag(layoutManager.getFlagged(), context);
        long time = System.currentTimeMillis() - start;
        thumbnail.diagramRendered(context.getContent(), visibleArea);
        staticIllustrationThumbnail.diagramRendered(context);

        this.eventBus.fireEventFromSource(new DiagramRenderedEvent(context.getContent(), visibleArea, items.size(), time), this);
    }

    private void drawInteractors(Box visibleArea) {
        if (context == null) return;
        String resource = interactorsManager.getCurrentResource();
        Collection<DiagramInteractor> items = context.getInteractors().getVisibleInteractors(resource, visibleArea);
        canvas.renderInteractors(items, context);
        canvas.highlightInteractor(interactorsManager.getHovered(), context);
    }

    @Override
    public DiagramStatus getDiagramStatus() {
        return this.context.getDiagramStatus();
    }

    @Override
    public void transform(Coordinate offset, double factor) {
        //An animation can be working and a new pathway might be requested :(
        if (this.context == null) return;
        DiagramStatus status = this.context.getDiagramStatus();
        status.setOffset(offset);
        status.setFactor(factor);
        this.forceDraw = true;
        Box visibleArea = this.context.getVisibleModelArea(viewportWidth, viewportHeight);
        this.eventBus.fireEventFromSource(new DiagramZoomEvent(factor, visibleArea), this);
        thumbnail.diagramZoomEvent(visibleArea);
    }

    private void highlightHoveredItem(HoveredItem hovered) {
        if (layoutManager.isHighlighted(hovered)) return;
        canvas.highlight(hovered, context);
        canvas.decorators(hovered, context);
        GraphObjectHoveredEvent event = layoutManager.setHovered(hovered);
        if (event != null) {
            // Outside notification is handled at a higher level.
            this.eventBus.fireEventFromSource(event, this);
            GraphObject obj = hovered != null ? hovered.getGraphObject() : null;
            thumbnail.graphObjectHovered(obj);
        }
    }

    public void highlightInteractor(DiagramInteractor hovered) {
        if (interactorsManager.isHighlighted(hovered)) return;
        // setInteractorHovered knows when an interactor is being dragged, so it won't set a new one if that case
        if (userActionsManager.setInteractorHovered(hovered)) {
            canvas.highlightInteractor(hovered, context);
            InteractorHoveredEvent event = interactorsManager.setHovered(hovered);
            if (event != null) {
                this.eventBus.fireEventFromSource(event, this);

            }
        }
    }

    @Override
    public boolean highlightGraphObject(GraphObject graphObject, boolean notify) {
        boolean rtn = false;
        HoveredItem hovered = new HoveredItem(graphObject);
        if (!layoutManager.isHighlighted(hovered)) {
            canvas.highlight(hovered, context);
            thumbnail.graphObjectHovered(graphObject);
            if (notify) {
                //we don't rely on the listener of the following event because finer grain of the hovering is lost
                eventBus.fireEventFromSource(new GraphObjectHoveredEvent(graphObject), this);
            }
            rtn = true;
        }
        return rtn;
    }

    @Override
    public void loadDiagram(String stId) {
        if (stId != null) {
            if (this.context == null || !stId.equals(this.context.getContent().getStableId())) {
                this.load(stId); //Names are interchangeable because there are symlinks
            }
        }
    }

    private void load(String identifier) {
        eventBus.fireEventFromSource(new ContentRequestedEvent(identifier), this);
    }

    @Override
    public void loadAnalysis() {
        forceDraw = true;
    }

    @Override
    public void resetAnalysis() {
        forceDraw = true;
    }

    @Override
    public void onAnalysisProfileChanged(AnalysisProfileChangedEvent event) {
        forceDraw = true;
    }

    /**
     * Helper method so extensions of the diagram project have a way to force update after adding render information.
     */
    public void forceDraw() {
        forceDraw = true;
    }

    @Override
    public void exportView() {
        if (context != null) {
            canvas.showExportDialog(context, layoutManager.getSelected(), context.getFlagTerm(), includeInteractors);
        }
    }

    @Override
    public void flagItems(Set<DiagramObject> flaggedItems, Boolean includeInteractors) {
        this.includeInteractors = includeInteractors;
        layoutManager.setFlagged(flaggedItems);
        forceDraw = true;
    }

    @Override
    public void resetFlag() {
        if (layoutManager.resetFlagged()) forceDraw = true;
    }

    @Override
    public void contentLoaded(Context context) {
        this.context = context;
        this.context.restoreDialogs();
    }

    @Override
    public void contentRequested() {
        this.resetDialogs();
        thumbnail.contentRequested();
        this.diagramManager.cancelDisplayAnimation();
        this.context = null;
    }

    public void expressionColumnChanged() {
        Coordinate model = context.getDiagramStatus().getModelCoordinate(mouseCurrent);
        DiagramObject hovered = layoutManager.getHoveredDiagramObject();
        canvas.notifyHoveredExpression(hovered, model);
        forceDraw = true; //We give priority to other listeners here
    }

    @Override
    public void interactorsCollapsed(String resource) {
        Collection<DiagramObject> diagramObjects = context.getContent().getDiagramObjects();
        context.getInteractors().resetBurstInteractors(resource, diagramObjects);
        forceDraw = true;
    }

    @Override
    public void interactorsFiltered() {
        Box visibleArea = context.getVisibleModelArea(viewportWidth, viewportHeight);
        drawInteractors(visibleArea);
    }

    @Override
    public void interactorsLayoutUpdated() {
        Box visibleArea = context.getVisibleModelArea(viewportWidth, viewportHeight);
        drawInteractors(visibleArea);
    }

    @Override
    public void interactorsLoaded() {
        forceDraw = true;
    }

    @Override
    public void interactorsResourceChanged(OverlayResource resource) {
        context.getContent().clearDisplayedInteractors();
        if (context.getInteractors().isInteractorResourceCached(resource.getIdentifier())) {
            context.getInteractors().restoreInteractorsSummary(resource.getIdentifier(), context.getContent());
        }
        forceDraw = true;
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        Scheduler.get().scheduleDeferred(this::initialise);
    }

    @Override
    public void onDiagramProfileChanged(DiagramProfileChangedEvent event) {
        thumbnail.diagramProfileChanged();
        forceDraw = true;
    }

    @Override
    public void onInteractorProfileChanged(InteractorProfileChangedEvent event) {
        forceDraw = true;
    }

    @Override
    public void onLayoutImageLoaded(StructureImageLoadedEvent event) {
        forceDraw = true;
    }

    public void setSize(int width, int height) {
        this.setWidth(width + "px");
        this.setHeight(height + "px");
        this.viewportWidth = width;
        this.viewportHeight = height;

        if (canvas != null) {
            canvas.setSize(width, height);
            forceDraw = true;

            if (this.context != null) {
                Box visibleArea = this.context.getVisibleModelArea(viewportWidth, viewportHeight);
                this.eventBus.fireEventFromSource(new ViewportResizedEvent(viewportWidth, viewportHeight, visibleArea), this);
                thumbnail.viewportResized(visibleArea);
            }
        }
    }

    @Override
    public void onThumbnailAreaMoved(ThumbnailAreaMovedEvent event) {
        this.padding(event.getCoordinate().multiply(context.getDiagramStatus().getFactor()));
    }

    private void resetDialogs() {
        if (this.context != null) {
            this.context.hideDialogs();
        }
    }

    @Override
    public boolean resetHighlight(boolean notify) {
        boolean rtn = false;
        if (context == null) return rtn;
        if (layoutManager.resetHovered()) {
            canvas.highlight(null, context);
            thumbnail.graphObjectHovered(null);
            if (notify) {
                eventBus.fireEventFromSource(new GraphObjectHoveredEvent(), this);
            }
            rtn = true;
        }
        return rtn;
    }

    @Override
    public boolean resetSelection(boolean notify) {
        boolean rtn = false;
        if (context == null) return rtn;
        if (layoutManager.resetSelected()) {
            thumbnail.graphObjectSelected(null);
            forceDraw = true;
            if (notify) {
                eventBus.fireEventFromSource(new GraphObjectSelectedEvent(null, false), this);
            }
            rtn = true;
        }
        return rtn;
    }

    @Override
    public boolean selectGraphObject(GraphObject graphObject, boolean notify) {
        if (graphObject != null) {
            return setSelection(new HoveredItem(graphObject), true, false, true);
        } else {
            return resetSelection(notify);
        }
    }

    @Override
    public void setMousePosition(Coordinate mouse) {
        mouseCurrent = mouse;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (context != null) {
            if (visible) context.restoreDialogs();
            else context.hideDialogs();
        }
    }

    @Override
    public void showDialog(DiagramObject item) {
        this.context.showDialog(this.eventBus, item, this.canvas);
    }

    @Override
    public HoveredItem getHoveredDiagramObject() {
        Coordinate model = context.getDiagramStatus().getModelCoordinate(mouseCurrent);
        Collection<HoveredItem> hoveredItems = layoutManager.getHovered(model);
        for (HoveredItem hovered : hoveredItems) {
            DiagramObject item = context.getContent().getDiagramObject(hovered.getDiagramId());
            hovered.setDiagramObject(item); //VERY IMPORTANT! Here we have access to the content and can transform diagramId to diagramObject

            //TODO: The graph has to be pruned in the server side
            if (item == null || item.getIsFadeOut() != null) continue;

            if (item.getGraphObject() instanceof GraphPhysicalEntity || item.getGraphObject() instanceof GraphEvent) {
                this.notifyHoveredExpression(item, model);
                return hovered;
            }
        }
        this.notifyHoveredExpression(null, model);
        return null;
    }

    @Override
    public DiagramInteractor getHoveredInteractor() {
        Coordinate model = context.getDiagramStatus().getModelCoordinate(mouseCurrent);
        Collection<DiagramInteractor> hoveredItems = interactorsManager.getHovered(model);
        DiagramInteractor rtn = null;
        for (DiagramInteractor item : hoveredItems) {
            if (item.isVisible()) {
                if (item instanceof InteractorEntity) { //Preference to nodes
                    return item;
                } else if (rtn == null) {
                    rtn = item; //In case there aren't nodes hovered, the "first" hovered link is returned
                }
            }
        }
        return rtn;
    }

    //Before notifying is good practise to check whether there is expression overlay or not
    private void notifyHoveredExpression(DiagramObject item, Coordinate model) {
        if (context.getAnalysisStatus() != null) {
            AnalysisType type = context.getAnalysisStatus().getAnalysisType();
            if (type == EXPRESSION || type == GSA_STATISTICS || type == GSVA || type == GSA_REGULATION) {
                //The reason why the notification is delegated to the canvas is because it keeps track of the
                //expression changes already, so this do not need to be done here.
                this.canvas.notifyHoveredExpression(item, model);
            }
        }
    }

    public void resetContext() {
        this.canvas.clear();
        this.canvas.clearThumbnail();
        this.context = null;
    }

    public void setContext(final Context context) {
        this.context = context;

        layoutManager.resetHovered();
        this.forceDraw = true;
        this.context.restoreDialogs();
    }

    @Override
    public void setSelection(boolean zoom, boolean fireExternally) {
        DiagramInteractor interactor = getHoveredInteractor();
        if (interactor != null) {
            eventBus.fireEventFromSource(new InteractorSelectedEvent(interactor.getUrl()), this);
        } else {
            setSelection(this.getHoveredDiagramObject(), zoom, fireExternally, true);
        }
    }


    private boolean setSelection(HoveredItem hoveredItem, boolean zoom, boolean fireExternally, boolean notify) {
        GraphObject toSelect = hoveredItem != null ? hoveredItem.getGraphObject() : null;
        if (toSelect != null) {
            if (hoveredItem.getAttachment() != null) {
                eventBus.fireEventFromSource(new EntityDecoratorSelectedEvent(toSelect, hoveredItem.getAttachment()), this);
            }
            if (hoveredItem.getSummaryItem() != null) {
                SummaryItem summaryItem = hoveredItem.getSummaryItem();
                if (summaryItem.getType().equals("TR")) {
                    forceDraw |= interactorsManager.update(summaryItem, (Node) hoveredItem.getHoveredObject());
                }
                eventBus.fireEventFromSource(new EntityDecoratorSelectedEvent(toSelect, hoveredItem.getSummaryItem()), this);
            }
            if (hoveredItem.getContextMenuTrigger() != null) {
                eventBus.fireEventFromSource(new EntityDecoratorSelectedEvent(toSelect, hoveredItem.getContextMenuTrigger()), this);
                DiagramObject item = layoutManager.getHoveredDiagramObject();
                context.showDialog(this.eventBus, item, this.canvas);
            }
        }
        return makeSelection(toSelect, zoom, fireExternally, true);
    }

    private boolean makeSelection(GraphObject toSelect, boolean zoom, boolean fireExternally, boolean notify) {
        boolean rtn = false;
        if (!layoutManager.isSelected(toSelect)) {
            layoutManager.setSelected(toSelect);
            rtn = true;
            if (zoom) {
                diagramManager.displayDiagramObjects(layoutManager.getHalo());
            }
            thumbnail.graphObjectSelected(toSelect);
            forceDraw = true;
            if (notify) {
                eventBus.fireEventFromSource(new GraphObjectSelectedEvent(toSelect, zoom, fireExternally), this);
            }
        }
        return rtn;
    }

    @Override
    public void fitDiagram(boolean animation) {
        diagramManager.fitDiagram(context.getContent(), animation);
    }

    private void overview() {
        fireEvent(new FireworksOpenedEvent(context.getContent().getDbId()));
    }

    @Override
    public void padding(int dX, int dY) {
        padding(CoordinateFactory.get(dX, dY));
    }

    @Override
    public void padding(Coordinate delta) {
        context.getDiagramStatus().padding(delta);
        forceDraw = true;
        Box visibleArea = context.getVisibleModelArea(viewportWidth, viewportHeight);
        thumbnail.diagramPanningEvent(visibleArea);
        eventBus.fireEventFromSource(new DiagramPanningEvent(visibleArea), this);
    }

    @Override
    public void zoomDelta(double deltaFactor) {
        zoom(context.getDiagramStatus().getFactor() + deltaFactor);
    }

    @Override
    public void zoomIn() {
        zoom(context.getDiagramStatus().getFactor() + UserActionsManager.ZOOM_DELTA);
    }

    @Override
    public void zoomOut() {
        zoom(context.getDiagramStatus().getFactor() - UserActionsManager.ZOOM_DELTA);
    }

    private void zoom(double factor) {
        Coordinate viewportCentre = CoordinateFactory.get(viewportWidth / 2, viewportHeight / 2);
        factor = ViewportUtils.checkFactor(factor);
        zoom(factor, viewportCentre);
    }

    @Override
    public void mouseZoom(double delta) {
        if (context == null) return;
        double factor = context.getDiagramStatus().getFactor();
        factor = ViewportUtils.checkFactor(factor - delta);
        zoom(factor, mouseCurrent);
    }

    @Override
    public void dragInteractor(InteractorEntity interactor, Coordinate delta) {
        delta = delta.divide(context.getDiagramStatus().getFactor());
        interactorsManager.drag(interactor, delta.getX(), delta.getY());
        Box visibleArea = context.getVisibleModelArea(viewportWidth, viewportHeight);
        drawInteractors(visibleArea);
    }

    private void zoom(double factor, Coordinate mouse) {
        DiagramStatus status = context.getDiagramStatus();
        if (factor == status.getFactor()) return;

        //current and new model positions are used to calculate the delta in order to perform
        //padding to the result of the zooming
        Coordinate current_model = status.getModelCoordinate(mouse);
        status.setFactor(factor);
        Coordinate new_model = status.getModelCoordinate(mouse);

        //the calculated delta also needs to be scaled to the factor and then applied to the status
        Coordinate delta = new_model.minus(current_model).multiply(factor);
        status.padding(delta);

        Box visibleArea = context.getVisibleModelArea(viewportWidth, viewportHeight);
        eventBus.fireEventFromSource(new DiagramZoomEvent(factor, visibleArea), this);
        thumbnail.diagramZoomEvent(visibleArea);
        forceDraw = true;  //IMPORTANT: Please leave it at the very end after the event firing
    }

    @Override
    public void refreshHoveredItem() {
        DiagramInteractor hoveredInteractor = getHoveredInteractor();
        canvas.setCursor(hoveredInteractor == null ? Style.Cursor.DEFAULT : Style.Cursor.POINTER);
        resetHighlight(true); // This resets the layout highlight -> please note that the method is defined in the DiagramViewer interface
        if (hoveredInteractor == null) { // It is needed otherwise the getHoveredDiagramObject will find a possible diagram layout object behind the interactor
            HoveredItem hovered = getHoveredDiagramObject();
            DiagramObject item = hovered != null ? hovered.getHoveredObject() : null;
            canvas.setCursor(item == null ? Style.Cursor.DEFAULT : Style.Cursor.POINTER);
            highlightHoveredItem(hovered);
        }
        highlightInteractor(hoveredInteractor); //if hovered i
    }

    public int getViewportWidth() {
        return viewportWidth;
    }

    public int getViewportHeight() {
        return viewportHeight;
    }

    @Override
    public GraphObject getSelected() {
        return layoutManager.getSelected();
    }
}
