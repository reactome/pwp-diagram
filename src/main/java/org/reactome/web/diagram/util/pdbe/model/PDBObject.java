package org.reactome.web.diagram.util.pdbe.model;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class PDBObject extends JavaScriptObject {

    protected PDBObject() {
    }

    public final native boolean isEmpty() /*-{
        return this.tax_id==undefined;
    }-*/;

    public final native int getTaxonomyId() /*-{
        return this.tax_id;
    }-*/;

    public final native String getCoverage() /*-{
        return "" + this.coverage;
    }-*/;

    public final native String getChain() /*-{
        return this.chain_id;
    }-*/;

    public final native String getPdbid() /*-{
        return this.pdb_id;
    }-*/;

    public final native String getExperimentalMethod() /*-{
        return this.experimental_method;
    }-*/;

    public final native String getResolution() /*-{
        return "" + this.resolution;
    }-*/;

//    public final native String getUniprotAcc() /*-{
//        return this.uniprot_acc;
//    }-*/;

    final native int getUniprot_Start() /*-{
        return this.unp_start;
    }-*/;

    final native int getUniprot_End() /*-{
        return this.unp_end;
    }-*/;

    public final Range getUniprotRange(){
        return new Range(getUniprot_Start(), getUniprot_End());
    }

    final native int getPdb_Start() /*-{
        return this.start;
    }-*/;

    final native int getPdb_End() /*-{
        return this.end;
    }-*/;

    public final Range getPdbRange(){
        return new Range(this.getPdb_Start(), this.getPdb_End());
    }
}