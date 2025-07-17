package org.reactome.web.diagram.data.layout.category;

import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.Segment;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public abstract class SegmentCategory {

    public static final int SEGMENT_WIDTH = 10; //TODO: Figure out the correct number
    private static final float EPSILON = SEGMENT_WIDTH * 30f;// 0.001f;

    public static boolean isInSegment(Segment segment, Coordinate coordinate){
        double crossProduct = crossProduct(segment.getFrom(), segment.getTo(), coordinate);
        if( Math.abs(crossProduct) > EPSILON ){
            return false;
        }

        double dotProduct = dotProduct(segment.getFrom(), segment.getTo(), coordinate);
        if( dotProduct < 0 ){
            return false;
        }

        double squaredLengthBA = squaredLengthBA(segment.getFrom(), segment.getTo());
        return ( dotProduct <= squaredLengthBA );
    }

    private static double crossProduct(Coordinate a, Coordinate b, Coordinate c){
        return (c.getY() - a.getY()) * (b.getX() - a.getX()) - (c.getX() - a.getX()) * (b.getY() - a.getY());
    }

    private static double dotProduct(Coordinate a, Coordinate b, Coordinate c){
        return (c.getX() - a.getX()) * (b.getX() - a.getX()) + (c.getY() - a.getY()) * (b.getY() - a.getY());
    }

    private static double squaredLengthBA(Coordinate a, Coordinate b){
        return (b.getX() - a.getX()) * (b.getX() - a.getX()) + (b.getY() - a.getY()) * (b.getY() -a.getY());
    }
}
