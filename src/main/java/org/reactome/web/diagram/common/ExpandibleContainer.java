package org.reactome.web.diagram.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;

import java.util.HashSet;
import java.util.Set;


/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */

@Deprecated
public class ExpandibleContainer extends AbsolutePanel implements ClickHandler, MouseOverHandler, MouseOutHandler {

    private IconButton primaryButton;
    private Set<Button> buttons;
    private boolean isExpanded = false;

    public ExpandibleContainer(String tooltip, ImageResource imageResource, String style) {
        setStyleName(RESOURCES.getCSS().container());
        this.primaryButton = new IconButton(imageResource, style, tooltip,this);
        this.primaryButton.addStyleName(RESOURCES.getCSS().baseButtons());
        this.primaryButton.addStyleName(RESOURCES.getCSS().primaryButton());
        this.add(primaryButton);

        buttons = new HashSet<>();
        primaryButton.addDomHandler(event -> {                  // This is required because some mobile browsers fire the
            event.preventDefault(); event.stopPropagation();    // OnMouseOver event if before the Click event is fired
        }, MouseOverEvent.getType());

        addDomHandler(this, MouseOverEvent.getType());
        addDomHandler(this, MouseOutEvent.getType());
    }

    public void addButton(Button button) {
        button.addStyleName(RESOURCES.getCSS().baseButtons());
        button.addStyleName(RESOURCES.getCSS().optionButtons());
        buttons.add(button);
        add(button);
    }

    public void collapse() {
        if (isExpanded) {
            removeStyleName(RESOURCES.getCSS().expandedContainer());
            isExpanded = false;
        }

    }

    public void expand() {
        if (!isExpanded) {
            addStyleName(RESOURCES.getCSS().expandedContainer());
            isExpanded = true;
        }
    }

    @Override
    public void onClick(ClickEvent event) {
        if (!isExpanded) {
            expand();
        } else {
            collapse();
        }
    }

    @Override
    public void onMouseOver(MouseOverEvent event) {
        expand();
    }

    @Override
    public void onMouseOut(MouseOutEvent event) {
        collapse();
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

    @CssResource.ImportedWithPrefix("diagram-ExpandibleButton")
    public interface ResourceCSS extends CssResource {
        String CSS = "org/reactome/web/diagram/common/ExpandibleContainer.css";

        String container();

        String expandedContainer();

        String baseButtons();

        String optionButtons();

        String primaryButton();

    }
}
