package org.reactome.web.diagram.data.layout;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface Coordinate {

    Double getX();

    Double getY();

    Coordinate add(Coordinate value);

    Coordinate divide(double factor);

    Coordinate minus(Coordinate value);

    Coordinate multiply(double factor);

    Coordinate transform(double factor, Coordinate delta);

}