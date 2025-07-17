package org.reactome.web.diagram.data.layout.impl;

import org.reactome.web.diagram.data.layout.Coordinate;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class CoordinateFactory implements Coordinate {
    private Double x;
    private Double y;

    private CoordinateFactory(Double x, Double y) {
        this.x = x;
        this.y = y;
    }

    public static Coordinate get(double x, double y){
        return new CoordinateFactory(x, y);
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
    public Coordinate add(Coordinate value) {
        return new CoordinateFactory(x + value.getX(), y + value.getY());
    }

    @Override
    public Coordinate divide(double factor) {
        return new CoordinateFactory(x / factor, y / factor);
    }

    @Override
    public Coordinate minus(Coordinate value) {
        return new CoordinateFactory(x - value.getX(), y - value.getY());
    }

    @Override
    public Coordinate multiply(double factor) {
        return new CoordinateFactory(x * factor, y * factor);
    }

    @Override
    public Coordinate transform(double factor, Coordinate delta) {
        return new CoordinateFactory(
                x * factor + delta.getX(),
                y * factor + delta.getY()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CoordinateFactory that = (CoordinateFactory) o;

        //noinspection SimplifiableIfStatement
        if (x != null ? !x.equals(that.x) : that.x != null) return false;
        return !(y != null ? !y.equals(that.y) : that.y != null);
    }

    @Override
    public int hashCode() {
        int result = x != null ? x.hashCode() : 0;
        result = 31 * result + (y != null ? y.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Position{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
