package org.reactome.web.diagram.client.visualisers.ehld;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Timer;
import org.reactome.web.analysis.client.model.EntityStatistics;
import org.reactome.web.analysis.client.model.ExpressionSummary;
import org.reactome.web.analysis.client.model.PathwaySummary;
import org.reactome.web.diagram.client.OptionalWidget;
import org.reactome.web.diagram.client.ViewerContainer;
import org.reactome.web.diagram.client.visualisers.Visualiser;
import org.reactome.web.diagram.client.visualisers.ehld.animation.SVGAnimation;
import org.reactome.web.diagram.client.visualisers.ehld.animation.SVGAnimationHandler;
import org.reactome.web.diagram.client.visualisers.ehld.context.SVGContextPanel;
import org.reactome.web.diagram.client.visualisers.ehld.events.SVGThumbnailAreaMovedEvent;
import org.reactome.web.diagram.client.visualisers.ehld.handlers.SVGThumbnailAreaMovedHandler;
import org.reactome.web.diagram.data.AnalysisStatus;
import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.content.Content;
import org.reactome.web.diagram.data.content.EHLDContent;
import org.reactome.web.diagram.data.content.EHLDObject;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPathway;
import org.reactome.web.diagram.data.interactors.common.OverlayResource;
import org.reactome.web.diagram.data.interactors.model.DiagramInteractor;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.impl.CoordinateFactory;
import org.reactome.web.diagram.events.AnalysisProfileChangedEvent;
import org.reactome.web.diagram.events.ContentRequestedEvent;
import org.reactome.web.diagram.events.GraphObjectHoveredEvent;
import org.reactome.web.diagram.events.GraphObjectSelectedEvent;
import org.reactome.web.diagram.handlers.AnalysisProfileChangedHandler;
import org.reactome.web.diagram.profiles.analysis.AnalysisColours;
import org.reactome.web.diagram.thumbnail.NullThumbnail;
import org.reactome.web.diagram.thumbnail.Thumbnail;
import org.reactome.web.diagram.thumbnail.ehld.SVGThumbnail;
import org.reactome.web.diagram.util.Console;
import org.reactome.web.diagram.util.svg.SVGUtil;
import org.reactome.web.pwp.model.client.classes.DatabaseObject;
import org.reactome.web.pwp.model.client.classes.Pathway;
import org.reactome.web.pwp.model.client.common.ContentClientHandler;
import org.reactome.web.pwp.model.client.content.ContentClient;
import org.reactome.web.pwp.model.client.content.ContentClientError;
import org.vectomatic.dom.svg.*;
import org.vectomatic.dom.svg.utils.DOMHelper;
import org.vectomatic.dom.svg.utils.SVGConstants;
import uk.ac.ebi.pwp.structures.quadtree.client.Box;

import java.util.*;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
@SuppressWarnings("All")
public class SVGVisualiser extends AbstractSVGPanel implements Visualiser,
        AnalysisProfileChangedHandler, ContentClientHandler.ObjectLoaded<DatabaseObject>,
        MouseOverHandler, MouseOutHandler, MouseDownHandler, MouseMoveHandler, MouseUpHandler, MouseWheelHandler,
        DoubleClickHandler, ContextMenuHandler, SVGAnimationHandler, SVGThumbnailAreaMovedHandler,
        TouchStartHandler, TouchMoveHandler, TouchEndHandler, OptionalWidget.Handler {

    private static final String OVERLAY_CLONE = "OVERLAYCLONE-";
    private static final String OVERLAY_BASE = "OVERLAYBASE-";

    private static final String CLIPPING_PATH = "CLIPPINGPATH-";
    private static final String CLIPPING_RECT = "CLIPPINGRECT-";

    private static final String CURSOR = "cursor: pointer;";
    private static final float ZOOM_IN_STEP = 1.1f;
    private static final float ZOOM_OUT_STEP = 0.9f;
    private static final float MAX_ZOOM = 8.0f;
    private static final float MIN_ZOOM = 0.05f;
    private static final float FRAME = 58;

    private Context context;

    private OMSVGDefsElement defs;

    private boolean initialised;
    private int viewportWidth = 0;
    private int viewportHeight = 0;

    private boolean mouseDown = false;
    private boolean allowSelection = true;

    private OMElement selected;
    private OMElement hovered;
    private OMSVGPoint origin;
    private Double touchDistance;
    private Set<OMElement> flagged;

    private SVGAnimation animation;

    private AnalysisStatus analysisStatus;
    private ExpressionSummary expressionSummary;
    private int selectedExpCol = 0;

    private SVGContextPanel contextPanel;
    private Thumbnail thumbnail;

    private Timer tapTimer = new Timer() {
        @Override
        public void run() { /* Nothing here */ }
    };

    public SVGVisualiser(EventBus eventBus) {
        super(eventBus);
        this.getElement().addClassName("pwp-SVGPanel");
        flagged = new HashSet<>();

        initHandlers();
//        setSize(width, height);
    }

    protected void initialise() {
        if (!initialised) {
            this.initialised = true;

            contextPanel = new SVGContextPanel(eventBus);
            thumbnail = OptionalWidget.THUMBNAIL.isVisible() ? new SVGThumbnail(eventBus) : new NullThumbnail();
            this.add(thumbnail);
            this.viewportWidth = getParent().getOffsetWidth();
            this.viewportHeight = getParent().getOffsetHeight();
            setSize(viewportWidth, viewportHeight);
        }
    }

    @Override
    public void fitDiagram(boolean animation) {
        fitALL(true);
    }

    @Override
    public void zoomDelta(double deltaFactor) {
        zoom((float) deltaFactor, getCentrePoint());
    }

    @Override
    public void zoomIn() {
        zoom(ZOOM_IN_STEP, getCentrePoint());
    }

    @Override
    public void zoomOut() {
        zoom(ZOOM_OUT_STEP, getCentrePoint());
    }

    @Override
    public void padding(int dX, int dY) {
        translate(dX, dY);
    }

    @Override
    public void exportView() {
        if (context != null) {

            String sel = null;
            if (selected != null) {
                sel = selected.getId().replace("REGION-", "");
            }

            showExportDialog(context, sel, context.getFlagTerm());
        }
    }

    @Override
    public void contentLoaded(Context context) {
        setContext(context);
    }

    @Override
    public void contentRequested() {
        context = null;
        if (svg != null) {
            if (getElement().getChildCount() > 1) {
                svg.getElement().removeFromParent();
            }
            svg = null;
        }
        thumbnail.contentRequested();
    }

    @Override
    public boolean highlightGraphObject(GraphObject graphObject, boolean notify) {
        boolean rtn = false;
        if (graphObject != null) {
            SVGEntity svgEntity = entities.get(graphObject.getStId());
            if (svgEntity != null && hovered != svgEntity.getHoverableElement()) {
                resetHighlight(false);
                highlightElement(svgEntity.getHoverableElement());
                updateUI();
                thumbnail.setHoveredItem(svgEntity.getHoverableElement().getId());
                if (notify) {
                    eventBus.fireEventFromSource(new GraphObjectHoveredEvent(graphObject), this);
                }
                rtn = true;
            }
        }
        return rtn;
    }

    @Override
    public boolean resetHighlight(boolean notify) {
        boolean rtn = false;
        if (context == null) return rtn;
        if (hovered != null) {
            unHighlightElement(hovered);
            updateUI();
            thumbnail.setHoveredItem(null);
            if (notify) {
                eventBus.fireEventFromSource(new GraphObjectHoveredEvent(), this);
            }
            rtn = true;
        }
        return rtn;
    }

    @Override
    public boolean selectGraphObject(GraphObject graphObject, boolean notify) {
        boolean rtn = false;
        if (graphObject != null) {
            SVGEntity svgEntity = entities.get(graphObject.getStId());
            if (svgEntity != null && selected != svgEntity.getHoverableElement()) {
                resetSelection(false);
                setSelectedElement(svgEntity.getHoverableElement());
                thumbnail.setSelectedItem(svgEntity.getHoverableElement().getId());
                if (notify) {
                    eventBus.fireEventFromSource(new GraphObjectSelectedEvent(graphObject, false, false), this);
                }
                rtn = true;
            }
        } else {
            //In case the entity is not in the graph, any previous selection has to be reset.
            resetSelection(true);
        }
        return rtn;
    }

    @Override
    public boolean resetSelection(boolean notify) {
        boolean rtn = false;
        if (context == null) return rtn;
        if (selected != null) {
            resetSelectedElement();
            thumbnail.setSelectedItem(null);
            if (notify) {
                eventBus.fireEventFromSource(new GraphObjectSelectedEvent(null, false, false), this);
            }
            rtn = true;
        }
        return rtn;
    }

    @Override
    public void onAnalysisProfileChanged(AnalysisProfileChangedEvent event) {
        if (context != null && svg != null) {
            clearOverlay();
            overlayAnalysisResults();
        }
    }

    @Override
    public void resetAnalysis() {
        analysisStatus = null;
        expressionSummary = null;
        selectedExpCol = 0;
        if (svg != null) {
            clearOverlay();
            clearAnalysisInfo();
        }
    }

    @Override
    public void loadAnalysis() {
        analysisStatus = context.getAnalysisStatus();
        expressionSummary = analysisStatus.getExpressionSummary();
        selectedExpCol = 0;
        if (svg != null) {
            clearOverlay();
            clearAnalysisInfo();
            overlayAnalysisResults();
            showAnalysisInfo(); // Prepare and show the analysis info
        }
    }

    @Override
    public void onContextMenu(ContextMenuEvent event) {
        event.preventDefault();
        event.stopPropagation();
    }

    @Override
    public void onObjectLoaded(DatabaseObject databaseObject) {
        if (databaseObject instanceof Pathway) {
            Pathway p = (Pathway) databaseObject;
            eventBus.fireEventFromSource(new ContentRequestedEvent(p.getReactomeIdentifier()), this);
        }
    }

    @Override
    public void onContentClientException(Type type, String message) {
        Console.error("Error getting pathway information...");
        //TODO: Decide what to do in this case
    }

    @Override
    public void onContentClientError(ContentClientError error) {
        Console.error("Error getting pathway information...");
        //TODO: Decide what to do in this case
    }

    @Override
    public void onDoubleClick(DoubleClickEvent event) {
        event.preventDefault();
        event.stopPropagation();
        OMElement el = (OMElement) event.getSource();
        openPathway(el);
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        Scheduler.get().scheduleDeferred(() -> initialise());
    }

    @Override
    public void onMouseDown(MouseDownEvent event) {
        event.preventDefault();
        event.stopPropagation();
        if (animation != null) animation.cancel();
        origin = getTranslatedPoint(event);
        mouseDown = true;
    }

    @Override
    public void onMouseMove(MouseMoveEvent event) {
        event.preventDefault();
        event.stopPropagation();
        if (mouseDown) {
            getElement().getStyle().setCursor(Style.Cursor.MOVE);
            OMSVGPoint end = getTranslatedPoint(event);
            Coordinate delta = CoordinateFactory.get(end.getX() - origin.getX(), end.getY() - origin.getY());
            // On mouse move is sometimes called for delta 0 (we cannot control that, but only consider it)
            if (delta.getX() != 0 && delta.getY() != 0) {
                allowSelection = false; //Selection can only be denied in case of panning

                OMSVGMatrix newMatrix = svg.createSVGMatrix().translate(delta.getX().floatValue(), delta.getY().floatValue());
                ctm = ctm.multiply(newMatrix);

                applyCTM(true);
            }
        }
    }

    @Override
    public void onMouseUp(MouseUpEvent event) {
        event.preventDefault();
        event.stopPropagation();
        getElement().getStyle().setCursor(Style.Cursor.DEFAULT);
        OMElement el = (OMElement) event.getSource();
        if (allowSelection) {
            String elementId = el.getId();
            if (SVGUtil.isAnnotated(elementId)) {
                int button = event.getNativeEvent().getButton();
                switch (button) {
                    case NativeEvent.BUTTON_RIGHT:
                        contextPanel.show(SVGUtil.keepStableId(elementId), event.getClientX(), event.getClientY());
                        break;
                    default:
                        if (!Objects.equals(selected, el)) {
                            thumbnail.setSelectedItem(elementId);
                            setSelectedElement(el);
                            notifySelection(elementId);
                        }
                        break;
                }
            } else {
                if (selected != null) {
                    resetSelectedElement();
                    thumbnail.setSelectedItem(null);
                    notifySelection(null);
                }
            }
        }
        mouseDown = false;
        allowSelection = true;
    }

    @Override
    public void onMouseOver(MouseOverEvent event) {
        event.preventDefault();
        event.stopPropagation();
        OMElement el = (OMElement) event.getSource();
        SVGEntity entity = entities.get(SVGUtil.keepStableId(el.getId()));

        // Highlight the region which this analysis info is part of
        OMElement toHighlight = entity.getHoverableElement();
        if (!toHighlight.equals(hovered)) {
            resetHighlight(false);
            highlightElement(toHighlight);
            updateUI();
            thumbnail.setHoveredItem(toHighlight.getId());
            notifyHovering(toHighlight.getId());
        }

        // Show the tooltip
        OMElement tooltipElement = entity.getAnalysisInfo();
        if (el.equals(tooltipElement)) {
            String tooltip = entity.getTooltipText();
            if (tooltip != null) {
                SVGTooltip.get().setText(tooltip);
                SVGTooltip.get().setPositionAndShow(
                        this,
                        event.getClientX() - getAbsoluteLeft(),
                        event.getClientY() - getAbsoluteTop(),
                        15 * zFactor
                );
            }
        }
    }

    @Override
    public void onMouseOut(MouseOutEvent event) {
        OMElement el = (OMElement) event.getSource();
        SVGEntity entity = entities.get(SVGUtil.keepStableId(el.getId()));

        // Unhighlight the region which this analysis info is part of
        OMElement toUnHighlight = entity.getHoverableElement();
        if (el.equals(toUnHighlight)) {
            unHighlightElement(toUnHighlight);
            updateUI();
            thumbnail.setHoveredItem(null);
            notifyHovering(null);
        }

        // Hide the tooltip
        SVGTooltip.get().hide();
        SVGTooltip.get().setText("");
    }

    @Override
    public void onMouseWheel(MouseWheelEvent event) {
        //Continue scrolling has priority to ehld user action
        if (ViewerContainer.windowScrolling.isRunning()) return;

        event.preventDefault();
        event.stopPropagation();
        contextPanel.hide();
        float delta = event.getDeltaY() * 0.020f;
        float zoom = (1 - delta) > 0 ? (1 - delta) : 1;
        zoom(zoom, getTranslatedPoint(event));
    }

    @Override
    public void onTouchEnd(TouchEndEvent event) {
        //Continue scrolling has priority to ehld user action
        if (ViewerContainer.windowScrolling.isRunning()) return;

        event.preventDefault();
        event.stopPropagation();
        OMElement el = (OMElement) event.getSource();
        if (allowSelection) {
            String elementId = el.getId();
            boolean isAnnotated = SVGUtil.isAnnotated(elementId);
            if (!tapTimer.isRunning()) {                            // Single tap
                tapTimer.schedule(300);
                if (isAnnotated && !Objects.equals(selected, el)) {
                    thumbnail.setSelectedItem(elementId);
                    setSelectedElement(el);
                    notifySelection(elementId);
                } else if (!isAnnotated && selected != null) {
                    resetSelectedElement();
                    thumbnail.setSelectedItem(null);
                    notifySelection(null);
                }

            } else {                                               // Double tap
                tapTimer.cancel();
                if (isAnnotated) {
                    openPathway(el);
                }
            }
        }
        origin = null;
        touchDistance = null;
    }

    @Override
    public void onTouchMove(TouchMoveEvent event) {
        //Continue scrolling has priority to ehld user action
        if (ViewerContainer.windowScrolling.isRunning()) return;

        event.preventDefault();
        event.stopPropagation();
        Element viewport = this.getElement();
        int numberOfTouches = event.getTouches().length();

        if (numberOfTouches == 1) {             // Panning
            Touch touch = event.getTouches().get(0);

            OMSVGPoint end = getTranslatedPoint(
                    touch.getRelativeX(viewport),
                    touch.getRelativeY(viewport)
            );

            if (origin == null) {
                origin = end;
            } else {
                Coordinate delta = CoordinateFactory.get(end.getX() - origin.getX(), end.getY() - origin.getY());
                // On mouse move is sometimes called for delta 0 (we cannot control that, but only consider it)
                if (isDeltaValid(delta)) {
                    OMSVGMatrix newMatrix = svg.createSVGMatrix().translate(delta.getX().floatValue(), delta.getY().floatValue());
                    ctm = ctm.multiply(newMatrix);
                    applyCTM(true);
                    allowSelection = false;             // Selection is denied in case of panning
                }
            }
        } else if (numberOfTouches == 2) {              // Zooming in and out
            Touch touch1 = event.getTouches().get(0);
            Touch touch2 = event.getTouches().get(1);

            Coordinate delta = CoordinateFactory.get(
                    touch2.getRelativeX(viewport) - touch1.getRelativeX(viewport),
                    touch2.getRelativeY(viewport) - touch1.getRelativeY(viewport)
            );
            Double newDistance = Math.sqrt(delta.getX() * delta.getX() + delta.getY() * delta.getY());

            if (touchDistance != null) {
                Double factor = newDistance / touchDistance;
                zoom(
                        factor.floatValue(),
                        getTranslatedPoint(         // Middle point between the 2 fingers is the zoom focus
                                touch1.getRelativeX(viewport) + delta.getX().intValue() / 2,
                                touch1.getRelativeY(viewport) + delta.getY().intValue() / 2
                        )
                );
            }
            touchDistance = newDistance;
            allowSelection = false;                     // Selection is denied in case of zooming
        }
    }

    @Override
    public void onTouchStart(TouchStartEvent event) {
        //Continue scrolling has priority to ehld user action
        if (ViewerContainer.windowScrolling.isRunning()) return;

        event.preventDefault();
        event.stopPropagation();
        Element viewport = this.getElement();
        if (animation != null) animation.cancel(); // Cancel any animation running
        int numberOfTouches = event.getTouches().length();
        if (numberOfTouches == 1) {
            Touch touch = event.getTouches().get(0);

            this.origin = getTranslatedPoint(
                    touch.getRelativeX(viewport),
                    touch.getRelativeY(viewport)
            ); // Get the origin touch
            allowSelection = true;
        } else if (numberOfTouches == 2) {
            Touch touch1 = event.getTouches().get(0);
            Touch touch2 = event.getTouches().get(1);

            Coordinate delta = CoordinateFactory.get(
                    touch2.getRelativeX(viewport) - touch1.getRelativeX(viewport),
                    touch2.getRelativeY(viewport) - touch1.getRelativeY(viewport)
            );
            this.touchDistance = Math.sqrt(delta.getX() * delta.getX() + delta.getY() * delta.getY());
        }
    }

    @Override
    public void onSVGThumbnailAreaMoved(SVGThumbnailAreaMovedEvent event) {
        OMSVGPoint padding = event.getPadding();
        ctm = ctm.translate(padding.getX(), padding.getY());
        applyCTM(true);
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
        Scheduler.get().scheduleDeferred(() -> notifyAboutChangeInView());
    }

    public void transform(OMSVGMatrix newTM) {
        ctm = newTM;
        applyCTM(true);
    }

    private void applyCTM(boolean fireEvent) {
        if (ctm == null) return;
        sb.setLength(0);
        sb.append("matrix(").append(ctm.getA()).append(",").append(ctm.getB()).append(",").append(ctm.getC()).append(",")
                .append(ctm.getD()).append(",").append(ctm.getE()).append(",").append(ctm.getF()).append(")");
        for (OMSVGElement svgLayer : svgLayers) {
            svgLayer.setAttribute(SVGConstants.SVG_TRANSFORM_ATTRIBUTE, sb.toString());
        }
        zFactor = ctm.getA();

        // Put the status of the view in the context
        if (context != null) {
            context.getSvgStatus().setCTM(ctm);
        }

        if (fireEvent) {
            notifyAboutChangeInView();
        }
    }

    private void clearOverlay() {
        for (SVGEntity entity : entities.values()) {
            String stId = entity.getStId();
            OMElement overlay = svg.getElementById(OVERLAY_CLONE + stId);
            if (overlay != null) {
                OMNode parent = overlay.getParentNode();
                parent.getParentNode().removeChild(parent);
            }
        }
    }

    private void clearAnalysisInfo() {
        for (SVGEntity entity : entities.values()) {
            if (entity.hasAnalysisInfo()) {
                // Make analysis info invisible again
                SVGUtil.removeClassName(entity.getAnalysisInfo(), ANALYSIS_INFO_CLASS);

                // Remove garbage text in case the user decides to download the svg file
                if (entity.hasAnalysisText()) {
                    entity.getAnalysisText().getElement().setInnerText("-");
                    entity.setTooltipText(null);
                }
            }
        }
    }

    private void createOrUpdateClippingPath(String stId, float ratio) {
        OMSVGRectElement rect = (OMSVGRectElement) svg.getElementById(CLIPPING_RECT + stId);
        // Important !!! Correct ratio so that it is visible
        ratio = ratio > 0 && ratio < (float) Context.ANALYSIS_MIN_PERCENTAGE ? (float) Context.ANALYSIS_MIN_PERCENTAGE : ratio;

        if (rect == null) {
            //Clipping path is not present.
            rect = new OMSVGRectElement(0.0f, 0.0f, ratio, 1f, 0, 0);
            rect.setId(CLIPPING_RECT + stId);

            OMSVGClipPathElement cp = new OMSVGClipPathElement();
            cp.setId(CLIPPING_PATH + stId);
            cp.setAttribute(SVGConstants.SVG_CLIP_PATH_UNITS_ATTRIBUTE, SVGConstants.SVG_OBJECT_BOUNDING_BOX_VALUE);
            cp.appendChild(rect);
            defs.appendChild(cp);
        } else {
            //Clipping path exists, simply re-use it
            rect.getWidth().getBaseVal().setValue(ratio);
        }
    }

    private void createOrUpdateOverlayElement(String stId, String overlayColour, String baseColour) {
        OMSVGGElement overlay = (OMSVGGElement) svg.getElementById(OVERLAY_CLONE + stId);
        if (overlay == null) {
            SVGEntity entity = entities.get(stId); //Entity cannot be null as it has been checked in a previous step.

            // Copy and prepare the base of the overlay
            OMSVGGElement base = (OMSVGGElement) entity.getOverlay().cloneNode(true);
            base.setId(OVERLAY_BASE + stId);
            removeAttributeFromChildren(base, SVGConstants.SVG_CLASS_ATTRIBUTE);
            base.setAttribute(SVGConstants.SVG_FILL_ATTRIBUTE, baseColour);
            base.setAttribute(SVGConstants.SVG_STROKE_ATTRIBUTE, HIT_BASIS_STROKE_COLOUR);
            base.setAttribute(SVGConstants.SVG_STROKE_WIDTH_ATTRIBUTE, HIT_BASIS_STROKE_WIDTH);

            // Remove Text elements
            List<OMElement> textElements = getAllTextElementsFrom(base);
            Iterator<OMElement> it = textElements.iterator();
            while (it.hasNext()) {
                it.next().getElement().removeFromParent();
            }

            overlay = (OMSVGGElement) entity.getOverlay().cloneNode(true);
            overlay.setId(OVERLAY_CLONE + stId);
            overlay.setAttribute(SVGConstants.SVG_CLIP_PATH_ATTRIBUTE, DOMHelper.toUrl(CLIPPING_PATH + stId));
            overlay.removeAttribute(SVGConstants.SVG_FILTER_ATTRIBUTE);
            overlay.removeAttribute(SVGConstants.SVG_TRANSFORM_ATTRIBUTE);
            overlay.setAttribute(SVGConstants.SVG_FILL_ATTRIBUTE, overlayColour);

            // Create a group to put the clone and the text
            OMSVGGElement overlayGroup = new OMSVGGElement();
            overlayGroup.appendChild(base);
            overlayGroup.appendChild(overlay);

            // Make sure all text elements are put in
            // front of the overlay with the proper style
            textElements = getAllTextElementsFrom(overlay);
            it = textElements.iterator();
            while (it.hasNext()) {
                OMElement el = it.next();
                SVGUtil.addClassName(el, OVERLAY_TEXT_CLASS);
                overlayGroup.appendChild(el);
            }
            // Remove styling and add the overlay group under the OVERLAY-R-SSS-NNNNNNN
            removeAttributeFromChildren(overlay, SVGConstants.SVG_CLASS_ATTRIBUTE);
            entity.getOverlay().appendChild(overlayGroup);
        }
    }

    private void fitALL(boolean animated) {
        OMSVGMatrix fitTM = calculateFitAll(FRAME);
        if (!SVGUtil.areEqual(ctm, fitTM)) {
            if (animated) {
                animation = new SVGAnimation(this, ctm);
                animation.animate(fitTM);
            } else {
                ctm = initialTM.multiply(fitTM);
                applyCTM(true);
            }
        }
    }

    @Deprecated
    private double getRatio(PathwaySummary p) {
        EntityStatistics stats = p.getEntities();
        Integer found = stats.getFound() != null ? stats.getFound() : 0;
        Integer total = stats.getTotal() != null ? stats.getTotal() : 0;
        Double rtn = found / (double) total;
        return !rtn.isInfinite() && !rtn.isNaN() ? rtn : 0.0;
    }

    private void highlightElement(OMElement el) {
        hovered = el;
        boolean isFlagged = flagged.contains(el);
        if (el.equals(selected)) {
            if (isFlagged) {
                el.setAttribute(SVGConstants.SVG_FILTER_ATTRIBUTE, DOMHelper.toUrl(SELECTION_FLAGGING_HOVERING_FILTER));
            } else {
                el.setAttribute(SVGConstants.SVG_FILTER_ATTRIBUTE, DOMHelper.toUrl(SELECTION_HOVERING_FILTER));
            }
        } else {
            if (isFlagged) {
                el.setAttribute(SVGConstants.SVG_FILTER_ATTRIBUTE, DOMHelper.toUrl(FLAGGING_HOVERING_FILTER));
            } else {
                el.setAttribute(SVGConstants.SVG_FILTER_ATTRIBUTE, DOMHelper.toUrl(HOVERING_FILTER));
            }
        }
    }

    private void unHighlightElement(OMElement el) {
        hovered = null;
        if (!el.equals(selected)) {
            if (flagged.contains(el)) {
                el.setAttribute(SVGConstants.SVG_FILTER_ATTRIBUTE, DOMHelper.toUrl(FLAGGING_FILTER));
            } else {
                el.removeAttribute(SVGConstants.SVG_FILTER_ATTRIBUTE);
            }
        } else {
            if (flagged.contains(el)) {
                el.setAttribute(SVGConstants.SVG_FILTER_ATTRIBUTE, DOMHelper.toUrl(SELECTION_FLAGGING_FILTER));
            } else {
                el.setAttribute(SVGConstants.SVG_FILTER_ATTRIBUTE, DOMHelper.toUrl(SELECTION_FILTER));
            }
        }
    }

    private void initHandlers() {
        eventBus.addHandler(SVGThumbnailAreaMovedEvent.TYPE, this);
        eventBus.addHandler(AnalysisProfileChangedEvent.TYPE, this);

        // !!! Important !!! //
        // Adding the MouseWheelEvent directly on the SVG is not working
        // on certain browsers. This is why we are adding the event handling
        // on the wrapping div.
        addDomHandler(this, MouseWheelEvent.getType());
        addDomHandler(SVGVisualiser.this, ContextMenuEvent.getType());
    }

    private void notifyAboutChangeInView() {
        if (svg != null && ctm != null) {
            OMSVGPoint from = svg.createSVGPoint(0, 0);
            from = from.matrixTransform(ctm.inverse());

            OMSVGPoint to = svg.createSVGPoint(getOffsetWidth(), getOffsetHeight());
            to = to.matrixTransform(ctm.inverse());

            thumbnail.diagramZoomEvent(new Box(from.getX(), from.getY(), to.getX(), to.getY()));
        }
    }

    private void notifySelection(String elementId) {
        GraphObject selected = null;
        if (elementId != null) {
            selected = context.getContent().getDatabaseObject(SVGUtil.keepStableId(elementId));
        }
        eventBus.fireEventFromSource(new GraphObjectSelectedEvent(selected, false), this);
    }

    private void notifyHovering(String elementId) {
        GraphObject hovered = null;
        if (elementId != null) {
            hovered = context.getContent().getDatabaseObject(SVGUtil.keepStableId(elementId));
        }
        eventBus.fireEventFromSource(new GraphObjectHoveredEvent(hovered), this);
    }

    private void overlayAnalysisResults() {
        if (analysisStatus != null) {
            for (GraphPathway graphPathway : context.getContent().getEncapsulatedPathways()) {
                overlayEntity(graphPathway);
            }
        }
    }

    private void overlayEntity(GraphPathway graphPathway) {
        SVGEntity entity = entities.get(graphPathway.getStId());
        if (entity != null) {
            OMElement el = entity.getOverlay();
            if (el != null) {
                float percentage;
                switch (analysisStatus.getAnalysisType()) {
                    case SPECIES_COMPARISON:
                    case OVERREPRESENTATION:
                        percentage = graphPathway.isHit() ? graphPathway.getPercentage().floatValue() : 0;

                        String enrichColour = "#C2C2C2";
                        if (graphPathway.isHit()) {
                            Double pValue = graphPathway.getStatistics().getpValue();
                            if (pValue > 0 && pValue < AnalysisColours.THRESHOLD) {
                                enrichColour = AnalysisColours.get().enrichmentGradient.getColor(pValue / AnalysisColours.THRESHOLD);
                            }
                        }

                        overlayEntity(graphPathway.getStId(), percentage, hex2Rgb(enrichColour, 0.9f), HIT_BASIS_COLOUR);
                        break;
                    case EXPRESSION:
                    case GSVA:
                    case GSA_STATISTICS:
                        percentage = graphPathway.isHit() ? graphPathway.getPercentage().floatValue() : 0;

                        String expressionColour = "#C2C2C2";
                        if (graphPathway.isHit()) {
                            Double pValue = graphPathway.getStatistics().getpValue();
                            if (pValue > 0 && pValue < AnalysisColours.THRESHOLD) {
                                expressionColour = AnalysisColours.get().expressionGradient.getColor(
                                        graphPathway.getExpression(selectedExpCol).floatValue(),
                                        expressionSummary.getMin(),
                                        expressionSummary.getMax()
                                );
                            }
                        }

                        overlayEntity(graphPathway.getStId(), percentage, hex2Rgb(expressionColour, 0.9f), HIT_BASIS_COLOUR);
                        break;
                    case GSA_REGULATION:
                        percentage = graphPathway.isHit() ? graphPathway.getPercentage().floatValue() : 0;

                        expressionColour = "#C2C2C2";
                        if (graphPathway.isHit()) {
                            Double pValue = graphPathway.getStatistics().getpValue();
                            if (pValue > 0 && pValue < AnalysisColours.THRESHOLD) {
                                expressionColour = AnalysisColours.get().expressionGradient.getColor(
                                        graphPathway.getExpression(selectedExpCol).floatValue(),
                                        expressionSummary.getMin(),
                                        expressionSummary.getMax()
                                );
                            }
                        }

                        overlayEntity(graphPathway.getStId(), percentage, hex2Rgb(expressionColour, 0.9f), HIT_BASIS_COLOUR);
                        break;
                }
            }
        }
    }

    private void overlayEntity(String stId, float percentage, String overlayColour, String baseColour) {
        createOrUpdateClippingPath(stId, percentage);
        createOrUpdateOverlayElement(stId, overlayColour, baseColour);
    }

    private void openPathway(OMElement el) {
        String stableId = SVGUtil.keepStableId(el.getAttribute("id"));
        if (stableId != null) {
            ContentClient.query(stableId, this);
        }
    }

    private void showAnalysisInfo() {
        if (analysisStatus != null) {
            for (GraphPathway graphPathway : context.getContent().getEncapsulatedPathways()) {
                showAnalysisInfoItem(graphPathway);
            }
        }
    }

    private void showAnalysisInfoItem(GraphPathway graphPathway) {
        if (!graphPathway.isHit()) return;

        SVGEntity entity = entities.get(graphPathway.getStId());
        if (entity != null && entity.hasAnalysisInfo()) {
            // Make the info box visible
            SVGUtil.addClassName(entity.getAnalysisInfo(), ANALYSIS_INFO_CLASS);

            if (entity.hasAnalysisText()) {
                OMElement info = entity.getAnalysisText();

                // Center text
                OMSVGGElement analysisInfo = (OMSVGGElement) entity.getAnalysisInfo();

                OMSVGPoint center = svg.createSVGPoint(
                        analysisInfo.getBBox().getCenterX(),
                        analysisInfo.getBBox().getCenterY()
                );

                center = center.matrixTransform(getInitialCTM());

                info.removeAttribute(SVGConstants.SVG_TRANSFORM_ATTRIBUTE);
                info.setAttribute(SVGConstants.CSS_ALIGNMENT_BASELINE_PROPERTY, "middle");
                info.setAttribute(SVGConstants.CSS_DOMINANT_BASELINE_PROPERTY, "middle");   // Especially for Firefox
                info.setAttribute(SVGConstants.SVG_TEXT_ANCHOR_ATTRIBUTE, "middle");
                info.setAttribute(SVGConstants.SVG_X_ATTRIBUTE, center.getX() + "");
                info.setAttribute(SVGConstants.SVG_Y_ATTRIBUTE, center.getY() + "");

                EntityStatistics stats = graphPathway.getStatistics();
                String msg = "Hit: " + stats.getFound() + "/" + stats.getTotal() + " - FDR: " + NumberFormat.getFormat("#.##E0").format(stats.getFdr());
                info.getElement().setInnerText(msg);

                if (stats.getCuratedTotal() == null) {
                    entity.setTooltipText(null);
                } else {
                    entity.setTooltipText("Hit: Curated(" + stats.getCuratedFound() + "/" + stats.getCuratedTotal()
                            + ") Interactors(" + stats.getInteractorsFound() + "/" + stats.getInteractorsTotal()
                            + ") - FDR: " + NumberFormat.getFormat("#.##E0").format(stats.getFdr())
                    );
                }
            }
        }
    }

    private void updateUI() {
        if (isSafari) {
            forceRepaint();         //Force reflow and repaint in Safari
        } else {
            applyCTM(false); //Normal refresh
        }
    }

    private void forceRepaint() {
        final Style style = getElement().getStyle();
        style.clearDisplay();
        Scheduler.get().scheduleDeferred(() -> style.setDisplay(Style.Display.INLINE_BLOCK));
    }

    private void resetSelectedElement() {
        if (selected != null) {
            boolean isFlagged = flagged.contains(selected);
            if (selected.equals(hovered)) {
                if (isFlagged) {
                    selected.setAttribute(SVGConstants.SVG_FILTER_ATTRIBUTE, DOMHelper.toUrl(FLAGGING_HOVERING_FILTER));
                } else {
                    selected.setAttribute(SVGConstants.SVG_FILTER_ATTRIBUTE, DOMHelper.toUrl(HOVERING_FILTER));
                }
            } else {
                if (isFlagged) {
                    selected.setAttribute(SVGConstants.SVG_FILTER_ATTRIBUTE, DOMHelper.toUrl(FLAGGING_FILTER));
                } else {
                    selected.removeAttribute(SVGConstants.SVG_FILTER_ATTRIBUTE);
                }
            }
            selected = null;
            updateUI();
        }
    }

    private void setSelectedElement(OMElement element) {
        if (selected != null && !selected.equals(element)) {
            resetSelectedElement();
        }

        boolean isFlagged = flagged.contains(element);
        if (element.equals(hovered)) {
            if (isFlagged) {
                element.setAttribute(SVGConstants.SVG_FILTER_ATTRIBUTE, DOMHelper.toUrl(SELECTION_FLAGGING_HOVERING_FILTER));
            } else {
                element.setAttribute(SVGConstants.SVG_FILTER_ATTRIBUTE, DOMHelper.toUrl(SELECTION_HOVERING_FILTER));
            }
        } else {
            if (isFlagged) {
                element.setAttribute(SVGConstants.SVG_FILTER_ATTRIBUTE, DOMHelper.toUrl(SELECTION_FLAGGING_FILTER));
            } else {
                element.setAttribute(SVGConstants.SVG_FILTER_ATTRIBUTE, DOMHelper.toUrl(SELECTION_FILTER));
            }
        }

        selected = element;
        updateUI();
    }

    private void translate(float x, float y) {
        OMSVGMatrix newMatrix = svg.createSVGMatrix().translate(x, y);
        ctm = ctm.multiply(newMatrix);
        applyCTM(true);
    }

    private void zoom(float zoom, OMSVGPoint c) {
        if (zoom != 1 && (zFactor * zoom <= MAX_ZOOM) && (zFactor * zoom >= MIN_ZOOM)) {
            OMSVGMatrix newMatrix = svg.createSVGMatrix().translate(c.getX(), c.getY()).scale(zoom).translate(-c.getX(), -c.getY());
            ctm = ctm.multiply(newMatrix);
            applyCTM(true);
        }
    }

    @Override
    public void highlightInteractor(DiagramInteractor diagramInteractor) {
        //Nothing here
    }

    @Override
    public GraphObject getSelected() {
        GraphObject rtn = null;
        if (context != null && selected != null) {
            rtn = context.getContent().getDatabaseObject(SVGUtil.keepStableId(selected.getId()));
        }
        return rtn;
    }

    @Override
    public void setContext(Context context) {
        this.context = context;
        Content content = context.getContent();
        this.svg = (OMSVGSVGElement) ((EHLDContent) content).getSVG().cloneNode(true);

        entities = new HashMap<>();
        for (OMElement child : SVGUtil.getAnnotatedOMElements(svg)) {
            addOrUpdateSVGEntity(child);
        }

        // Some browsers fail to redraw after the filter attribute has changed.
        // To avoid this known bug, we move all active regions and their siblings to the root.
        // This forces the application of the transformation matrix on these layers and thus their redraw.
        if (!entities.isEmpty()) {
            Map.Entry<String, SVGEntity> entry = entities.entrySet().iterator().next();
            OMElement region = entry.getValue().getHoverableElement();

            OMNode parent = region.getParentNode();
            OMNodeList<OMNode> list = parent.getChildNodes();

            //aux contains a static list of elements to be moved
            final OMNode[] aux = new OMNode[list.getLength()];
            for (int i = 0; i < list.getLength(); i++) {
                aux[i] = list.getItem(i);
            }

            //swap node from its current location to the svg root
            for (OMNode node : aux) {
                parent.removeChild(node);
                svg.appendChild(node);
            }
        }

        // Remove title to avoid tooltips appearing in Safari
        removeTitleFrom(svg);

        for (SVGEntity svgEntity : entities.values()) {
            OMElement child = svgEntity.getHoverableElement();
            if (child != null) {
                child.addDomHandler(SVGVisualiser.this, MouseUpEvent.getType());
                child.addDomHandler(SVGVisualiser.this, TouchEndEvent.getType());
                child.addDomHandler(SVGVisualiser.this, MouseOverEvent.getType());
                child.addDomHandler(SVGVisualiser.this, MouseOutEvent.getType());
                child.addDomHandler(SVGVisualiser.this, DoubleClickEvent.getType());
                // Set the pointer to the active regions
                child.setAttribute("style", CURSOR);

                if (svgEntity.hasAnalysisInfo()) {
                    // This is to show/hide the tooltip
                    svgEntity.getAnalysisInfo().addDomHandler(SVGVisualiser.this, MouseOverEvent.getType());
                    svgEntity.getAnalysisInfo().addDomHandler(SVGVisualiser.this, MouseOutEvent.getType());
                }
            }
        }

        // Identify all layers by getting all top-level g elements
        svgLayers = getRootLayers(svg);

        // Clone and attach defs (filters - clipping paths) to the root SVG structure
        defs = (OMSVGDefsElement) SVGUtil.getOrCreateDefs(svg, baseDefs);

        // Add the inline CSS classes for overlaying analysis results
        SVGUtil.addInlineWithChildrenStyle(svg, OVERLAY_TEXT_CLASS, OVERLAY_TEXT_STYLE);
        SVGUtil.addInlineStyle(svg, ANALYSIS_INFO_CLASS, ANALYSIS_INFO_STYLE);

        // Add the event handlers
        svg.addMouseDownHandler(this);
        svg.addMouseMoveHandler(this);
        svg.addMouseUpHandler(this);

        svg.addTouchStartHandler(this);
        svg.addTouchMoveHandler(this);
        svg.addTouchEndHandler(this);

        //Get viewport
        OMSVGRect viewportBB = svg.createSVGRect();
        svg.getViewBox().getBaseVal().assignTo(viewportBB);

        // Remove viewbox and set size
        svg.removeAttribute(SVGConstants.SVG_VIEW_BOX_ATTRIBUTE);
        setSize(getOffsetWidth(), getOffsetHeight());

        Element div = SVGVisualiser.this.getElement();
        if (div.getChildCount() > 1) {
            div.replaceChild(svg.getElement(), div.getLastChild());
        } else {
            div.appendChild(svg.getElement());
        }

        // Set initial translation matrix
        initialTM = getInitialCTM();
        initialBB = viewportBB;

        if (context.getSvgStatus().getCTM() == null) {
            ctm = initialTM;
            fitALL(false);
        } else {
            ctm = context.getSvgStatus().getCTM();
            applyCTM(false);
        }

        // The following is to avoid the bug (Windows 10) where the SVG appears cropped
        if (svg != null) {
            svg.setWidth(Style.Unit.PX, getOffsetWidth());
            svg.setHeight(Style.Unit.PX, getOffsetHeight());
        }

        // Render thumbnail
        thumbnail.diagramRendered(content, null);

        // The following is to avoid the bug (Windows) where the SVG appears cropped
        svg.removeAttribute(SVGConstants.SVG_VIEW_BOX_ATTRIBUTE);
    }

    @Override
    public void resetContext() {
        this.context = null;
    }

    @Override
    public void expressionColumnChanged() {
        if (context != null) {
            AnalysisStatus analysisStatus = context.getAnalysisStatus();
            if (analysisStatus != null) {
                selectedExpCol = context.getAnalysisStatus().getColumn();
                if (svg != null) {
                    clearOverlay();
                    overlayAnalysisResults();
                    showAnalysisInfo();
                }
            }
        }
    }

    @Override
    public void interactorsCollapsed(String resource) {
        //Nothing here
    }

    @Override
    public void interactorsFiltered() {
        //Nothing here
    }

    @Override
    public void interactorsLayoutUpdated() {
        //Nothing here
    }

    @Override
    public void interactorsLoaded() {
        //Nothing here
    }

    @Override
    public void interactorsResourceChanged(OverlayResource resource) {
        //Nothing here
    }

    private void flagElement(OMElement element) {
        if (element != null) {
            if (element.equals(selected)) {
                if (element.equals(hovered)) {
                    element.setAttribute(SVGConstants.SVG_FILTER_ATTRIBUTE, DOMHelper.toUrl(SELECTION_FLAGGING_HOVERING_FILTER));
                } else {
                    element.setAttribute(SVGConstants.SVG_FILTER_ATTRIBUTE, DOMHelper.toUrl(SELECTION_FLAGGING_FILTER));
                }
            } else {
                if (element.equals(hovered)) {
                    element.setAttribute(SVGConstants.SVG_FILTER_ATTRIBUTE, DOMHelper.toUrl(FLAGGING_HOVERING_FILTER));
                } else {
                    element.setAttribute(SVGConstants.SVG_FILTER_ATTRIBUTE, DOMHelper.toUrl(FLAGGING_FILTER));
                }
            }
        }
    }

    private void unFlagElement(OMElement element) {
        if (element != null) {
            if (element.equals(selected)) {
                if (element.equals(hovered)) {
                    element.setAttribute(SVGConstants.SVG_FILTER_ATTRIBUTE, DOMHelper.toUrl(SELECTION_HOVERING_FILTER));
                } else {
                    element.setAttribute(SVGConstants.SVG_FILTER_ATTRIBUTE, DOMHelper.toUrl(SELECTION_FILTER));
                }
            } else {
                if (element.equals(hovered)) {
                    element.setAttribute(SVGConstants.SVG_FILTER_ATTRIBUTE, DOMHelper.toUrl(HOVERING_FILTER));
                } else {
                    element.removeAttribute(SVGConstants.SVG_FILTER_ATTRIBUTE);
                }
            }
        }
    }

    @Override
    public void flagItems(Set<DiagramObject> flaggedItems, Boolean includeInteractors) {
        resetFlag();
        this.includeInteractors = includeInteractors;
        for (DiagramObject diagramObject : flaggedItems) {
            if (diagramObject instanceof EHLDObject) {
                EHLDObject item = (EHLDObject) diagramObject;
                SVGEntity svg = entities.get(item.getStableId());
                if (svg != null) {
                    flagged.add(svg.getHoverableElement());
                    flagElement(svg.getHoverableElement());
                }
            }
        }
        updateUI();
    }

    @Override
    public void resetFlag() {
        for (OMElement item : flagged) {
            unFlagElement(item);
        }
        flagged = new HashSet<>();
        updateUI();
    }

    public SVGThumbnail getSVGThumbnail() {
        return (SVGThumbnail) thumbnail;
    }

    private boolean isDeltaValid(Coordinate delta) {
        return delta.getX() >= 2 || delta.getX() <= -2 || delta.getY() >= 2 || delta.getY() <= -2;
    }
}
