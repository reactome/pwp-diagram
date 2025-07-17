package org.reactome.web.diagram.data.interactors.custom.raw;

import java.util.List;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public interface RawInteractorError {

    /**
     * The error code e/g 500 or 404
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

}
