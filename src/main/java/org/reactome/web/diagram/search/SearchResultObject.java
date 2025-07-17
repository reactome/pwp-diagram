package org.reactome.web.diagram.search;

import com.google.gwt.resources.client.ImageResource;
import org.reactome.web.pwp.model.client.factory.SchemaClass;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface SearchResultObject {

    ImageResource getImageResource();

    String getPrimarySearchDisplay();

    String getPrimaryTooltip();

    String getSecondarySearchDisplay();

    default String getTertiarySearchDisplay() {
        return "-";
    }

    void setSearchDisplay(SearchArguments arguments);

    SchemaClass getSchemaClass();

    default boolean isDisplayed() {
        return false;
    }
}
