package org.reactome.web.diagram.util.slider;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.DoubleBox;


/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class ValueBox extends DoubleBox implements KeyUpHandler, KeyPressHandler {
    private final int updateDelay = 300;
    private final Timer timer;
    private Double value;

    public ValueBox(Double value) {
        this.value = value;
        this.setStyleName(Slider.RESOURCES.getCSS().sliderValueBox());
        this.timer = new Timer() {
            @Override
            public void run() {
                checkContent();
            }
        };

        addKeyUpHandler(this);
        addKeyPressHandler(this);
    }

    public HandlerRegistration addValueBoxUpdatedHandler(ValueBoxUpdatedHandler handler){
        return addHandler(handler, ValueBoxUpdatedEvent.TYPE);
    }

    @Override
    public void onKeyUp(KeyUpEvent event) {
        if (timer.isRunning()) {
            timer.cancel();
        }
        timer.schedule(this.updateDelay);
    }

    @Override
    public void onKeyPress(KeyPressEvent event) {
        int keyCode = event.getNativeEvent().getKeyCode();
        if(!Character.isDigit(event.getCharCode()) &&
                keyCode != 0 && //the decimal dot character
                keyCode != KeyCodes.KEY_NUM_PERIOD &&
                keyCode != KeyCodes.KEY_LEFT &&
                keyCode != KeyCodes.KEY_RIGHT &&
                keyCode != KeyCodes.KEY_DELETE &&
                keyCode != KeyCodes.KEY_BACKSPACE &&
                keyCode != KeyCodes.KEY_ESCAPE)
            ((DoubleBox)event.getSource()).cancelKey();
    }

    protected void checkContent(){
        Double value = getValue();
        if(value==null){
            return;
        }
        if(!this.value.equals(value)){
            this.value = value;
            this.fireEvent(new ValueBoxUpdatedEvent(value));
        }
    }
}
