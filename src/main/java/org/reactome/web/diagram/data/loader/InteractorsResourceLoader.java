package org.reactome.web.diagram.data.loader;

import com.google.gwt.http.client.*;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import org.reactome.web.diagram.client.DiagramFactory;
import org.reactome.web.diagram.data.interactors.custom.raw.RawInteractorError;
import org.reactome.web.diagram.data.interactors.custom.raw.factory.UploadResponseException;
import org.reactome.web.diagram.data.interactors.custom.raw.factory.UploadResponseFactory;
import org.reactome.web.diagram.data.interactors.raw.RawResource;
import org.reactome.web.diagram.data.interactors.raw.factory.InteractorsException;
import org.reactome.web.diagram.data.interactors.raw.factory.InteractorsFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class InteractorsResourceLoader implements RequestCallback {

    public interface Handler {
        void interactorsResourcesLoaded(List<RawResource> resourceList, long time);

        void onInteractorsResourcesLoadError(RawInteractorError error);

        void onInteractorsResourcesLoadException(String message);
    }

    final static String PREFIX = DiagramFactory.SERVER + "/ContentService/interactors/";

    static InteractorsResourceLoader loader;
    Handler handler;
    Request request;

    InteractorsResourceLoader(Handler handler) {
        this.handler = handler;
    }

    public static void loadResources(Handler handler) {
        if (loader != null && loader.request != null && loader.request.isPending()) loader.request.cancel();
        loader = new InteractorsResourceLoader(handler);
        loader.load();
    }

    private void load() {
        String url = PREFIX + "psicquic/resources/";
        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
        try {
            this.request = requestBuilder.sendRequest(null, this);
        } catch (RequestException e) {
            this.handler.onInteractorsResourcesLoadException(e.getMessage());
        }
    }

    @Override
    public void onResponseReceived(Request request, Response response) {
        switch (response.getStatusCode()) {
            case Response.SC_OK:
                long start = System.currentTimeMillis();
                List<RawResource> resources = new LinkedList<>();
                try {
                    JSONArray list = JSONParser.parseStrict(response.getText()).isArray();
                    for (int i = 0; i < list.size(); ++i) {
                        JSONObject object = list.get(i).isObject();
                        resources.add(InteractorsFactory.getInteractorObject(RawResource.class, object.toString()));
                    }
                } catch (InteractorsException e) {
                    this.handler.onInteractorsResourcesLoadException(e.getMessage());
                    return;
                }
                this.handler.interactorsResourcesLoaded(resources, System.currentTimeMillis() - start);
                break;
            default:
                try {
                    RawInteractorError error = UploadResponseFactory.getUploadResponseObject(RawInteractorError.class, response.getText());
                    this.handler.onInteractorsResourcesLoadError(error);
                } catch (UploadResponseException e) {
                    this.handler.onInteractorsResourcesLoadException("Unable to connect to server [" + response.getStatusCode() + "]");
                }
        }
    }

    @Override
    public void onError(Request request, Throwable throwable) {
        this.handler.onInteractorsResourcesLoadException(throwable.getMessage());
    }
}
