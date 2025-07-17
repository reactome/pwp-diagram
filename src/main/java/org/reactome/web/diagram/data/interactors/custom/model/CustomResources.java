package org.reactome.web.diagram.data.interactors.custom.model;

import java.util.List;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public interface CustomResources {

    List<CustomResource> getCustomResources();

    void setCustomResources(List<CustomResource> list);
}
