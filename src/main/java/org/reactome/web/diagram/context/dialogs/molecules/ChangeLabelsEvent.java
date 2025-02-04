package org.reactome.web.diagram.context.dialogs.molecules;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class ChangeLabelsEvent extends GwtEvent<ChangeLabelsHandler> {
    public static Type<ChangeLabelsHandler> TYPE = new Type<>();

    private boolean showIds;

    public ChangeLabelsEvent(boolean showIds) {
        this.showIds = showIds;
    }

    @Override
    public Type<ChangeLabelsHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ChangeLabelsHandler handler) {
        handler.onChangeLabels(this);
    }

    public boolean getShowIds() {
        return showIds;
    }
}
