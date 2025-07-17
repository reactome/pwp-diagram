package org.reactome.web.diagram.data.interactors.raw;

import java.util.List;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface RawInteractorEntity {

    /**
     * Diagram entity identifier (UniProt, ChEBI)
     */
    String getAcc();

    /**
     * The total number of interactions for the given interactor
     */
    Integer getCount();

    /**
     * List of interactors for a given resource (specified in DiagramInteractors interface)
     */
    List<RawInteractor> getInteractors();
}
