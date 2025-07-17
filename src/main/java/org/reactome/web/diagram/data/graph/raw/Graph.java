package org.reactome.web.diagram.data.graph.raw;

import java.util.List;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface Graph {

    Long getDbId();

    String getStId();

    /**
     * The species name of the diagram. e.g. 'Homo sapiens'
     */
    String getSpeciesName();

    List<EntityNode> getNodes();

    List<EventNode> getEdges();

    List<SubpathwayNode> getSubpathways();

}
