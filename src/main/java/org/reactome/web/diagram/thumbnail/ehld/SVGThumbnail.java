package org.reactome.web.diagram.thumbnail.ehld;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.EventBus;
import org.reactome.web.diagram.client.visualisers.ehld.AbstractSVGPanel;
import org.reactome.web.diagram.client.visualisers.ehld.SVGEntity;
import org.reactome.web.diagram.client.visualisers.ehld.events.SVGThumbnailAreaMovedEvent;
import org.reactome.web.diagram.data.content.Content;
import org.reactome.web.diagram.data.content.EHLDContent;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.thumbnail.Thumbnail;
import org.reactome.web.diagram.util.position.MousePosition;
import org.reactome.web.diagram.util.svg.SVGUtil;
import org.vectomatic.dom.svg.*;
import org.vectomatic.dom.svg.utils.DOMHelper;
import org.vectomatic.dom.svg.utils.SVGConstants;
import uk.ac.ebi.pwp.structures.quadtree.client.Box;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
@SuppressWarnings("all")
public class SVGThumbnail extends AbstractSVGPanel implements Thumbnail, ContextMenuHandler,
        MouseDownHandler, MouseMoveHandler, MouseUpHandler, MouseOutHandler, MouseWheelHandler {
    private static final int HEIGHT = 75;
    private static final int FALLBACK_WIDTH = 100;
    private static final int FRAME = 14;

    private Canvas frame;
    private OMSVGPoint from;
    private OMSVGPoint to;

    private OMSVGPoint mouseDown;
    private OMSVGPoint delta;

    private OMElement selected;
    private OMElement hovered;

    public SVGThumbnail(EventBus eventBus) {
        super(eventBus);
        this.getElement().addClassName("pwp-SVGThumbnail");

        this.frame = this.createCanvas(0, 0);

        this.setStyle();
        this.initListeners();
    }

    @Override
    public void diagramProfileChanged() {
        // Nothing here
    }

    @Override
    public void contentRequested() {
        clearThumbnail();
    }

    @Override
    public void viewportResized(Box visibleArea) {
        //TODO Implement this
    }

    @Override
    public void diagramRendered(Content content, Box visibleArea) {
        EHLDContent ehldContent = (EHLDContent) content;
        svg = (OMSVGSVGElement) ehldContent.getSVG().cloneNode(true);

        // Remove all text elements from thumbnail
        List<OMElement> textElements = getAllTextElementsFrom(svg);
        for (OMElement textElement : textElements) {
            textElement.getElement().removeFromParent();
        }

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

        // Remove the reactome logo from the thumbnail
        removeLogoFrom(svg);

        from = svg.createSVGPoint();
        to = svg.createSVGPoint();

        OMSVGRect svgSize = getSVGInitialSize();
        double factor = HEIGHT / (svgSize.getHeight() + FRAME);
        int width = (int) Math.ceil(factor * (svgSize.getWidth() + FRAME));
        setSize(width, HEIGHT);

        //Get viewport
        OMSVGRect viewportBB = svg.createSVGRect();
        svg.getViewBox().getBaseVal().assignTo(viewportBB);

        svg.removeAttribute(SVGConstants.SVG_VIEW_BOX_ATTRIBUTE);
        svg.removeAttribute(SVGConstants.SVG_ENABLE_BACKGROUND_ATTRIBUTE);

        Element div = getElement();
        if(div.getChildCount() == 1) {
            // Only the canvas is added
            div.insertFirst(svg.getElement());
        } else {
            // both the canvas and the svg have been added
            div.replaceChild(svg.getElement(), div.getFirstChild());
        }

        this.setVisible(true);

        // Identify all layers by getting all top-level g elements
        svgLayers = getRootLayers(svg);

        // Append the filters
        SVGUtil.getOrCreateDefs(svg, baseDefs);

        // The following is to avoid the bug (Windows 10) where the SVG appears cropped
        if(svg != null) {
            svg.setWidth(Style.Unit.PX, getOffsetWidth());
            svg.setHeight(Style.Unit.PX, getOffsetHeight());
        }

        // Set initial translation matrix
        initialTM = getInitialCTM();
        ctm = initialTM;
        initialBB = viewportBB;
        OMSVGMatrix fitTM = calculateFitAll(FRAME, width, HEIGHT);
        ctm = initialTM.multiply(fitTM);
        applyCTM();

        // The following is to avoid the bug (Windows 10) where the SVG appears cropped
        Scheduler.get().scheduleDeferred(() -> svg.removeAttribute(SVGConstants.SVG_VIEW_BOX_ATTRIBUTE));
    }

    @Override
    public void onContextMenu(ContextMenuEvent event) {
            event.preventDefault();
            event.stopPropagation();
    }

    @Override
    public void onMouseDown(MouseDownEvent event) {
        event.stopPropagation(); event.preventDefault();
        OMSVGPoint p = svg.createSVGPoint(MousePosition.getX(event), MousePosition.getY(event));
        if (isMouseInVisibleArea(p)) {
            mouseDown = svg.createSVGPoint(p);
            delta = svg.createSVGPoint(mouseDown.substract(from));
        }
    }

    @Override
    public void onMouseMove(MouseMoveEvent event) {
        event.stopPropagation(); event.preventDefault();
        OMSVGPoint p = svg.createSVGPoint(MousePosition.getX(event), MousePosition.getY(event));
        if (mouseDown != null) {
            if (from != null && to != null) {
                //Do not change any property of the status since it will be updated once the corresponding
                //action is performed in the main view and notified (thumbnail status changes on demand)
                OMSVGPoint padding = svg.createSVGPoint(from.substract(p.substract(delta)).scale(0.4f/ctm.getA()));
                //This solution is not as accurate as scaling with 1/ctm.getA() but it reduces the shaky effect
                eventBus.fireEventFromSource(new SVGThumbnailAreaMovedEvent(padding), this);
            }
        } else {
            if (isMouseInVisibleArea(p)) {
                getElement().getStyle().setCursor(Style.Cursor.MOVE);
            } else {
                getElement().getStyle().setCursor(Style.Cursor.DEFAULT);
            }
        }
    }

    @Override
    public void onMouseUp(MouseUpEvent event) {
        event.stopPropagation(); event.preventDefault();
        this.mouseDown = null;
    }

    @Override
    public void onMouseOut(MouseOutEvent event) {
        event.stopPropagation(); event.preventDefault();
        this.mouseDown = null;
    }

    @Override
    public void graphObjectHovered(GraphObject hovered) {
//        String id = hovered != null ? "REGION_" + hovered.getStId() : null;
//        setHovered(id);
    }

    @Override
    public void setHoveredItem(String id) {
        setHovered(id);
    }


    @Override
    public void graphObjectSelected(GraphObject selected) {
//        String id = selected != null ? "REGION_" + selected.getStId() : null;
//        setSelected(id);
    }

    @Override
    public void setSelectedItem(String id) {
        setSelected(id);
    }

    @Override
    public void diagramPanningEvent(Box visibleArea) {
        updateFrame(visibleArea);
    }

    @Override
    public void diagramZoomEvent(Box visibleArea) {
        updateFrame(visibleArea);
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);

        frame.setCoordinateSpaceWidth(width);
        frame.setCoordinateSpaceHeight(height);
        frame.setPixelSize(width, height);

        Context2d ctx = this.frame.getContext2d();
        ctx.setStrokeStyle("#000000");
        ctx.setFillStyle("rgba(200,200,200, 0.4)");
        ctx.setLineWidth(0.5);
    }

    private void applyCTM() {
        if (ctm == null) return;
        sb.setLength(0);
        sb.append("matrix(").append(ctm.getA()).append(",").append(ctm.getB()).append(",").append(ctm.getC()).append(",")
                .append(ctm.getD()).append(",").append(ctm.getE()).append(",").append(ctm.getF()).append(")");
        for (OMSVGElement svgLayer : svgLayers) {
            svgLayer.setAttribute(SVGConstants.SVG_TRANSFORM_ATTRIBUTE, sb.toString());
        }
        zFactor = ctm.getA();
    }


    private void cleanFrame() {
        frame.getContext2d().clearRect(0, 0, frame.getOffsetWidth(), frame.getOffsetHeight());
    }

    private void clearThumbnail() {
        if(svg != null) {
            getElement().getFirstChild().removeFromParent();
            svg = null;
            cleanFrame();
        }
        this.setVisible(false);
    }

    private Canvas createCanvas(int width, int height) {
        Canvas canvas = Canvas.createIfSupported();
        canvas.setCoordinateSpaceWidth(width);
        canvas.setCoordinateSpaceHeight(height);
        canvas.setPixelSize(width, height);
        this.add(canvas, 0, 0);
        return canvas;
    }

    private void drawFrame(OMSVGPoint from, OMSVGPoint to) {
        float tX = from.getX();
        float tY = from.getY();
        float width = to.getX() - tX;
        float height = to.getY() - tY;

        cleanFrame();

        Context2d ctx = this.frame.getContext2d();
        ctx.fillRect(0, 0, this.frame.getOffsetWidth(), this.frame.getOffsetHeight());
        ctx.clearRect(tX, tY, width, height);
        ctx.strokeRect(tX, tY, width, height);
    }

    private OMSVGRect getSVGInitialSize() {
        return svg.getViewBox().getBaseVal()!=null ? svg.getViewBox().getBaseVal() : svg.createSVGRect(0, 0, FALLBACK_WIDTH, HEIGHT);
    }

    private void initListeners() {
        frame.addMouseDownHandler(this);
        frame.addMouseMoveHandler(this);
        frame.addMouseUpHandler(this);
        frame.addMouseOutHandler(this);
        addDomHandler(this, ContextMenuEvent.getType());
        addDomHandler(this, MouseWheelEvent.getType());
    }

    private boolean isMouseInVisibleArea(OMSVGPoint mouse) {
        return mouse.getX() >= this.from.getX()
                && mouse.getY() >= this.from.getY()
                && mouse.getX() <= this.to.getX()
                && mouse.getY() <= this.to.getY();
    }

    private void setHovered(String elementId) {
        if(hovered != null && hovered != selected) {
            hovered.removeAttribute(SVGConstants.SVG_FILTER_ATTRIBUTE);
        }

        if(elementId != null) {
            OMElement newHovered = svg.getElementById(elementId);
            if (newHovered != null && newHovered != selected) {
                newHovered.setAttribute(SVGConstants.SVG_FILTER_ATTRIBUTE, DOMHelper.toUrl(HOVERING_OVERLAY_FILTER));
                hovered = newHovered;
            }
        }
        applyCTM();
    }

    private void setSelected(String elementId) {
        if(selected != null) {
            selected.removeAttribute(SVGConstants.SVG_FILTER_ATTRIBUTE);
            selected = null;
        }

        if(elementId != null) {
            OMElement newSelected = svg.getElementById(elementId);
            newSelected.setAttribute(SVGConstants.SVG_FILTER_ATTRIBUTE, DOMHelper.toUrl(SELECTION_OVERLAY_FILTER));
            selected = newSelected;
        }
        applyCTM();
    }

    @SuppressWarnings("Duplicates")
    private void setStyle() {
        Style style = this.getElement().getStyle();
        style.setBackgroundColor("white");
        style.setBorderStyle(Style.BorderStyle.SOLID);
        style.setBorderWidth(0.5, Style.Unit.PX);
        style.setBorderColor("grey");
        style.setPosition(Style.Position.ABSOLUTE);
        style.setBottom(0, Style.Unit.PX);
    }

    private void updateFrame(Box visibleArea) {
        if(svg!=null) {
            from = svg.createSVGPoint((float) visibleArea.getMinX(), (float) visibleArea.getMinY());
            to = svg.createSVGPoint((float) visibleArea.getMaxX(), (float) visibleArea.getMaxY());

            from = from.matrixTransform(ctm);
            to = to.matrixTransform(ctm);

            drawFrame(from, to);
        }
    }

    @Override
    public void onMouseWheel(MouseWheelEvent event) {
        event.preventDefault();
        event.stopPropagation();
    }
}
