package org.reactome.web.diagram.data.layout.category;

import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.Segment;
import org.reactome.web.diagram.data.layout.Shape;
import org.reactome.web.diagram.data.layout.impl.SegmentFactory;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public abstract class ShapeCategory {

    public static boolean isHovered(Shape shape, Coordinate coordinate){
        String type = shape.getType();
        switch (type) {
            case "ARROW":
                return pointInTriangle(coordinate, shape.getA(), shape.getB(), shape.getC());
            case "BOX":
                return pointInBox(coordinate, shape.getA(), shape.getB());
            case "CIRCLE":
            case "DOUBLE_CIRCLE":
                return pointInCircle(coordinate, shape.getC(), shape.getR());
            case "STOP":
                Segment segment = SegmentFactory.get(shape.getA(), shape.getB());
                return SegmentCategory.isInSegment(segment, coordinate);
            default:
                throw new RuntimeException("Do not know shape " + type);
        }
    }

    private static double sign(Coordinate p1, Coordinate p2, Coordinate p3) {
        return (p1.getX() - p3.getX()) * (p2.getY() - p3.getY()) - (p2.getX() - p3.getX()) * (p1.getY() - p3.getY());
    }

    private static boolean pointInTriangle(Coordinate pt, Coordinate v1, Coordinate v2, Coordinate v3) {
        boolean b1, b2, b3;
        b1 = sign(pt, v1, v2) < 0.0d;
        b2 = sign(pt, v2, v3) < 0.0d;
        b3 = sign(pt, v3, v1) < 0.0d;
        return ((b1 == b2) && (b2 == b3));
    }

    private static boolean pointInCircle(Coordinate pt, Coordinate centre, double radius){
        double square_dist = Math.pow(centre.getX() - pt.getX(), 2) + Math.pow(centre.getY() - pt.getY(),2);
        return square_dist <=  Math.pow(radius,2);
    }

    private static boolean pointInBox(Coordinate pt, Coordinate min, Coordinate max){
        return  (
                pt.getX() >= min.getX() &&
                pt.getX() <= max.getX() &&
                pt.getY() >= min.getY() &&
                pt.getY() <= max.getY()
        );
    }
}
