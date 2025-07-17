package org.reactome.web.diagram.common;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class IconToggleButton extends IconButton {

    private boolean isActive = false;
    private ImageResource img;
    private ImageResource imgActive;

    public IconToggleButton(String text, ImageResource img, ImageResource imgActive, ClickHandler handler) {
        super(text, img);
        this.img = img;
        this.imgActive = imgActive;

        addClickHandler(handler);
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
        update();
    }

    private void update() {
        if(isActive) {
            setImage(imgActive);
        } else {
            setImage(img);
        }
    }
}
