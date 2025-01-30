package org.reactome.web.diagram.controls.navigation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import org.reactome.web.diagram.client.OptionalWidget;
import org.reactome.web.diagram.common.IconButton;
import org.reactome.web.diagram.events.ControlActionEvent;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class MainControlPanel extends FlowPanel implements ClickHandler, OptionalWidget.Handler {

    private EventBus eventBus;
    private IconButton fitAll;
    private IconButton fireworks;

    public MainControlPanel(EventBus eventBus) {
        this.eventBus = eventBus;

        this.addStyleName(RESOURCES.getCSS().mainControlPanel());

        if (OptionalWidget.SHOW_ALL.isVisible()) {
            this.fitAll = new IconButton(RESOURCES.fitallIcon(), RESOURCES.getCSS().fitall(), "Show all", this);
            this.add(this.fitAll);
        }

        if (OptionalWidget.FIREWORKS.isVisible()) {
            this.fireworks = new IconButton(RESOURCES.fireworksIcon(), RESOURCES.getCSS().fireworks(), "Pathway overview", this);
            this.add(this.fireworks);
        }
    }

    @Override
    public void onClick(ClickEvent event) {
        Button btn = (Button) event.getSource();
        if (btn.equals(this.fitAll)) {
            this.eventBus.fireEventFromSource(new ControlActionEvent(ControlAction.FIT_ALL), this);
        } else if (btn.equals(this.fireworks)) {
            this.eventBus.fireEventFromSource(new ControlActionEvent(ControlAction.FIREWORKS), this);
        }
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

        @Source("images/fitall.png")
        ImageResource fitallIcon();

        @Source("images/fireworks.png")
        ImageResource fireworksIcon();
    }

    /**
     * Styles used by this widget.
     */
    @CssResource.ImportedWithPrefix("diagram-MainControlPanel")
    public interface ResourceCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/diagram/controls/navigation/MainControlPanel.css";

        String mainControlPanel();

        String fitall();

        String fireworks();
    }
}
