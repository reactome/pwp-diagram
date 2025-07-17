package org.reactome.web.diagram.data.layout.impl;

import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.Shape;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class ShapeFactory implements Shape {
    protected Coordinate a;
    protected Coordinate b;
    protected Coordinate c;
    protected Double r;
    protected Double r1;
    protected String s;
    protected String type;
    protected Boolean empty;

    private ShapeFactory(Coordinate a, Coordinate b, Coordinate c, Double r, Double r1, String s, Boolean empty, String type) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.r = r;
        this.r1 = r1;
        this.s = s;
        this.empty = empty;
        this.type = type;
    }

    @Override
    public Coordinate getA() {
        return a;
    }

    @Override
    public Coordinate getB() {
        return b;
    }

    @Override
    public Coordinate getC() { return c; }

    @Override
    public Double getR() {
        return r;
    }

    @Override
    public Double getR1() {
        return r1;
    }

    @Override
    public String getS() {
        return s;
    }

    @Override
    public Boolean getEmpty() {
        return empty;
    }


    @Override
    public String getType() {
        return type;
    }

    public static Shape transform(Shape shape, double factor, Coordinate delta) {
        return new ShapeFactory(
                (shape.getA()!=null ? shape.getA().multiply(factor).add(delta): null),
                (shape.getB()!=null ? shape.getB().multiply(factor).add(delta): null),
                (shape.getC()!=null ? shape.getC().multiply(factor).add(delta): null),
                (shape.getR()!=null ? shape.getR() * factor: null),
                (shape.getR1()!=null ? shape.getR1() * factor: null),
                shape.getS(),
                shape.getEmpty(),
                shape.getType()
        );
    }
}
