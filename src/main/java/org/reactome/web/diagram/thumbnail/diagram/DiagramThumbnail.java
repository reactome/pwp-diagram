package org.reactome.web.diagram.thumbnail.diagram;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AbsolutePanel;
import org.reactome.web.diagram.client.DiagramFactory;
import org.reactome.web.diagram.data.content.Content;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.layout.Compartment;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.impl.CoordinateFactory;
import org.reactome.web.diagram.events.ThumbnailAreaMovedEvent;
import org.reactome.web.diagram.profiles.diagram.DiagramColours;
import org.reactome.web.diagram.thumbnail.Thumbnail;
import org.reactome.web.diagram.thumbnail.diagram.render.ThumbnailRenderer;
import org.reactome.web.diagram.util.AdvancedContext2d;
import org.reactome.web.diagram.util.position.MousePosition;
import uk.ac.ebi.pwp.structures.quadtree.client.Box;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class DiagramThumbnail extends AbsolutePanel implements Thumbnail,
        MouseDownHandler, MouseMoveHandler, MouseUpHandler, MouseOutHandler {

    private static final int FRAME = 26;
    private static final int HEIGHT = 75;
    private static final double MIN_LINE_WIDTH = 0.15;
    private static final int MAX_WIDTH_THRESHOLD = 160;

    private final EventBus eventBus;

    private Content content;
    private Coordinate offset;
    private double factor;
    private Coordinate from;
    private Coordinate to;
    private Coordinate mouseDown = null;

    private Coordinate delta = null;
    private Canvas compartments;
    private Canvas items;
    private Canvas highlight;
    private Canvas selection;
    private Canvas frame;

    private List<Canvas> canvases = new LinkedList<>();

    private int width;
    private int height;

    public DiagramThumbnail(EventBus eventBus) {
        this.getElement().addClassName("pwp-DiagramThumbnail");
        this.eventBus = eventBus;

        this.compartments = this.createCanvas(0, 0);
        this.items = this.createCanvas(0, 0);
        this.highlight = this.createCanvas(0, 0);
        this.selection = this.createCanvas(0, 0);
        this.frame = this.createCanvas(0, 0);

        this.initListeners();
        this.setStyle();
    }

    private void initListeners() {
        this.frame.addMouseDownHandler(this);
        this.frame.addMouseMoveHandler(this);
        this.frame.addMouseUpHandler(this);
        this.frame.addMouseOutHandler(this);
    }

    @Override
    public void graphObjectSelected(GraphObject graphObject) {
        if (content == null) return;
        this.cleanCanvas(this.selection);
        if (graphObject != null) {
            for (DiagramObject selected : graphObject.getDiagramObjects()) {
                this.select(selected);
            }
        }
    }

    @Override
    public void setSelectedItem(String id) {
        if (content == null) return;
        this.cleanCanvas(this.selection);
        if (id != null) {
            GraphObject graphObject = content.getDatabaseObject(id);
            if(graphObject != null) {
                for (DiagramObject selected : graphObject.getDiagramObjects()) {
                    this.select(selected);
                }
            }
        }
    }

    @Override
    public void graphObjectHovered(GraphObject hovered) {
        if (content == null)  return;
        this.cleanCanvas(this.highlight);
        List<DiagramObject> toHover = hovered != null ? hovered.getDiagramObjects() : new LinkedList<>();
        for (DiagramObject item : toHover) {
            this.highlight(item);
        }
    }

    @Override
    public void setHoveredItem(String id) {
        if (content == null)  return;
        this.cleanCanvas(this.highlight);
        if (id != null) {
            GraphObject hovered = content.getDatabaseObject(id);
            if (hovered != null) {
                List<DiagramObject> toHover = hovered != null ? hovered.getDiagramObjects() : new LinkedList<>();
                for (DiagramObject item : toHover) {
                    this.highlight(item);
                }
            }
        }
    }

    @Override
    public void diagramProfileChanged() {
        if (content == null) return;
        this.setCanvasProperties();
        this.drawThumbnail();
    }

    @Override
    public void diagramRendered(Content content, Box visibleArea) {
        this.setContent(content, visibleArea);
    }

    @Override
    public void contentRequested() {
        this.content = null;
        this.clearThumbnail();
    }

    @Override
    public void diagramPanningEvent(Box visibleArea) {
        this.setVisibleArea(visibleArea);
    }

    @Override
    public void diagramZoomEvent(Box visibleArea) {
        this.setVisibleArea(visibleArea);
    }

    @Override
    public void viewportResized(Box visibleArea) {
        this.setVisibleArea(visibleArea);
    }

    @Override
    public void onMouseDown(MouseDownEvent event) {
        event.stopPropagation();
        event.preventDefault();
        Coordinate c = CoordinateFactory.get(MousePosition.getX(event), MousePosition.getY(event));
        if (this.isMouseInVisibleArea(c)) {
            this.mouseDown = c;
            this.delta = this.mouseDown.minus(this.from);
        }
    }

    @Override
    public void onMouseMove(MouseMoveEvent event) {
        event.stopPropagation();
        event.preventDefault();
        Coordinate mouse = CoordinateFactory.get(MousePosition.getX(event), MousePosition.getY(event));
        if (this.mouseDown != null) {
            if (this.from != null && this.to != null) {
                //Do not change any property of the status since it will be updated once the corresponding
                //action is performed in the main view and notified (thumbnail status changes on demand)
                Coordinate padding = this.from.minus(mouse.minus(this.delta)).divide(this.factor);
                this.eventBus.fireEventFromSource(new ThumbnailAreaMovedEvent(padding), this);
            }
        } else {
            if (this.isMouseInVisibleArea(mouse)) {
                getElement().getStyle().setCursor(Style.Cursor.MOVE);
            } else {
                getElement().getStyle().setCursor(Style.Cursor.DEFAULT);
            }
        }
    }

    @Override
    public void onMouseOut(MouseOutEvent event) {
        event.stopPropagation();
        event.preventDefault();
        this.mouseDown = null;
    }

    @Override
    public void onMouseUp(MouseUpEvent event) {
        event.stopPropagation();
        event.preventDefault();
        this.mouseDown = null;
    }

    private void clearThumbnail() {
        for (Canvas canvas : this.canvases) {
            this.cleanCanvas(canvas);
        }
        this.setVisible(false);
    }

    private void cleanCanvas(Canvas canvas) {
        canvas.getContext2d().clearRect(0, 0, canvas.getOffsetWidth(), canvas.getOffsetHeight());
    }

    private Canvas createCanvas(int width, int height) {
        Canvas canvas = Canvas.createIfSupported();
        canvas.setCoordinateSpaceWidth(width);
        canvas.setCoordinateSpaceHeight(height);
        canvas.setPixelSize(width, height);
        this.add(canvas, 0, 0);
        this.canvases.add(canvas);
        return canvas;
    }

    private void drawThumbnail() {
        this.setVisible(true);

        AdvancedContext2d compartments = this.compartments.getContext2d().cast();
        AdvancedContext2d items = this.items.getContext2d().cast();

        this.cleanCanvas(this.items);
        for (DiagramObject item : this.content.getDiagramObjects()) {
            ThumbnailRenderer renderer = ThumbnailRendererManager.get().getRenderer(item);
            if (renderer == null) continue;
            if (item instanceof Compartment) {
                renderer.draw(compartments, item, this.factor, this.offset);
            } else {
                renderer.draw(items, item, this.factor, this.offset);
            }
        }
    }

    private void select(DiagramObject item) {
        if (item == null) return;
        AdvancedContext2d ctx = this.selection.getContext2d().cast();
        ThumbnailRenderer renderer = ThumbnailRendererManager.get().getRenderer(item);
        if (renderer != null) {
            renderer.highlight(ctx, item, this.factor, this.offset);
        }
    }

    private void highlight(DiagramObject item) {
        if (item == null) return;
        AdvancedContext2d ctx = this.highlight.getContext2d().cast();
        ThumbnailRenderer renderer = ThumbnailRendererManager.get().getRenderer(item);
        if (renderer != null) {
            renderer.highlight(ctx, item, this.factor, this.offset);
        }
    }

    private void setContent(Content content, Box visibleArea) {
        if (this.content == content) return;
        this.content = content;

        this.factor = HEIGHT / (this.content.getHeight() + FRAME);
        int width = (int) Math.ceil((this.content.getWidth() + FRAME) * this.factor);

        if (DiagramFactory.WIDGET_JS && width >= MAX_WIDTH_THRESHOLD) {
            // Adjusting DiagramThumbnail width in Widgets.
            this.factor -= 0.01;
            width = (int) Math.ceil((this.content.getWidth() + FRAME) * this.factor);
        }

        this.resize(width, HEIGHT);

        this.offset = CoordinateFactory.get(FRAME / 2.0 - content.getMinX(), FRAME / 2.0 - content.getMinY());
        this.items.getContext2d().setLineWidth(Math.max(this.factor, MIN_LINE_WIDTH));

        this.setVisible(true);
        this.setVisibleArea(visibleArea);
        this.drawThumbnail();
        this.drawFrame();

        this.setCanvasProperties();
    }

    private void resize(int w, int h) {
        width = w;
        height = h;
        this.setWidth(w + "px");
        this.setHeight(h + "px");

        for (Canvas canvas : canvases) {
            canvas.setCoordinateSpaceWidth(w);
            canvas.setCoordinateSpaceHeight(h);
            canvas.setPixelSize(w, h);
        }

        Context2d ctx = this.frame.getContext2d();
        ctx.setStrokeStyle("#000000");
        ctx.setFillStyle("rgba(200,200,200, 0.4)");
        ctx.setLineWidth(0.5);
    }

    private void setStyle() {
        this.getElement().getStyle().setProperty("position", "");

        Style style = this.getElement().getStyle();
        style.setBackgroundColor("white");
        style.setBorderStyle(Style.BorderStyle.SOLID);
        style.setBorderWidth(1, Style.Unit.PX);
        style.setBorderColor("grey");
        style.setBottom(0, Style.Unit.PX);
    }

    private void drawFrame() {
        if (this.from == null || this.to == null) return;
        Coordinate diff = this.to.minus(this.from);
        cleanCanvas(this.frame);
        Context2d ctx = this.frame.getContext2d();
        ctx.fillRect(0, 0, this.frame.getOffsetWidth(), this.frame.getOffsetHeight());
        ctx.clearRect(this.from.getX(), this.from.getY(), diff.getX(), diff.getY());
        ctx.strokeRect(this.from.getX(), this.from.getY(), diff.getX(), diff.getY());
    }

    boolean isMouseInVisibleArea(Coordinate mouse) {
        return mouse.getX() >= this.from.getX()
                && mouse.getY() >= this.from.getY()
                && mouse.getX() <= this.to.getX()
                && mouse.getY() <= this.to.getY();
    }

    private void setCanvasProperties() {
        AdvancedContext2d ctx = this.highlight.getContext2d().cast();
        ctx.setFillStyle(DiagramColours.get().PROFILE.getProperties().getHighlight());
        ctx.setStrokeStyle(DiagramColours.get().PROFILE.getProperties().getHighlight());
        ctx.setLineWidth(1);

        ctx = this.selection.getContext2d().cast();
        ctx.setFillStyle(DiagramColours.get().PROFILE.getProperties().getSelection());
        ctx.setStrokeStyle(DiagramColours.get().PROFILE.getProperties().getSelection());
        ctx.setLineWidth(1);
    }

    private void setVisibleArea(Box visibleArea) {
        Coordinate from = CoordinateFactory.get(visibleArea.getMinX(), visibleArea.getMinY());
        Coordinate to = CoordinateFactory.get(visibleArea.getMaxX(), visibleArea.getMaxY());
        this.updateViewport(from, to);
    }

    private void updateViewport(Coordinate from, Coordinate to) {
        if (this.content != null) {
            this.from = from.add(this.offset).multiply(this.factor);
            this.to = to.add(this.offset).multiply(this.factor);
            this.drawFrame();
        }
    }

    @Override
    public int getWidth() {
        return width;
    }
}
