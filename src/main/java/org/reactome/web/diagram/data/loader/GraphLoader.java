package org.reactome.web.diagram.data.loader;

import com.google.gwt.http.client.*;
import org.reactome.web.diagram.client.DiagramFactory;
import org.reactome.web.diagram.data.graph.raw.Graph;
import org.reactome.web.diagram.data.graph.raw.factory.GraphFactory;
import org.reactome.web.diagram.data.layout.factory.DiagramObjectException;
import org.reactome.web.pwp.model.client.util.ResponseUtils;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class GraphLoader implements RequestCallback {

    public interface Handler {
        void graphLoaded(Graph graph, long time);
        void onGraphLoaderError(Throwable exception);
    }

    public static String PREFIX = DiagramFactory.SERVER + "/download/current/diagram/";
    public static String SUFFIX = ".graph.json?v=" + LoaderManager.version;

    private Handler handler;
    private Request request;

    GraphLoader(Handler handler) {
        this.handler = handler;
    }

    public void cancel(){
        if(this.request!=null && this.request.isPending()){
            this.request.cancel();
        }
    }

    public void load(String stId){
        String url = PREFIX + stId + SUFFIX;
        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
        try {
            this.request = requestBuilder.sendRequest(null, this);
        } catch (RequestException e) {
            this.handler.onGraphLoaderError(e);
        }
    }

    @Override
    public void onResponseReceived(Request request, Response response) {
        switch (response.getStatusCode()){
            case Response.SC_OK:
                long start = System.currentTimeMillis();
                Graph graph;
                try {
                    //Creates the graph
                    graph = GraphFactory.getGraphObject(Graph.class, response.getText());
                } catch (DiagramObjectException e) {
                    this.handler.onGraphLoaderError(e);
                    return;
                }
                this.handler.graphLoaded(graph, System.currentTimeMillis() - start);
                break;
            default:
                this.handler.onGraphLoaderError(new Exception(ResponseUtils.getStatusText(response.getStatusCode())));
        }
    }

    @Override
    public void onError(Request request, Throwable exception) {
        this.handler.onGraphLoaderError(exception);
    }
}
