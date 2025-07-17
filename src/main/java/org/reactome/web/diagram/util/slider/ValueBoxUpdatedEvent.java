package org.reactome.web.diagram.util.slider;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class ValueBoxUpdatedEvent extends GwtEvent<ValueBoxUpdatedHandler> {
    public static Type<ValueBoxUpdatedHandler> TYPE = new Type<>();

    private Double value;

    public ValueBoxUpdatedEvent(Double value) {
        this.value = value;
    }

    @Override
    public Type<ValueBoxUpdatedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ValueBoxUpdatedHandler handler) {
        handler.onValueBoxUpdated(this);
    }

    public Double getValue() {
        return value;
    }
}
