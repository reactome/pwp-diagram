package org.reactome.web.diagram.search.results.data.model;

import java.util.List;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public interface SearchResult {

    List<Entry> getEntries();

    Integer getFound();

    List<FacetContainer> getFacets();
}
