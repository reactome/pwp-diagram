package org.reactome.web.diagram.search.results.local;

import com.google.gwt.http.client.*;
import com.google.gwt.safehtml.shared.UriUtils;
import org.reactome.web.diagram.client.DiagramFactory;
import org.reactome.web.diagram.search.results.data.SearchException;
import org.reactome.web.diagram.search.results.data.SearchResultFactory;
import org.reactome.web.diagram.search.results.data.model.Occurrences;
import org.reactome.web.diagram.search.results.data.model.SearchError;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public abstract class LocalOccurrencesFactory {

    private static final String URL = DiagramFactory.SERVER + "/ContentService/search/diagram/##DIAGRAM##/occurrences/##INSTANCE##";
    private static Request request;

    public interface Handler {
        void onOccurrencesSuccess(Occurrences occurrences);
        void onOccurrencesError(SearchError error);
        void onOccurrencesException(String msg);
    }

    public static void searchForInstanceInDiagram(final String instance, final String diagram, final Handler handler) {
        if (request != null && request.isPending()) {
            request.cancel();
        }

        String url = URL.replace("##DIAGRAM##", UriUtils.encode(diagram))
                        .replace("##INSTANCE##", UriUtils.encode(instance));

        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
        requestBuilder.setHeader("Accept", "application/json");
        try {
            request = requestBuilder.sendRequest(null, new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    switch (response.getStatusCode()){
                        case Response.SC_OK:
                            handler.onOccurrencesSuccess(getOccurrences(response.getText(), handler));
                            break;
                        default:
                            handler.onOccurrencesError(getError(response.getText(), handler));
                    }
                }
                @Override
                public void onError(Request request, Throwable exception) {
                    handler.onOccurrencesException(exception.getMessage());
                }
            });
        } catch (RequestException ex) {
            handler.onOccurrencesException(ex.getMessage());
        }
    }

    private static Occurrences getOccurrences(final String json, final Handler handler) {
        Occurrences rtn = null;
        try {
            rtn = SearchResultFactory.getSearchObject(Occurrences.class, json);
        } catch (SearchException ex) {
            handler.onOccurrencesException(ex.getMessage());
        }
        return rtn;
    }

    private static SearchError getError(final String json, final Handler handler) {
        SearchError rtn = null;
        try {
            rtn = SearchResultFactory.getSearchObject(SearchError.class, json);
        } catch (SearchException ex) {
            handler.onOccurrencesException(ex.getMessage());
        }
        return rtn;
    }
}
