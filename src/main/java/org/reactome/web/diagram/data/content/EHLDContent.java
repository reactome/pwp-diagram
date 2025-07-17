package org.reactome.web.diagram.data.content;

import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPathway;
import org.reactome.web.diagram.data.graph.model.GraphSubpathway;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.util.MapSet;
import org.vectomatic.dom.svg.OMSVGSVGElement;
import uk.ac.ebi.pwp.structures.quadtree.client.Box;

import java.util.*;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class EHLDContent extends GenericContent {

    private Map<String, DiagramObject> tempDiagramObjectMap;
    private Map<Long, DiagramObject> diagramObjectMap;
    private Map<String, GraphObject> graphObjectCache;
//    private Map<String, GraphSubpathway> subpathwaysCache;
    private MapSet<String, GraphObject> identifierMap;
    private Set<GraphPathway> encapsulatedPathways;

    private OMSVGSVGElement svg;

    public EHLDContent(OMSVGSVGElement svg) {
        this.svg = svg;
        this.tempDiagramObjectMap = new HashMap<>();
        this.diagramObjectMap = new TreeMap<>();
        this.graphObjectCache = new HashMap<>();
        this.identifierMap = new MapSet<>();
        this.encapsulatedPathways = new HashSet<>();
    }

    @Override
    public Content init() {
        return this;
    }

    @Override
    public void cache(GraphObject dbObject) {
        this.graphLoaded = true;
        if (dbObject.getDbId() != null) {
            graphObjectCache.put(dbObject.getDbId() + "", dbObject);
        }
        if (dbObject.getStId() != null) {
            graphObjectCache.put(dbObject.getStId(), dbObject);
        }

        if (dbObject instanceof GraphPathway) {
            encapsulatedPathways.add((GraphPathway) dbObject);
        }
    }

    @Override
    public void cache(List<? extends DiagramObject> diagramObjects) {
        if (diagramObjects == null) return;
        for (DiagramObject diagramObject : diagramObjects) {
            EHLDObject ehldObject = (EHLDObject) diagramObject;
            diagramObjectMap.put(ehldObject.getId(), ehldObject);
            tempDiagramObjectMap.put(ehldObject.getStableId(), ehldObject);
        }
    }

    @Override
    public boolean containsOnlyEncapsulatedPathways() {
        return true;
    }

    @Override
    public boolean containsEncapsulatedPathways() {
        return true;
    }

    @Override
    public Collection<DiagramObject> getHoveredTarget(Coordinate p, double factor) {
        return Collections.EMPTY_LIST;
    }

    @Override
    public Set<GraphPathway> getEncapsulatedPathways() {
        return encapsulatedPathways;
    }

    @Override
    public GraphObject getDatabaseObject(String identifier) {
        return this.graphObjectCache.get(identifier);
    }

    @Override
    public GraphObject getDatabaseObject(Long dbId) {
        return this.graphObjectCache.get(dbId.toString());
    }

    @Override
    public GraphSubpathway getGraphSubpathway(String stId) {
        return null;
    }

    @Override
    public GraphSubpathway getGraphSubpathway(Long dbId) {
        return null;
    }

    @Override
    public Collection<GraphSubpathway> getSubpathways() {
        return new ArrayList<>();
    }

    @Override
    public Collection<GraphObject> getAllInvolvedPathways() {
        Set<GraphObject> rtn = new HashSet<>();
        rtn.addAll(encapsulatedPathways);
        rtn.addAll(getSubpathways());
        return rtn;
    }

    @Override
    public DiagramObject getDiagramObject(Long id) {
        return this.diagramObjectMap.get(id);
    }

    @Override
    public DiagramObject getDiagramObject(String stId) {
        return this.tempDiagramObjectMap.get(stId);
    }

    @Override
    public Collection<GraphObject> getDatabaseObjects() {
        return new HashSet<>(this.graphObjectCache.values());
    }

    @Override
    public Collection<DiagramObject> getDiagramObjects() {
        return this.diagramObjectMap.values();
    }

    @Override
    public MapSet<String, GraphObject> getIdentifierMap() {
        return identifierMap;
    }

    @Override
    public double getMinX() {
        return 0;
    }

    @Override
    public double getMaxX() {
        return 0;
    }

    @Override
    public double getMinY() {
        return 0;
    }

    @Override
    public double getMaxY() {
        return 0;
    }

    @Override
    public double getWidth() {
        return 0;
    }

    @Override
    public double getHeight() {
        return 0;
    }

    @Override
    public Collection<DiagramObject> getVisibleItems(Box visibleArea) {
        return null;
    }

    @Override
    public int getNumberOfBurstEntities() {
        return 0;
    }

    @Override
    public void clearDisplayedInteractors() {
        //Nothing Here
    }

    public OMSVGSVGElement getSVG() {
        return svg;
    }

    @Override
    public Type getType(){
        return Type.SVG;
    }
}
