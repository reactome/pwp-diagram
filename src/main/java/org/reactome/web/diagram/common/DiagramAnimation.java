package org.reactome.web.diagram.common;

import com.google.gwt.animation.client.Animation;
import org.reactome.web.diagram.data.layout.Coordinate;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class DiagramAnimation extends Animation {

    private static final int MIN_ANIMATION_DURATION = 500;
    /**
     * The maximum duration of the animation.
     */
    private static final int MAX_ANIMATION_DURATION = 2000;

    private DiagramAnimationHandler handler;

    private Double currentFactor;
    private Coordinate currentOffset;

    private Coordinate deltaOffset;
    private double deltaFactor;

    private boolean canceled;

    public DiagramAnimation(DiagramAnimationHandler handler, Double currentFactor, Coordinate currentOffset){
        this.handler = handler;
        this.currentFactor = currentFactor;
        this.currentOffset = currentOffset;
        this.canceled = false;
    }

    public void animate(Coordinate delta, double deltaFactor){
        int time = time(distance(delta), deltaFactor);
        this.animate(delta, deltaFactor, time);
    }

    private void animate(Coordinate deltaOffset, double deltaFactor, int time){
        this.canceled = false;
        this.deltaOffset = deltaOffset;
        this.deltaFactor = deltaFactor;
        if(time > 0) { //If no need to move time is zero because distance is zero :)
            run(time); //DO NOT RUN THIS WHEN TIME IS ZERO
        }
    }

    @Override
    protected void onCancel() {
        this.canceled = true;
        super.onCancel();
    }

    @Override
    protected void onComplete() {
        if(!canceled){
            super.onComplete(); //By avoiding the call to "super" if cancelled, a composition of movement is created
//            onUpdate(1.0);
        }
    }

    @Override
    protected void onUpdate(double progress) {
        Coordinate offset = currentOffset.add(this.deltaOffset.multiply(progress));
        double factor = this.currentFactor + this.deltaFactor * progress;
        handler.transform(offset, factor);
    }

    private int time(double distance, double deltaFactor){
        int d = (int) Math.ceil(distance) * 10;
        if(d==0 && deltaFactor>0) d = MIN_ANIMATION_DURATION;
        return d > MAX_ANIMATION_DURATION ? MAX_ANIMATION_DURATION : d;
    }

    private double distance(Coordinate delta){
        double dX = delta.getX();
        double dY = delta.getY();
        return Math.sqrt(dX*dX + dY*dY);
    }
}
