package org.reactome.web.diagram.context.popups.export;

import com.google.gwt.resources.client.ImageResource;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public interface DownloadType {

    String getName();

    String getTooltip();

    String getTemplateURL();

    ImageResource getIcon();

    default String getInfo() {
        return null;
    }

    default boolean hasQualityOptions() {
        return false;
    }
}
