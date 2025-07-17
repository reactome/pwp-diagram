package org.reactome.web.diagram.util;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public abstract class ViewportUtils {

    private static final double MIN_FACTOR = 0.1;
    private static final double MAX_FACTOR = 15;

    public static double getFactor(double viewportWidth, double viewportHeight, double modelWidth, double modelHeight){
        double fW = viewportWidth / modelWidth;
        double fH = viewportHeight / modelHeight;
        double factor = fW < fH ? fW : fH;
        return checkFactor(factor);
    }

    public static double checkFactor(double factor){
        if (factor < MIN_FACTOR){
            factor = MIN_FACTOR;
        }else if(factor > MAX_FACTOR){
            factor = MAX_FACTOR;
        }
        return factor;
    }
}
