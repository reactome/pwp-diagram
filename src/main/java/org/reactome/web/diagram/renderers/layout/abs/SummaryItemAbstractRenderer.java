package org.reactome.web.diagram.renderers.layout.abs;

import com.google.gwt.canvas.dom.client.Context2d;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.Shape;
import org.reactome.web.diagram.data.layout.SummaryItem;
import org.reactome.web.diagram.data.layout.impl.ShapeFactory;
import org.reactome.web.diagram.renderers.common.RendererProperties;
import org.reactome.web.diagram.util.AdvancedContext2d;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public abstract class SummaryItemAbstractRenderer {
    enum Type {
        TL("#FFFFFF", "#FF8A65", "#B80000"),
        TR("#FFFFFF", "#F44336", "#B71C1C"),
        BR("#FFFFFF", "#ABABAB", "#ABABAB"),
        BL("#FFFFFF", "#ABABAB", "#ABABAB"),
    	DG("#FFFFFF", "#B89AE6", "#B89AE6");

        private String txtColour;
        private String stdColour;
        private String selColour;

        Type(String txtColour, String stdColour, String selColour) {
            this.txtColour = txtColour;
            this.stdColour = stdColour;
            this.selColour = selColour;
        }

        static Type getType(String type) {
            for (Type t : values()) {
                if (t.toString().equals(type.toUpperCase())) {
                    return t;
                }
            }
            return null;
        }
    }

    public static void draw(AdvancedContext2d ctx, SummaryItem summaryItem, Double factor, Coordinate offset) {
        draw(ctx, summaryItem, factor, offset, false);
    }

    public static void draw(AdvancedContext2d ctx, SummaryItem summaryItem, Double factor, Coordinate offset, boolean highlight) {
        if (summaryItem == null || summaryItem.getNumber() == null) return;
        Type type = Type.getType(summaryItem.getType());
        if (type == null) return;
        Shape shape = summaryItem.getShape();
        if (shape == null) return;
        shape = ShapeFactory.transform(shape, factor, offset);
        double x, y;
        switch (shape.getType()) {
            case "CIRCLE":
                x = shape.getC().getX();
                y = shape.getC().getY();
                ctx.beginPath();
                ctx.arc(
                        shape.getC().getX(),
                        shape.getC().getY(),
                        shape.getR(),
                        0,
                        2 * Math.PI
                );
                ctx.stroke();

                //The only way we have for the time being to distinguish between selection or any other action is
                //by checking whether the background colour is the standard one
                boolean isSelection = ctx.getFillStyle().toString().equals("#000000");
                if (isSelection) {
                    double r = shape.getR();
                    ctx.save();         //Here we need to apply a small trick to clear the inside of the summary item
                    ctx.arc(x, y, r, 0, 2 * Math.PI);   //First we set the arc defining the inside of the summary item
                    ctx.clip();         //Clipping forces all future drawing to be limited to the clipped region
                    ctx.clearRect(x - r, y - r, r * 2, r * 2);  //Clear rect will actually clear ONLY the circle
                    ctx.restore();      //Restoring brings all future actions to "normal" :)
                } else {
                    if (highlight) {
                        ctx.fill();
                    } else {
                        ctx.save();
                        if (summaryItem.getPressed() != null && summaryItem.getPressed()) {
                            ctx.setFillStyle(type.selColour);
                        } else {
                            ctx.setFillStyle(type.stdColour);
                        }
                        ctx.fill();
                        ctx.restore();
                    }
                }
                break;
            default:
                throw new RuntimeException("Do not know how to draw summary item for " + shape.getType());
        }

        if (!highlight && summaryItem.getNumber() != null) {
            ctx.save();
            ctx.setFont(RendererProperties.getFont(RendererProperties.WIDGET_FONT_SIZE));
            ctx.setTextAlign(Context2d.TextAlign.CENTER);
            ctx.setTextBaseline(Context2d.TextBaseline.MIDDLE);
            ctx.setFillStyle(type.txtColour);
            ctx.fillText(summaryItem.getNumber() + "", x, y);
            ctx.restore();
        }
    }
}
