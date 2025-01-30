package org.reactome.web.diagram.renderers.common;

import com.google.gwt.i18n.client.NumberFormat;
import org.reactome.web.diagram.renderers.layout.abs.ChemicalDrugAbstractRenderer;


/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public abstract class RendererProperties {
    private static final NumberFormat myFormatter = NumberFormat.getFormat(".##");
    private static double FACTOR;

    static {
        setFactor(1.0);
    }

    public static double ARROW_ANGLE = Math.PI / 6;
    public static double ARROW_LENGTH;
    public static double[] DASHED_LINE_PATTERN;
    public static double EDGE_TYPE_WIDGET_WIDTH;
    public static double ROUND_RECT_ARC_WIDTH;
    public static double COMPLEX_RECT_ARC_WIDTH;
    public static double RNA_LOOP_WIDTH;
    public static double GENE_SYMBOL_PAD;
    public static double GENE_SYMBOL_WIDTH;
    public static double WIDGET_FONT_SIZE;
    public static double MAX_WIDGET_FONT_SIZE = 19;
    public static double NOTE_FONT_SIZE;
    public static double SEPARATION;
    public static double CELL_SEPARATION;
    public static double PROCESS_NODE_INSET_WIDTH;
    public static double NODE_TEXT_PADDING;
    public static double NODE_LINE_WIDTH;
    public static double INTERACTOR_FONT_SIZE;
    public static double DRUG_RX_BOX = 7;

    public static void setFactor(double factor) {
        FACTOR = factor;
        ARROW_LENGTH = 8 * factor;
        DASHED_LINE_PATTERN = new double[]{5.0d * factor, 5.0d * factor};
        EDGE_TYPE_WIDGET_WIDTH = 12 * factor;
        ROUND_RECT_ARC_WIDTH = 6 * factor;
        COMPLEX_RECT_ARC_WIDTH = 6 * factor;
        RNA_LOOP_WIDTH = 16 * factor;
        GENE_SYMBOL_PAD = 4 * factor;
        GENE_SYMBOL_WIDTH = 50 * factor;
        SEPARATION = 3 * factor;
        CELL_SEPARATION = 2 * factor;
        PROCESS_NODE_INSET_WIDTH = 10 * factor;
        NODE_TEXT_PADDING = 10 * factor;
        WIDGET_FONT_SIZE = 9 * factor;
        if (WIDGET_FONT_SIZE > MAX_WIDGET_FONT_SIZE) {
            WIDGET_FONT_SIZE = MAX_WIDGET_FONT_SIZE;
        }
        NOTE_FONT_SIZE = 10 * factor;
        NODE_LINE_WIDTH = 2 * factor;
        INTERACTOR_FONT_SIZE = 6.33 * factor;
        DRUG_RX_BOX = ChemicalDrugAbstractRenderer.CHEMICAL_DRUG_RX_BOX * factor;
    }

    public static double getFactor() {
        return FACTOR;
    }

    public static String getFont(double fontSize) {
        return "bold " + myFormatter.format(fontSize) + "px Arial";
    }
}
