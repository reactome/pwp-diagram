package org.reactome.web.diagram.context.sections;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class SectionCellSelectedEvent extends GwtEvent<SectionCellSelectedHandler> {
    public static GwtEvent.Type<SectionCellSelectedHandler> TYPE = new GwtEvent.Type<SectionCellSelectedHandler>();

    private SelectionSummary selection;

    public SectionCellSelectedEvent(SelectionSummary selection) {
        this.selection = selection;
    }

    @Override
    public GwtEvent.Type<SectionCellSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(SectionCellSelectedHandler handler) {
        handler.onCellSelected(this);
    }

    public SelectionSummary getSelection() {
        return selection;
    }

    @Override
    public String toString() {
        return "SectionCellSelectedEvent{" +
                "selection=" + selection +
                '}';
    }
}
