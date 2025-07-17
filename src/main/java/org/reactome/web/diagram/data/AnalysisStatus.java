package org.reactome.web.diagram.data;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.URL;
import org.reactome.web.analysis.client.filter.ResultFilter;
import org.reactome.web.analysis.client.model.AnalysisSummary;
import org.reactome.web.analysis.client.model.AnalysisType;
import org.reactome.web.analysis.client.model.ExpressionSummary;
import org.reactome.web.diagram.events.ExpressionColumnChangedEvent;
import org.reactome.web.diagram.handlers.ExpressionColumnChangedHandler;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class AnalysisStatus implements ExpressionColumnChangedHandler {

    private String token;
    private ResultFilter filter;

    private Integer column;

    private AnalysisSummary analysisSummary;
    private ExpressionSummary expressionSummary;

    public AnalysisStatus(EventBus eventBus, String token, ResultFilter filter) {
        if(token == null || filter == null) throw new RuntimeException("token cannot be null");
        this.token = URL.decode(token);
        this.filter = filter;
        this.column = 0;
        eventBus.addHandler(ExpressionColumnChangedEvent.TYPE, this);
    }

    public String getToken() {
        return token;
    }

    public String getResource() {
        return filter.getResource();
    }

    public ResultFilter getFilter() {
        return filter;
    }

    public Integer getColumn() {
        return column;
    }

    public AnalysisType getAnalysisType(){
        return AnalysisType.getType(analysisSummary.getType());
    }

    public AnalysisSummary getAnalysisSummary() {
        return analysisSummary;
    }

    public boolean isEmpty() {
        return this.token == null || this.token.isEmpty();
    }

    @Override
    public void onExpressionColumnChanged(ExpressionColumnChangedEvent e) {
        this.column = e.getColumn();
    }

    public void setAnalysisSummary(AnalysisSummary analysisSummary) {
        this.analysisSummary = analysisSummary;
    }

    public ExpressionSummary getExpressionSummary() {
        return expressionSummary;
    }

    public void setExpressionSummary(ExpressionSummary expressionSummary) {
        this.expressionSummary = expressionSummary;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AnalysisStatus that = (AnalysisStatus) o;

        return !(token != null ? !token.equals(that.token) : that.token != null) &&
                !(filter != null ? !filter.equals(that.filter) : that.filter != null);

    }

    @Override
    public int hashCode() {
        int result = token != null ? token.hashCode() : 0;
        result = 31 * result + (filter != null ? filter.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AnalysisStatus{" +
                "token='" + token + '\'' +
                ", resource='" + filter.getResource() + '\'' +
                '}';
    }
}
