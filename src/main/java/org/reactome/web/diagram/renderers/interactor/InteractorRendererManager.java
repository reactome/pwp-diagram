package org.reactome.web.diagram.renderers.interactor;

import com.google.gwt.event.shared.EventBus;
import org.reactome.web.diagram.data.interactors.model.*;
import org.reactome.web.diagram.events.ContentLoadedEvent;
import org.reactome.web.diagram.events.DiagramZoomEvent;
import org.reactome.web.diagram.handlers.ContentLoadedHandler;
import org.reactome.web.diagram.handlers.DiagramZoomHandler;
import org.reactome.web.diagram.renderers.common.RendererProperties;
import org.reactome.web.diagram.renderers.interactor.s000.DynamicLinkRenderer000;
import org.reactome.web.diagram.renderers.interactor.s000.InteractorEntityRenderer000;
import org.reactome.web.diagram.renderers.interactor.s000.LoopLinkRenderer000;
import org.reactome.web.diagram.renderers.interactor.s000.StaticLinkRenderer000;
import org.reactome.web.diagram.renderers.interactor.s050.DynamicLinkRenderer050;
import org.reactome.web.diagram.renderers.interactor.s050.InteractorEntityRenderer050;
import org.reactome.web.diagram.renderers.interactor.s050.LoopLinkRenderer050;
import org.reactome.web.diagram.renderers.interactor.s050.StaticLinkRenderer050;
import org.reactome.web.diagram.renderers.interactor.s100.DynamicLinkRenderer100;
import org.reactome.web.diagram.renderers.interactor.s100.InteractorEntityRenderer100;
import org.reactome.web.diagram.renderers.interactor.s100.LoopLinkRenderer100;
import org.reactome.web.diagram.renderers.interactor.s100.StaticLinkRenderer100;
import org.reactome.web.diagram.renderers.interactor.s300.DynamicLinkRenderer300;
import org.reactome.web.diagram.renderers.interactor.s300.InteractorEntityRenderer300;
import org.reactome.web.diagram.renderers.interactor.s300.LoopLinkRenderer300;
import org.reactome.web.diagram.renderers.interactor.s300.StaticLinkRenderer300;

import java.util.HashMap;
import java.util.Map;

import static org.reactome.web.diagram.data.content.Content.Type.DIAGRAM;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class InteractorRendererManager implements DiagramZoomHandler, ContentLoadedHandler {

    private static InteractorRendererManager manager;

    private final EventBus eventBus;

    private final Map<Class<? extends DiagramInteractor>, InteractorRenderer> s000 = new HashMap<>();
    private final Map<Class<? extends DiagramInteractor>, InteractorRenderer> s050 = new HashMap<>();
    private final Map<Class<? extends DiagramInteractor>, InteractorRenderer> s100 = new HashMap<>();
    //    private final Map<Class<? extends DiagramInteractor>, InteractorRenderer> s200 = new HashMap<>();
    private final Map<Class<? extends DiagramInteractor>, InteractorRenderer> s300 = new HashMap<>();

    private Map<Class<? extends DiagramInteractor>, InteractorRenderer> current = s100;

    private InteractorRendererManager(EventBus eventBus) {
        this.eventBus = eventBus;
        initHandlers();
        initialiseRenderers();
    }

    private void initHandlers() {
        this.eventBus.addHandler(DiagramZoomEvent.TYPE, this);
        this.eventBus.addHandler(ContentLoadedEvent.TYPE, this);
    }

    public static void initialise(EventBus eventBus) {
        if (InteractorRendererManager.manager != null) {
            throw new RuntimeException("Interactor Renderer Manager has already been initialised. Only one initialisation is permitted per Diagram Viewer instance.");
        }
        InteractorRendererManager.manager = new InteractorRendererManager(eventBus);
    }

    public static InteractorRendererManager get() {
        if (InteractorRendererManager.manager == null) {
            throw new RuntimeException("Interactor Renderer Manager has not been initialised yet. Please call initialise before using 'get'");
        }
        return InteractorRendererManager.manager;
    }

    public InteractorRenderer getRenderer(Class<? extends DiagramInteractor> clazz) {
        if (clazz == null) return null;
        return this.current.get(clazz);
    }

    public InteractorRenderer getRenderer(DiagramInteractor diagramInteractor) {
        if (diagramInteractor == null) return null;
        return this.current.get(diagramInteractor.getClass());
    }

    public void initialiseRenderers() {
        s000.put(InteractorEntity.class, new InteractorEntityRenderer000());
        s000.put(DynamicLink.class, new DynamicLinkRenderer000());
        s000.put(StaticLink.class, new StaticLinkRenderer000());
        s000.put(LoopLink.class, new LoopLinkRenderer000());

        s050.put(InteractorEntity.class, new InteractorEntityRenderer050());
        s050.put(DynamicLink.class, new DynamicLinkRenderer050());
        s050.put(StaticLink.class, new StaticLinkRenderer050());
        s050.put(LoopLink.class, new LoopLinkRenderer050());

        s100.put(InteractorEntity.class, new InteractorEntityRenderer100());
        s100.put(DynamicLink.class, new DynamicLinkRenderer100());
        s100.put(StaticLink.class, new StaticLinkRenderer100());
        s100.put(LoopLink.class, new LoopLinkRenderer100());

        s300.put(InteractorEntity.class, new InteractorEntityRenderer300());
        s300.put(DynamicLink.class, new DynamicLinkRenderer300());
        s300.put(StaticLink.class, new StaticLinkRenderer300());
        s300.put(LoopLink.class, new LoopLinkRenderer300());
    }

    @Override
    public void onDiagramZoomEvent(DiagramZoomEvent event) {
        setFactor(event.getFactor());
    }

    @Override
    public void onContentLoaded(ContentLoadedEvent event) {
        if (event.getContext().getContent().getType() == DIAGRAM) {
            setFactor(event.getContext().getDiagramStatus().getFactor());
        }
    }

    private void setFactor(double factor) {
        RendererProperties.setFactor(factor);
        if (factor < 0.5) {
            this.current = this.s000;
        } else if (factor < 1) {
            this.current = this.s050;
        } else if (factor < 2) {
            this.current = this.s100;
        } else if (factor < 3) {
            this.current = this.s100;
        } else {
            this.current = this.s300;
        }
    }
}
