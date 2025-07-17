package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.handlers.AnalysisProfileChangedHandler;
import org.reactome.web.diagram.profiles.analysis.model.AnalysisProfile;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class AnalysisProfileChangedEvent extends GwtEvent<AnalysisProfileChangedHandler> {
    public static Type<AnalysisProfileChangedHandler> TYPE = new Type<AnalysisProfileChangedHandler>();

    AnalysisProfile analysisProfile;

    public AnalysisProfileChangedEvent(AnalysisProfile analysisProfile) {
        this.analysisProfile = analysisProfile;
    }

    public AnalysisProfile getAnalysisProfile() {
        return analysisProfile;
    }

    @Override
    public Type<AnalysisProfileChangedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AnalysisProfileChangedHandler handler) {
        handler.onAnalysisProfileChanged(this);
    }

    @Override
    public String toString() {
        return "AnalysisProfileChangedEvent{" +
                "analysisProfile=" + analysisProfile.getName() +
                '}';
    }
}
