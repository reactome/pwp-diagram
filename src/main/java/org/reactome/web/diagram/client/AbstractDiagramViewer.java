package org.reactome.web.diagram.client;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.ResizeComposite;
import org.reactome.web.diagram.events.*;
import org.reactome.web.diagram.handlers.*;
import org.reactome.web.diagram.util.DiagramEventBus;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public abstract class AbstractDiagramViewer extends ResizeComposite implements DiagramViewer {

    protected EventBus eventBus;

    boolean initialised = false;
    int viewportWidth = 0;
    int viewportHeight = 0;

    AbstractDiagramViewer() {
        this.eventBus = new DiagramEventBus();
    }

    protected void initialise() {
        this.initialised = true;
        this.viewportWidth = getOffsetWidth();
        this.viewportHeight = getOffsetHeight();
    }

    @Override
    public HandlerRegistration addAnalysisResetHandler(AnalysisResetHandler handler) {
        return this.addHandler(handler, AnalysisResetEvent.TYPE);
    }

    @Override
    public HandlerRegistration addCanvasNotSupportedEventHandler(CanvasNotSupportedHandler handler) {
        return this.eventBus.addHandler(CanvasNotSupportedEvent.TYPE, handler);
    }

    @Override
    public HandlerRegistration addDatabaseObjectSelectedHandler(GraphObjectSelectedHandler handler) {
        return this.addHandler(handler, GraphObjectSelectedEvent.TYPE);
    }

    @Override
    public HandlerRegistration addDatabaseObjectHoveredHandler(GraphObjectHoveredHandler handler) {
        return this.addHandler(handler, GraphObjectHoveredEvent.TYPE);
    }

    @Override
    public HandlerRegistration addDiagramLoadedHandler(ContentLoadedHandler handler) {
        return this.addHandler(handler, ContentLoadedEvent.TYPE);
    }

    @Override
    public HandlerRegistration addDiagramObjectsFlaggedHandler(DiagramObjectsFlaggedHandler handler) {
        return this.addHandler(handler, DiagramObjectsFlaggedEvent.TYPE);
    }

    @Override
    public HandlerRegistration addDiagramObjectsFlagResetHandler(DiagramObjectsFlagResetHandler handler) {
        return this.addHandler(handler, DiagramObjectsFlagResetEvent.TYPE);
    }

    @Override
    public HandlerRegistration addInteractorHoveredHandler(InteractorHoveredHandler handler) {
        return this.addHandler(handler, InteractorHoveredEvent.TYPE);
    }

    @Override
    public HandlerRegistration addFireworksOpenedHandler(FireworksOpenedHandler handler) {
        return this.addHandler(handler, FireworksOpenedEvent.TYPE);
    }

    @Override
    public HandlerRegistration addDiagramProfileChangedHandler(DiagramProfileChangedHandler handler) {
        return this.addHandler(handler, DiagramProfileChangedEvent.TYPE);
    }

    @Override
    public HandlerRegistration addAnalysisProfileChangedHandler(AnalysisProfileChangedHandler handler) {
        return this.addHandler(handler, AnalysisProfileChangedEvent.TYPE);
    }

    public int getViewportWidth() {
        return viewportWidth;
    }

    public int getViewportHeight() {
        return viewportHeight;
    }
}
