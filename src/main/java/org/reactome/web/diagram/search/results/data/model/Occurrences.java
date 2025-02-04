package org.reactome.web.diagram.search.results.data.model;

import java.util.List;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public interface Occurrences {

    boolean getInDiagram();

    List<String> getOccurrences();

    List<String> getInteractsWith();
}
