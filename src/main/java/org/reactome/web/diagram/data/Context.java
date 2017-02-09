package org.reactome.web.diagram.data;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import org.reactome.web.analysis.client.model.*;
import org.reactome.web.diagram.context.ContextDialogPanel;
import org.reactome.web.diagram.data.content.Content;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPathway;
import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.Node;
import org.reactome.web.diagram.data.layout.SummaryItem;
import org.reactome.web.diagram.renderers.common.ColourProfileType;
import org.reactome.web.diagram.util.MapSet;
import uk.ac.ebi.pwp.structures.quadtree.client.Box;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The context is kept in an LruCache so previously loaded information (content and status) is kept
 * and presented back to the user in the 'near' future.
 *
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class Context {
    public static final double ANALYSIS_MIN_PERCENTAGE = 0.03;

    private DiagramStatus diagramStatus;
    private AnalysisStatus analysisStatus;

    private Content content;
    private InteractorsContent interactors;

    private Map<GraphObject, ContextDialogPanel> dialogMap = new HashMap<>();

    public Context(Content content) {
        //Status needs to be created every time we load a new content
        this.diagramStatus = new DiagramStatus();

        this.content = content; //created and initialised by the DiagramContentFactory
        this.interactors = new InteractorsContent(content.getMinX(), content.getMinY(), content.getMaxX(), content.getMaxY());
    }

    public void clearAnalysisOverlay() {
        analysisStatus = null;
        for (GraphObject graphObject : content.getDatabaseObjects()) {
            if (graphObject instanceof GraphPhysicalEntity) {
                ((GraphPhysicalEntity) graphObject).resetHit();
                for (DiagramObject diagramObject : graphObject.getDiagramObjects()) {
                    Node node = (Node) diagramObject;
                    SummaryItem summaryItem = node.getInteractorsSummary();
                    if (summaryItem != null) {
                        summaryItem.setHit(null);
                    }
                }
            } else if (graphObject instanceof GraphPathway) {
                ((GraphPathway) graphObject).resetHit();
            }
        }
    }

    public void setAnalysisOverlay(AnalysisStatus analysisStatus, FoundElements foundElements, List<PathwaySummary> pathwaySummaries) {
        this.analysisStatus = analysisStatus;
        MapSet<String, GraphObject> map = this.content.getIdentifierMap();
        if (foundElements != null && foundElements.getEntities() != null) {
            for (FoundEntity entity : foundElements.getEntities()) {
                for (IdentifierMap identifierMap : entity.getMapsTo()) {
                    for (String id : identifierMap.getIds()) {
                        Set<GraphObject> elements = map.getElements(id);
                        if (elements == null) continue;
                        for (GraphObject graphObject : elements) {
                            if (graphObject instanceof GraphPhysicalEntity) {
                                GraphPhysicalEntity pe = (GraphPhysicalEntity) graphObject;
                                pe.setIsHit(entity.getId(), entity.getExp());
                            }
                        }
                    }
                }
            }
        }
        if (pathwaySummaries != null) {
            for (PathwaySummary pathwaySummary : pathwaySummaries) {
                EntityStatistics statistics = pathwaySummary.getEntities();
                if (statistics.getFound() > 0) {
                    //In this case process nodes DO NOT HAVE an identifier, but DO NOT use null here! use empty string
                    GraphPathway pathway = (GraphPathway) this.content.getDatabaseObject(pathwaySummary.getDbId());
                    Double percentage = statistics.getFound() / statistics.getTotal().doubleValue();
                    if (percentage < ANALYSIS_MIN_PERCENTAGE) percentage = ANALYSIS_MIN_PERCENTAGE;
                    pathway.setIsHit(percentage, pathwaySummary.getEntities().getExp(), statistics);
                }
            }
        }
    }

    public AnalysisStatus getAnalysisStatus() {
        return analysisStatus;
    }

    public Content getContent() {
        return content;
    }

    public InteractorsContent getInteractors() {
        return interactors;
    }

    public DiagramStatus getDiagramStatus() {
        return diagramStatus;
    }

    public Box getVisibleModelArea(int width, int height) {
        return this.diagramStatus.getVisibleModelArea(width, height);
    }

    public ColourProfileType getColourProfileType() {
        if (content.getForNormalDraw() == null || content.getForNormalDraw()) {
            return ColourProfileType.NORMAL;
        } else {
            return ColourProfileType.FADE_OUT;
        }
    }

    public void hideDialogs() {
        for (ContextDialogPanel dialogPanel : dialogMap.values()) {
            dialogPanel.hide();
        }
    }

    public void restoreDialogs() {
        for (ContextDialogPanel dialogPanel : dialogMap.values()) {
            dialogPanel.restore();
        }
    }

    public void showDialog(EventBus eventBus, DiagramObject item, Widget canvas) {
        if (item == null) return;
        if (!dialogMap.containsKey(item.getGraphObject())) {
            dialogMap.put(item.getGraphObject(), new ContextDialogPanel(eventBus, item, this, canvas));
        } else {
            dialogMap.get(item.getGraphObject()).show(true);
        }
    }

    @Override
    public String toString() {
        return "DiagramContext{" +
                "content=" + content.getType() +
                ", status=" + diagramStatus +
                '}';
    }
}
