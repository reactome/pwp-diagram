package org.reactome.web.diagram.data.interactors.raw;

import java.util.List;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface RawInteractors {

    /**
     * It is the PSICQUIC resource (e.g. IntAct, MINT, etc)
     */
    String getResource();

    /**
     * The list of entities with their interactor for the diagram
     */
    List<RawInteractorEntity> getEntities();
}
