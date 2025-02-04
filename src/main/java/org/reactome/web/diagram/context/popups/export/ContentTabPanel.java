package org.reactome.web.diagram.context.popups.export;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class ContentTabPanel extends FlowPanel {
    private static String DESCRIPTION = "To enable integration with other tools and resources, the content of our pathway diagrams can be exported in the following formats:";
    private String stId;
    private Long id;

    public ContentTabPanel(final String stId, final Long id) {
        this.stId = stId;
        this.id = id;

        initUI();
    }

    private void initUI() {
        FlowPanel mainPanel = new FlowPanel();
        mainPanel.addStyleName(RESOURCES.getCSS().mainPanel());
        Label info = new Label(DESCRIPTION);
        info.setStyleName(RESOURCES.getCSS().description());
        mainPanel.add(info);
        mainPanel.add(getButtonsPanel());
        this.add(mainPanel);
    }

    private Widget getButtonsPanel() {
        FlowPanel rtn = new FlowPanel();
        rtn.setStyleName(RESOURCES.getCSS().buttonsPanel());
        for (ContentDownloadType type : ContentDownloadType.values()) {
            FlowPanel item = new FlowPanel();
            item.addStyleName(RESOURCES.getCSS().itemPanel());

            DownloadButton<ContentDownloadType> btn = new DownloadButton<>(type, generateUrlList(type));
            btn.addStyleName(RESOURCES.getCSS().itemIcon());

            Label infoLabel = new Label(type.getInfo());
            infoLabel.setStyleName(RESOURCES.getCSS().itemLabel());

            item.add(btn);
            item.add(infoLabel);
            rtn.add(item);
        }

        return rtn;
    }

    public List<String> generateUrlList(ContentDownloadType type) {
        List<String> rtn = new ArrayList<>();

        rtn.add(type.getTemplateURL()
                .replace("__STID__", stId)
                .replace("__ID__", "" + id));
        return rtn;
    }



    public static Resources RESOURCES;
    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    /**
     * A ClientBundle of resources used by this widget.
     */
    public interface Resources extends ClientBundle {
        /**
         * The styles used in this widget.
         */
        @Source(ResourceCSS.CSS)
        ResourceCSS getCSS();

    }

    /**
     * Styles used by this widget.
     */
    @CssResource.ImportedWithPrefix("diagram-ContentTabPanel")
    public interface ResourceCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/diagram/context/popups/export/ContentTabPanel.css";

        String mainPanel();

        String description();

        String buttonsPanel();

        String itemPanel();

        String itemIcon();

        String itemLabel();

        String unselectable();

    }

}
