package org.reactome.web.diagram.legends;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ToggleButton;


/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class SpeedButton extends ToggleButton implements MouseOutHandler, MouseOverHandler, ValueChangeHandler<Boolean> {

    private LegendPanel.LegendResources resources;
    private boolean mouseOver = false;

    public SpeedButton(LegendPanel.LegendResources resources) {
        this.resources = resources;
        this.setButtonStandardImages();
        addMouseOutHandler(this);
        addMouseOverHandler(this);
        addValueChangeHandler(this);
    }

    public void enable(){
        getElement().getStyle().setCursor(Style.Cursor.POINTER);
        setEnabled(true);
        this.setButtonStandardImages();
    }

    public void disable(){
        getElement().getStyle().setCursor(Style.Cursor.DEFAULT);
        getDownFace().setImage(new Image(this.resources.speedDisabled()));
        getUpFace().setImage(new Image(this.resources.speedDisabled()));
        setEnabled(false);
    }

    @Override
    public void onMouseOut(MouseOutEvent event) {
        this.mouseOver = false;
        this.setButtonStandardImages();
    }

    @Override
    public void onMouseOver(MouseOverEvent event) {
        if(!this.mouseOver) {
            Face face = isDown() ? getDownFace() : getUpFace();
            face.setImage(new Image(this.resources.speedHovered()));
            this.mouseOver = true;
        }
    }

    @Override
    public void onValueChange(ValueChangeEvent<Boolean> event) {
        this.setButtonStandardImages();
    }

    private void setButtonStandardImages(){
        if(isDown()) {
            getDownFace().setImage(new Image(this.resources.speedClicked()));
        }else{
            getUpFace().setImage(new Image(this.resources.speedNormal()));
        }
    }
}
