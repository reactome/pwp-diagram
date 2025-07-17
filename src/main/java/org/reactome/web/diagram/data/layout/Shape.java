package org.reactome.web.diagram.data.layout;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface Shape {

    Coordinate getA();

    Coordinate getB();

    Coordinate getC();

    Double getR();

    Double getR1();

    String getS();

    String getType();

    Boolean getEmpty();

}
