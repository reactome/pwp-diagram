package org.reactome.web.diagram.search.results.data.model;

import java.util.List;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public interface SearchError {

    /**
     * The error code
     */
    Integer getCode();

    /**
     * The general reason of this error
     */
    String getReason();

    /**
     * A list of detailed errors
     */
    List<String> getMessages();

    /**
     * A list of terms that may be targets for annotation
     */
    List<TargetTerm> getTargets();
}
