package org.reactome.web.diagram.controls.notifications;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class ExpiringNotification extends AbsolutePanel implements Notification, ClickHandler {
    private int timeToExpire;

    private Label msgLabel;
    private SimplePanel spinnerContainer;
    private SimplePanel spinner;
    private Timer fadeoutTimer;
    private Timer removeTimer;

    public ExpiringNotification(String message, int timeToExpire) {
        this.timeToExpire = timeToExpire;
        setStyleName(RESOURCES.getCSS().container());

        spinner = new SimplePanel();
        spinner.setStyleName(RESOURCES.getCSS().loader());
        spinnerContainer = new SimplePanel();
        spinnerContainer.setStyleName(RESOURCES.getCSS().loaderContainer());
        spinnerContainer.add(spinner);

        msgLabel = new Label(message);
        add(spinnerContainer);
        add(msgLabel);

        addDomHandler(this, ClickEvent.getType());

        fadeoutTimer = new Timer() {
            @Override
            public void run() {
                conceal();
            }
        };
        removeTimer = new Timer() {
            @Override
            public void run() {
                removeFromParent();
            }
        };
    }

    @Override
    public void display() {
        Scheduler.get().scheduleDeferred(() -> {        // This is deferred in order to show the fadein effect.
            addStyleName(RESOURCES.getCSS().fadeIn());
            fadeoutTimer.schedule(timeToExpire);
        });
    }

    @Override
    public void conceal() {
        removeStyleName(RESOURCES.getCSS().fadeIn());
        removeTimer.schedule(400);
    }

    @Override
    public void onClick(ClickEvent clickEvent) {
        conceal();
    }

    @Override
    public Widget asWidget() {
        return this;
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

    @CssResource.ImportedWithPrefix("diagram-Notification")
    public interface ResourceCSS extends CssResource {
        String CSS = "org/reactome/web/diagram/controls/notifications/Notification.css";

        String container();

        String fadeIn();

        String loaderContainer();

        String loader();
    }
}
