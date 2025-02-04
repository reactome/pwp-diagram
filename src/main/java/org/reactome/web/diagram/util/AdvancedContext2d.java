package org.reactome.web.diagram.util;

import com.google.gwt.canvas.dom.client.Context2d;
import org.reactome.web.diagram.renderers.layout.abs.DashedLineAbstractRenderer;

/**
 * This class extends the drawing functionality of the GWT Context2d.
 * Methods include drawing dashed lines and shapes, ellipses, arrows
 * and shapes specific to the Reactome diagram viewer.
 *
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class AdvancedContext2d extends Context2d {

    protected AdvancedContext2d() {
    }

    //////////////////////////////////////
    //  Methods for Rounded Rectangles  //
    //////////////////////////////////////

    /**
     * Draws a rectangle with rounded edges.
     */
    public final void roundedRectangle(double x,
                                       double y,
                                       double width,
                                       double height,
                                       double arcWidth) {
        double right = x + width;
        double bottom = y + height;

        beginPath();
        moveTo(x + arcWidth, y);
        lineTo(right - arcWidth, y);
        quadraticCurveTo(right, y, right, y + arcWidth);
        lineTo(right, bottom - arcWidth);
        quadraticCurveTo(right, bottom, right - arcWidth, bottom);
        lineTo(x + arcWidth, bottom);
        quadraticCurveTo(x, bottom, x, bottom - arcWidth);
        lineTo(x, y + arcWidth);
        quadraticCurveTo(x, y, x + arcWidth, y);
        closePath();
    }

    /**
     * Draws a dashed rectangle with rounded edges.
     */
    public final void dashedRoundedRectangle(double x,
                                             double y,
                                             double width,
                                             double height,
                                             double arcWidth,
                                             double[] dashedLinePattern) {
        // Draw the four dashed lines
        double dashLength = dashedLinePattern[0];
        double gapLength = dashedLinePattern[1];
        beginPath();
        DashedLineAbstractRenderer.drawDashedLine(this, x + arcWidth + gapLength, y, x + width - arcWidth, y, dashLength, gapLength);
        DashedLineAbstractRenderer.drawDashedLine(this, x + width, y + arcWidth, x + width, y + height - arcWidth, dashLength, gapLength);
        DashedLineAbstractRenderer.drawDashedLine(this, x + width - arcWidth, y + height, x + arcWidth, y + height, dashLength, gapLength);
        DashedLineAbstractRenderer.drawDashedLine(this, x, y + height - arcWidth, x, y + arcWidth, dashLength, gapLength);

        // Need to draw rounded corners
        moveTo(x, y + arcWidth);
        quadraticCurveTo(x, y, x + arcWidth, y);

        moveTo(x + width - arcWidth, y);
        quadraticCurveTo(x + width, y, x + width, y + arcWidth);

        moveTo(x + width, y + height - arcWidth);
        quadraticCurveTo(x + width, y + height, x + width - arcWidth, y + height);

        moveTo(x + arcWidth, y + height);
        quadraticCurveTo(x, y + height, x, y + height - arcWidth);
        closePath();
    }

    public final void homePlate(double x,
                                double y,
                                double width,
                                double height,
                                double offset) {
        beginPath();
        moveTo(x, y);
        lineTo(x, y + height);
        lineTo(x + width - offset, y + height);
        lineTo( x + width, y + height / 2);
        lineTo(x + width - offset, y);
        closePath();
    }
    public final void hexagonNode(double x,
                                  double y,
                                  double width,
                                  double height,
                                  double offset) {
        beginPath();
        moveTo(x, y + height / 2);
        lineTo(x + offset, y + height);
        lineTo(x + width - offset, y + height);
        lineTo( x + width, y + height / 2);
        lineTo(x + width - offset, y);
        lineTo(x + offset, y);
        closePath();
    }

    //////////////////////////////
    //  Methods for Octagons    //
    //////////////////////////////

    /**
     * Draws an octagon - used to draw complexes.
     */
    public final void octagon(double x,
                              double y,
                              double width,
                              double height,
                              double arcWidth) {

        double x1 = x + arcWidth;
        double y1 = y;

        beginPath();
        moveTo(x1, y1);
        x1 += (width - 2 * arcWidth);
        lineTo(x1, y1);
        x1 = x + width;
        y1 = y + arcWidth;
        lineTo(x1, y1);
        y1 += (height - 2 * arcWidth);
        lineTo(x1, y1);
        x1 = x + width - arcWidth;
        y1 = y + height;
        lineTo(x1, y1);
        x1 = x + arcWidth;
        lineTo(x1, y1);
        x1 = x;
        y1 = y + height - arcWidth;
        lineTo(x1, y1);
        y1 = y + arcWidth;
        lineTo(x1, y1);
        closePath();
    }

    /**
     * Draws a dashed octagon - used to depict complexes.
     */
    public final void dashedOctagon(double x,
                                    double y,
                                    double width,
                                    double height,
                                    double arcWidth,
                                    double[] dashedLinePattern) {

        double dashLength = dashedLinePattern[0];
        double gapLength = dashedLinePattern[1];
        beginPath();
        DashedLineAbstractRenderer.drawDashedLine(this, x + arcWidth, y, x + width - arcWidth, y, dashLength, gapLength);
        DashedLineAbstractRenderer.drawDashedLine(this, x + width, y + arcWidth, x + width, y + height - arcWidth, dashLength, gapLength);
        DashedLineAbstractRenderer.drawDashedLine(this, x + arcWidth, y + height, x + width - arcWidth, y + height, dashLength, gapLength);
        DashedLineAbstractRenderer.drawDashedLine(this, x, y + arcWidth, x, y + height - arcWidth, dashLength, gapLength);

        moveTo(x, y + arcWidth);
        lineTo(x + arcWidth, y);

        moveTo(x + width - arcWidth, y);
        lineTo(x + width, y + arcWidth);

        moveTo(x + width, y + height - arcWidth);
        lineTo(x + width - arcWidth, y + height);

        moveTo(x + arcWidth, y + height);
        lineTo(x, y + height - arcWidth);
    }

    ////////////////////////////////////
    //  Methods for various shapes    //
    ////////////////////////////////////

    public final void boneShape(double x,
                                double y,
                                double width,
                                double height,
                                double loopWidth) {
        double right = x + width;
        double bottom = y + height;

        double xAux = x + loopWidth;
        double yAux = y + loopWidth / 2;
        moveTo(xAux, yAux);
        xAux = right - loopWidth;
        lineTo(xAux, yAux);
        yAux = y + height / 2;
        quadraticCurveTo(right, y, right, yAux);

        xAux = right - loopWidth;
        yAux = bottom - loopWidth / 2;
        quadraticCurveTo(right, bottom, xAux, yAux);

        xAux = x + loopWidth;
        lineTo(xAux, yAux);
        yAux = y + height / 2;
        quadraticCurveTo(x, bottom, x, yAux);

        xAux = x + loopWidth;
        yAux = y + loopWidth / 2;
        quadraticCurveTo(x, y, xAux, yAux);
        closePath();
    }

    public final void geneTextHolder(double x,
                                     double y,
                                     double width,
                                     double height,
                                     double symbolWidth,
                                     double arcWidth) {
        double y1 = y + symbolWidth / 2;
        double right = x + width;
        double bottom = y + height;

        // Draw the horizontal line
        beginPath();
        moveTo(x, y1);
        lineTo(right, y1);
        lineTo(right, bottom - arcWidth);
        quadraticCurveTo(right, bottom, right - arcWidth, bottom);
        lineTo(x + arcWidth, bottom);
        quadraticCurveTo(x, bottom, x, bottom - arcWidth);
        closePath();
    }

    /**
     * Draws the shape used to depict a gene.
     */
    public final void geneShape(double x,
                                double y,
                                double width,
                                double symbolPad,
                                double symbolWidth,
                                double arrowLength,
                                double arrowAngle) {
        // Draw the horizontal line
        double y1 = y + symbolWidth / 2;
        double x2 = x + width;
        double y2 = y + symbolWidth / 2;

        // Draw the horizontal line
        beginPath();
        moveTo(x, y1);
        lineTo(x2, y2);

        // Draw the vertical line
        double x1 = x2 - symbolPad;
        x2 = x1;
        y2 = (int) (y1 - symbolWidth / 2.0) + 2; // Need an extra 2 pixel. Not sure why!
        // Looks nice with one pixel offset
        moveTo(x1, y1);
        lineTo(x2, y2);
        // another very short horizontal line
        x1 += symbolPad;
        lineTo(x1, y2);
        stroke();
        // draw the arrow
        //String color = node.getLineColor();
        //if (color == null){  color = "rgba(0, 0, 0, 1)"; }
        // Keep it to reset it to the original style is always a good practice
        //FillStrokeStyle oldStyle = getFillStyle();
        //setFillStyle(CssColor.make(color));
        drawArrow(x1, y2, x1 + arrowLength, y2, arrowLength, arrowAngle);
        //setFillStyle(oldStyle); // Reset it back
    }

    //////////////////////////////
    //  Methods for Arrows      //
    //////////////////////////////

    /**
     * Draws an arrow from one point to another taking into account the angle of the line.
     */
    private void drawArrow(double fromX,
                                double fromY,
                                double toX,
                                double toY,
                                double arrowLength,
                                double arrowAngle) {
        // Get the angle of the line segment
        double alpha = Math.atan((toY - fromY) / (toX - fromX));
        if (fromX > toX)
            alpha += Math.PI;
        double angle = arrowAngle - alpha;
        float x1 = (float) (toX - arrowLength * Math.cos(angle));
        float y1 = (float) (toY + arrowLength * Math.sin(angle));
        beginPath(); // Have to call this begin path. Otherwise, the following path will be mixed with other unknown drawings.
        moveTo(x1, y1);
        lineTo(toX, toY);
        angle = arrowAngle + alpha;
        float x2 = (float) (toX - arrowLength * Math.cos(angle));
        float y2 = (float) (toY - arrowLength * Math.sin(angle));
        lineTo(x2, y2);
        closePath();
        fill();
        stroke();
    }


    //////////////////////////////
    //  Methods for Ellipses    //
    //////////////////////////////

    public final void ellipse(double x, double y, double width, double height) {
        double offset = height * 0.15;
        double x1 = x + width;
        double y1 = y + height / 2;
        // Draws an ellipse based on bezier curves. Please note
        beginPath();
        moveTo(x, y1);
        bezierCurveTo(x, y - offset, x1, y - offset, x1, y1);
        bezierCurveTo(x + width, y + height + offset, x, y + height + offset, x, y1);
        closePath();
    }

    public final void bubble(double minX, double minY, double maxX, double maxY) {
        double offset = (maxY - minY) * 0.15;
        double y1 = minY + (maxY - minY) / 2;
        // Draws an ellipse
        beginPath();
        moveTo(minX, y1);
        bezierCurveTo(minX, minY - offset, maxX, minY - offset, maxX, y1);
        bezierCurveTo(maxX, maxY + offset, minX, maxY + offset, minX, y1);
        closePath();
    }
}