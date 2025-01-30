package org.reactome.web.diagram.data.loader;

import com.google.gwt.http.client.*;
import org.reactome.web.diagram.client.DiagramFactory;
import org.reactome.web.diagram.data.content.Content;
import org.reactome.web.diagram.search.results.data.SearchException;
import org.reactome.web.diagram.search.results.data.SearchResultFactory;
import org.reactome.web.diagram.search.results.data.model.Occurrences;
import org.reactome.web.pwp.model.client.util.ResponseUtils;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class FlaggedElementsLoader implements RequestCallback {

    public interface Handler {
        void flaggedElementsLoaded(String term, Occurrences toFlag, boolean notify);

        void onFlaggedElementsLoaderError(Throwable exception);
    }

    static final String PREFIX = DiagramFactory.SERVER + "/ContentService/search/diagram/##pathway##/flag?query=##term##";

    private String term;
    private Boolean notify;

    private Handler handler;
    private Request request;

    public FlaggedElementsLoader(Handler handler) {
        this.handler = handler;
    }

    public void cancel() {
        if (request != null && request.isPending()) request.cancel();
        this.term = null;
        this.notify = null;
    }

    public void load(Content content, String term, boolean notify) {
        // Any previous request has to be canceled
        cancel();

        if (content == null) return;

        this.term = term;
        this.notify = notify;

        String url = PREFIX.replace("##pathway##", content.getStableId()).replace("##term##", term);
        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
        try {
            this.request = requestBuilder.sendRequest(null, this);
        } catch (RequestException e) {
            this.handler.onFlaggedElementsLoaderError(e);
        }
    }

    @Override
    public void onResponseReceived(Request request, Response response) {
        Occurrences toFlag = null;
        switch (response.getStatusCode()) {
            case Response.SC_OK:
                try {
                    toFlag = SearchResultFactory.getSearchObject(Occurrences.class, response.getText());
                    handler.flaggedElementsLoaded(term, toFlag, notify);
                } catch (SearchException ex) {
                    handler.onFlaggedElementsLoaderError(ex);
                }
                break;
            case Response.SC_NOT_FOUND:
                handler.flaggedElementsLoaded(term, null, notify);
                break;
            default:
                //TODO: Propagate the error from the response from the returned JSON instead?
                handler.onFlaggedElementsLoaderError(new Exception(ResponseUtils.getStatusText(response.getStatusCode())));
        }
    }

    @Override
    public void onError(Request request, Throwable exception) {
        handler.onFlaggedElementsLoaderError(exception);
    }
}
