package org.reactome.web.diagram.controls.settings.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class InfoLabel extends FlowPanel implements ClickHandler{

    private boolean isExpanded = false;

    public InfoLabel(String labelText, TextResource infoText){
        InlineLabel label = new InlineLabel(labelText);

        Label img = new Label();
        img.addClickHandler(this);
        img.setTitle("Click to learn more");
        img.setStyleName(RESOURCES.getCSS().infoLabelBtn());

        Label info = new Label(infoText.getText());
        info.setStyleName(RESOURCES.getCSS().infoText());

        this.setStyleName(RESOURCES.getCSS().infoLabel());
        this.add(label);
        this.add(img);
        this.add(info);
    }

    @Override
    public void onClick(ClickEvent event) {
        toggle();
    }

    private void toggle(){
        if(isExpanded) {
            this.removeStyleName(RESOURCES.getCSS().infoLabelExpanded());
        } else {
            this.addStyleName(RESOURCES.getCSS().infoLabelExpanded());
        }
        isExpanded = !isExpanded;
    }

    public static Resources RESOURCES;
    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }


    public interface Resources extends ClientBundle {
        @Source(ResourceCSS.CSS)
        ResourceCSS getCSS();

        @Source("../images/info_normal.png")
        ImageResource infoNormal();

        @Source("../images/info_hovered.png")
        ImageResource infoHovered();
    }

    @CssResource.ImportedWithPrefix("diagram-InfoLabel")
    public interface ResourceCSS extends CssResource {
        String CSS = "org/reactome/web/diagram/controls/settings/common/InfoLabel.css";

        String infoLabel();

        String infoLabelExpanded();

        String infoLabelBtn();

        String infoText();
    }
}
