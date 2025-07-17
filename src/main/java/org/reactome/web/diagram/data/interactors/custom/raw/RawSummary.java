package org.reactome.web.diagram.data.interactors.custom.raw;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public interface RawSummary {

    /**
     * The name of the file used during submission
     */
    String getFileName();

    /**
     * The total number of interactions for this custom resource
     */
    Integer getInteractions();

    /**
     * The total number of the unique interactors for this custom resource
     */
    Integer getInteractors();

    /**
     * The name selected for this custom resource by the user during submission
     */
    String getName();

    /**
     * The token automatically assigned to this resource during sumbission
     */
    String getToken();
}
