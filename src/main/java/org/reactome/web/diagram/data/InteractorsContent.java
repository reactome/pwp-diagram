package org.reactome.web.diagram.data;

import org.reactome.web.analysis.client.model.FoundInteractor;
import org.reactome.web.analysis.client.model.IdentifierSummary;
import org.reactome.web.diagram.client.DiagramFactory;
import org.reactome.web.diagram.data.content.Content;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.data.interactors.common.InteractorsSummary;
import org.reactome.web.diagram.data.interactors.common.OverlayResource;
import org.reactome.web.diagram.data.interactors.model.DiagramInteractor;
import org.reactome.web.diagram.data.interactors.model.InteractorEntity;
import org.reactome.web.diagram.data.interactors.model.InteractorLink;
import org.reactome.web.diagram.data.interactors.model.InteractorSearchResult;
import org.reactome.web.diagram.data.interactors.raw.RawInteractor;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.Node;
import org.reactome.web.diagram.data.layout.SummaryItem;
import org.reactome.web.diagram.search.SearchResultObject;
import org.reactome.web.diagram.util.MapSet;
import org.reactome.web.pwp.model.client.util.LruCache;
import uk.ac.ebi.pwp.structures.quadtree.client.Box;
import uk.ac.ebi.pwp.structures.quadtree.client.QuadTree;

import java.util.*;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class InteractorsContent {

    static final int INTERACTORS_RESOURCE_CACHE_SIZE = 5;
    static final int INTERACTORS_FRAME_OFFSET = 1000;

    //The number of elements for every QuadTree quadrant node
    static final int NUMBER_OF_ELEMENTS = 25;
    //Quadrant minimum area (width * height):             180
    //  Right now an area of 180 x 80 = 14400 would     [--][--] 8
    //  host 4 entities of 90x40 each                   [--][--] 0
    //  An area of 60,000 includes 25 entities
    static final int MIN_AREA = 90000;

    static final double DEFAULT_SCORE = 0.45;

    static Map<String, Double> interactorsThreshold = new HashMap<>();

    private Map<String, MapSet<String, RawInteractor>> rawInteractorsCache; //resource -> node acc -> raw interactors
    private Map<String, IdentifierSummary> interactorsAnalysis; // interactor acc -> interactor analysis

    private MapSet<String, InteractorsSummary> interactorsSummaryMap; //resource -> InteractorsSummary
    private Map<String, Map<String, InteractorEntity>> interactorsCache; //resource -> interactor acc -> interactors
    private Map<String, MapSet<Node, InteractorLink>> interactionsPerNode; //resource -> layout node -> interaction

    private LruCache<String, QuadTree<DiagramInteractor>> interactorsTreeCache;
    private double minX, minY, maxX, maxY;

    public InteractorsContent(double minX, double minY, double maxX, double maxY) {
        this.rawInteractorsCache = new HashMap<>();
        this.interactorsSummaryMap = new MapSet<>();
        this.interactorsCache = new HashMap<>();
        this.interactionsPerNode = new HashMap<>();

        this.interactorsAnalysis = new HashMap<>();

        this.interactorsTreeCache = new LruCache<>(INTERACTORS_RESOURCE_CACHE_SIZE);
        this.minX = minX - INTERACTORS_FRAME_OFFSET;
        this.maxX = maxX + INTERACTORS_FRAME_OFFSET;
        this.minY = minY - INTERACTORS_FRAME_OFFSET;
        this.maxY = maxY + INTERACTORS_FRAME_OFFSET;
    }

    public void cache(String resource, String acc, RawInteractor rawInteractor) {
        IdentifierSummary identifier = interactorsAnalysis.get(rawInteractor.getAcc());
        if (identifier != null) {
            rawInteractor.setIsHit(true);
            rawInteractor.setExp(identifier.getExp());
        }
        getOrCreateRawInteractorCachedResource(resource).add(acc, rawInteractor);
    }

    public MapSet<String, RawInteractor> getOrCreateRawInteractorCachedResource(String resource) {
        MapSet<String, RawInteractor> map = rawInteractorsCache.get(resource);
        if (map == null) {
            map = new MapSet<>();
            rawInteractorsCache.put(resource, map);
        }
        return map;
    }

    public void cache(String resource, InteractorEntity interactor) {
        Map<String, InteractorEntity> map = interactorsCache.get(resource);
        if (map == null) {
            map = new HashMap<>();
            interactorsCache.put(resource, map);
        }
        map.put(interactor.getAccession(), interactor);
    }

    public void cache(String resource, Node node, InteractorLink link) {
        MapSet<Node, InteractorLink> cache = interactionsPerNode.get(resource);
        if (cache == null) {
            cache = new MapSet<>();
            interactionsPerNode.put(resource, cache);
        }
        cache.add(node, link);
    }

    public void setAnalysisResult(List<FoundInteractor> interactors) {
        clearAnalysisResults();
        if (interactors != null) {
            MapSet<String, RawInteractor> cache = rawInteractorsCache.get(DiagramFactory.INTERACTORS_INITIAL_RESOURCE);
            Map<String, InteractorEntity> map = interactorsCache.get(DiagramFactory.INTERACTORS_INITIAL_RESOURCE);

            for (FoundInteractor interactor : interactors) {
                if (cache != null) {
                    //If the interactors for the initial resource are present, then the analysis data is "injected"
                    for (String nodeAcc : interactor.getInteractsWith().getIds()) {
                        Set<RawInteractor> rawInteractors = cache.getElements(nodeAcc);
                        if (rawInteractors != null) {
                            for (RawInteractor rawInteractor : rawInteractors) {
                                if (interactor.getMapsTo().contains(rawInteractor.getAcc())) {
                                    rawInteractor.setIsHit(true);
                                    rawInteractor.setExp(interactor.getExp());
                                }
                            }
                        }
                    }
                } else {
                    //If the interactors for the initial resource are not present, caching is necessary so analysis
                    //data will be set once they are retrieved
                    for (String s : interactor.getMapsTo()) interactorsAnalysis.put(s, interactor);
                }

                if (map != null) {
                    //If there are any InteractorEntity for the initial resource, the analysis data is "injected"
                    for (String acc : interactor.getMapsTo()) {
                        InteractorEntity interactorEntity = map.get(acc);
                        if (interactorEntity != null) {
                            interactorEntity.setIsHit(true);
                            interactorEntity.setExp(interactor.getExp());
                        }
                    }
                }
            }
        }
    }

    public void cacheInteractors(String resource, String acc, Integer number, MapSet<String, GraphObject> identifierMap) {
        if (number == 0) return;
        Set<GraphObject> elements = identifierMap.getElements(acc);
        if (elements != null) {
            for (GraphObject graphObject : elements) {
                if (graphObject instanceof GraphPhysicalEntity) {
                    GraphPhysicalEntity pe = (GraphPhysicalEntity) graphObject;
                    for (DiagramObject diagramObject : pe.getDiagramObjects()) {
                        //TODO: Check what is going on here!
                        InteractorsSummary summary = new InteractorsSummary(acc, diagramObject.getId(), number);
                        interactorsSummaryMap.add(resource, summary);
                        Node node = (Node) diagramObject;
                        if (node.getInteractorsSummary() != null) {
                            node.getInteractorsSummary().setNumber(summary.getNumber());
                            node.getInteractorsSummary().setPressed(summary.isPressed());
                            //The changes need to be updated in the cache, so when restoring, the pressed ones are known
                            node.setDiagramEntityInteractorsSummary(summary);
                        }
                    }
                }
            }
        }
    }

    public void clearAnalysisResults() {
        interactorsAnalysis = new HashMap<>();
        MapSet<String, RawInteractor> cache = rawInteractorsCache.get(DiagramFactory.INTERACTORS_INITIAL_RESOURCE);
        if (cache != null) {
            for (RawInteractor rawInteractor : cache.values()) {
                rawInteractor.setIsHit(null);
                rawInteractor.setExp(null);
            }
        }
        Map<String, InteractorEntity> map = interactorsCache.get(DiagramFactory.INTERACTORS_INITIAL_RESOURCE);
        if (map != null) {
            for (InteractorEntity entity : map.values()) {
                entity.resetAnalysis();
            }
        }
    }

    //This method is not checking whether the interactors where previously put in place since
    //when it is called, the interactors have probably been retrieved "again" from the server
    //IMPORTANT: To avoid loading data that already exists -> CHECK BEFORE RETRIEVING :)
    public void addToView(String resource, DiagramInteractor interactor) {
        QuadTree<DiagramInteractor> tree = interactorsTreeCache.get(resource);
        if (tree == null) {
            tree = new QuadTree<>(minX, minY, maxX, maxY, NUMBER_OF_ELEMENTS, MIN_AREA);
            interactorsTreeCache.put(resource, tree);
        }
        tree.add(interactor);
    }

    public void updateView(String resource, DiagramInteractor interactor) {
        QuadTree<DiagramInteractor> tree = interactorsTreeCache.get(resource);
        if (tree != null) {
            tree.remove(interactor);
            tree.add(interactor);
        }
    }

    public void removeFromView(String resource, DiagramInteractor interactor) {
        QuadTree<DiagramInteractor> tree = interactorsTreeCache.get(resource);
        if (tree != null) tree.remove(interactor);
    }

    public void clearInteractors(String resource) {
        Map<String, InteractorEntity> entities = interactorsCache.get(resource);
        if (entities != null) {
            for (InteractorEntity entity : entities.values()) {
                entity.getLinks().clear();
            }
        }
        QuadTree<DiagramInteractor> tree = interactorsTreeCache.get(resource);
        if (tree != null) {
            tree.clear();
        }
        interactionsPerNode.remove(resource);
    }

    public void removeInteractorLink(String resource, InteractorLink link) {
        MapSet<Node, InteractorLink> cache = interactionsPerNode.get(resource);
        if (cache != null) {
            Set<InteractorLink> links = cache.getElements(link.getNodeFrom());
            if (links != null) links.remove(link);
        }
    }

    public List<InteractorLink> getInteractorLinks(String resource, Node node) {
        MapSet<Node, InteractorLink> cache = interactionsPerNode.get(resource);
        if (cache != null) {
            Set<InteractorLink> set = cache.getElements(node);
            if (set != null) {
                List<InteractorLink> rtn = new ArrayList<>(set);
                Collections.sort(rtn);
                return rtn;
            }
        }
        return new LinkedList<>();
    }

    public InteractorEntity getInteractorEntity(String resource, String acc) {
        Map<String, InteractorEntity> cache = interactorsCache.get(resource);
        if (cache != null) return cache.get(acc);
        return null;
    }


    public Collection<DiagramInteractor> getHoveredTarget(String resource, Coordinate p, double factor) {
        double f = 1 / factor;
        return getVisibleInteractors(resource, new Box(p.getX() - f, p.getY() - f, p.getX() + f, p.getY() + f));
    }

    //We keep this cache to avoid creating it every time
    private Map<String, List<InteractorSearchResult>> interactorsSearchItemsPerResource = new HashMap<>();
    public List<InteractorSearchResult> getInteractorSearchResult(OverlayResource resource, Content content) {
        // IMPORTANT: First check whether the rawInteractors have been loaded
        // If not then there is no point in searching for a term and caching the results
        MapSet<String, RawInteractor> map = rawInteractorsCache.get(resource.getIdentifier());
        if (map == null || map.isEmpty()) return new ArrayList<>();

        List<InteractorSearchResult> rtn = interactorsSearchItemsPerResource.get(resource.getIdentifier());
        if (rtn != null) return rtn;
        rtn = new ArrayList<>();
        Map<String, InteractorSearchResult> cache = new HashMap<>();
        for (String diagramAcc : map.keySet()) {
            for (RawInteractor rawInteractor : map.getElements(diagramAcc)) {
                String accession = rawInteractor.getAcc();

                // If the interactor is in the diagram we do not
                // present it as a separate result
                if (!map.keySet().contains(accession)) {
                    InteractorSearchResult result = cache.get(accession);
                    if (result == null) {
                        result = new InteractorSearchResult(resource, accession, rawInteractor.getAlias());
                        cache.put(accession, result);
                        rtn.add(result);
                    }
                    result.addInteractsWith(rawInteractor.getId(), getInteractsWith(diagramAcc, content));
                    result.addInteraction(rawInteractor);
                }
            }
        }
        interactorsSearchItemsPerResource.put(resource.getIdentifier(), rtn);
        return rtn;
    }

    private Set<GraphObject> getInteractsWith(String diagramAcc, Content content) {
        Set<GraphObject> aux = content.getIdentifierMap().getElements(diagramAcc);
        if (aux != null) return aux;
        return new HashSet<>();
    }

    public List<SearchResultObject> queryForInteractors(OverlayResource resource, Content content, String query) {
        List<SearchResultObject> rtn = new ArrayList<>();
        for (InteractorSearchResult obj : getInteractorSearchResult(resource, content)) {
            if (obj.containsTerm(query)) {
                rtn.add(obj);
            }
        }
        return rtn.isEmpty() ? null : rtn;
    }

    public List<RawInteractor> getRawInteractors(String resource, String acc) {
        List<RawInteractor> rtn = new ArrayList<>();
        MapSet<String, RawInteractor> map = rawInteractorsCache.get(resource);
        if (map != null) {
            Set<RawInteractor> set = map.getElements(acc);
            if (set != null) {
                rtn.addAll(set);
                Collections.sort(rtn, (o1, o2) -> {
                    int c = Double.compare(o2.getScore(), o1.getScore());
                    if (c == 0) return o1.getAcc().compareTo(o2.getAcc());
                    return c;
                });
            }
        }
        return rtn;
    }

    public MapSet<String, RawInteractor> getRawInteractorsPerResource(String resource) {
        return rawInteractorsCache.get(resource);
    }

    public int getUniqueRawInteractorsCountPerResource(String resource) {
        Set<String> superSet = new HashSet<>();
        MapSet<String, RawInteractor> rawMap = getRawInteractorsPerResource(resource);
        if (rawMap != null) {
            for (String key : rawMap.keySet()) {
                for (RawInteractor interactor : rawMap.getElements(key)) {
                    superSet.add(interactor.getAcc());
                }
            }
        }
        return superSet.size();
    }

    public boolean isResourceLoaded(String resource) {
        return rawInteractorsCache.keySet().contains(resource);
    }

    public Set<String> getLoadedResources() {
        return rawInteractorsCache.keySet();
    }

    public Collection<DiagramInteractor> getVisibleInteractors(String resource, Box visibleArea) {
        Set<DiagramInteractor> rtn = new HashSet<>();
        if (resource != null) {
            QuadTree<DiagramInteractor> quadTree = interactorsTreeCache.get(resource);
            if (quadTree != null) rtn = quadTree.getItems(visibleArea);
        }
        return rtn;
    }

    public boolean isInteractorResourceCached(String resource) {
        return interactorsSummaryMap.keySet().contains(resource);
    }

    public void resetBurstInteractors(String resource, Collection<DiagramObject> diagramObjects) {
        Set<InteractorsSummary> summaries = interactorsSummaryMap.getElements(resource);
        if (summaries != null) {
            for (InteractorsSummary summary : summaries) {
                summary.setPressed(false);
            }
        }
        for (DiagramObject diagramObject : diagramObjects) {
            if (diagramObject instanceof Node) {
                Node node = (Node) diagramObject;
                SummaryItem summaryItem = node.getInteractorsSummary();
                if (summaryItem != null) {
                    summaryItem.setPressed(null);
                }
            }
        }
    }

    public void restoreInteractorsSummary(String resource, Content content) {
        Set<InteractorsSummary> items = interactorsSummaryMap.getElements(resource);
        if (items == null) return;
        for (InteractorsSummary summary : items) {
            Node node = (Node) content.getDiagramObject(summary.getDiagramId());
            //The node can be "inside" a process node (subpathway)
            if (node != null) {
                node.getInteractorsSummary().setNumber(summary.getNumber());
                node.getInteractorsSummary().setPressed(summary.isPressed());
                //The changes need to be updated in the cache, so when restoring, the pressed ones are known
                node.setDiagramEntityInteractorsSummary(summary);
            }
        }
    }

    public static double getInteractorsThreshold(String resource) {
        Double threshold = interactorsThreshold.get(resource);
        if (threshold == null) {
            threshold = DEFAULT_SCORE;
            setInteractorsThreshold(resource, threshold);
        }
        return threshold;
    }

    public static void setInteractorsThreshold(String resource, double threshold) {
        interactorsThreshold.put(resource, threshold);
    }
}
