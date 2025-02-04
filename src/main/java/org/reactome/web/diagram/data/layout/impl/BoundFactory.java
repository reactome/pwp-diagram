package org.reactome.web.diagram.data.layout.impl;

import org.reactome.web.diagram.data.layout.Bound;
import org.reactome.web.diagram.data.layout.Coordinate;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class BoundFactory implements Bound {

    private Double x;
    private Double y;
    private Double width;
    private Double height;

    private BoundFactory(Double x, Double y, Double width, Double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public static Bound get(double x, double y, double width, double height){
        return new BoundFactory(x, y, width, height);
    }

    @Override
    public Double getX() {
        return x;
    }

    @Override
    public Double getY() {
        return y;
    }

    @Override
    public Double getWidth() {
        return width;
    }

    @Override
    public Double getHeight() {
        return height;
    }

    public static Bound transform(Bound bound, double factor, Coordinate delta) {
        return new BoundFactory(
                bound.getX() * factor + delta.getX(),
                bound.getY() * factor + delta.getY(),
                bound.getWidth() * factor,
                bound.getHeight() * factor
        );
    }
}
