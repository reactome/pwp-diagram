package org.reactome.web.diagram.data.content;

import org.reactome.web.analysis.client.model.EntityStatistics;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPathway;
import org.reactome.web.diagram.data.graph.model.GraphSubpathway;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.util.MapSet;
import uk.ac.ebi.pwp.structures.quadtree.client.Box;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public interface Content {

    enum Type {DIAGRAM, SVG}

    Content init();

    void cache(GraphObject dbObject);

    void cache(List<? extends DiagramObject> diagramObjects);

    boolean containsOnlyEncapsulatedPathways();

    boolean containsEncapsulatedPathways();

    boolean isGraphLoaded();

    Long getDbId();

    Collection<DiagramObject> getHoveredTarget(Coordinate p, double factor);

    String getStableId();

    String getDisplayName();

    String getSpeciesName();

    Set<GraphPathway> getEncapsulatedPathways();

    Boolean getIsDisease();

    Boolean getForNormalDraw();

    GraphObject getDatabaseObject(String identifier);

    GraphObject getDatabaseObject(Long dbId);

    GraphSubpathway getGraphSubpathway(String stId);

    GraphSubpathway getGraphSubpathway(Long dbId);

    Collection<GraphSubpathway> getSubpathways();

    /***
     * Returns all the subpathway components of a given diagram, including
     * pathway nodes (ugly green boxes and subpathways.
     */
    Collection<GraphObject> getAllInvolvedPathways();

    DiagramObject getDiagramObject(Long id);

    DiagramObject getDiagramObject(String id);

    Collection<GraphObject> getDatabaseObjects();

    Collection<DiagramObject> getDiagramObjects();

    MapSet<String, GraphObject> getIdentifierMap();

    double getMinX();

    double getMaxX();

    double getMinY();

    double getMaxY();

    double getWidth();

    double getHeight();

    void setDbId(Long dbId);

    void setStableId(String stableId);

    void setDisplayName(String displayName);

    void setSpeciesName(String speciesName);

    void setIsDisease(Boolean isDisease);

    void setForNormalDraw(Boolean forNormalDraw);

    void setMinX(double minX);

    void setMaxX(double maxX);

    void setMinY(double minY);

    void setMaxY(double maxY);

    Collection<DiagramObject> getVisibleItems(Box visibleArea);

    int getNumberOfBurstEntities();

    void clearDisplayedInteractors();

    Type getType();

    EntityStatistics getStatistics();

    void setStatistics(EntityStatistics statistics);
}
