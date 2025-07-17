package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Image;
import org.reactome.web.diagram.handlers.StructureImageLoadedHandler;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class StructureImageLoadedEvent extends GwtEvent<StructureImageLoadedHandler> {
    public static final Type<StructureImageLoadedHandler> TYPE = new Type<>();

    private Image image;

    public StructureImageLoadedEvent(Image image) {
        this.image = image;
    }

    @Override
    public Type<StructureImageLoadedHandler> getAssociatedType() {
        return TYPE;
    }


    @Override
    protected void dispatch(StructureImageLoadedHandler handler) {
        handler.onLayoutImageLoaded(this);
    }

    public Image getImage() {
        return image;
    }

    @Override
    public String toString() {
        return "StructureImageLoadedEvent{" +
                "image=" + image.getAltText() +
                '}';
    }
}
