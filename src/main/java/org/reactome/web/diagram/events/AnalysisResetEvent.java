package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.handlers.AnalysisResetHandler;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class AnalysisResetEvent extends GwtEvent<AnalysisResetHandler> {
    public static Type<AnalysisResetHandler> TYPE = new Type<AnalysisResetHandler>();

    private boolean fireExternally;

    public AnalysisResetEvent(){
        this(true);
    }

    public AnalysisResetEvent(boolean fireExternally) {
        this.fireExternally = fireExternally;
    }

    @Override
    public Type<AnalysisResetHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AnalysisResetHandler handler) {
        handler.onAnalysisReset(this);
    }

    public boolean getFireExternally() {
        return fireExternally;
    }

    @Override
    public String toString() {
        return "AnalysisResetEvent{" +
                "fireExternally=" + fireExternally +
                '}';
    }
}
