package org.reactome.web.diagram.data.graph.raw;

import java.util.List;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface EventNode extends GraphNode {

    List<Long> getPreceding();

    List<Long> getFollowing();

    List<Long> getInputs();

    List<Long> getOutputs();

    List<Long> getCatalysts();

    List<Long> getInhibitors();

    List<Long> getActivators();

    List<Long> getRequirements();

    List<Long> getDiagramIds();

}