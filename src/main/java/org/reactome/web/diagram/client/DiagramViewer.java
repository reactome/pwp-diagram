package org.reactome.web.diagram.client;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;
import org.reactome.web.analysis.client.filter.ResultFilter;
import org.reactome.web.diagram.handlers.*;
import org.reactome.web.diagram.search.handlers.SearchPerformedHandler;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public interface DiagramViewer extends IsWidget, HasHandlers, RequiresResize {

    HandlerRegistration addSearchPerformedHandler(SearchPerformedHandler handler);

    HandlerRegistration addAnalysisResetHandler(AnalysisResetHandler handler);

    HandlerRegistration addCanvasNotSupportedEventHandler(CanvasNotSupportedHandler handler);

    HandlerRegistration addDatabaseObjectSelectedHandler(GraphObjectSelectedHandler handler);

    HandlerRegistration addDatabaseObjectHoveredHandler(GraphObjectHoveredHandler handler);

    HandlerRegistration addDiagramLoadedHandler(ContentLoadedHandler handler);

    HandlerRegistration addDiagramObjectsFlaggedHandler(DiagramObjectsFlaggedHandler handler);

    HandlerRegistration addDiagramObjectsFlagResetHandler(DiagramObjectsFlagResetHandler handler);

    HandlerRegistration addFireworksOpenedHandler(FireworksOpenedHandler handler);

    HandlerRegistration addInteractorHoveredHandler(InteractorHoveredHandler handler);

    HandlerRegistration addDiagramProfileChangedHandler(DiagramProfileChangedHandler handler);

    HandlerRegistration addAnalysisProfileChangedHandler(AnalysisProfileChangedHandler handler);

    void flagItems(String identifier, Boolean includeInteractors);

    void highlightItem(String stableIdentifier);

    void highlightItem(Long dbIdentifier);

    void loadDiagram(String stId);

    void loadDiagram(Long dbId);

    void resetAnalysis();

    void resetFlaggedItems();

    void resetHighlight();

    void resetSelection();

    void selectItem(String stableIdentifier);

    void selectItem(Long dbIdentifier);

    void setAnalysisToken(String token, ResultFilter filter);

    void setVisible(boolean visible);

}
