package org.reactome.web.diagram.data.interactors.common;

import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.NodeProperties;
import org.reactome.web.diagram.data.layout.impl.CoordinateFactory;
import uk.ac.ebi.pwp.structures.quadtree.client.QuadTreeBox;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
@SuppressWarnings("Duplicates")
public class DiagramBox implements QuadTreeBox {
    protected double minX, minY, maxX, maxY, width, height;

    public DiagramBox(QuadTreeBox box) {
        this(box.getMinX(), box.getMinY(), box.getMaxX(), box.getMaxY());
    }

    public DiagramBox(NodeProperties prop){
        this(prop.getX(), prop.getY(), prop.getX() + prop.getWidth(), prop.getY() + prop.getHeight());
    }

    public DiagramBox(double minX, double minY, double maxX, double maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        this.width = Math.abs(maxX - minX);
        this.height = Math.abs(maxY - minY);
    }

    public DiagramBox transform(double factor, Coordinate delta) {
        return new DiagramBox(
                minX * factor + delta.getX(),
                minY * factor + delta.getY(),
                maxX * factor + delta.getX(),
                maxY * factor + delta.getY()
        );
    }

    public List<DiagramBox> splitHorizontally(double... w) {
        List<DiagramBox> rtn = new LinkedList<>();
        double o = 0;
        for (double v : w) {
            rtn.add(new DiagramBox(minX + o, minY, minX + v , maxY));
            o = v;
        }
        rtn.add(new DiagramBox(minX + o, minY, maxX, maxY));
        return rtn;
    }

    public List<DiagramBox> splitVertically(double... h){
        List<DiagramBox> rtn = new LinkedList<>();
        double o = 0;
        for (double v : h) {
            rtn.add(new DiagramBox(minX, minY + o, maxX, minY + v));
            o = v;
        }
        rtn.add(new DiagramBox(minX, minY + o, maxX, maxY));
        return rtn;
    }

    @Override
    public double getMinX() {
        return minX;
    }

    @Override
    public double getMinY() {
        return minY;
    }

    @Override
    public double getMaxX() {
        return maxX;
    }

    @Override
    public double getMaxY() {
        return maxY;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public Coordinate getCentre() {
        return CoordinateFactory.get(minX + width / 2, minY + height / 2);
    }
}
