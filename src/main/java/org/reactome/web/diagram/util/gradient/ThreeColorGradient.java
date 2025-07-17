package org.reactome.web.diagram.util.gradient;

import com.google.gwt.core.client.GWT;
import org.reactome.web.diagram.profiles.analysis.model.ProfileGradient;


/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class ThreeColorGradient {

    private TwoColorGradient first;
    private TwoColorGradient second;

    public ThreeColorGradient(ProfileGradient gradient){
        this(gradient.getMin(), gradient.getStop(), gradient.getMax());
    }

    public ThreeColorGradient(String hexFrom, String hexStop, String hexTo) {
        if(hexStop==null){
            hexStop = hexTo;
            hexTo = null;
        }
        try {
            this.first = new TwoColorGradient(hexStop, hexFrom);
        } catch (Exception e) {
            GWT.log(e.getMessage());
        }

        if(hexTo != null) { //When set up to null, only applies two color gradient
            try {
                this.second = new TwoColorGradient(hexTo, hexStop);
            } catch (Exception e) {
                GWT.log(e.getMessage());
            }
        }
    }

    public String getColor(double p){
        if(this.second==null){ //Only applies two color gradient
            return this.first.getColor(p);
        }
        if(p <= 0.5){
            return this.first.getColor(p/0.5);
        } else {
            double aux = p - 0.5;
            return this.second.getColor(aux/0.5);
        }
    }

    public String getColor(double point, double min, double max){
        return getColor(getPercentage(point, min, max));
    }

    public static double getPercentage(double point, double min, double max){
        double length = Math.abs(max - min);
        double delta = Math.abs(point - max);
        return delta / length;
    }
}
