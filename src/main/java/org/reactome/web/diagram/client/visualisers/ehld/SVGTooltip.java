package org.reactome.web.diagram.client.visualisers.ehld;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class SVGTooltip extends PopupPanel {
    private static SVGTooltip tooltip;
    private static int DELAY = 1000;
    private Timer timer;

    private int offsetX;
    private int offsetY;
    private int distance;

    private SVGTooltip() {
        this.setStyleName(RESOURCES.getCSS().popup());
        timer = new Timer() {
            @Override
            public void run() {
                showWithDelay();
            }
        };
    }

    public static SVGTooltip get() {
        if (tooltip == null) {
            tooltip = new SVGTooltip();
        }
        return tooltip;
    }

    public void setText(String text){
        tooltip.clear();
        tooltip.add(new InlineLabel(text != null ? text : ""));
    }

    public void hide() {
        setVisible(false);
        if (timer.isRunning()) { timer.cancel(); }
    }

    @Override
    public void add(Widget w) {
        this.clear();
        super.add(w);
    }

    public void setPositionAndShow(AbsolutePanel container, double offsetX, double offsetY, double distance) {
        if (timer.isRunning()) { timer.cancel(); }
        this.offsetX = (int)offsetX;
        this.offsetY = (int)offsetY;
        this.distance = (int)distance;
        container.getElement().appendChild(this.getElement());

        timer.schedule(DELAY);
    }

    private void showWithDelay() {
        this.setVisible(true);
        this.setPosition(offsetX + distance, offsetY + distance);
    }

    private void setPosition(int left, int top) {
        Element elem = getElement();
        elem.getStyle().setPropertyPx("left", left);
        elem.getStyle().setPropertyPx("top", top);
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

    @CssResource.ImportedWithPrefix("diagram-ToolTips")
    public interface ResourceCSS extends CssResource {
        String CSS = "org/reactome/web/diagram/tooltips/SVGTooltip.css";

        String popup();
    }
}
