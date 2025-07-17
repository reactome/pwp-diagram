package org.reactome.web.diagram.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;


/**
 * A simple button that when clicked presents the user with two buttons to confirm the selected action.
 * The button automatically collapses to its original status shortly after the mouse moves out of it.
 *
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class ConfirmationButton extends Composite implements ClickHandler, MouseOverHandler, MouseOutHandler {
    private static int WIDTH_COLLAPSED = 19;
    private static int WIDTH_EXPANDED = 40;
    private boolean isExpanded;

    private FocusPanel container;
    private FlowPanel innerContainer;
    private IconButton normalButton;
    private IconButton okButton;

    private IconButton cancelButton;
    private String title;
    private ImageResource icon;

    private ClickHandler clickHandler;
    private Timer collapseTimer;

    public ConfirmationButton(String title, ImageResource icon, ClickHandler clickHandler) {
        this.title = title;
        this.icon = icon;
        this.clickHandler = clickHandler;

        collapseTimer = new Timer() {
            @Override
            public void run() {
                collapse();
            }
        };

        init();
    }

    @Override
    public void onClick(ClickEvent event) {
        Button btn = (Button) event.getSource();
        if (btn.equals(normalButton)) {
            expand();
        } else {
            collapse();
        }
    }

    @Override
    public void onMouseOut(MouseOutEvent event) {
        if(isExpanded) {
           collapseTimer.schedule(500);
        }
    }

    @Override
    public void onMouseOver(MouseOverEvent event) {
        if(isExpanded && collapseTimer.isRunning()) {
            collapseTimer.cancel();
        }
    }

    private void init() {
        normalButton = new IconButton("", icon);
        normalButton.addClickHandler(this);
        normalButton.setTitle(title);
        normalButton.setStyleName(RESOURCES.getCSS().normalBtn());

        okButton = new IconButton("", RESOURCES.okIcon());
        okButton.addClickHandler(clickHandler);
        okButton.setTitle("Proceed");
        okButton.setStyleName(RESOURCES.getCSS().okBtn());

        cancelButton = new IconButton("", RESOURCES.cancelIcon());
        cancelButton.addClickHandler(this);
        cancelButton.setTitle("Cancel");
        cancelButton.setStyleName(RESOURCES.getCSS().cancelBtn());

        innerContainer = new FlowPanel();
        innerContainer.setStyleName(RESOURCES.getCSS().innerContainer());
        innerContainer.add(normalButton);
        innerContainer.add(okButton);
        innerContainer.add(cancelButton);

        container = new FocusPanel();
        container.setStyleName(RESOURCES.getCSS().container());
        container.add(innerContainer);
        container.addMouseOutHandler(this);
        container.addMouseOverHandler(this);
        this.initWidget(container);

        //Set it collapsed originally
        collapse();
    }

    private void collapse(){
        container.getElement().getStyle().setWidth(WIDTH_COLLAPSED, Style.Unit.PX);
        innerContainer.getElement().getStyle().setMarginLeft(0, Style.Unit.PX);
        isExpanded = false;
    }


    private void expand() {
        container.getElement().getStyle().setWidth(WIDTH_EXPANDED, Style.Unit.PX);
        innerContainer.getElement().getStyle().setMarginLeft(-WIDTH_COLLAPSED, Style.Unit.PX);
        isExpanded = true;
    }


    public static Resources RESOURCES;
    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    public interface Resources extends ClientBundle {
        @Source(ResourceCSS.CSS)
        ResourceCSS getCSS();

        @Source("images/ok.png")
        ImageResource okIcon();

        @Source("images/cancel.png")
        ImageResource cancelIcon();

    }

    @CssResource.ImportedWithPrefix("diagram-ConfirmationButton")
    public interface ResourceCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/diagram/common/ConfirmationButton.css";

        String container();

        String innerContainer();

        String normalBtn();

        String okBtn();

        String cancelBtn();
    }
}
