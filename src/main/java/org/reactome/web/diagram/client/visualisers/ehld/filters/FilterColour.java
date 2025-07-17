package org.reactome.web.diagram.client.visualisers.ehld.filters;

/**
 * An enumeration of all ColorMatrices used.
 * Please have a look at this http://alistapart.com/article/finessing-fecolormatrix
 * for more information on how the color matrix works.
 *
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public enum FilterColour {
    YELLOW( "1 0 0 1 0 " +
            "0 1 0 1 0 " +
            "0 0 0 0 0 " +
            "0 0 0 1 0 " ),

    BLUE(   "0 0 0 0 0 " +
            "0 0 0 0 0 " +
            "0 0 1 1 0 " +
            "0 0 0 1 0 " ),

    CYAN(   "1 0 0 1 0 " +
            "0 0 0 0 0 " +
            "0 0 1 1 0 " +
            "0 0 0 1 0 " );


    public final String colourMatrix;

    FilterColour(String colourMatrix) {
        this.colourMatrix = colourMatrix;
    }
}
