package org.reactome.web.diagram.data.graph.raw;

import java.util.List;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public interface EntityNode extends GraphNode {

    List<Long> getParents();

    List<Long> getChildren();

    String getIdentifier();

    List<String> getGeneNames();

    List<Long> getDiagramIds();

    String getReferenceType();
}
