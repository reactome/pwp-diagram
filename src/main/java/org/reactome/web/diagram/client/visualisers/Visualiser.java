package org.reactome.web.diagram.client.visualisers;

import com.google.gwt.user.client.ui.IsWidget;
import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.interactors.common.OverlayResource;
import org.reactome.web.diagram.data.interactors.model.DiagramInteractor;
import org.reactome.web.diagram.data.layout.DiagramObject;

import java.util.Set;


/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public interface Visualiser extends IsWidget {
    //ControlActions
    void fitDiagram(boolean animation);

    void zoomDelta(double deltaFactor);

    void zoomIn();

    void zoomOut();

    void padding(int dX, int dY);

    void exportView();

    void contentLoaded(Context context);

    void contentRequested();

    boolean highlightGraphObject(GraphObject graphObject, boolean notify);

    void highlightInteractor(DiagramInteractor diagramInteractor);

    boolean resetHighlight(boolean notify);

    boolean resetSelection(boolean notify);

    boolean selectGraphObject(GraphObject graphObject, boolean notify);

    GraphObject getSelected();

    void loadAnalysis();

    void resetAnalysis();

    void setContext(final Context context);

    void resetContext();

    void expressionColumnChanged();

    void interactorsCollapsed(String resource);

    void interactorsFiltered();

    void interactorsLayoutUpdated();

    void interactorsLoaded();

    void interactorsResourceChanged(OverlayResource resource);

    void setSize(int width, int height);

    void flagItems(Set<DiagramObject> flaggedItems, Boolean includeInteractors);

    void resetFlag();
}
