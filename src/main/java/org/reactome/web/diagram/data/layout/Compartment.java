package org.reactome.web.diagram.data.layout;

import java.util.List;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface Compartment extends NodeCommon {

    List<Long> getComponentIds();

}
