package org.reactome.web.diagram.util.slider;

import com.google.gwt.canvas.dom.client.Context2d;

/**
* @author Antonio Fabregat (fabregat@ebi.ac.uk)
*/
class SliderBar {
    String color = "#6E6E6E";
    int tick;
    int y;
    int r;

    SliderBar(double tick, double y, double r) {
        this.tick = (int) tick * 2;
        this.y = (int) y;
        this.r = (int) r / 2;
    }

    public void draw(Context2d ctx){
        int w = ctx.getCanvas().getWidth() - r;
        ctx.setFillStyle(color);
        ctx.beginPath();
        ctx.moveTo(r, y + tick);
        ctx.lineTo(w, y + tick);
        ctx.lineTo(w, y);
        ctx.moveTo(r, y + tick);
        ctx.fill();
        ctx.closePath();
    }
}
