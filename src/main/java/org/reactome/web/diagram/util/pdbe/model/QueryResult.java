package org.reactome.web.diagram.util.pdbe.model;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class QueryResult extends JavaScriptObject {

    protected QueryResult() {
    }

    public static native QueryResult buildQueryResult(String json) /*-{
        return eval('(' + json + ')');
    }-*/;

    public final native JsArray<PDBObject> getPDBObject(String accession) /*-{
        if(Object.keys(this).length === 0) return [];
        return this[accession];
    }-*/;

}
