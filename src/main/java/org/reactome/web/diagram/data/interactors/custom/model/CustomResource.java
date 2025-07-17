package org.reactome.web.diagram.data.interactors.custom.model;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public interface CustomResource {

    /**
     * The name of the user-defined resource
     */
    String getName();

    void setName(String name);

    /**
     * The token associated to this resource by the server
     */
    String getToken();

    void setToken(String token);

    /**
     * The name of the submitted file
     */
    String getFilename();

    void setFilename(String filename);
}
