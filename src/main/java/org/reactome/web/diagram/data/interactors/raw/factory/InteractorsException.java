package org.reactome.web.diagram.data.interactors.raw.factory;

import org.reactome.web.diagram.events.InteractorsErrorEvent;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class InteractorsException extends Exception {

    private InteractorsErrorEvent.Level level;
    private String resource;

    public InteractorsException() {}

    public InteractorsException(String resource, String message) {
        this(resource, message, InteractorsErrorEvent.Level.ERROR);
    }

    public InteractorsException(String resource, String message, InteractorsErrorEvent.Level level) {
        super(message);
        this.resource = resource;
        this.level = level;
    }

    public String getResource() {
        return resource;
    }

    public InteractorsErrorEvent.Level getLevel() {
        return level;
    }
}
