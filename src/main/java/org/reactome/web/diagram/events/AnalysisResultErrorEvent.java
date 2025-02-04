package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.handlers.AnalysisResultErrorHandler;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class AnalysisResultErrorEvent extends GwtEvent<AnalysisResultErrorHandler> {
    public static Type<AnalysisResultErrorHandler> TYPE = new Type<AnalysisResultErrorHandler>();

    @Override
    public Type<AnalysisResultErrorHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AnalysisResultErrorHandler handler) {
        handler.onAnalysisResultError(this);
    }

    @Override
    public String toString() {
        return "AnalysisResultErrorEvent{}";
    }
}
