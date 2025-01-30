package org.reactome.web.diagram.renderers.layout.abs;

import com.google.gwt.canvas.dom.client.Context2d;
import org.reactome.web.diagram.data.layout.Compartment;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.NodeProperties;
import org.reactome.web.diagram.data.layout.impl.CoordinateFactory;
import org.reactome.web.diagram.data.layout.impl.NodePropertiesFactory;
import org.reactome.web.diagram.profiles.diagram.DiagramColours;
import org.reactome.web.diagram.renderers.common.ColourProfileType;
import org.reactome.web.diagram.renderers.common.RendererProperties;
import org.reactome.web.diagram.renderers.helper.RoundedRectangleHelper;
import org.reactome.web.diagram.util.AdvancedContext2d;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public abstract class CompartmentAbstractRenderer extends AbstractRenderer {
    private static final Coordinate GWU_CORRECTION = CoordinateFactory.get(14, 18);

    @Override
    public void drawText(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        Compartment compartment = (Compartment) item;
        Coordinate pos = compartment.getTextPosition().add(GWU_CORRECTION).transform(factor, offset);
        TextRenderer textRenderer = new TextRenderer(RendererProperties.WIDGET_FONT_SIZE);
        textRenderer.drawTextSingleLine(ctx, item.getDisplayName(), pos);
    }

    @Override
    public void draw(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        if (!isVisible(item)) return;


        Compartment compartment = (Compartment) item;
        NodeProperties prop = NodePropertiesFactory.transform(compartment.getProp(), factor, offset);

        RoundedRectangleHelper helper = new RoundedRectangleHelper(prop).setArc(3 * RendererProperties.ROUND_RECT_ARC_WIDTH);
        helper.trace(ctx);
        ctx.stroke();
        ctx.fill();

        if (isInsetsNeeded(compartment)) {
            NodeProperties insets = new NodeProperties.Builder()
                    .copy(compartment.getInsets())
                    .transform(factor, offset).build();

            ctx.beginPath();
            new RoundedRectangleHelper(insets, 0d, 3 * RendererProperties.ROUND_RECT_ARC_WIDTH - (insets.getX() - prop.getX())).trace(ctx);
            ctx.stroke();
            ctx.fill();
        }
    }

    @Override
    public void highlight(AdvancedContext2d ctx, DiagramObject item, Double factor, Coordinate offset) {
        //Nothing here
    }

    @Override
    public boolean isVisible(DiagramObject item) {
        return true;
    }

    public boolean isInsetsNeeded(Compartment compartment) {
        String name = compartment.getDisplayName();
        return name != null && !name.endsWith("membrane") && !name.equals("Unidentified Compartment");
    }

    @Override
    public void setColourProperties(AdvancedContext2d ctx, ColourProfileType type) {
        type.setColourProfile(ctx, DiagramColours.get().PROFILE.getCompartment());
    }

    @Override
    public void setTextProperties(AdvancedContext2d ctx, ColourProfileType type) {
        ctx.setFont(RendererProperties.getFont(RendererProperties.WIDGET_FONT_SIZE));
        ctx.setTextAlign(Context2d.TextAlign.LEFT);
        ctx.setTextBaseline(Context2d.TextBaseline.TOP);
        type.setTextProfile(ctx, DiagramColours.get().PROFILE.getCompartment());
    }
}
