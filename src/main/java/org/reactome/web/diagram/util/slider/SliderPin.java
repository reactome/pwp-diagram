package org.reactome.web.diagram.util.slider;

import com.google.gwt.canvas.dom.client.Context2d;

/**
* @author Antonio Fabregat (fabregat@ebi.ac.uk)
*/
class SliderPin {
    Point pos;
    int xDiff;
    double r;

    public SliderPin(int x, int y, double r) {
        this.pos = new Point(x, y);
        this.r = r;
    }

    public void draw(Context2d ctx, String colour){
        ctx.beginPath();
        ctx.setFillStyle(colour);
        ctx.arc(this.pos.x, this.pos.y, r, 0, 2 * Math.PI);
        ctx.fill();
        ctx.closePath();
    }

    public boolean isPointInside(Point p){
        double distance = Math.sqrt(Math.pow(this.pos.x - p.x, 2) + Math.pow(this.pos.y - p.y, 2));
        return distance <= this.r;
    }

    public void setDownPoint(Point down){
        this.xDiff = down.x - pos.x;
    }

    public void setPos(Point pos, int w, int r){
        int dX = pos.x - xDiff;
        if(dX < r){
            dX = r;
        }else if(dX > w - r){
            dX = w - r;
        }
        this.pos = new Point(dX, this.pos.y);
    }

    public void setNewPos(Point pos){
        this.pos = pos;
    }
}
