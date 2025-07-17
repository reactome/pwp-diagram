package org.reactome.web.diagram.controls.notifications;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;


/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class NotificationsContainer extends FlowPanel {

    public NotificationsContainer(EventBus eventBus) {
        this.setStyleName(RESOURCES.getCSS().container());
        this.setVisible(true);
    }

//    @Override
//    public void onCanvasExportRequested(CanvasExportRequestedEvent event) {
//        if(event.getOption().equals(PPTX) || event.getOption().equals(SBGN)) {
//                showNotification();
//        }
//    }

    private void showNotification() {
        Notification notification = new ExpiringNotification("Your download will be completed shortly", 2000);
        add(notification);
        notification.display();
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

    @CssResource.ImportedWithPrefix("diagram-NotificationsContainer")
    public interface ResourceCSS extends CssResource {
        String CSS = "org/reactome/web/diagram/controls/notifications/NotificationsContainer.css";

        String container();

    }
}
