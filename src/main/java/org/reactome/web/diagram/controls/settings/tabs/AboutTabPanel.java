package org.reactome.web.diagram.controls.settings.tabs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class AboutTabPanel extends Composite {

    public AboutTabPanel(String header, TextResource about) {
        FlowPanel main = new FlowPanel();
        main.setStyleName(RESOURCES.getCSS().aboutPanel());

        Label tabHeader = new Label(header);
        tabHeader.setStyleName(RESOURCES.getCSS().tabHeader());
        main.add(tabHeader);
        HTMLPanel htmlPanel = new HTMLPanel(about.getText());
        htmlPanel.setStyleName(RESOURCES.getCSS().htmlPanel());
        main.add(htmlPanel);

        initWidget(main);
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

    @CssResource.ImportedWithPrefix("diagram-AboutTabPanel")
    public interface ResourceCSS extends CssResource {
        String CSS = "org/reactome/web/diagram/controls/settings/tabs/AboutTabPanel.css";

        String aboutPanel();

        String htmlPanel();

        String tabHeader();
    }
}
