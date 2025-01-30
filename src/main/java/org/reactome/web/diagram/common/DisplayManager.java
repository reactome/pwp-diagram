package org.reactome.web.diagram.common;

import org.reactome.web.diagram.data.DiagramStatus;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.NodeCommon;
import org.reactome.web.diagram.data.layout.NodeProperties;
import org.reactome.web.diagram.data.layout.impl.CoordinateFactory;
import org.reactome.web.diagram.util.ViewportUtils;
import uk.ac.ebi.pwp.structures.quadtree.client.Box;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class DisplayManager {

    private final static double MAX_FACTOR = 1.25;

    private DiagramAnimationHandler handler;

    private DiagramAnimation diagramAnimation;

    public DisplayManager(DiagramAnimationHandler handler) {
        this.handler = handler;
    }

    public void cancelDisplayAnimation(){
        if(this.diagramAnimation!=null) this.diagramAnimation.cancel();
    }

    public void display(Collection<DiagramObject> items, boolean animation){
        if(items!=null && !items.isEmpty()) {
            Box box = getBoundaries(items);
            this.display(box.getMinX(), box.getMinY(), box.getMaxX(), box.getMaxY(), animation);
        }
    }

    public void display(double minX, double minY, double maxX, double maxY, boolean animation){
        //1- Growing the box a "space" bigger as the view offset
        double space = 40;
        minX -= space; minY -= space; maxX += space; maxY += space;

        //2- Calculate the
        double vpWidth = this.handler.getViewportWidth();
        double vpHeight = this.handler.getViewportHeight();
        double width = (maxX - minX);
        double height = (maxY - minY);
        double p = vpWidth / vpHeight;

        if(Double.isNaN(p)){
            p = 1; //This happens when the window where the widget is attached to has not visible area
        }

        //3- Calculate the factor
        double factor = ViewportUtils.getFactor(vpWidth, vpHeight, width, height);
        factor =  factor > MAX_FACTOR ? MAX_FACTOR : factor;

        //3- Calculating proportions (and corrections for positioning)
        if(width > height){
            double aux = height;
            height = width * p;
            minY -= (height - aux)/2.0; //if wider then height and minY corrected
        }else{
            double aux = width;
            width = height / p;
            minX -= (width - aux)/2.0; //if higher then width and minX corrected
        }

        //4-
        DiagramStatus status = handler.getDiagramStatus();
        double deltaFactor = factor - status.getFactor();
        Coordinate targetCentre = CoordinateFactory.get(minX + width / 2.0, minY + height / 2.0);

        Coordinate viewportCentre = CoordinateFactory.get(vpWidth / 2.0, vpHeight / 2.0);
        Coordinate centre = status.getModelCoordinate(viewportCentre);

        Coordinate deltaOffset = centre.multiply(status.getFactor()).minus(targetCentre.multiply(factor));

        //5- Display the area
        if(this.diagramAnimation!=null) this.diagramAnimation.cancel();
        if(animation) {
            //5.1- Animates the movement
            this.diagramAnimation = new DiagramAnimation(this.handler, status.getFactor(), status.getOffset());
            this.diagramAnimation.animate(deltaOffset, deltaFactor);
        }else{
            //5.2- Direct to the final coordinate (No animation)
            this.handler.transform(deltaOffset, factor);
        }
    }

    private Box getBoundaries(Collection<DiagramObject> items){
        List<Double> xx = new LinkedList<>();
        List<Double> yy = new LinkedList<>();
        for (DiagramObject item : items) {
            if(item instanceof NodeCommon){
                NodeCommon node = (NodeCommon) item;
                NodeProperties prop = node.getProp();
                xx.add(prop.getX());
                xx.add(prop.getX() + prop.getWidth());
                yy.add(prop.getY());
                yy.add(prop.getY() + prop.getHeight());
            }else {
                xx.add(item.getMinX());
                xx.add(item.getMaxX());
                yy.add(item.getMinY());
                yy.add(item.getMaxY());
            }
        }
        return new Box(
                Collections.min(xx),
                Collections.min(yy),
                Collections.max(xx),
                Collections.max(yy)
        );
    }
}
