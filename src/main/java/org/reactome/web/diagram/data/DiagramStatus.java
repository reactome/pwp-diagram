package org.reactome.web.diagram.data;

import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.impl.CoordinateFactory;
import uk.ac.ebi.pwp.structures.quadtree.client.Box;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class DiagramStatus {

    private double factor = 1;
    private Coordinate offset = CoordinateFactory.get(0, 0);

    public double getFactor() {
        return factor;
    }

    public void setFactor(double factor){
        if(factor==0) return;
        this.factor = factor;
    }

    public void padding(Coordinate delta) {
        this.offset = this.offset.add(delta);
    }

    public Coordinate getOffset() {
        return offset;
    }

    public void setOffset(Coordinate offset) {
        this.offset = offset;
    }

    public Coordinate getModelCoordinate(Coordinate viewport) {
        return viewport.minus(offset).divide(factor);
    }

    public Box getVisibleModelArea(double width, double height) {
        Coordinate topLeft = this.getModelCoordinate(CoordinateFactory.get(0, 0));
        Coordinate bottomRight = this.getModelCoordinate(CoordinateFactory.get(width, height));
        return new Box(topLeft.getX(), topLeft.getY(), bottomRight.getX(), bottomRight.getY());
    }

    @Override
    public String toString() {
        return "DiagramStatus{" +
                "factor=" + factor +
                ", offset=" + offset +
                '}';
    }
}
