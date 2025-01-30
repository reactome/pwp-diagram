package org.reactome.web.diagram.search.facets;

import com.google.gwt.http.client.*;
import org.reactome.web.diagram.client.DiagramFactory;
import org.reactome.web.diagram.search.SearchArguments;
import org.reactome.web.diagram.search.results.data.SearchException;
import org.reactome.web.diagram.search.results.data.SearchResultFactory;
import org.reactome.web.diagram.search.results.data.model.SearchSummary;
import org.reactome.web.diagram.util.Console;
import org.reactome.web.pwp.model.client.util.ResponseUtils;

/**
 *
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public abstract class SearchSummaryFactory {
    private static final String BASE_URL = DiagramFactory.SERVER + "/ContentService/search/diagram/summary?query=##QUERY##&species=##SPECIES##&diagram=##DIAGRAM##";
    private static Request request;

    public interface Handler {
        void onSearchSummaryReceived(SearchSummary summary);
        void onSearchSummaryError(String msg);
    }

    public static void queryForSummary(final SearchArguments arguments, final Handler handler){
        if (request != null && request.isPending()) {
            request.cancel();
        }

        String url = BASE_URL.replace("##QUERY##", URL.encode(arguments.getQuery()))
                             .replace("##SPECIES##", arguments.getSpecies())
                             .replace("##DIAGRAM##", arguments.getDiagramStId());

        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
        requestBuilder.setHeader("Accept", "application/json");

        try {
            request = requestBuilder.sendRequest(null, new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    switch (response.getStatusCode()){
                        case Response.SC_OK:
                            handler.onSearchSummaryReceived(getSummary(response.getText(), handler));
                            break;
                        default:
                            handler.onSearchSummaryError(ResponseUtils.getStatusText(response.getStatusCode()));
                    }
                }
                @Override
                public void onError(Request request, Throwable exception) {
                    Console.error(exception.getCause());
                    handler.onSearchSummaryError(exception.getMessage());
                }
            });
        } catch (RequestException ex) {
            handler.onSearchSummaryError(ex.getMessage());
        }
    }

    private static SearchSummary getSummary(final String json, final Handler handler) {
        SearchSummary rtn = null;
        try {
            rtn = SearchResultFactory.getSearchObject(SearchSummary.class, json);
        } catch (SearchException ex) {
            handler.onSearchSummaryError(ex.getMessage());
        }
        return rtn;
    }
}
