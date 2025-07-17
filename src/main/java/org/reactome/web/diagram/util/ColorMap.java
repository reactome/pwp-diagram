package org.reactome.web.diagram.util;

import org.reactome.web.diagram.util.gradient.ThreeColorGradient;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public class ColorMap {
    private static String DEFAULT_NOT_FOUND = "#999999";
    private Map<Integer, String> palette = new HashMap<>();

    private ColorMap(ThreeColorGradient gradient) {
        palette.put( 2, gradient.getColor(0.0));                    // Sig up
        palette.put( 1, gradient.getColor(0.25));                   // Non sig up
        palette.put( 0, getNotFoundColor(gradient.getColor(0.5)));  // Not Found
        palette.put(-1, gradient.getColor(0.75));                   // Non sig down
        palette.put(-2, gradient.getColor(1.0));                    // Sig down
    }

    public static ColorMap fromGradient(ThreeColorGradient gradient) {
        if (gradient == null) throw new RuntimeException("Unable to initialise ColorMap. Color gradient cannot be null.");
        return new ColorMap(gradient);
    }

    public String getColor(Integer p) {
        return palette.getOrDefault(p, "#FF000");
    }

    public Map<Integer, String> getPalette() {
        return new HashMap<>(palette);
    }


    public static double getPercentage(double point, double min, double max){
        return ( (point - max) * (0.90 - 0.10)/(min - max) ) + 0.10;
    }

    @Override
    public String toString() {
        return "ColorMap" + palette;
    }

    private String getNotFoundColor(String input) {
        String rtn;
        try {
            Color c = new Color(input);
            rtn = "#" + c.getGray().getHex();
        } catch (Exception e) {
            rtn = DEFAULT_NOT_FOUND;
        }
        return rtn;
    }
}
