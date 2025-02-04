package org.reactome.web.diagram.renderers.layout.abs;

import com.google.gwt.canvas.dom.client.Context2d;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.NodeAttachment;
import org.reactome.web.diagram.data.layout.Shape;
import org.reactome.web.diagram.data.layout.impl.ShapeFactory;
import org.reactome.web.diagram.profiles.diagram.DiagramColours;
import org.reactome.web.diagram.renderers.common.RendererProperties;
import org.reactome.web.diagram.util.AdvancedContext2d;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public abstract class AttachmentAbstractRenderer {

    public static void draw(AdvancedContext2d ctx, NodeAttachment attachment, Double factor, Coordinate offset, boolean fill){
        Shape s = ShapeFactory.transform(attachment.getShape(), factor, offset);
        ctx.beginPath();
        ctx.rect(
                s.getA().getX(),
                s.getA().getY(),
                s.getB().getX() - s.getA().getX(),
                s.getB().getY() - s.getA().getY()
        );
        ctx.stroke();
        if (fill) {
            ctx.fill();
        } else {
            ctx.clearRect(
                    s.getA().getX(),
                    s.getA().getY(),
                    s.getB().getX() - s.getA().getX(),
                    s.getB().getY() - s.getA().getY()
            );
        }

        if(attachment.getLabel()!=null) {
            ctx.save();
            ctx.setTextAlign(Context2d.TextAlign.CENTER);
            ctx.setTextBaseline(Context2d.TextBaseline.MIDDLE);
            ctx.setFont(RendererProperties.getFont(RendererProperties.WIDGET_FONT_SIZE));
            ctx.setFillStyle(DiagramColours.get().PROFILE.getAttachment().getText());
            Coordinate c = s.getB().minus(s.getA()).divide(2).add(s.getA());
            ctx.fillText(attachment.getLabel(), c.getX(), c.getY());
            ctx.restore();
        }
    }
}
