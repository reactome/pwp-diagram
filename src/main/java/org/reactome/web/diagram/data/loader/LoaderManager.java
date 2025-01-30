package org.reactome.web.diagram.data.loader;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.EventBus;
import org.reactome.web.diagram.client.DiagramFactory;
import org.reactome.web.diagram.data.ContentFactory;
import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.content.DiagramContent;
import org.reactome.web.diagram.data.graph.raw.Graph;
import org.reactome.web.diagram.data.interactors.common.OverlayResource;
import org.reactome.web.diagram.data.interactors.raw.RawInteractors;
import org.reactome.web.diagram.data.interactors.raw.factory.InteractorsException;
import org.reactome.web.diagram.data.layout.Diagram;
import org.reactome.web.diagram.events.*;
import org.reactome.web.diagram.handlers.ContentLoadedHandler;
import org.reactome.web.diagram.handlers.ContentRequestedHandler;
import org.reactome.web.diagram.handlers.InteractorsRequestCanceledHandler;
import org.reactome.web.diagram.handlers.InteractorsResourceChangedHandler;
import org.reactome.web.pwp.model.client.util.LruCache;
import org.vectomatic.dom.svg.OMSVGSVGElement;

import static org.reactome.web.diagram.data.content.Content.Type.DIAGRAM;

/**
 * Implements a combination of two series of steps.
 * A: Load SVG for the diagram and use it
 * B: If there is not SVG then it follows a three step loading strategy
 *      1st step: Loads Diagram
 *      2nd step: Loads Graph
 *      3rd step: Loads Interactors (if INTERACTORS_RESOURCE is not null)
 *
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class LoaderManager implements SVGLoader.Handler, LayoutLoader.Handler, GraphLoader.Handler, InteractorsLoader.Handler,
        InteractorsResourceChangedHandler, InteractorsRequestCanceledHandler, ContentRequestedHandler, ContentLoadedHandler {

    //Every time the diagram widget is loaded will retrieve new data from the sever
    public static String version = String.valueOf(System.currentTimeMillis()); //UNIQUE per session

    //It has a value by default but it can be set to a different one so in every load
    //the "user preferred" interactors resource will be selected
    public static OverlayResource INTERACTORS_RESOURCE = new OverlayResource(DiagramFactory.INTERACTORS_INITIAL_RESOURCE, DiagramFactory.INTERACTORS_INITIAL_RESOURCE_NAME, OverlayResource.ResourceType.STATIC);

    protected LruCache<String, Context> contextMap = new LruCache<>(5);
    private final EventBus eventBus;

    protected SVGLoader svgLoader;
    public LayoutLoader layoutLoader;
    private final GraphLoader graphLoader;
    private final InteractorsLoader interactorsLoader;
    protected Context context;

    public LoaderManager(EventBus eventBus) {
        this.eventBus = eventBus;
        svgLoader = new SVGLoader(this);
        layoutLoader = new LayoutLoader(this);
        graphLoader = new GraphLoader(this);
        interactorsLoader = new InteractorsLoader(this);

        //For the time being we only want to do something on demand for interactors
        eventBus.addHandler(InteractorsResourceChangedEvent.TYPE, this);
        eventBus.addHandler(InteractorsRequestCanceledEvent.TYPE, this);
        eventBus.addHandler(ContentLoadedEvent.TYPE, this);
        eventBus.addHandler(ContentRequestedEvent.TYPE, this);
    }

    public void cancel() {
        svgLoader.cancel();
        layoutLoader.cancel();
        graphLoader.cancel();
        interactorsLoader.cancel();
        context = null;
    }

    public void load(String identifier) {
        eventBus.fireEventFromSource(new ContentRequestedEvent(identifier), this);
        loadOnRequest(identifier);
    }

    private void loadOnRequest(String identifier) {
        cancel(); //First cancel possible loading process
        context = this.contextMap.get(identifier);
        if (context != null) {
            eventBus.fireEventFromSource(new ContentLoadedEvent(context), this);
        } else {
            if (SVGLoader.isSVGAvailable(identifier)) {
                svgLoader.load(identifier);
            } else {
                layoutLoader.load(identifier);
            }
        }
    }

    @Override
    public void onContentRequested(ContentRequestedEvent event) {
        if(!event.getSource().equals(this)) {
            Scheduler.get().scheduleDeferred(() -> loadOnRequest(event.getIdentifier()));
        }
    }

    @Override
    public void onSvgLoaded(String stId, boolean isInDisease, OMSVGSVGElement svg, long time) {
        Context context = new Context(ContentFactory.getContent(stId, isInDisease, svg));
        //caching the context
        contextMap.put(context.getContent().getStableId(), context);
        this.context = context;
        graphLoader.load(stId);
    }

    @Override
    public void onSvgLoaderError(String stId, Throwable exception) {
        layoutLoader.load(stId);
    }

    @Override
    public void layoutLoaded(Diagram diagram, long time) {
        //This is querying the server so the following code is executed straight forward
        long start = System.currentTimeMillis();
        Context context = new Context(ContentFactory.getContent(diagram));
        //caching the context
        contextMap.put(context.getContent().getStableId(), context);
        this.context = context;
        graphLoader.load(diagram.getStableId());
        time += System.currentTimeMillis() - start;
        eventBus.fireEventFromSource(new LayoutLoadedEvent(context, time), this);
    }

    @Override
    public void onLayoutLoaderError(Throwable exception) {
        eventBus.fireEventFromSource(
                new DiagramInternalErrorEvent("There was a problem while loading the diagram",
                        "Layout content error: " + exception.getMessage()
                ), this);
    }

    @Override
    public void graphLoaded(Graph graph, long time) {
        long start = System.currentTimeMillis();
        ContentFactory.fillGraphContent(context.getContent(), graph);
        time += System.currentTimeMillis() - start;
        eventBus.fireEventFromSource(new GraphLoadedEvent(context.getContent(), time), this);
        //Once the graph is loaded the ContentLoadedEvent can be fired
        eventBus.fireEventFromSource(new ContentLoadedEvent(context), this);
    }

    @Override
    public void onGraphLoaderError(Throwable exception) {
        eventBus.fireEventFromSource(
                new DiagramInternalErrorEvent("There was a problem while loading the diagram",
                        "Graph content error: " + exception.getMessage()), this);
    }

    @Override
    public void interactorsLoaded(RawInteractors interactors, long time) {
        long start = System.currentTimeMillis();
        ContentFactory.fillInteractorsContent(context, interactors);
        time += System.currentTimeMillis() - start;
        eventBus.fireEventFromSource(new InteractorsLoadedEvent(interactors, time), this);
    }

    @Override
    public void onInteractorsLoaderError(InteractorsException exception) {
        eventBus.fireEventFromSource(new InteractorsErrorEvent(exception.getResource(), exception.getMessage(), exception.getLevel()), this);
    }

    //The interactors loader is meant to be used not only when loading a new diagram but also on demand
    @Override
    public void onInteractorsResourceChanged(InteractorsResourceChangedEvent event) {
        INTERACTORS_RESOURCE = event.getResource();
        interactorsLoader.cancel();
        if (INTERACTORS_RESOURCE != null && !context.getInteractors().isInteractorResourceCached(INTERACTORS_RESOURCE.getIdentifier())) {
            interactorsLoader.load(context.getContent(), INTERACTORS_RESOURCE);
        }
    }

    @Override
    public void onInteractorsRequestCanceled(InteractorsRequestCanceledEvent event) {
        interactorsLoader.cancel();
    }

    @Override
    public void onContentLoaded(ContentLoadedEvent event) {
        if (event.getContext().getContent().getType() == DIAGRAM) {
            context = event.getContext();
            if (INTERACTORS_RESOURCE != null) {   //Checking here so no error message is displayed in this case
                //This fakes a resource changed so the control will show the loading message
                //The behaviour of the three steps loading continues as expected
                Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                    //We use the schedule deferred here because all the diagram loaded subscribers should be called BEFORE
                    //the InteractorsResourceChangedEvent is fired (to avoid messing the order of the events)
                    @Override
                    public void execute() {
                        if(context.getContent() instanceof DiagramContent) {
                            eventBus.fireEventFromSource(new InteractorsResourceChangedEvent(INTERACTORS_RESOURCE), LoaderManager.this);
                        }
                    }
                });
            }
        }
    }
}