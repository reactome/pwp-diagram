package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.handlers.AnalysisResultRequestedHandler;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class AnalysisResultRequestedEvent extends GwtEvent<AnalysisResultRequestedHandler> {
    public static Type<AnalysisResultRequestedHandler> TYPE = new Type<AnalysisResultRequestedHandler>();

    private Long dbId;

    public AnalysisResultRequestedEvent(Long dbId) {
        this.dbId = dbId;
    }

    @Override
    public Type<AnalysisResultRequestedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AnalysisResultRequestedHandler handler) {
        handler.onAnalysisResultRequested(this);
    }

    public Long getDbId() {
        return dbId;
    }

    @Override
    public String toString() {
        return "AnalysisResultRequestedEvent{" +
                "dbId=" + dbId +
                '}';
    }
}
