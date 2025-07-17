package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.analysis.client.filter.ResultFilter;
import org.reactome.web.analysis.client.model.*;
import org.reactome.web.diagram.handlers.AnalysisResultLoadedHandler;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class AnalysisResultLoadedEvent extends GwtEvent<AnalysisResultLoadedHandler> {
    public static Type<AnalysisResultLoadedHandler> TYPE = new Type<>();

    private AnalysisSummary summary;
    private ExpressionSummary expressionSummary;
    private FoundElements foundElements;
    private List<PathwaySummary> pathwaySummaries;
    private ResultFilter filter;
    private long time;

    public AnalysisResultLoadedEvent(AnalysisSummary summary, ExpressionSummary expressionSummary, FoundElements foundElements, List<PathwaySummary> pathwaySummaries, ResultFilter filter, long time) {
        this.summary = summary;
        this.expressionSummary = expressionSummary;
        this.foundElements = foundElements;
        this.pathwaySummaries = pathwaySummaries == null ? new LinkedList<PathwaySummary>() : pathwaySummaries;
        this.filter = filter;
        this.time = time;
    }

    @Override
    public Type<AnalysisResultLoadedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AnalysisResultLoadedHandler handler) {
        handler.onAnalysisResultLoaded(this);
    }

    public FoundElements getFoundElements() {
        return foundElements;
    }

    public ExpressionSummary getExpressionSummary() {
        return expressionSummary;
    }

    public List<PathwaySummary> getPathwaySummaries() {
        return pathwaySummaries;
    }

    public AnalysisSummary getSummary() {
        return summary;
    }

    public AnalysisType getType() {
        return AnalysisType.getType(this.summary.getType());
    }

    public boolean isReset() {
        return summary == null;
    }

    public ResultFilter getFilter() {
        return filter;
    }

    public long getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "AnalysisResultLoadedEvent{" +
                "time=" + time +
                ", pathwayEntities=" + (foundElements != null ? foundElements.getEntities().size() : "null") +
                ", pathwayInteractors=" + (foundElements != null ? foundElements.getInteractors().size() : "null") +
                ", type=" + (summary != null ?  summary.getType() : "null") +
                ", resource=" + (foundElements != null ? foundElements.getResources() : "null") +
                '}';
    }
}
