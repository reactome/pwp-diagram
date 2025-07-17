package org.reactome.web.diagram.thumbnail.diagram.render;

import org.reactome.web.diagram.data.layout.*;
import org.reactome.web.diagram.data.layout.impl.CoordinateFactory;
import org.reactome.web.diagram.util.AdvancedContext2d;

import java.util.List;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public abstract class AbstractThumbnailRenderer implements ThumbnailRenderer {

    @Override
    public void highlight(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        Node node = (Node) item;
        drawBox(ctx, node, factor, offset);
    }

    public void drawNode(AdvancedContext2d ctx, Node node, Double factor, Coordinate offset) {
        drawBox(ctx, node, factor, offset);
        for (Connector connector : node.getConnectors()) {
            drawSegments(ctx, connector.getSegments(), factor, offset);
        }
    }

    void drawSegments(AdvancedContext2d ctx, List<Segment> segments, Double factor, Coordinate offset) {
        for (Segment segment : segments) {
            ctx.beginPath();
            Coordinate from = segment.getFrom().add(offset).multiply(factor);
            Coordinate to = segment.getTo().add(offset).multiply(factor);
            ctx.moveTo(from.getX(), from.getY());
            ctx.lineTo(to.getX(), to.getY());
            ctx.stroke();
        }
    }

    void drawShape(AdvancedContext2d ctx, Shape shape, Double factor, Coordinate offset){
        if(shape==null) return;
        Coordinate a = shape.getA().add(offset).multiply(factor);
        Coordinate b = shape.getB().add(offset).multiply(factor);
        Coordinate c = shape.getC().add(offset).multiply(factor);

        ctx.beginPath();
        ctx.moveTo(a.getX(), a.getY());
        ctx.lineTo(b.getX(), b.getY());
        ctx.lineTo(c.getX(), c.getY());
        ctx.closePath();
        //ctx.setFillStyle(shape.getFillColour());
        ctx.fill();
    }

    void drawBox(AdvancedContext2d ctx, Node node, Double factor, Coordinate offset){
        NodeProperties prop = node.getProp();
        Coordinate corner = CoordinateFactory.get(prop.getX(), prop.getY()).add(offset).multiply(factor);
        double w = prop.getWidth() * factor;
        double h = prop.getHeight() * factor;

        ctx.beginPath();
        ctx.rect(
                corner.getX(),
                corner.getY(),
                w,
                h
        );
        ctx.fill();
        ctx.stroke();
    }
}
