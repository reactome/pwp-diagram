package org.reactome.web.diagram.data.layout.impl;

import org.reactome.web.diagram.data.interactors.common.DiagramBox;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.NodeProperties;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class NodePropertiesFactory implements NodeProperties {
    private Double x;
    private Double y;
    private Double width;
    private Double height;

    private NodePropertiesFactory(Double x, Double y, Double width, Double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public static NodeProperties get(double x, double y, double width, double height){
        return new NodePropertiesFactory(x, y, width, height);
    }

    public static NodeProperties get(NodeProperties prop){
        return new NodePropertiesFactory(prop.getX(), prop.getY(), prop.getWidth(), prop.getHeight());
    }

    public static NodeProperties get(DiagramBox box){
        return new NodePropertiesFactory(box.getMinX(), box.getMinY(), box.getWidth(), box.getHeight());
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

    public static NodeProperties transform(NodeProperties prop, double factor, Coordinate delta) {
        return new NodePropertiesFactory(
                prop.getX() * factor + delta.getX(),
                prop.getY() * factor + delta.getY(),
                prop.getWidth() * factor,
                prop.getHeight() * factor
        );
    }
}
