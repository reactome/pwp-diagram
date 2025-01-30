package org.reactome.web.diagram.data.loader;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.http.client.*;
import org.reactome.web.diagram.client.DiagramFactory;
import org.reactome.web.diagram.data.content.Content;
import org.reactome.web.diagram.data.graph.model.GraphComplex;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.data.interactors.common.OverlayResource;
import org.reactome.web.diagram.data.interactors.raw.RawInteractors;
import org.reactome.web.diagram.data.interactors.raw.factory.InteractorsException;
import org.reactome.web.diagram.data.interactors.raw.factory.InteractorsFactory;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.events.InteractorsErrorEvent;
import org.reactome.web.pwp.model.client.util.ResponseUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class InteractorsLoader implements RequestCallback {

    public interface Handler {
        void interactorsLoaded(RawInteractors interactors, long time);

        void onInteractorsLoaderError(InteractorsException exception);
    }

    final static String PREFIX_INTERACTOR = DiagramFactory.SERVER + "/ContentService/interactors/";
    final static String PREFIX_DISEASE = DiagramFactory.SERVER + "/overlays/disgenet/";

    Handler handler;
    Request request;

    OverlayResource resource;

    public InteractorsLoader(Handler handler) {
        this.handler = handler;
    }

    public void cancel() {
        if (request != null && request.isPending()) {
            request.cancel();
        }
    }

    public void load(Content content, OverlayResource resource) {
        // Any previous request has to be canceled
        cancel();

        if (resource == null) {
            this.handler.onInteractorsLoaderError(new InteractorsException(null, "Resource not specified"));
            return;
        }
        this.resource = resource;

        String post = getPostData(content.getDiagramObjects());
        if (post != null) {
            String url = "";
            switch (resource.getType()) {
                case CUSTOM:
                    url = PREFIX_INTERACTOR + "token/" + resource.getIdentifier();
                    break;
                case STATIC:
                    if (resource.getName().equals(DiagramFactory.INTERACTORS_INITIAL_RESOURCE_NAME)) {
                        url = PREFIX_INTERACTOR + "static/molecules/details/";
                    } else {
                        url = PREFIX_DISEASE + "findByGenes";
                    }
                    break;
                case PSICQUIC:
                    url = PREFIX_INTERACTOR + "psicquic/molecules/" + resource.getIdentifier() + "/details";
                    break;
            }
            url += "?v=" + LoaderManager.version;

            RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.POST, url);
            try {
                this.request = requestBuilder.sendRequest(post, this);
            } catch (RequestException e) {
                fireDeferredErrorEvent(resource.getIdentifier(), e.getMessage(), InteractorsErrorEvent.Level.ERROR_RECOVERABLE); //TODO
            }
        } else {
            fireDeferredErrorEvent(resource.getIdentifier(), "No target entities for interactors", InteractorsErrorEvent.Level.WARNING);
        }
    }

    /**
     * This method iterates over diagram objects, gets their accession
     * and creates a comma separated string to be placed in the POST request
     */
    private String getPostData(Collection<DiagramObject> items) {
        Set<String> ids = new HashSet<>(); //this is to avoid duplicate ids in the request
        StringBuilder post = new StringBuilder();
        for (DiagramObject diagramObject : items) {
            GraphObject graphObject = diagramObject.getGraphObject();
            if (graphObject instanceof GraphPhysicalEntity) {
                if (graphObject instanceof GraphComplex) {
                    GraphComplex complex = (GraphComplex) graphObject;
                    Set<GraphPhysicalEntity> participants = complex.getParticipants();
                    if (participants.size() == 1) {
                        for (GraphPhysicalEntity participant: participants) {
                            addPhysicalEntityToPost(participant, ids, post);
                        }

                    }
                } else {
                    addPhysicalEntityToPost((GraphPhysicalEntity) graphObject, ids, post);
                }
            }
        }
        if (post.length() > 0) {
            post.delete(post.length() - 1, post.length());
            return post.toString();
        }
        return null;
    }

    private void addPhysicalEntityToPost(GraphPhysicalEntity participant, Set<String> ids, StringBuilder post) {
        if (participant.getIdentifier() != null && ids.add(participant.getIdentifier())) { //this is to avoid re-iterating the set
            post.append(participant.getIdentifier()).append(",");
        }
    }

    private void fireDeferredErrorEvent(final String resource, final String message, final InteractorsErrorEvent.Level level) {
        // Firing of the error event is deferred to ensure that InteractorsResourceChanged
        // event is handled first by the rest of the modules.
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                handler.onInteractorsLoaderError(new InteractorsException(resource, message, level));
            }
        });
    }

    @Override
    public void onResponseReceived(Request request, Response response) {
        switch (response.getStatusCode()) {
            case Response.SC_OK:
                long start = System.currentTimeMillis();
                RawInteractors interactors;
                try {
                    interactors = InteractorsFactory.getInteractorObject(RawInteractors.class, response.getText());
                } catch (InteractorsException e) {
                    this.handler.onInteractorsLoaderError(e);
                    return;
                }
                long time = System.currentTimeMillis() - start;
                this.handler.interactorsLoaded(interactors, time);
                break;
            default:
                this.handler.onInteractorsLoaderError(new InteractorsException(resource.getIdentifier(), ResponseUtils.getStatusText(response.getStatusCode()), InteractorsErrorEvent.Level.ERROR_RECOVERABLE));
        }

    }

    @Override
    public void onError(Request request, Throwable exception) {
        this.handler.onInteractorsLoaderError(new InteractorsException(resource.getIdentifier(), exception.getMessage()));
    }
}
