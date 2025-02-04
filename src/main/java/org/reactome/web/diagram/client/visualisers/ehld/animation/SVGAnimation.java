package org.reactome.web.diagram.client.visualisers.ehld.animation;

import com.google.gwt.animation.client.Animation;
import org.vectomatic.dom.svg.OMSVGMatrix;
import org.vectomatic.dom.svg.OMSVGPoint;
import org.vectomatic.dom.svg.OMSVGSVGElement;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class SVGAnimation extends Animation {

    private static final int MIN_ANIMATION_DURATION = 300, MAX_ANIMATION_DURATION = 1000;

    private SVGAnimationHandler handler;
    private static OMSVGSVGElement svg;

    private OMSVGMatrix ctm;
    private OMSVGPoint delta;
    private double scaleDelta;

    private boolean canceled;

    public SVGAnimation(SVGAnimationHandler handler, OMSVGMatrix ctm) {
        this.handler = handler;
        this.ctm = ctm;
        svg = new OMSVGSVGElement();
    }

    public void animate(OMSVGMatrix targetTM) {
        canceled = false;
        delta = getTranslationDelta(targetTM);
        scaleDelta = getScaleDelta(targetTM);

        int time = time(distanceTo(targetTM));
        if(time > 0) {
            run(time);
        }
    }

    @Override
    protected double interpolate(double progress) {
        // EaseInOut method taken from https://gist.github.com/gre/1650294
        return progress < .5 ? 2 * progress * progress : -1 + (4 - 2 * progress) * progress;
    }

    @Override
    protected void onCancel() {
        canceled = true;
        super.onCancel();
    }

    @Override
    protected void onComplete() {
        if(!canceled){
            super.onComplete(); //By avoiding the call to "super" if cancelled, a composition of movement is created
        }
    }

    @Override
    protected void onUpdate(double progress) {
        float dX = ctm.getE() + (float) (delta.getX() * progress);
        float dY = ctm.getF() + (float) (delta.getY() * progress);
        float dS = ctm.getA() + (float) (scaleDelta * progress);
        handler.transform(getNewMatrix(dX, dY, dS));
    }

    private OMSVGPoint getTranslationDelta(OMSVGMatrix targetTM) {
        OMSVGPoint delta = svg.createSVGPoint();
        delta.setX(targetTM.getE() - ctm.getE());
        delta.setY(targetTM.getF() - ctm.getF());
        return delta;
    }

    private double getScaleDelta(OMSVGMatrix targetTM) {
        return targetTM.getA() - ctm.getA();
    }

    private double distanceTo(OMSVGMatrix targetTM) {
        double dX = targetTM.getE() - ctm.getE();
        double dY = targetTM.getF() - ctm.getF();
        return Math.sqrt(dX*dX + dY*dY);
    }

    private OMSVGMatrix getNewMatrix(float translateX, float translateY, float scale) {
        OMSVGMatrix newCTM = svg.createSVGMatrix();
        newCTM.setA(scale);
        newCTM.setB(ctm.getB());
        newCTM.setC(ctm.getC());
        newCTM.setD(scale);
        newCTM.setE(translateX);
        newCTM.setF(translateY);
        return newCTM;
    }

    private int time(double distance) {
        int d = (int) Math.ceil(distance) * 5;
        return d > MAX_ANIMATION_DURATION ? MAX_ANIMATION_DURATION : d < MIN_ANIMATION_DURATION ? MIN_ANIMATION_DURATION : d;
    }
}
