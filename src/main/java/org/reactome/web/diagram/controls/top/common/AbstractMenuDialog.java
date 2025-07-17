package org.reactome.web.diagram.controls.top.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import org.reactome.web.diagram.common.PwpButton;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public abstract class AbstractMenuDialog extends DialogBox implements ClickHandler {

    private FlowPanel container;

    public AbstractMenuDialog(String title) {
        super(false, false);

        setTitlePanel(title);

        this.container = new FlowPanel();
        clear();

        setWidget(this.container);
        setStyleName(RESOURCES.getCSS().menuDialog());
    }

    public void add(Widget w){
        container.add(w);
    }

    @Override
    public void clear() {
        this.container.clear();
        this.container.add(new PwpButton("Close", RESOURCES.getCSS().close(), this));
    }

    @Override
    public void onClick(ClickEvent event) {
        if(isShowing()){
            hide();
        }else {
            center();
            show();
        }
    }

    private void setTitlePanel(String title) {
        Label label = new Label(title);
        label.setStyleName(RESOURCES.getCSS().headerText());
        SafeHtml safeHtml = SafeHtmlUtils.fromSafeConstant(label.toString());
        getCaption().setHTML(safeHtml);
        getCaption().asWidget().setStyleName(RESOURCES.getCSS().header());
    }


    public static Resources RESOURCES;
    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    public interface Resources extends ClientBundle {
        @Source(ResourceCSS.CSS)
        ResourceCSS getCSS();

        @Source("../images/diagramKey.png")
        ImageResource diagramKey();

        @Source("../images/close_clicked.png")
        ImageResource closeClicked();

        @Source("../images/close_disabled.png")
        ImageResource closeDisabled();

        @Source("../images/close_hovered.png")
        ImageResource closeHovered();

        @Source("../images/close_normal.png")
        ImageResource closeNormal();
    }

    @CssResource.ImportedWithPrefix("diagram-abstractMenuDialog")
    public interface ResourceCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/diagram/controls/top/common/AbstractMenuDialog.css";

        String menuDialog();

        String header();

        String headerText();

        String close();
    }
}
