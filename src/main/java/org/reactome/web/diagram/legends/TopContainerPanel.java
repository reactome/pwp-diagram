package org.reactome.web.diagram.legends;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class TopContainerPanel extends FlowPanel {

    public TopContainerPanel() {
        setStyleName(RESOURCES.getCSS().container());
    }

    public static Resources RESOURCES;
    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    public interface Resources extends ClientBundle {
        @Source(ResourceCSS.CSS)
        ResourceCSS getCSS();
    }

    @CssResource.ImportedWithPrefix("diagram-TopContainerPanel")
    public interface ResourceCSS extends CssResource {
        String CSS = "org/reactome/web/diagram/legends/TopContainerPanel.css";

        String container();
    }
}
