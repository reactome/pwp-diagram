package org.reactome.web.diagram.common.validation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public abstract class AbstractValidator extends FlowPanel{

    private Image icon;

    public AbstractValidator() {
        setStyleName(RESOURCES.getCSS().container());
        icon = new Image(RESOURCES.inValid());
        icon.setStyleName(RESOURCES.getCSS().icon());
        add(icon);
        icon.setVisible(false);
    }

    public abstract boolean validate(String input);

    protected void showIcon(boolean isValid) {
        if(isValid) {
            icon.setResource(RESOURCES.valid());
            icon.setTitle(null);
        } else {
            icon.setResource(RESOURCES.inValid());
        }
        icon.setVisible(true);
    }

    protected void setTooltip(String message) {
        icon.setTitle(message);
    }

    public void clear(){
        icon.setVisible(false);
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

        @Source("images/valid.png")
        ImageResource valid();

        @Source("images/invalid.png")
        ImageResource inValid();

    }

    /**
     * Styles used by this widget.
     */
    @CssResource.ImportedWithPrefix("diagram-AbstractValidator")
    public interface ResourceCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/diagram/common/validation/Validator.css";

        String container();

        String icon();
    }
}
