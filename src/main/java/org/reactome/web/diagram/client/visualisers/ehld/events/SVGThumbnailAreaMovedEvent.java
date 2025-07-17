package org.reactome.web.diagram.client.visualisers.ehld.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.client.visualisers.ehld.handlers.SVGThumbnailAreaMovedHandler;
import org.vectomatic.dom.svg.OMSVGPoint;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class SVGThumbnailAreaMovedEvent  extends GwtEvent<SVGThumbnailAreaMovedHandler> {
    public static Type<SVGThumbnailAreaMovedHandler> TYPE = new Type<>();

    private OMSVGPoint padding;

    public SVGThumbnailAreaMovedEvent(OMSVGPoint padding) {
        this.padding = padding;
    }

    @Override
    public Type<SVGThumbnailAreaMovedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(SVGThumbnailAreaMovedHandler handler) {
        handler.onSVGThumbnailAreaMoved(this);
    }

    public OMSVGPoint getPadding() {
        return padding;
    }

    @Override
    public String toString() {
        return "SVGThumbnailAreaMovedEvent{" +
                "padding=" + padding.getDescription() +
                '}';
    }
}
