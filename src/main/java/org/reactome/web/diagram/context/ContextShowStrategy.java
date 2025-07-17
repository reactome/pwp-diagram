package org.reactome.web.diagram.context;

import org.reactome.web.diagram.data.layout.Bound;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.NodeProperties;
import org.reactome.web.diagram.data.layout.Shape;
import org.reactome.web.diagram.data.layout.impl.BoundFactory;
import org.reactome.web.diagram.data.layout.impl.CoordinateFactory;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public abstract class ContextShowStrategy {

    public static Coordinate getPosition(int dialogWidth, int dialogHeight, NodeProperties prop, Bound canvas){
        Bound bound = BoundFactory.get(prop.getX(), prop.getY(), prop.getWidth(), prop.getHeight());
        return getPosition(dialogWidth, dialogHeight, bound, canvas);
    }

    public static Coordinate getPosition(int dialogWidth, int dialogHeight, Shape shape, Bound canvas){
        Bound bound;

        String type = shape.getType();
        if (type.equals("BOX")) {
            bound = BoundFactory.get(
                    shape.getA().getX(),
                    shape.getA().getY(),
                    shape.getB().getX() - shape.getA().getX(),
                    shape.getB().getY() - shape.getA().getY());
        } else if (type.equals("CIRCLE") || type.equals("DOUBLE_CIRCLE")) {
            bound = BoundFactory.get(
                    shape.getC().getX() - shape.getR(),
                    shape.getC().getY() - shape.getR(),
                    shape.getC().getX() + shape.getR(),
                    shape.getC().getY() + shape.getR());
        } else {
            // This should not happen
            bound = BoundFactory.get(
                    canvas.getWidth()/2,
                    canvas.getHeight()/2,
                    canvas.getWidth()/2 + 10,
                    canvas.getHeight()/2 + 10);
        }

        return getPosition(dialogWidth, dialogHeight, bound, canvas);
    }

    private static Coordinate getPosition(int dialogWidth, int dialogHeight, Bound bound, Bound canvas){
        int left; int top;
        int leftSpace = bound.getX().intValue() > 0 ? bound.getX().intValue() : 0;
        int rightScace = canvas.getWidth().intValue() - (bound.getX().intValue() + bound.getWidth().intValue());
        int topSpace = bound.getY().intValue() > 0 ? bound.getY().intValue() : 0;
        int bottomSpace = canvas.getHeight().intValue() - (bound.getY().intValue() + bound.getHeight().intValue());

        // Horizontal positioning
        if (rightScace>dialogWidth) {
            left = canvas.getX().intValue() + bound.getX().intValue() + bound.getWidth().intValue();    // Place it right
        } else if (leftSpace>dialogWidth) {
            left = canvas.getX().intValue() + bound.getX().intValue() - dialogWidth;                    // Place it left
        }else{
            left = canvas.getX().intValue() + canvas.getWidth().intValue()/2;                           // Extreme case
        }

        // Vertical positioning
        if (bottomSpace>dialogHeight) {
            top = canvas.getY().intValue() + bound.getY().intValue() + bound.getHeight().intValue();    // Place it bottom
        } else if (topSpace>dialogHeight) {
            top = canvas.getY().intValue() + bound.getY().intValue() - dialogHeight;                    // Place it top
        }else {
            top = canvas.getY().intValue() + canvas.getHeight().intValue()/2;                           // Extreme case
        }

        return CoordinateFactory.get(left,top);
    }
}
