package org.reactome.web.diagram.client.visualisers.ehld.context;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import org.reactome.web.diagram.events.ContentRequestedEvent;
import org.reactome.web.pwp.model.client.classes.DatabaseObject;
import org.reactome.web.pwp.model.client.classes.Pathway;
import org.reactome.web.pwp.model.client.common.ContentClientHandler;
import org.reactome.web.pwp.model.client.content.ContentClient;
import org.reactome.web.pwp.model.client.content.ContentClientError;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class SVGContextPanel extends DialogBox implements ClickHandler, ContentClientHandler.ObjectLoaded<DatabaseObject> {
    private static String MESSAGE = "Go to ";

    private EventBus eventBus;
    private Label label;

    private Pathway targetPathway;

    public SVGContextPanel(EventBus eventBus) {
        super();
        this.eventBus = eventBus;
        setAutoHideEnabled(true);
        setModal(false);
        setStyleName(RESOURCES.getCSS().popup());

        this.label = new Label("");
        this.label.addClickHandler(this);
        label.setStyleName(RESOURCES.getCSS().label());

        FlowPanel fp = new FlowPanel();
        fp.add(this.label);

        setWidget(fp);
    }

    @Override
    public void onClick(ClickEvent event) {
        if(event.getSource().equals(label)) {
            eventBus.fireEventFromSource(new ContentRequestedEvent(targetPathway.getReactomeIdentifier()), this);
        }
        hide();
    }

    @Override
    public void onObjectLoaded(DatabaseObject databaseObject) {
        if(databaseObject instanceof Pathway) {
            targetPathway = (Pathway) databaseObject;
            label.setText(MESSAGE + targetPathway.getDisplayName() + " (" + targetPathway.getReactomeIdentifier() + ")");
            super.show();
        }
    }

    @Override
    public void onContentClientException(Type type, String message) {
        label.setText("");
        hide();
    }

    @Override
    public void onContentClientError(ContentClientError error) {
        label.setText("");
        hide();
    }

    public void show(String stableId, int x, int y) {
        if(stableId != null) {
            ContentClient.query(stableId, this);
            setPosition(x, y);
        }
    }

    private void setPosition(int x, int y) {
        //TODO sort out the details of the positioning
        setPopupPosition(x + 5, y + 5);
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

    @CssResource.ImportedWithPrefix("diagram-SVGContextPanel")
    public interface ResourceCSS extends CssResource {

        String CSS = "org/reactome/web/diagram/client/SVGContextPanel.css";

        String popup();

        String label();
    }
}
