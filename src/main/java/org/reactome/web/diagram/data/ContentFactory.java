package org.reactome.web.diagram.data;

import org.reactome.web.diagram.data.content.Content;
import org.reactome.web.diagram.data.content.DiagramContent;
import org.reactome.web.diagram.data.content.EHLDContent;
import org.reactome.web.diagram.data.content.EHLDObject;
import org.reactome.web.diagram.data.graph.model.*;
import org.reactome.web.diagram.data.graph.raw.EntityNode;
import org.reactome.web.diagram.data.graph.raw.EventNode;
import org.reactome.web.diagram.data.graph.raw.Graph;
import org.reactome.web.diagram.data.graph.raw.SubpathwayNode;
import org.reactome.web.diagram.data.interactors.raw.RawInteractor;
import org.reactome.web.diagram.data.interactors.raw.RawInteractorEntity;
import org.reactome.web.diagram.data.interactors.raw.RawInteractors;
import org.reactome.web.diagram.data.layout.Diagram;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.util.Console;
import org.reactome.web.diagram.util.MapSet;
import org.reactome.web.diagram.util.svg.SVGUtil;
import org.vectomatic.dom.svg.OMElement;
import org.vectomatic.dom.svg.OMSVGSVGElement;

import java.util.*;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public abstract class ContentFactory {

    public static Content getContent(Diagram diagram) {
        DiagramContent content = new DiagramContent();

        //Read and set general pathway information
        content.setStableId(diagram.getStableId());
        content.setDisplayName(diagram.getDisplayName());
        content.setDbId(diagram.getDbId());
        content.setForNormalDraw(diagram.getForNormalDraw());
        content.setIsDisease(diagram.getIsDisease());

        content.cache(diagram.getNodes());
        content.cache(diagram.getNotes());
        content.cache(diagram.getEdges());
        content.cache(diagram.getLinks());
        content.cache(diagram.getCompartments());
        content.cache(diagram.getShadows());

        content.setMinX(diagram.getMinX().doubleValue());
        content.setMaxX(diagram.getMaxX().doubleValue());
        content.setMinY(diagram.getMinY().doubleValue());
        content.setMaxY(diagram.getMaxY().doubleValue());

        return content.init();
    }

    public static Content getContent(String stId, boolean isInDisease, OMSVGSVGElement svg) {
        EHLDContent content = new EHLDContent(svg);

        //Read and set general pathway information
        content.setStableId(stId);
        content.setIsDisease(isInDisease);

        //Create EHLDObjects to include in the content
        List<EHLDObject> pathwayNodes = new LinkedList<>();
        Set<String> aux = new HashSet<>();
        long id = 0L;
        for (OMElement child : SVGUtil.getAnnotatedOMElements(svg)) {
            String stID = SVGUtil.keepStableId(child.getId());
            if (aux.add(stID)) {
                pathwayNodes.add(new EHLDObject(id++, stID)); //just a placeholder
            }
        }

        content.cache(pathwayNodes);
        return content.init();
    }

    public static void fillGraphContent(Content content, Graph graph) {
        if (content instanceof DiagramContent) {
            fillGraphContent((DiagramContent) content, graph);
        } else if (content instanceof EHLDContent) {
            fillGraphContent((EHLDContent) content, graph);
        } else {
            Console.warn("Ooops. Don't know what to do with " + content.getClass().getSimpleName());
        }
    }

    private static void fillGraphContent(EHLDContent content, Graph graph) {
        GraphObjectFactory.content = content;
        content.setSpeciesName(graph.getSpeciesName());

        // !Important: In a normal diagram layout the DBID is
        // included in the layout JSON file. However, in an EHLD we have
        // to wait until the graph JSON arrived.
        content.setDbId(graph.getDbId());

        for (EntityNode node : graph.getNodes()) {
            GraphObjectFactory.getOrCreateDatabaseObject(node);
        }

        for (EntityNode node : graph.getNodes()) {
            GraphObject obj = content.getDatabaseObject(node.getDbId());
            if (obj instanceof GraphPathway) {
                GraphPathway pathway = (GraphPathway) obj;
                DiagramObject diagramObject = getDiagramObjectByStableId(node.getStId());
                if (diagramObject != null) {
                    pathway.addDiagramObject(diagramObject);
                    diagramObject.setGraphObject(pathway);
                }
            }
        }

    }

    private static void fillGraphContent(DiagramContent content, Graph graph) {
        GraphObjectFactory.content = content;
        content.setSpeciesName(graph.getSpeciesName());

        for (EntityNode node : graph.getNodes()) {
            GraphObjectFactory.getOrCreateDatabaseObject(node);
        }
        for (EventNode edge : graph.getEdges()) {
            GraphObjectFactory.getOrCreateDatabaseObject(edge);
        }

        for (EntityNode node : graph.getNodes()) {
            GraphObject obj = content.getDatabaseObject(node.getDbId());
            if (obj instanceof GraphPhysicalEntity) {
                GraphPhysicalEntity pe = (GraphPhysicalEntity) obj;
                for (DiagramObject diagramObject : getDiagramObjects(node.getDiagramIds())) {
                    pe.addDiagramObject(diagramObject);
                    diagramObject.setGraphObject(pe);
                }

                List<GraphPhysicalEntity> parents = getDatabaseObjects(node.getParents());
                pe.addParent(parents);

                List<GraphPhysicalEntity> children = getDatabaseObjects(node.getChildren());
                pe.addChildren(children);
            } else if (obj instanceof GraphPathway) {
                GraphPathway pathway = (GraphPathway) obj;
                for (DiagramObject diagramObject : getDiagramObjects(node.getDiagramIds())) {
                    pathway.addDiagramObject(diagramObject);
                    diagramObject.setGraphObject(pathway);
                }

                //TODO: Need to keep parents and/or children?
            }
        }

        for (EntityNode node : graph.getNodes()) {
            GraphObject obj = content.getDatabaseObject(node.getDbId());
            if (obj instanceof GraphComplex) {
                content.cacheParticipants((GraphPhysicalEntity) obj);
            }
        }

        for (EventNode edge : graph.getEdges()) {
            GraphReactionLikeEvent event = (GraphReactionLikeEvent) content.getDatabaseObject(edge.getDbId());

            for (DiagramObject diagramObject : getDiagramObjects(edge.getDiagramIds())) {
                event.addDiagramObject(diagramObject);
                diagramObject.setGraphObject(event);
            }

            List<GraphPhysicalEntity> inputs = getDatabaseObjects(edge.getInputs());
            event.setInputs(inputs);

            List<GraphPhysicalEntity> outputs = getDatabaseObjects(edge.getOutputs());
            event.setOutputs(outputs);

            List<GraphPhysicalEntity> catalysts = getDatabaseObjects(edge.getCatalysts());
            event.setCatalysts(catalysts);

            List<GraphPhysicalEntity> activators = getDatabaseObjects(edge.getActivators());
            event.setActivators(activators);

            List<GraphPhysicalEntity> inhibitors = getDatabaseObjects(edge.getInhibitors());
            event.setInhibitors(inhibitors);

            List<GraphPhysicalEntity> requirements = getDatabaseObjects(edge.getRequirements());
            event.setRequirements(requirements);

            List<GraphReactionLikeEvent> preceding = getDatabaseObjects(edge.getPreceding());
            event.setPrecedingEvents(preceding);

            List<GraphReactionLikeEvent> following = getDatabaseObjects(edge.getFollowing());
            event.setFollowingEvents(following);
        }

        if (graph.getSubpathways() != null) {
            for (SubpathwayNode subpathway : graph.getSubpathways()) {
                GraphSubpathway sp = GraphObjectFactory.getOrCreateDatabaseObject(subpathway);
                for (Long event : subpathway.getEvents()) {
                    sp.addContainedEvent((GraphEvent) content.getDatabaseObject(event));
                }
            }
        }
    }

    private static List<DiagramObject> getDiagramObjects(List<Long> ids) {
        List<DiagramObject> rtn = new ArrayList<>();
        if (ids != null) {
            for (Long id : ids) {
                rtn.add(GraphObjectFactory.content.getDiagramObject(id));
            }
        }
        return rtn;
    }

    private static DiagramObject getDiagramObjectByStableId(String stId) {
        DiagramObject rtn = null;
        if (stId != null) {
            rtn = GraphObjectFactory.content.getDiagramObject(stId);
        }
        return rtn;
    }

    private static <T extends GraphObject> List<T> getDatabaseObjects(List<Long> dbIds) {
        List<T> rtn = new ArrayList<>();
        if (dbIds != null) {
            for (Long dbId : dbIds) {
                //noinspection unchecked
                T t = (T) GraphObjectFactory.content.getDatabaseObject(dbId);
                if (t == null) {
                    Console.error("There is no information for " + dbId);
                } else {
                    rtn.add(t);
                }
            }
        }
        return rtn;
    }

    public static void fillInteractorsContent(Context context, RawInteractors rawInteractors) {
        InteractorsContent interactors = context.getInteractors();

        String resource = rawInteractors.getResource();
        //The next line creates an empty entry in the cache
        interactors.getOrCreateRawInteractorCachedResource(resource);

        MapSet<String, GraphObject> identifierMap = context.getContent().getIdentifierMap();
        List<RawInteractorEntity> entities = rawInteractors.getEntities() == null ? Collections.EMPTY_LIST : rawInteractors.getEntities();

        for (RawInteractorEntity interactorEntity : entities) {
            String acc = interactorEntity.getAcc();
            interactors.cacheInteractors(resource, acc, interactorEntity.getCount(), identifierMap);
            if (interactorEntity.getInteractors() != null && !interactorEntity.getInteractors().isEmpty()) {
                for (RawInteractor rawInteractor : interactorEntity.getInteractors()) {
                    interactors.cache(resource, acc, rawInteractor);
                }
            }
        }
    }
}
