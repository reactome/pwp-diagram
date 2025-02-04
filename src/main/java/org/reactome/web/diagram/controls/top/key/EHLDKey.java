package org.reactome.web.diagram.controls.top.key;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;
import org.reactome.web.diagram.controls.navigation.ControlAction;
import org.reactome.web.diagram.controls.top.common.AbstractMenuDialog;
import org.reactome.web.diagram.events.ControlActionEvent;
import org.reactome.web.diagram.handlers.ControlActionHandler;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class EHLDKey extends AbstractMenuDialog implements ControlActionHandler {

    private EventBus eventBus;
    private Image image;

    public EHLDKey(EventBus eventBus) {
        super("Diagram Key");
        this.eventBus = eventBus;
        this.initHandlers();

        image = new Image(RESOURCES.ehldkey());
        image.setStyleName(RESOURCES.getCSS().image());
        add(image);

//        HTMLPanel bullets = new HTMLPanel(RESOURCES.fireworkskey().getText());
//        bullets.setStyleName(RESOURCES.getCSS().bullets());
//        add(bullets);

    }

    public void addDiseaseKey(){
        image.setResource(RESOURCES.ehldkeyDisease());
        image.setStyleName(RESOURCES.getCSS().imageDisease());
        add(image);
    }

    public void removeDiseaseKey() {
        image.setResource(RESOURCES.ehldkey());
        image.setStyleName(RESOURCES.getCSS().image());
        add(image);
    }


    @Override
    public void onControlAction(ControlActionEvent event) {
        if(event.getAction().equals(ControlAction.FIREWORKS)) {
            hide();
        }
    }

    private void initHandlers() {
        this.eventBus.addHandler(ControlActionEvent.TYPE, this);
    }

    public static Resources RESOURCES;
    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    public interface Resources extends ClientBundle {
        @Source(ResourceCSS.CSS)
        ResourceCSS getCSS();

        @Source("data/EHLDKey.png")
        ImageResource ehldkey();

        @Source("data/EHLDKey_disease.png")
        ImageResource ehldkeyDisease();

//        @Source("data/fireworkskey.html")
//        TextResource fireworkskey();
    }

    @CssResource.ImportedWithPrefix("diagram-EHLDKey")
    public interface ResourceCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/diagram/controls/top/key/EHLDKey.css";

        String image();

        String imageDisease();
    }
}
