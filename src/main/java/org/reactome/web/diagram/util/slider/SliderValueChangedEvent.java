package org.reactome.web.diagram.util.slider;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class SliderValueChangedEvent extends GwtEvent<SliderValueChangedHandler> {
    public static Type<SliderValueChangedHandler> TYPE = new Type<SliderValueChangedHandler>();

    double percentage;

    public SliderValueChangedEvent(double percentage) {
        this.percentage = percentage;
    }

    @Override
    public Type<SliderValueChangedHandler> getAssociatedType() {
        return TYPE;
    }

    public double getPercentage() {
        return percentage;
    }

    @Override
    protected void dispatch(SliderValueChangedHandler handler) {
        handler.onSliderValueChanged(this);
    }
}
