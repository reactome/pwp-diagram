package org.reactome.web.diagram.data.loader;

import com.google.gwt.http.client.*;

import java.util.HashSet;
import java.util.Set;


/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public abstract class AnalysisTokenValidator {

    private static final Set<String> validTokens = new HashSet<>();

    public interface TokenAvailabilityHandler {
        void onTokenAvailabilityChecked(boolean available, String message);
    }

    public static void addValidToken(String token){
        if(token!=null && !token.isEmpty()) validTokens.add(token);
    }

    public static void checkTokenAvailability(final String token, final TokenAvailabilityHandler handler) {
        if (token == null || validTokens.contains(token)) { //YES, a null token is valid (it means there is not analysis overlay))
            handler.onTokenAvailabilityChecked(true, null);
        } else {
            String url = AnalysisDataLoader.PREFIX + token + "?pageSize=0&page=1";
            RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
            try {
                requestBuilder.sendRequest(null, new RequestCallback() {
                    @Override
                    public void onResponseReceived(Request request, Response response) {
                        switch (response.getStatusCode()) {
                            case Response.SC_OK:
                                addValidToken(token);
                                handler.onTokenAvailabilityChecked(true, null);
                                break;
                            case Response.SC_GONE:
                                handler.onTokenAvailabilityChecked(false, "Your analysis result may have been deleted due to a new content release.\n" +
                                                                          "Please submit your data again to obtain results from the latest version of our database");
                                break;
                            default:
                                handler.onTokenAvailabilityChecked(false, "There is no result associated with the provided token (in the url) from a previous analysis");
                        }
                    }

                    @Override
                    public void onError(Request request, Throwable exception) {
                        handler.onTokenAvailabilityChecked(false, "An error happened while checking the analysis results availability");
                    }
                });
            } catch (RequestException ex) {
                handler.onTokenAvailabilityChecked(false, "Could not connect to the server to check the analysis results availability");
            }
        }
    }
}
