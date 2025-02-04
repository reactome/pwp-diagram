package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.handlers.InteractorsErrorHandler;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class InteractorsErrorEvent extends GwtEvent<InteractorsErrorHandler> {
    public static final Type<InteractorsErrorHandler> TYPE = new Type<>();

    public enum Level {
        WARNING,
        ERROR,
        ERROR_RECOVERABLE
    }

    private String message;
    private String resource;
    private Level level;

    public InteractorsErrorEvent(String resource, String message, Level level) {
        this.message = message;
        this.resource = resource;
        this.level = level;
    }

    @Override
    public Type<InteractorsErrorHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(InteractorsErrorHandler handler) {
        handler.onInteractorsError(this);
    }

    public Level getLevel() {
        return level;
    }

    public String getMessage() {
        return message;
    }

    public String getResource() {
        return resource;
    }

    @Override
    public String toString() {
        return "InteractorsErrorEvent{" +
                "message='" + message + '\'' +
                ", resource='" + resource + '\'' +
                ", level=" + level +
                '}';
    }
}
