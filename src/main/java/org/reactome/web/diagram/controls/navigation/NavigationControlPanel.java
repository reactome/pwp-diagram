package org.reactome.web.diagram.controls.navigation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import org.reactome.web.diagram.common.IconButton;
import org.reactome.web.diagram.events.ContentLoadedEvent;
import org.reactome.web.diagram.events.ContentRequestedEvent;
import org.reactome.web.diagram.events.ControlActionEvent;
import org.reactome.web.diagram.events.LayoutLoadedEvent;
import org.reactome.web.diagram.handlers.ContentLoadedHandler;
import org.reactome.web.diagram.handlers.ContentRequestedHandler;
import org.reactome.web.diagram.handlers.LayoutLoadedHandler;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class NavigationControlPanel extends AbsolutePanel implements ClickHandler,
        ContentRequestedHandler, LayoutLoadedHandler, ContentLoadedHandler {

    protected EventBus eventBus;

    private IconButton zoomIn;
    private IconButton zoomOut;
    private IconButton up;
    private IconButton right;
    private IconButton down;
    private IconButton left;

    public NavigationControlPanel(EventBus eventBus) {
        this.eventBus = eventBus;
        this.initHandlers();

        //Setting the legend style
        getElement().getStyle().setPosition(com.google.gwt.dom.client.Style.Position.ABSOLUTE);
        setStyleName(RESOURCES.getCSS().controlPanel());

        ControlPanelCSS css = RESOURCES.getCSS();

        this.zoomIn = new IconButton(RESOURCES.zoomInIcon(), css.zoomIn(), "Zoom in", this);
        this.add(this.zoomIn);

        this.zoomOut = new IconButton(RESOURCES.zoomOutIcon(), css.zoomOut(), "Zoom out", this);
        this.add(this.zoomOut);

        this.up = new IconButton(RESOURCES.upIcon(), css.up(), "Move up", this);
        this.add(this.up);
        this.left = new IconButton(RESOURCES.leftIcon(), css.left(), "Move left", this);
        this.add(this.left);
        this.right = new IconButton(RESOURCES.rightIcon(), css.right(), "Move right", this);
        this.add(this.right);
        this.down = new IconButton(RESOURCES.downIcon(), css.down(), "Move down", this);
        this.add(this.down);

        this.setVisible(false);
    }

    @Override
    public void onClick(ClickEvent event) {
        ControlAction action = ControlAction.NONE;
        Button btn = (Button) event.getSource();
        if (btn.equals(this.zoomIn)) {
            action = ControlAction.ZOOM_IN;
        } else if (btn.equals(this.zoomOut)) {
            action = ControlAction.ZOOM_OUT;
        } else if (btn.equals(this.up)) {
            action = ControlAction.UP;
        } else if (btn.equals(this.right)) {
            action = ControlAction.RIGHT;
        } else if (btn.equals(this.down)) {
            action = ControlAction.DOWN;
        } else if (btn.equals(this.left)) {
            action = ControlAction.LEFT;
        }
        if (!action.equals(ControlAction.NONE)) {
            this.eventBus.fireEventFromSource(new ControlActionEvent(action), this);
        }
    }

    @Override
    public void onContentRequested(ContentRequestedEvent event) {
        this.setVisible(false);
    }

    @Override
    public void onContentLoaded(ContentLoadedEvent event) {
        this.setVisible(true);
    }

    @Override
    public void onLayoutLoaded(LayoutLoadedEvent event) {
        this.setVisible(true);
    }

    private void initHandlers() {
        this.eventBus.addHandler(ContentRequestedEvent.TYPE, this);
        this.eventBus.addHandler(ContentLoadedEvent.TYPE, this);
        this.eventBus.addHandler(LayoutLoadedEvent.TYPE, this);
    }


    public static ControlResources RESOURCES;

    static {
        RESOURCES = GWT.create(ControlResources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    /**
     * A ClientBundle of resources used by this widget.
     */
    public interface ControlResources extends ClientBundle {
        /**
         * The styles used in this widget.
         */
        @Source(ControlPanelCSS.CSS)
        ControlPanelCSS getCSS();

        @Source("images/down.png")
        ImageResource downIcon();

        @Source("images/left.png")
        ImageResource leftIcon();

        @Source("images/right.png")
        ImageResource rightIcon();

        @Source("images/up.png")
        ImageResource upIcon();

        @Source("images/zoomin.png")
        ImageResource zoomInIcon();

        @Source("images/zoomout.png")
        ImageResource zoomOutIcon();
    }

    /**
     * Styles used by this widget.
     */
    @CssResource.ImportedWithPrefix("diagram-ControlPanel")
    public interface ControlPanelCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/diagram/controls/navigation/NavigationControlPanel.css";

        String controlPanel();

        String down();

        String left();

        String right();

        String up();

        String zoomIn();

        String zoomOut();
    }
}
