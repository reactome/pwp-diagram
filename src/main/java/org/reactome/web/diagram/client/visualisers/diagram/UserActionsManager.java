package org.reactome.web.diagram.client.visualisers.diagram;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.Timer;
import org.reactome.web.diagram.client.ViewerContainer;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPathway;
import org.reactome.web.diagram.data.interactors.model.DiagramInteractor;
import org.reactome.web.diagram.data.interactors.model.InteractorEntity;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.impl.CoordinateFactory;
import org.reactome.web.diagram.renderers.common.HoveredItem;
import org.reactome.web.diagram.util.actions.MouseActionsHandlers;
import org.reactome.web.diagram.util.position.MousePosition;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
class UserActionsManager implements MouseActionsHandlers {

    interface Handler {
        HoveredItem getHoveredDiagramObject();
        DiagramInteractor getHoveredInteractor();
        void dragInteractor(InteractorEntity interactor, Coordinate delta);
        void loadDiagram(String stId);
        void mouseZoom(double delta);
        void padding(Coordinate delta);
        void refreshHoveredItem();
        void setMousePosition(Coordinate mouse);
        void setSelection(boolean zoom, boolean fireExternally);
        void showDialog(DiagramObject item);
    }

    static final double ZOOM_FACTOR = 0.025;
    static final double ZOOM_DELTA = 0.25;
    static final double ZOOM_TOUCH_DELTA = 200;
    static final int LONG_TOUCH_TIME = 2000;
    static final int DOUBLE_TAP_TIME = 400;

    InteractorEntity hoveredInteractor;

    Coordinate mouseDown = null;

    private final DiagramCanvas canvas;
    private Handler handler;

    private boolean diagramMoved = false;
    private boolean interactorDragged = false;
    private Double fingerDistance;

    private Timer doubleTapTimer;
    private Timer longTapTimer;

    public UserActionsManager(Handler handler, DiagramCanvas canvas) {
        this.handler = handler;
        this.canvas = canvas;

        doubleTapTimer =  new Timer() {
            @Override
            public void run() {
                //Nothing here
            }
        };
        longTapTimer = new Timer() {
            @Override
            public void run() {
                //Nothing here
            }
        };
    }

    @Override
    public void onClick(ClickEvent event) {
        event.preventDefault();
        event.stopPropagation();
        if (!diagramMoved && !interactorDragged) {
            diagramMoved = false;
            interactorDragged = false;
            handler.setSelection(false, true);
        }
    }

    @Override
    public void onDoubleClick(DoubleClickEvent event) {
        HoveredItem hovered = handler.getHoveredDiagramObject();
        DiagramObject item = hovered != null ? hovered.getHoveredObject() : null;
        GraphObject toOpen = item != null ? item.getGraphObject() : null;
        if (toOpen instanceof GraphPathway) {
            handler.loadDiagram(toOpen.getStId() != null ? toOpen.getStId() : toOpen.getDbId().toString());
        }
    }

    @Override
    public void onMouseDown(MouseDownEvent event) {
        event.stopPropagation();
        event.preventDefault();
        diagramMoved = false;
        interactorDragged = false;
        int button = event.getNativeEvent().getButton();
        switch (button) {
            case NativeEvent.BUTTON_RIGHT:
                HoveredItem hovered = handler.getHoveredDiagramObject();
                DiagramObject item = hovered != null ? hovered.getHoveredObject() : null;
                handler.setSelection(false, true);
                handler.showDialog(item);
                break;
            default:
                setMouseDownPosition(event);
        }
    }

    @Override
    public void onMouseMove(MouseMoveEvent event) {
        setMousePosition(event);
        if (mouseDown != null) {
            Coordinate mouse = CoordinateFactory.get(MousePosition.getX(event), MousePosition.getY(event));
            Coordinate delta = mouse.minus(mouseDown);
            if (isDeltaValid(delta,2)) {
                canvas.setCursor(Style.Cursor.MOVE);
                if (hoveredInteractor == null) {
                    diagramMoved = true;
                    handler.padding(delta);

                } else {
                    interactorDragged = true;
                    handler.dragInteractor(hoveredInteractor, delta);
                }
                setMouseDownPosition(event);
            }
        }
    }

    @Override
    public void onMouseOut(MouseOutEvent event) {
        diagramMoved = false;
        mouseDown = null;
        canvas.setCursor(Style.Cursor.DEFAULT);
        // With this we force to re-calculate the hovered element
        // (assuming there is nothing in the provided position)
        handler.setMousePosition(CoordinateFactory.get(-2000, -2000));
    }

    @Override
    public void onMouseUp(MouseUpEvent event) {
        event.stopPropagation();
        event.preventDefault();
        HoveredItem hovered = handler.getHoveredDiagramObject();
        canvas.setCursor(hovered == null ? Style.Cursor.DEFAULT : Style.Cursor.POINTER);
        mouseDown = null;
    }

    @Override
    public void onMouseWheel(MouseWheelEvent event) {
        //Continue scrolling has priority to ehld user action
        if(ViewerContainer.windowScrolling.isRunning()) return;

        event.stopPropagation();
        event.preventDefault();
        setMousePosition(event);
        handler.mouseZoom(event.getDeltaY() * ZOOM_FACTOR);
    }

    @Override
    public void onTouchCancel(TouchCancelEvent event) {
        if (longTapTimer.isRunning()) { longTapTimer.cancel(); }
        mouseDown = null;
        diagramMoved = false;
        interactorDragged = false;
        fingerDistance = null;
    }

    @Override
    public void onTouchEnd(TouchEndEvent event) {
        //Continue scrolling has priority to ehld user action
        if(ViewerContainer.windowScrolling.isRunning()) return;

        event.preventDefault(); event.stopPropagation();
        if (longTapTimer.isRunning()) { longTapTimer.cancel(); }
        if (!this.diagramMoved && !interactorDragged) {
            setMousePosition(getTouchCoordinate(event.getChangedTouches().get(0)));
            HoveredItem hovered = handler.getHoveredDiagramObject();
            DiagramObject item = hovered != null ? hovered.getHoveredObject() : null;
            DiagramInteractor interactor = handler.getHoveredInteractor();
            if (!doubleTapTimer.isRunning()) {                    // Single tap
                doubleTapTimer.schedule(DOUBLE_TAP_TIME);
                if (interactor == null) {
                    handler.setSelection(false, true);
                }
            } else {
                doubleTapTimer.cancel();                          // Double tap
                GraphObject toOpen = item != null ? item.getGraphObject() : null;
                if (toOpen instanceof GraphPathway) {
                    handler.loadDiagram(toOpen.getStId() != null ? toOpen.getStId() : toOpen.getDbId().toString());
                } else if (interactor != null) {
                    handler.setSelection(false, true);
                }
            }
        }
        mouseDown = null;
        diagramMoved = false;
        interactorDragged = false;
        fingerDistance = null;
    }

    @Override
    public void onTouchMove(TouchMoveEvent event) {
        //Continue scrolling has priority to ehld user action
        if(ViewerContainer.windowScrolling.isRunning()) return;

        event.stopPropagation(); event.preventDefault();
        int numberOfTouches =  event.getTouches().length();
        if (numberOfTouches == 1 && hoveredInteractor == null) {                     // Panning
            Coordinate mouseCurrent = getTouchCoordinate(event.getTouches().get(0)); // Get the first touch
            if (mouseDown == null) {
                this.mouseDown = mouseCurrent;
            } else {
                Coordinate delta = mouseCurrent.minus(this.mouseDown);
                // On mouse move is sometimes called for delta 0 (we cannot control that, but only consider it)
                if (isDeltaValid(delta, 4)) {
                    handler.padding(delta);
                    this.mouseDown = mouseCurrent;
                    this.diagramMoved = true;                               //Selection is denied in case of panning
                    interactorDragged = false;
                    if (longTapTimer.isRunning()) { longTapTimer.cancel(); }
                }

            }
        } else if (numberOfTouches == 1) {                                           //Interactor dragging
            if (longTapTimer.isRunning()) { longTapTimer.cancel(); }
            Coordinate mouseCurrent = getTouchCoordinate(event.getTouches().get(0)); // Get the first touch
            Coordinate delta = mouseCurrent.minus(this.mouseDown);
            handler.dragInteractor(hoveredInteractor, delta);
            this.mouseDown = mouseCurrent;
            interactorDragged = true;
        } else if (numberOfTouches == 2) {                                          // Zooming in and out
            if (longTapTimer.isRunning()) { longTapTimer.cancel(); }
            Coordinate finger1 = getTouchCoordinate(event.getTouches().get(0));
            Coordinate finger2 = getTouchCoordinate(event.getTouches().get(1));
            Coordinate delta = finger2.minus(finger1);
            Double newFingerDistance = Math.sqrt(delta.getX() * delta.getX() + delta.getY() * delta.getY());
            double deltaFactor = (fingerDistance - newFingerDistance) / ZOOM_TOUCH_DELTA;
            setMousePosition(finger1.add(delta.divide(2))); // Middle point between the 2 fingers is the zoom focus
            handler.mouseZoom(deltaFactor);
            // With this we force to re-calculate the hovered element
            // and avoid hovering anything at the point of zoom
            setMousePosition(CoordinateFactory.get(-2000, -2000));
            this.fingerDistance = newFingerDistance;
            this.diagramMoved = true;                       //Selection is denied in case of zooming
        }
    }

    @Override
    public void onTouchStart(TouchStartEvent event) {
        //Continue scrolling has priority to ehld user action
        if(ViewerContainer.windowScrolling.isRunning()) return;

        event.stopPropagation(); event.preventDefault();
        if (longTapTimer.isRunning()) { longTapTimer.cancel(); }
        int numberOfTouches =  event.getTouches().length();
        if (numberOfTouches == 1) {
            setMousePosition(getTouchCoordinate(event.getTouches().get(0)));
            handler.refreshHoveredItem();
            this.mouseDown = getTouchCoordinate(event.getTouches().get(0)); // Get the first touch
            this.diagramMoved = false;
            longTapTimer = new Timer() {                   // Show context dialogs on long press
                @Override
                public void run() {
                    HoveredItem hovered = handler.getHoveredDiagramObject();
                    DiagramObject item = hovered != null ? hovered.getHoveredObject() : null;
                    if (item != null) {
                        handler.showDialog(item);
                    }
                }
            };
            longTapTimer.schedule(LONG_TOUCH_TIME);
        } else if (numberOfTouches == 2){
            Coordinate finger1 = getTouchCoordinate(event.getTouches().get(0));
            Coordinate finger2 = getTouchCoordinate(event.getTouches().get(1));
            Coordinate delta = finger2.minus(finger1);
            fingerDistance = Math.sqrt(delta.getX() * delta.getX() + delta.getY() * delta.getY());
        }
    }

    public boolean setInteractorHovered(DiagramInteractor hovered){
        if (mouseDown != null) return false;
        if (hovered != null && hovered instanceof InteractorEntity) {
            hoveredInteractor = (InteractorEntity) hovered;
        } else {
            hoveredInteractor = null;
        }
        return true;
    }

    protected void setMouseDownPosition(MouseEvent event) {
        this.mouseDown = CoordinateFactory.get(MousePosition.getX(event), MousePosition.getY(event));
    }

    protected void setMousePosition(MouseEvent event) {
        Coordinate mouseCurrent = CoordinateFactory.get(MousePosition.getX(event), MousePosition.getY(event));
        handler.setMousePosition(mouseCurrent);
    }

    protected void setMousePosition(Coordinate position) {
        handler.setMousePosition(position);
    }

    protected Coordinate getTouchCoordinate(Touch touch) {
        int x = touch.getRelativeX(canvas.getElement());
        int y = touch.getRelativeY(canvas.getElement());
        return CoordinateFactory.get(x, y);
    }

    private boolean isDeltaValid(Coordinate delta, int threshold) {
        return delta.getX() >= threshold  || delta.getX() <= -threshold || delta.getY() >= threshold || delta.getY() <= -threshold;
    }
}
