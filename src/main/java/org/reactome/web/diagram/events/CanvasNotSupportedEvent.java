package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.handlers.CanvasNotSupportedHandler;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class CanvasNotSupportedEvent extends GwtEvent<CanvasNotSupportedHandler> {
    public static Type<CanvasNotSupportedHandler> TYPE = new Type<CanvasNotSupportedHandler>();

    final String message = "Your browser does not support the HTML5 Canvas. Please upgrade your browser.";

    @Override
    public Type<CanvasNotSupportedHandler> getAssociatedType() {
        return TYPE;
    }

    public String getMessage() {
        return message;
    }

    @Override
    protected void dispatch(CanvasNotSupportedHandler canvasNotSupportedHandler) {
        canvasNotSupportedHandler.onCanvasNotSupported(this);
    }

    @Override
    public String toString() {
        return "CanvasNotSupportedEvent{" +
                "message='" + message + '\'' +
                '}';
    }
}
