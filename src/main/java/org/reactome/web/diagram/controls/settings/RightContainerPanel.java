package org.reactome.web.diagram.controls.settings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class RightContainerPanel extends FlowPanel {

    public RightContainerPanel() {
        this.setStyleName(RESOURCES.getCSS().container());
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

    @CssResource.ImportedWithPrefix("diagram-RightContainerPanel")
    public interface ResourceCSS extends CssResource {
        String CSS = "org/reactome/web/diagram/controls/settings/RightContainerPanel.css";

        String container();
    }
}
