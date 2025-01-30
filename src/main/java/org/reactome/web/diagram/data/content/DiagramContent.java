package org.reactome.web.diagram.data.content;

import org.reactome.web.diagram.data.graph.model.*;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.Node;
import org.reactome.web.diagram.data.layout.SummaryItem;
import org.reactome.web.diagram.util.MapSet;
import uk.ac.ebi.pwp.structures.quadtree.client.Box;
import uk.ac.ebi.pwp.structures.quadtree.client.QuadTree;

import java.util.*;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class DiagramContent extends GenericContent {

    //The number of elements for every QuadTree quadrant node
    private static final int NUMBER_OF_ELEMENTS = 25;
    //Quadrant minimum area (width * height):             180
    //  Right now an area of 180 x 80 = 14400 would     [--][--] 8
    //  host 4 entities of 90x40 each                   [--][--] 0
    //  An area of 90,000 includes 25 entities
    private static final int MIN_AREA = 90000;

    private Map<Long, DiagramObject> diagramObjectMap;
    private Map<String, GraphObject> graphObjectCache;
    private Map<String, GraphSubpathway> subpathwaysCache;
    private MapSet<String, GraphObject> identifierMap;
    private Set<GraphPathway> encapsulatedPathways;

    private QuadTree<DiagramObject> diagramObjects;

    public DiagramContent() {
        this.diagramObjectMap = new TreeMap<>();
        this.graphObjectCache = new HashMap<>();
        this.identifierMap = new MapSet<>();
        this.encapsulatedPathways = new HashSet<>();
        this.subpathwaysCache = new HashMap<>();
    }

    //Please note that the way the content is created is by injecting the values without the constructor
    //init has to be called to be called once every value has been set up
    public Content init(){
        this.diagramObjects = new QuadTree<>(minX, minY, maxX, maxY, NUMBER_OF_ELEMENTS, MIN_AREA);
        for (DiagramObject diagramObject : getDiagramObjects()) {
            this.diagramObjects.add(diagramObject);
        }
        return this;
    }

    public void cacheParticipants(GraphPhysicalEntity dbObject) {
        Set<GraphPhysicalEntity> participants = dbObject.getParticipants();
        if (participants.size() == 1) {
            for (GraphPhysicalEntity participant: participants) {
                identifierMap.add(participant.getIdentifier(), dbObject);
            }
        }
    }

    public void cache(GraphObject dbObject) {
        this.graphLoaded = true;
        if (dbObject.getDbId() != null) {
            graphObjectCache.put(dbObject.getDbId() + "", dbObject);
        }
        if (dbObject.getStId() != null) {
            graphObjectCache.put(dbObject.getStId(), dbObject);
        }

        if (dbObject instanceof GraphPhysicalEntity) {
            GraphPhysicalEntity pe = (GraphPhysicalEntity) dbObject;
            if (pe.getIdentifier() != null) {
                identifierMap.add(pe.getIdentifier(), dbObject);
            }
            if (pe.getGeneNames() != null) {
                for (String gene : pe.getGeneNames()) {
                    identifierMap.add(gene, dbObject);
                }
            }
        } else if (dbObject instanceof GraphPathway) {
            encapsulatedPathways.add((GraphPathway) dbObject);
        }

        if (dbObject instanceof GraphSubpathway) {
            GraphSubpathway gsp = (GraphSubpathway) dbObject;
            this.subpathwaysCache.put("" + gsp.getDbId(), gsp);
            this.subpathwaysCache.put(gsp.getStId(), gsp);
        }
    }

    public void cache(List<? extends DiagramObject> diagramObjects) {
        if (diagramObjects == null) return;
        for (DiagramObject diagramObject : diagramObjects) {
            this.diagramObjectMap.put(diagramObject.getId(), diagramObject);
        }
    }

    public boolean containsOnlyEncapsulatedPathways() {
        return (getDatabaseObjects().size() == encapsulatedPathways.size());
    }

    public boolean containsEncapsulatedPathways() {
        return encapsulatedPathways.size() > 0;
    }

    public Collection<DiagramObject> getHoveredTarget(Coordinate p, double factor) {
        double f = 1 / factor;
        return diagramObjects.getItems(new Box(p.getX() - f, p.getY() - f, p.getX() + f, p.getY() + f));
    }

    public Set<GraphPathway> getEncapsulatedPathways() {
        return encapsulatedPathways;
    }

    public GraphObject getDatabaseObject(String identifier) {
        return this.graphObjectCache.get(identifier);
    }

    public GraphObject getDatabaseObject(Long dbId) {
        return this.graphObjectCache.get(dbId.toString());
    }

    public GraphSubpathway getGraphSubpathway(String stId) {
        return this.subpathwaysCache.get(stId);
    }

    public GraphSubpathway getGraphSubpathway(Long dbId) {
        return getGraphSubpathway(dbId.toString());
    }

    @Override
    public Collection<GraphSubpathway> getSubpathways() {
        Set<GraphSubpathway> rtn = new HashSet<>();
        if (subpathwaysCache != null) {
            rtn.addAll(subpathwaysCache.values());
        }
        return rtn;
    }

    @Override
    public Collection<GraphObject> getAllInvolvedPathways() {
        Set<GraphObject> rtn = new HashSet<>();
        rtn.addAll(encapsulatedPathways);
        rtn.addAll(getSubpathways());
        return rtn;
    }

    public DiagramObject getDiagramObject(Long id) {
        return this.diagramObjectMap.get(id);
    }

    public DiagramObject getDiagramObject(String id) {
        return null;
    }

    public Collection<GraphObject> getDatabaseObjects() {
        return new HashSet<>(this.graphObjectCache.values());
    }

    public Collection<DiagramObject> getDiagramObjects() {
        return this.diagramObjectMap.values();
    }

    public MapSet<String, GraphObject> getIdentifierMap() {
        return identifierMap;
    }

    public Collection<DiagramObject> getVisibleItems(Box visibleArea) {
        return this.diagramObjects.getItems(visibleArea);
    }

    public int getNumberOfBurstEntities() {
        int n = 0;
        for (DiagramObject diagramObject : getDiagramObjects()) {
            if(diagramObject instanceof Node){
                Node node = (Node) diagramObject;
                SummaryItem summaryItem = node.getInteractorsSummary();
                if(summaryItem!=null){
                    if(summaryItem.getPressed()!=null && summaryItem.getPressed()) n++;
                }
            }
        }
        return n;
    }

    public void clearDisplayedInteractors() {
        for (DiagramObject diagramObject : getDiagramObjects()) {
            if (diagramObject instanceof Node) {
                Node node = (Node) diagramObject;
                SummaryItem interactorsSummary = node.getInteractorsSummary();
                if (interactorsSummary != null) {
                    interactorsSummary.setNumber(null);
                    interactorsSummary.setPressed(null);
                }
                node.setDiagramEntityInteractorsSummary(null);
            }
        }
    }

    @Override
    public Type getType(){
        return Type.DIAGRAM;
    }

    @Override
    public String toString() {
        return "DiagramContent{" +
                "stableId='" + stableId + '\'' +
                ", displayName='" + displayName + '\'' +
                '}';
    }
}
