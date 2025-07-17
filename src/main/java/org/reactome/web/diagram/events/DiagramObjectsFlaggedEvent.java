package org.reactome.web.diagram.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.handlers.DiagramObjectsFlaggedHandler;

import java.util.Set;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class DiagramObjectsFlaggedEvent extends GwtEvent<DiagramObjectsFlaggedHandler> {
    public static final Type<DiagramObjectsFlaggedHandler> TYPE = new Type<>();

    protected String term;
    protected Boolean includeInteractors;
    protected Set<DiagramObject> flaggedItems;
    protected boolean notify;

    public DiagramObjectsFlaggedEvent(String term, Boolean includeInteractors, Set<DiagramObject> flaggedItems, boolean notify) {
        this.term = term;
        this.includeInteractors = includeInteractors;
        this.flaggedItems = flaggedItems;
        this.notify = notify;
    }

    @Override
    public Type<DiagramObjectsFlaggedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(DiagramObjectsFlaggedHandler handler) {
        handler.onDiagramObjectsFlagged(this);
    }

    public String getTerm() {
        return term;
    }

    public Boolean getIncludeInteractors() {
        return includeInteractors;
    }

    public Set<DiagramObject> getFlaggedItems() {
        return flaggedItems;
    }

    public boolean getNotify() {
        return notify;
    }

    @Override
    public String toString() {
        return "DiagramObjectsFlaggedEvent{" +
                "term='" + term + '\'' +
                ", includeInteractors=" + includeInteractors +
                ", flaggedItems=" + (flaggedItems != null ? flaggedItems.size() : 0) +
                '}';
    }
}
