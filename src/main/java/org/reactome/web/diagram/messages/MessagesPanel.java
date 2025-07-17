package org.reactome.web.diagram.messages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public abstract class MessagesPanel extends SimplePanel {

    public static MessagesResources RESOURCES;
    static {
        RESOURCES = GWT.create(MessagesResources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    /**
     * A ClientBundle of resources used by this widget.
     */
    public interface MessagesResources extends ClientBundle {
        /**
         * The styles used in this widget.
         */
        @Source(MessagesPanelCSS.CSS)
        MessagesPanelCSS getCSS();

        @Source("images/error.png")
        ImageResource error();

        @Source("images/loader_lines.gif")
        ImageResource loader();

        @Source("images/close_red_clicked.png")
        ImageResource closeRedClicked();

        @Source("images/close_red_hovered.png")
        ImageResource closeRedHovered();

        @Source("images/close_red_normal.png")
        ImageResource closeRedNormal();
    }

    /**
     * Styles used by this widget.
     */
    @CssResource.ImportedWithPrefix("diagram-MessagesPanel")
    public interface MessagesPanelCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/diagram/messages/messages.css";

        String messagesPanel();

        String loadingMessage();

        String analysisOverlayMessage();

        String errorMessage();

        String errorMessageText();

        String errorMessageTitle();

        String close();
    }

    protected EventBus eventBus;

    public MessagesPanel(EventBus eventBus) {
        this.eventBus = eventBus;
        //Setting the legend style
        setStyleName(RESOURCES.getCSS().messagesPanel());
    }
}
