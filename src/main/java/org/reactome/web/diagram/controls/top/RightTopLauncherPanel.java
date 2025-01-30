package org.reactome.web.diagram.controls.top;

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
import org.reactome.web.diagram.controls.top.common.AbstractMenuDialog;
import org.reactome.web.diagram.controls.top.key.DiagramKey;
import org.reactome.web.diagram.controls.top.key.EHLDKey;
import org.reactome.web.diagram.data.content.Content;
import org.reactome.web.diagram.events.CanvasExportRequestedEvent;
import org.reactome.web.diagram.events.ContentLoadedEvent;
import org.reactome.web.diagram.handlers.ContentLoadedHandler;

import java.util.HashMap;
import java.util.Map;

import static org.reactome.web.diagram.data.content.Content.Type.DIAGRAM;
import static org.reactome.web.diagram.data.content.Content.Type.SVG;


/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class RightTopLauncherPanel extends FlowPanel implements ClickHandler, ContentLoadedHandler, OptionalWidget.Handler {

    private EventBus eventBus;

    private AbstractMenuDialog diagramKey;
    private Map<Content.Type, AbstractMenuDialog> keys;

    private IconButton exportBtn;
    private IconButton diagramKeyBtn;

    public RightTopLauncherPanel(EventBus eventBus) {
        this.setStyleName(RESOURCES.getCSS().launcherPanel());
        this.eventBus = eventBus;

        this.keys = new HashMap<>();
        this.keys.put(DIAGRAM, new DiagramKey(eventBus));
        this.keys.put(SVG, new EHLDKey(eventBus));
        this.diagramKey = keys.get(SVG);

        if (OptionalWidget.EXPORT.isVisible()) {
            this.exportBtn = new IconButton(RESOURCES.exportIcon(), RESOURCES.getCSS().export(), "Export diagram", this);
            this.add(exportBtn);
        }

        if (OptionalWidget.LEGEND.isVisible()) {
            this.diagramKeyBtn = new IconButton(RESOURCES.keyIcon(), RESOURCES.getCSS().key(), "Diagram key", this);
            this.add(this.diagramKeyBtn);
        }

        eventBus.addHandler(ContentLoadedEvent.TYPE, this);
        this.setVisible(true);
    }

    @Override
    public void onClick(ClickEvent event) {
        Button btn = (Button) event.getSource();
        if (btn.equals(this.exportBtn)) {
            this.eventBus.fireEventFromSource(new CanvasExportRequestedEvent(), this);
        } else if (btn.equals(this.diagramKeyBtn)) {
            if (this.diagramKey.isShowing()) {
                this.diagramKey.hide();
            } else {
                this.diagramKey.showRelativeTo(this.diagramKeyBtn);
            }
        }
    }

    @Override
    public void onContentLoaded(ContentLoadedEvent event) {
        diagramKey.hide();
        diagramKey = keys.get(event.getContext().getContent().getType());

        // EHLD Disease uses different key
        if (event.getContext().getContent().getType().equals(SVG)) {
            if (event.getContext().getContent().getIsDisease() != null && event.getContext().getContent().getIsDisease()) {
                ((EHLDKey) diagramKey).addDiseaseKey();
            } else {
                ((EHLDKey) diagramKey).removeDiseaseKey();
            }
        }
    }


    public static Resources RESOURCES;

    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    public interface Resources extends ClientBundle {
        @Source(ResourceCSS.CSS)
        ResourceCSS getCSS();

        @Source("images/illustrations.png")
        ImageResource illustrationsIcon();

        @Source("images/key.png")
        ImageResource keyIcon();

        @Source("images/export.png")
        ImageResource exportIcon();

    }

    @CssResource.ImportedWithPrefix("diagram-RightTopLauncherPanel")
    public interface ResourceCSS extends CssResource {
        String CSS = "org/reactome/web/diagram/controls/top/RightTopLauncherPanel.css";

        String launcherPanel();

        String export();

        String key();
    }
}
